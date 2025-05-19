from typing import List

from backend.app import models, schemas
from backend.app.database import get_db
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

router = APIRouter(prefix="/qrs", tags=["qrs"])


# Crear QR para un producto (con hash y trazabilidad)
@router.post("/", response_model=schemas.ProductQROut)
def create_qr(qr: schemas.ProductQRCreate, db: Session = Depends(get_db)):
    db_qr = models.ProductQR(**qr.dict())
    db.add(db_qr)
    db.commit()
    db.refresh(db_qr)
    return db_qr


# Obtener trazabilidad por código QR
@router.get("/{qr_code}", response_model=schemas.ProductQROut)
def get_qr(qr_code: str, db: Session = Depends(get_db)):
    qr = db.query(models.ProductQR).filter(models.ProductQR.qr_code == qr_code).first()
    if not qr:
        raise HTTPException(status_code=404, detail="QR no encontrado")
    return qr


# Obtener QR por producto
@router.get("/product/{product_id}", response_model=List[schemas.ProductQROut])
def get_qrs_by_product(product_id: int, db: Session = Depends(get_db)):
    return db.query(models.ProductQR).filter(models.ProductQR.product_id == product_id).all()
