from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from backend.app import schemas, database, models
from backend.app.database import get_db

router = APIRouter(prefix="/qrs", tags=["Qrs"])

@router.post("/", response_model=schemas.QrOut)
def create_qr(item: schemas.QRCreate, db: Session = Depends(get_db)):
    db_item = models.QR(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item

@router.get("/", response_model=list[schemas.QrOut])
def read_qrs(db: Session = Depends(get_db)):
    return db.query(models.QR).all()
