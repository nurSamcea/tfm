from fastapi import APIRouter, Depends, HTTPException, Body
from sqlalchemy.orm import Session
from geopy.distance import geodesic

from backend.app import schemas, database, models
from backend.app.schemas.product import ProductFilterRequest, ProductOptimizedResponse

router = APIRouter(prefix="/products", tags=["Products"])

@router.post("/", response_model=schemas.ProductRead)
def create_product(product: schemas.ProductCreate, db: Session = Depends(database.get_db)):
    db_product = models.Product(**product.dict())
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return db_product

@router.get("/", response_model=list[schemas.ProductRead])
def get_all_products(
    db: Session = Depends(database.get_db),
    search: str = None,
    provider_role: str = None
):
    query = db.query(models.Product)
    if search:
        query = query.filter(
            (models.Product.name.ilike(f"%{search}%")) |
            (models.Product.description.ilike(f"%{search}%"))
        )
    if provider_role:
        query = query.join(models.User).filter(models.User.role == provider_role)
    return query.all()

@router.get("/{product_id}", response_model=schemas.ProductRead)
def get_product(product_id: int, db: Session = Depends(database.get_db)):
    product = db.query(models.Product).get(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    return product

@router.put("/{product_id}", response_model=schemas.ProductRead)
def update_product(product_id: int, product_data: schemas.ProductCreate, db: Session = Depends(database.get_db)):
    product = db.query(models.Product).get(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    for k, v in product_data.dict().items():
        setattr(product, k, v)
    db.commit()
    db.refresh(product)
    return product

@router.delete("/{product_id}")
def delete_product(product_id: int, db: Session = Depends(database.get_db)):
    product = db.query(models.Product).get(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    db.delete(product)
    db.commit()
    return {"message": "Product deleted"}

@router.post("/optimized/", response_model=list[ProductOptimizedResponse])
def get_products_optimized(
    request: ProductFilterRequest = Body(...),
    db: Session = Depends(database.get_db)
):
    # 1. Obtener productos base
    query = db.query(models.Product)
    if request.search:
        query = query.filter(
            (models.Product.name.ilike(f"%{request.search}%")) |
            (models.Product.description.ilike(f"%{request.search}%"))
        )
    productos = query.all()

    # 2. Preprocesar productos
    productos_dict = []
    for p in productos:
        prod = {
            "id": p.id,
            "name": p.name,
            "price": float(p.price),
            "category": p.category,
            "is_eco": p.is_eco,
            "is_gluten_free": getattr(p, "is_gluten_free", False),
            "provider_id": p.provider_id,
            "distance_km": None
        }
        # Calcular distancia si se requiere
        if request.user_lat and request.user_lon and hasattr(p, "provider") and p.provider and p.provider.location_lat and p.provider.location_lon:
            prod["distance_km"] = geodesic(
                (request.user_lat, request.user_lon),
                (float(p.provider.location_lat), float(p.provider.location_lon))
            ).km
        productos_dict.append(prod)

    # 3. Filtrar según filtros booleanos
    for filtro, activo in request.filters.items():
        if activo:
            if filtro == "eco":
                productos_dict = [p for p in productos_dict if p["is_eco"]]
            if filtro == "gluten_free":
                productos_dict = [p for p in productos_dict if p["is_gluten_free"]]
            # Puedes añadir más filtros aquí

    # 4. Normalizar valores para score
    def normaliza(lista, clave):
        valores = [p[clave] for p in lista if p[clave] is not None]
        if not valores or min(valores) == max(valores):
            return {p["id"]: 0.0 for p in lista}
        min_v, max_v = min(valores), max(valores)
        return {p["id"]: (p[clave] - min_v) / (max_v - min_v) if p[clave] is not None else 0.0 for p in lista}

    norm_price = normaliza(productos_dict, "price")
    norm_distance = normaliza(productos_dict, "distance_km") if any(p["distance_km"] is not None for p in productos_dict) else {p["id"]: 0.0 for p in productos_dict}

    # 5. Pesos por defecto si no se especifican
    # weights = request.weights or {"price": 0.7, "distance": 0.3}
    # if not weights.get("price"): weights["price"] = 0.7
    # if not weights.get("distance"): weights["distance"] = 0.3
    # 5. Usar solo pesos si el usuario los envía
    weights = request.weights if request.weights else None

    # 6. Calcular score ponderado solo si hay pesos
    if weights and any(v > 0 for v in weights.values()):
        for p in productos_dict:
            score = 0.0
            if "price" in weights:
                score += norm_price[p["id"]] * weights.get("price", 0)
            if "distance" in weights:
                score += norm_distance[p["id"]] * weights.get("distance", 0)
            p["score"] = score
    else:
        for p in productos_dict:
            p["score"] = 0  # Score por defecto si no se usa

    # 7. Ordenar productos según lógica clara
    if weights and any(v > 0 for v in weights.values()):
        productos_dict.sort(key=lambda x: x["score"])
    elif request.filters.get("price", False):
        productos_dict.sort(key=lambda x: x["price"])
    else:
        productos_dict.sort(key=lambda x: x["name"].lower())

    return productos_dict
