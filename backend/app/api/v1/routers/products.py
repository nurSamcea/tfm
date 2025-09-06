from fastapi import APIRouter, Depends, HTTPException, Body, UploadFile, File, Form, status, Request
from sqlalchemy.orm import Session
from geopy.distance import geodesic
import os
import logging
import json

from backend.app import schemas, database, models
from backend.app.schemas.product import ProductFilterRequest, ProductOptimizedResponse
from backend.app.models.product import ProductCategory

router = APIRouter(prefix="/products", tags=["Products"])

@router.post("/", response_model=schemas.ProductRead)
def create_product(product: schemas.ProductCreate, db: Session = Depends(database.get_db)):
    db_product = models.Product(**product.dict())
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return db_product

@router.post("/upload", response_model=schemas.ProductRead)
async def create_product_with_image(
    name: str = Form(...),
    provider_id: int = Form(...),
    description: str = Form(None),
    price: float = Form(None),
    currency: str = Form(None),
    unit: str = Form(None),
    category: str = Form(None),
    stock_available: float = Form(None),
    expiration_date: str = Form(None),
    is_eco: bool = Form(False),
    image: UploadFile = File(None),
    db: Session = Depends(database.get_db)
):
    """Crear producto con imagen opcional"""
    try:
        # Procesar imagen si se proporciona
        image_url = ""
        if image:
            # Crear directorio media si no existe
            os.makedirs("media", exist_ok=True)
            
            # Generar nombre único para la imagen
            file_extension = os.path.splitext(image.filename)[1] if image.filename else ".jpg"
            filename = f"product_{provider_id}_{name.replace(' ', '_')}{file_extension}"
            file_path = os.path.join("media", filename)
            
            # Guardar imagen
            with open(file_path, "wb") as buffer:
                content = await image.read()
                buffer.write(content)
            
            image_url = f"/media/{filename}"
            logging.info(f"Imagen guardada: {file_path}")
        
        # Crear objeto de producto
        product_data = {
            "name": name,
            "provider_id": provider_id,
            "description": description,
            "price": price,
            "currency": currency,
            "unit": unit,
            "category": category,
            "stock_available": stock_available,
            "expiration_date": expiration_date,
            "is_eco": is_eco,
            "image_url": image_url,
            # "is_hidden": False  # Descomentar cuando se añada la columna
        }
        
        # Filtrar valores None
        product_data = {k: v for k, v in product_data.items() if v is not None}
        
        # Crear producto en la base de datos
        db_product = models.Product(**product_data)
        db.add(db_product)
        db.commit()
        db.refresh(db_product)
        
        logging.info(f"Producto creado: {db_product.id} - {db_product.name}")
        return db_product
        
    except Exception as e:
        logging.error(f"Error al crear producto: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )

@router.get("/categories")
def get_product_categories():
    """Obtener todas las categorías de productos disponibles."""
    return [{"value": category.value, "label": category.value.replace("_", " ").title()} 
            for category in ProductCategory]

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
def update_product(product_id: int, product_data: schemas.ProductUpdate, db: Session = Depends(database.get_db)):
    try:
        product = db.query(models.Product).get(product_id)
        if not product:
            raise HTTPException(status_code=404, detail="Product not found")
        
        # Actualizar TODOS los campos, incluyendo los que son None
        update_data = product_data.dict(exclude_none=False)
        
        # Manejar la fecha de expiración desde el campo string
        if 'expiration_date_string' in update_data and update_data['expiration_date_string'] is not None:
            try:
                from datetime import datetime
                expiration_date = datetime.strptime(update_data['expiration_date_string'], '%Y-%m-%d').date()
                product.expiration_date = expiration_date
            except Exception as e:
                logging.error(f"Error al parsear fecha: {e}")
        
        # Actualizar todos los campos directamente (excepto expiration_date_string y expiration_date que ya se manejaron)
        for k, v in update_data.items():
            # Solo proteger is_hidden de ser None
            if k == 'is_hidden' and v is None:
                v = False
            # Saltar expiration_date_string y expiration_date ya que se manejaron arriba
            if k in ['expiration_date_string', 'expiration_date']:
                continue
            setattr(product, k, v)
        
        db.commit()
        db.refresh(product)
        return product
        
    except Exception as e:
        logging.error(f"Error al actualizar producto {product_id}: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Error interno: {str(e)}")

@router.delete("/{product_id}")
def delete_product(product_id: int, db: Session = Depends(database.get_db)):
    product = db.query(models.Product).get(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    db.delete(product)
    db.commit()
    return {"message": "Product deleted"}


@router.patch("/{product_id}/toggle-hidden")
def toggle_product_hidden(product_id: int, db: Session = Depends(database.get_db)):
    product = db.query(models.Product).get(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    
    product.is_hidden = not product.is_hidden
    db.commit()
    db.refresh(product)
    return {"message": f"Product {'hidden' if product.is_hidden else 'visible'}", "is_hidden": product.is_hidden}

@router.patch("/{product_id}/image", response_model=schemas.ProductRead)
async def update_product_image(
    product_id: int,
    image: UploadFile = File(...),
    db: Session = Depends(database.get_db)
):
    """Actualizar imagen de un producto existente"""
    try:
        product = db.query(models.Product).get(product_id)
        if not product:
            raise HTTPException(status_code=404, detail="Product not found")
        
        # Procesar nueva imagen
        if image:
            # Crear directorio media si no existe
            os.makedirs("media", exist_ok=True)
            
            # Generar nombre único para la imagen
            file_extension = os.path.splitext(image.filename)[1] if image.filename else ".jpg"
            filename = f"product_{product.provider_id}_{product.name.replace(' ', '_')}_{product_id}{file_extension}"
            file_path = os.path.join("media", filename)
            
            # Guardar nueva imagen
            with open(file_path, "wb") as buffer:
                content = await image.read()
                buffer.write(content)
            
            # Actualizar URL de imagen en la base de datos
            product.image_url = f"/media/{filename}"
            db.commit()
            db.refresh(product)
            
            logging.info(f"Imagen actualizada para producto {product_id}: {file_path}")
            return product
        
    except Exception as e:
        logging.error(f"Error al actualizar imagen del producto {product_id}: {str(e)}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )


@router.get("/farmer/{farmer_id}", response_model=list[schemas.ProductRead])
def get_farmer_products(farmer_id: int, db: Session = Depends(database.get_db)):
    """Obtener todos los productos de un agricultor (incluyendo ocultos)"""
    products = db.query(models.Product).filter(models.Product.provider_id == farmer_id).all()
    return products


@router.post("/optimized/", response_model=list[ProductOptimizedResponse])
def get_products_optimized(
    request: ProductFilterRequest = Body(...),
    db: Session = Depends(database.get_db)
):
    # 1. Obtener productos base (join con User para tener coordenadas del proveedor)
    # Solo productos visibles para consumidores (temporalmente comentado hasta añadir columna is_hidden)
    query = db.query(models.Product).join(models.User, models.Product.provider_id == models.User.id)
    # query = query.filter(models.Product.is_hidden == False)  # Descomentar cuando se añada la columna
    
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
            "price": float(p.price) if p.price is not None else 0.0,
            "category": p.category,
            "is_eco": p.is_eco if p.is_eco is not None else False,
            "is_gluten_free": getattr(p, "is_gluten_free", False),
            "provider_id": p.provider_id,
            "distance_km": None
        }
        # Calcular distancia si se requiere
        if request.user_lat and request.user_lon and hasattr(p, "provider") and p.provider and p.provider.location_lat and p.provider.location_lon:
            try:
                prod["distance_km"] = geodesic(
                    (request.user_lat, request.user_lon),
                    (float(p.provider.location_lat), float(p.provider.location_lon))
                ).km
            except (ValueError, TypeError):
                prod["distance_km"] = None
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
