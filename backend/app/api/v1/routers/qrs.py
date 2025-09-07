from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session

from backend.app import schemas, models
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
def read_qrs(db: Session = Depends(get_db), product_id: int | None = Query(default=None)):
    query = db.query(models.QR)
    if product_id is not None:
        query = query.filter(models.QR.product_id == product_id)
    return query.all()


@router.get("/{qr_id}", response_model=schemas.QrOut)
def get_qr(qr_id: int, db: Session = Depends(get_db)):
    item = db.query(models.QR).get(qr_id)
    if not item:
        from fastapi import HTTPException
        raise HTTPException(status_code=404, detail="QR not found")
    return item
