from typing import List

from backend.app import models, schemas
from backend.app.database import get_db
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

router = APIRouter(prefix="/products", tags=["products"])


# Crear producto
@router.post("/", response_model=schemas.ProductOut)
def create_product(product: schemas.ProductCreate, db: Session = Depends(get_db)):
    db_product = models.Product(**product.dict())
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return db_product


# Obtener todos los productos activos
@router.get("/", response_model=List[schemas.ProductOut])
def get_products(db: Session = Depends(get_db)):
    return db.query(models.Product).filter(models.Product.is_active == True).all()


# Obtener un producto por ID
@router.get("/{product_id}", response_model=schemas.ProductOut)
def get_product(product_id: int, db: Session = Depends(get_db)):
    product = db.query(models.Product).get(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    return product


# Obtener productos por usuario (dueño)
@router.get("/by_owner/{owner_id}", response_model=List[schemas.ProductOut])
def get_products_by_owner(owner_id: int, db: Session = Depends(get_db)):
    return db.query(models.Product).filter(models.Product.owner_id == owner_id).all()


# Actualizar producto
@router.put("/{product_id}", response_model=schemas.ProductOut)
def update_product(product_id: int, updated: schemas.ProductCreate, db: Session = Depends(get_db)):
    product = db.query(models.Product).get(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    for key, value in updated.dict().items():
        setattr(product, key, value)
    db.commit()
    db.refresh(product)
    return product


# Eliminar producto (soft delete)
@router.delete("/{product_id}")
def delete_product(product_id: int, db: Session = Depends(get_db)):
    product = db.query(models.Product).get(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    product.is_active = False
    db.commit()
    return {"message": "Producto eliminado"}
