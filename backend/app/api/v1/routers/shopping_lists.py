from typing import List

from geopy.distance import geodesic

from backend.app.api.v1.routers.dependencies import get_current_user
from backend.app.database import get_db
from backend.app.models import Product, ShoppingList, ShoppingListGroup, ShoppingListItem, User
from backend.app.schemas import OptimizationRequest

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from backend.app import schemas, models, database
from backend.app.algorithms.optimize_products import generate_optimal_basket
from backend.app.algorithms.impact_calculator import ImpactCalculator

router = APIRouter(prefix="/shopping-lists", tags=["Shopping Lists"])


@router.post("/", response_model=schemas.ShoppingListRead)
def create_shopping_list(list_data: schemas.ShoppingListCreate, db: Session = Depends(database.get_db)):
    db_list = models.ShoppingList(**list_data.dict())
    db.add(db_list)
    db.commit()
    db.refresh(db_list)
    return db_list


@router.get("/", response_model=list[schemas.ShoppingListRead])
def get_all_lists(db: Session = Depends(database.get_db)):
    return db.query(models.ShoppingList).all()


@router.get("/{list_id}", response_model=schemas.ShoppingListRead)
def get_list(list_id: int, db: Session = Depends(database.get_db)):
    db_list = db.query(models.ShoppingList).get(list_id)
    if not db_list:
        raise HTTPException(status_code=404, detail="List not found")
    return db_list


@router.put("/{list_id}", response_model=schemas.ShoppingListRead)
def update_list(list_id: int, new_data: schemas.ShoppingListCreate, db: Session = Depends(database.get_db)):
    db_list = db.query(models.ShoppingList).get(list_id)
    if not db_list:
        raise HTTPException(status_code=404, detail="List not found")
    for k, v in new_data.dict().items():
        setattr(db_list, k, v)
    db.commit()
    db.refresh(db_list)
    return db_list


@router.delete("/{list_id}")
def delete_list(list_id: int, db: Session = Depends(database.get_db)):
    db_list = db.query(models.ShoppingList).get(list_id)
    if not db_list:
        raise HTTPException(status_code=404, detail="List not found")
    db.delete(db_list)
    db.commit()
    return {"message": "Shopping list deleted"}


@router.post("/optimize/")
def optimize_basket(
    request: OptimizationRequest,
    db: Session = Depends(get_db)
):
    user_location = (request.user_lat, request.user_lon)

    # Obtener todos los proveedores y sus coordenadas
    providers = db.query(User).filter(User.location_lat.isnot(None), User.location_lon.isnot(None)).all()
    provider_coords = {
        provider.id: (float(provider.location_lat), float(provider.location_lon))
        for provider in providers
    }
    
    # Obtener tipos de proveedores
    provider_types = {
        provider.id: provider.provider_type
        for provider in providers
    }

    # Obtener todos los productos
    productos_db = db.query(Product).filter(Product.stock_available > 0).all()

    # Preprocesar productos para incluir lat/lon del proveedor
    productos_disponibles = []
    for p in productos_db:
        coords = provider_coords.get(p.provider_id)
        if not coords:
            continue

        producto_dict = {
            "id": p.id,
            "name": p.name,
            "price": float(p.price),
            "stock": p.stock_available,
            "provider_id": p.provider_id,
            "provider_lat": coords[0],
            "provider_lon": coords[1],
            "is_eco": p.is_eco,
            "is_gluten_free": True,  # Suponiendo que todos lo son por ahora
            "category": p.category if hasattr(p, 'category') else None
        }
        productos_disponibles.append(producto_dict)

    # Algoritmo de selección óptima
    basket, total_cost, additional_info = generate_optimal_basket(
        user_location=user_location,
        requested_products=[{"name": p.name, "quantity": p.quantity} for p in request.requested_products],
        available_products=productos_disponibles,
        filters={
            "eco": request.filters.eco,
            "gluten_free": request.filters.gluten_free,
            "max_distance_km": request.filters.max_distance_km
        },
        criteria={
            "price_weight": request.criteria.price_weight,
            "distance_weight": request.criteria.distance_weight,
            "provider_weight": request.criteria.provider_weight,
            "route_efficiency_weight": 0.1  # Peso por defecto para la eficiencia de ruta
        }
    )

    # Calcular métricas adicionales
    total_price = sum(item["price"] * item["quantity"] for item in basket)
    total_distance = sum(item["distance_km"] for item in basket)
    unique_providers = len(additional_info["provider_groups"])
    
    # Preparar respuesta con información agrupada por proveedor
    provider_summary = {}
    for provider_id, items in additional_info["provider_groups"].items():
        provider = next((p for p in providers if p.id == provider_id), None)
        if provider:
            provider_summary[provider_id] = {
                "provider_name": provider.name,
                "location": {
                    "lat": provider.location_lat,
                    "lon": provider.location_lon
                },
                "items": items,
                "total_items": len(items),
                "total_price": sum(item["price"] * item["quantity"] for item in items)
            }

    # Calcular impacto ambiental y social
    impact_metrics = ImpactCalculator.calculate_basket_impact(basket, provider_types)

    return {
        "optimized_basket": basket,
        "total_price": round(total_price, 2),
        "total_distance_km": round(total_distance, 2),
        "total_cost": total_cost,
        "provider_summary": provider_summary,
        "alternative_products": additional_info["alternatives"],
        "metrics": {
            "unique_providers": unique_providers,
            "average_distance_per_provider": round(total_distance / unique_providers, 2) if unique_providers > 0 else 0,
            "average_price_per_item": round(total_price / len(basket), 2) if basket else 0
        },
        "impact_metrics": impact_metrics
    }
