from datetime import datetime
from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session

from backend.app import schemas, database, models
from backend.app.database import get_db

router = APIRouter(prefix="/sensor_readings", tags=["Sensor Readings"])

@router.post("/", response_model=schemas.SensorReadingOut)
def create_sensor_reading(item: schemas.SensorReadingCreate, db: Session = Depends(get_db)):
    db_item = models.SensorReading(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item

@router.get("/", response_model=list[schemas.SensorReadingOut])
def read_sensor_readings(
    db: Session = Depends(get_db),
    product_id: int | None = Query(default=None),
    date_from: datetime | None = Query(default=None),
    date_to: datetime | None = Query(default=None),
):
    query = db.query(models.SensorReading)
    if product_id is not None:
        query = query.filter(models.SensorReading.product_id == product_id)
    if date_from is not None:
        query = query.filter(models.SensorReading.created_at >= date_from)
    if date_to is not None:
        query = query.filter(models.SensorReading.created_at <= date_to)
    query = query.order_by(models.SensorReading.created_at.desc())
    return query.all()
