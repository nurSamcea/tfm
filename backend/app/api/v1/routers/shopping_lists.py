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
from backend.app.models import Product

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
            "is_gluten_free": True  # Suponiendo que todos lo son por ahora, puedes mejorarlo
        }
        productos_disponibles.append(producto_dict)

    # Algoritmo de selección óptima
    cesta = []
    for pedido in request.requested_products:
        candidatos = [
            p for p in productos_disponibles
            if p["name"].lower() == pedido.name.lower()
            and p["stock"] >= pedido.quantity
            and (not request.filters.eco or p["is_eco"])
            and (not request.filters.gluten_free or p["is_gluten_free"])
            and geodesic(user_location, (p["provider_lat"], p["provider_lon"])).km <= request.filters.max_distance_km
        ]

        if not candidatos:
            continue

        mejor = min(
            candidatos,
            key=lambda p: (
                request.criteria.price_weight * p["price"] +
                request.criteria.distance_weight * geodesic(user_location, (p["provider_lat"], p["provider_lon"])).km
            )
        )

        distancia = geodesic(user_location, (mejor["provider_lat"], mejor["provider_lon"])).km
        cesta.append({
            "product_id": mejor["id"],
            "name": mejor["name"],
            "provider_id": mejor["provider_id"],
            "price": mejor["price"],
            "quantity": pedido.quantity,
            "distance_km": round(distancia, 2)
        })

    total_price = sum(item["price"] * item["quantity"] for item in cesta)
    total_distance = sum(item["distance_km"] for item in cesta)
    unique_providers = len(set(item["provider_id"] for item in cesta))
    total_cost = round(
        request.criteria.price_weight * total_price +
        request.criteria.distance_weight * total_distance +
        request.criteria.provider_weight * unique_providers, 2
    )

    return {
        "optimized_basket": cesta,
        "total_price": round(total_price, 2),
        "total_distance_km": round(total_distance, 2),
        "total_cost": total_cost
    }
