from fastapi import APIRouter, Depends, HTTPException, Body, UploadFile, File, Form, status, Request
from sqlalchemy.orm import Session
from sqlalchemy import text
from geopy.distance import geodesic
import os
import logging
import json

from backend.app import schemas, database, models
from backend.app.schemas.product import ProductFilterRequest, ProductOptimizedResponse
from backend.app.api.v1.routers.dependencies import get_current_user, get_current_user_optional
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
    price: str = Form(None),
    currency: str = Form(None),
    unit: str = Form(None),
    category: str = Form(None),
    stock_available: str = Form(None),
    expiration_date: str = Form(None),
    is_eco: str = Form("false"),
    image: UploadFile = File(None),
    db: Session = Depends(database.get_db)
):
    """Crear producto con imagen opcional"""
    try:
        print(f"=== DEBUG: Datos recibidos ===")
        print(f"name: '{name}' (tipo: {type(name)})")
        print(f"provider_id: {provider_id} (tipo: {type(provider_id)})")
        print(f"description: '{description}' (tipo: {type(description)})")
        print(f"price: '{price}' (tipo: {type(price)})")
        print(f"currency: '{currency}' (tipo: {type(currency)})")
        print(f"unit: '{unit}' (tipo: {type(unit)})")
        print(f"category: '{category}' (tipo: {type(category)})")
        print(f"stock_available: '{stock_available}' (tipo: {type(stock_available)})")
        print(f"expiration_date: '{expiration_date}' (tipo: {type(expiration_date)})")
        print(f"is_eco: '{is_eco}' (tipo: {type(is_eco)})")
        print(f"image: {image} (tipo: {type(image)})")
        print(f"===============================")
        
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
        
        # Procesar y validar datos
        print("=== DEBUG: Iniciando procesamiento de datos ===")
        processed_data = {
            "name": name.strip() if name else None,
            "provider_id": provider_id,
            "description": description.strip() if description and description.strip() else None,
            "image_url": image_url if image_url else None,
        }
        print(f"processed_data inicial: {processed_data}")
        
        # Procesar precio
        if price and price.strip():
            try:
                processed_data["price"] = float(price.strip())
            except ValueError:
                processed_data["price"] = None
        else:
            processed_data["price"] = None
            
        # Procesar moneda
        if currency and currency.strip():
            processed_data["currency"] = currency.strip()
        else:
            processed_data["currency"] = None
            
        # Procesar unidad
        if unit and unit.strip():
            processed_data["unit"] = unit.strip()
        else:
            processed_data["unit"] = None
            
        # Procesar categoría
        print(f"=== DEBUG: Procesando categoría '{category}' ===")
        if category and category.strip():
            try:
                # Convertir string a enum
                category_enum = ProductCategory(category.strip())
                processed_data["category"] = category_enum
                print(f"Categoría convertida exitosamente: {category_enum}")
            except ValueError as e:
                print(f"Error convirtiendo categoría: {e}")
                # Si la categoría no es válida, usar 'otros'
                processed_data["category"] = ProductCategory.otros
                print(f"Usando categoría por defecto: {ProductCategory.otros}")
        else:
            processed_data["category"] = None
            print("Categoría es None")
            
        # Procesar stock disponible
        if stock_available and stock_available.strip():
            try:
                processed_data["stock_available"] = float(stock_available.strip())
            except ValueError:
                processed_data["stock_available"] = None
        else:
            processed_data["stock_available"] = None
            
        # Procesar fecha de expiración
        if expiration_date and expiration_date.strip():
            try:
                from datetime import datetime
                processed_data["expiration_date"] = datetime.strptime(expiration_date.strip(), '%Y-%m-%d').date()
            except ValueError:
                processed_data["expiration_date"] = None
        else:
            processed_data["expiration_date"] = None
            
        # Procesar is_eco
        if is_eco and is_eco.strip().lower() in ['true', '1', 'yes']:
            processed_data["is_eco"] = True
        else:
            processed_data["is_eco"] = False
        
        # Filtrar valores None (excepto los campos requeridos)
        product_data = {k: v for k, v in processed_data.items() if v is not None or k in ["name", "provider_id"]}
        print(f"=== DEBUG: Datos finales para crear producto ===")
        print(f"product_data: {product_data}")
        print(f"===============================")
        
        # Crear producto en la base de datos
        print("=== DEBUG: Creando producto en la base de datos ===")
        db_product = models.Product(**product_data)
        print(f"Producto creado: {db_product}")
        db.add(db_product)
        print("Producto añadido a la sesión")
        
        try:
            db.commit()
            print("Commit realizado")
            db.refresh(db_product)
            print(f"Producto refrescado: {db_product}")
            return db_product
        except Exception as commit_error:
            print(f"Error en commit: {commit_error}")
            db.rollback()
            
            # Si es un error de secuencia, corregir y reintentar
            if "duplicate key value violates unique constraint" in str(commit_error):
                print("=== Corrigiendo secuencia de products ===")
                try:
                    # Obtener el máximo ID actual
                    max_id_result = db.execute(text("SELECT COALESCE(MAX(id), 0) FROM products"))
                    max_id = max_id_result.scalar()
                    new_value = max_id + 1
                    
                    # Corregir la secuencia
                    db.execute(text("SELECT setval('products_id_seq', :new_value, false)"), {"new_value": new_value})
                    db.commit()
                    print(f"Secuencia corregida a {new_value}")
                    
                    # Intentar crear el producto nuevamente
                    db_product = models.Product(**product_data)
                    db.add(db_product)
                    db.commit()
                    db.refresh(db_product)
                    print(f"Producto creado exitosamente: {db_product}")
                    return db_product
                    
                except Exception as fix_error:
                    print(f"Error corrigiendo secuencia: {fix_error}")
                    db.rollback()
                    raise fix_error
            
            raise commit_error
        
    except Exception as e:
        print(f"=== DEBUG: ERROR CAPTURADO ===")
        print(f"Tipo de error: {type(e)}")
        print(f"Mensaje de error: {str(e)}")
        print(f"Traceback completo:")
        import traceback
        traceback.print_exc()
        print(f"===============================")
        logging.error(f"Error creando producto: {str(e)}")
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
            
            return product
        
    except Exception as e:
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
    db: Session = Depends(database.get_db),
    current_user: models.User | None = Depends(get_current_user_optional)
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
    elif current_user and current_user.location_lat and current_user.location_lon:
        try:
            user_location = (float(current_user.location_lat), float(current_user.location_lon))
        except Exception:
            user_location = None
    
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
            # Exponer también 'stock' para compatibilidad con clientes/algoritmos
            "stock": float(p.stock_available) if p.stock_available is not None else 0.0,
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


