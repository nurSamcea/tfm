from fastapi import APIRouter, Depends, HTTPException, Body, UploadFile, File, Form, status, Request
from sqlalchemy.orm import Session
from geopy.distance import geodesic
import os
import logging
import json

from backend.app import schemas, database, models
from backend.app.schemas.product import ProductFilterRequest, ProductOptimizedResponse
from backend.app.models.product import ProductCategory
from backend.app.algorithms.optimize_products import sort_products_by_priority, calculate_product_score

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
    provider_role: str = None,
    available_only: bool = True
):
    query = db.query(models.Product)
    
    # Filtrar solo productos disponibles (con stock > 0)
    if available_only:
        query = query.filter(models.Product.stock_available > 0)
    
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
    # Solo productos visibles para consumidores y con stock disponible
    query = db.query(models.Product).join(models.User, models.Product.provider_id == models.User.id)
    query = query.filter(models.Product.stock_available > 0)  # Solo productos con stock
    # query = query.filter(models.Product.is_hidden == False)  # Descomentar cuando se añada la columna
    
    if request.search:
        query = query.filter(
            (models.Product.name.ilike(f"%{request.search}%")) |
            (models.Product.description.ilike(f"%{request.search}%"))
        )
    productos = query.all()

    # 2. Preprocesar productos
    productos_dict = []
    user_location = None
    if request.user_lat and request.user_lon:
        user_location = (request.user_lat, request.user_lon)
    
    for p in productos:
        prod = {
            "id": p.id,
            "name": p.name,
            "price": float(p.price) if p.price is not None else 0.0,
            "category": p.category,
            "is_eco": p.is_eco if p.is_eco is not None else False,
            "is_gluten_free": getattr(p, "is_gluten_free", False),
            "provider_id": p.provider_id,
            "stock_available": float(p.stock_available) if p.stock_available is not None else 0.0,
            "stock": float(p.stock) if p.stock is not None else 0.0,
            "score": getattr(p, "score", 0) or 0,  # Score de sostenibilidad del producto
            "distance_km": None,
            "provider_lat": None,
            "provider_lon": None
        }
        
        # Calcular distancia y obtener coordenadas del proveedor
        if user_location and hasattr(p, "provider") and p.provider:
            if p.provider.location_lat and p.provider.location_lon:
                try:
                    prod["provider_lat"] = float(p.provider.location_lat)
                    prod["provider_lon"] = float(p.provider.location_lon)
                    prod["distance_km"] = geodesic(
                        user_location,
                        (prod["provider_lat"], prod["provider_lon"])
                    ).km
                except (ValueError, TypeError):
                    prod["distance_km"] = None
        productos_dict.append(prod)

    # 3. Filtrar según filtros booleanos
    for filtro, activo in request.filters.items():
        if activo:
            if filtro == "eco":
                productos_dict = [p for p in productos_dict if p["is_eco"]]
            elif filtro == "gluten_free":
                productos_dict = [p for p in productos_dict if p["is_gluten_free"]]
            elif filtro == "price" and request.weights and "price" in request.weights:
                # Si se prioriza precio, filtrar productos muy caros
                max_price = 5.0  # Precio máximo razonable
                productos_dict = [p for p in productos_dict if p["price"] <= max_price]
            elif filtro == "distance" and user_location:
                # Si se prioriza distancia, filtrar productos muy lejanos
                max_distance = 50.0  # Distancia máxima en km
                productos_dict = [p for p in productos_dict if p["distance_km"] is None or p["distance_km"] <= max_distance]

    # 4. Determinar criterio de ordenación
    sort_criteria = request.sort_criteria or "optimal"  # Usar criterio del request o óptimo por defecto
    
    # Si no se especifica criterio pero hay filtros activos, inferir criterio
    if not request.sort_criteria:
        if request.filters.get("price", False):
            sort_criteria = "price"
        elif request.filters.get("distance", False):
            sort_criteria = "distance"
        elif request.filters.get("eco", False):
            sort_criteria = "eco"
        elif request.filters.get("sustainability", False):
            sort_criteria = "sustainability"

    # 5. Usar el nuevo algoritmo de ordenación
    productos_ordenados = sort_products_by_priority(
        productos_dict,
        user_location=user_location,
        weights=request.weights,
        filters=request.filters,
        sort_criteria=sort_criteria
    )

    # 6. Asegurar que todos los productos tengan un score calculado
    for producto in productos_ordenados:
        if "optimization_score" not in producto:
            producto["optimization_score"] = calculate_product_score(
                producto, user_location, request.weights, request.filters
            )

    return productos_ordenados