@router.get("/nearby/", response_model=list[dict])
def get_products_by_distance(
    user_lat: float,
    user_lon: float,
    max_distance_km: float = 50.0,
    limit: int = 100,
    db: Session = Depends(database.get_db)
):
    """
    Obtener productos de proveedores cercanos a la ubicación del usuario.
    
    Args:
        user_lat: Latitud del usuario
        user_lon: Longitud del usuario
        max_distance_km: Distancia máxima en kilómetros (por defecto 50km)
        limit: Número máximo de productos a retornar (por defecto 100)
    
    Returns:
        Lista de productos con información de distancia y proveedor
    """
    try:
        # Obtener todos los productos con sus proveedores
        products_query = db.query(models.Product, models.User).join(
            models.User, models.Product.provider_id == models.User.id
        ).filter(
            models.Product.stock_available > 0,
            models.User.location_lat.isnot(None),
            models.User.location_lon.isnot(None)
        )
        
        products_with_providers = products_query.all()
        
        # Calcular distancias y filtrar
        nearby_products = []
        user_location = (user_lat, user_lon)
        
        for product, provider in products_with_providers:
            try:
                provider_location = (float(provider.location_lat), float(provider.location_lon))
                distance_km = geodesic(user_location, provider_location).km
                
                if distance_km <= max_distance_km:
                    product_data = {
                        "id": product.id,
                        "name": product.name,
                        "description": product.description,
                        "price": float(product.price),
                        "stock_available": float(product.stock_available),
                        "category": product.category,
                        "is_eco": product.is_eco,
                        "image_url": product.image_url,
                        "provider_id": provider.id,
                        "provider_name": provider.name,
                        "provider_entity_name": provider.entity_name,
                        "provider_lat": float(provider.location_lat),
                        "provider_lon": float(provider.location_lon),
                        "distance_km": round(distance_km, 2),
                        "provider_role": provider.role.value
                    }
                    nearby_products.append(product_data)
                    
            except (ValueError, TypeError) as e:
                logging.warning(f"Error calculando distancia para producto {product.id}: {e}")
                continue
        
        # Ordenar por distancia
        nearby_products.sort(key=lambda x: x["distance_km"])
        
        # Limitar resultados
        return nearby_products[:limit]
        
    except Exception as e:
        logging.error(f"Error obteniendo productos cercanos: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Error interno del servidor al obtener productos cercanos"
        )


@router.get("/providers/nearby/", response_model=list[dict])
def get_providers_by_distance(
    user_lat: float,
    user_lon: float,
    max_distance_km: float = 50.0,
    provider_role: str = None,
    limit: int = 50,
    db: Session = Depends(database.get_db)
):
    """
    Obtener proveedores (granjeros/supermercados) cercanos a la ubicación del usuario.
    
    Args:
        user_lat: Latitud del usuario
        user_lon: Longitud del usuario
        max_distance_km: Distancia máxima en kilómetros (por defecto 50km)
        provider_role: Tipo de proveedor ('farmer' o 'supermarket')
        limit: Número máximo de proveedores a retornar (por defecto 50)
    
    Returns:
        Lista de proveedores con información de distancia
    """
    try:
        # Construir query base
        query = db.query(models.User).filter(
            models.User.location_lat.isnot(None),
            models.User.location_lon.isnot(None)
        )
        
        # Filtrar por rol si se especifica
        if provider_role:
            query = query.filter(models.User.role == provider_role)
        
        providers = query.all()
        
        # Calcular distancias y filtrar
        nearby_providers = []
        user_location = (user_lat, user_lon)
        
        for provider in providers:
            try:
                provider_location = (float(provider.location_lat), float(provider.location_lon))
                distance_km = geodesic(user_location, provider_location).km
                
                if distance_km <= max_distance_km:
                    # Contar productos disponibles del proveedor
                    products_count = db.query(models.Product).filter(
                        models.Product.provider_id == provider.id,
                        models.Product.stock_available > 0
                    ).count()
                    
                    provider_data = {
                        "id": provider.id,
                        "name": provider.name,
                        "entity_name": provider.entity_name,
                        "role": provider.role.value,
                        "lat": float(provider.location_lat),
                        "lon": float(provider.location_lon),
                        "distance_km": round(distance_km, 2),
                        "products_available": products_count,
                        "email": provider.email
                    }
                    nearby_providers.append(provider_data)
                    
            except (ValueError, TypeError) as e:
                logging.warning(f"Error calculando distancia para proveedor {provider.id}: {e}")
                continue
        
        # Ordenar por distancia
        nearby_providers.sort(key=lambda x: x["distance_km"])
        
        # Limitar resultados
        return nearby_providers[:limit]
        
    except Exception as e:
        logging.error(f"Error obteniendo proveedores cercanos: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Error interno del servidor al obtener proveedores cercanos"
        )
