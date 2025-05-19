from typing import List

from backend.app import models, schemas
from backend.app.database import get_db
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

router = APIRouter(prefix="/sensors", tags=["sensors"])


# Añadir nueva lectura de sensor
@router.post("/", response_model=schemas.SensorReadingOut)
def create_sensor_reading(reading: schemas.SensorReadingCreate, db: Session = Depends(get_db)):
    db_reading = models.SensorReading(**reading.dict())
    db.add(db_reading)
    db.commit()
    db.refresh(db_reading)
    return db_reading


# Obtener lecturas por producto
@router.get("/by_product/{product_id}", response_model=List[schemas.SensorReadingOut])
def get_readings_by_product(product_id: int, db: Session = Depends(get_db)):
    return db.query(models.SensorReading).filter(models.SensorReading.product_id == product_id).all()


# Listar todas las lecturas (admin/debug)
@router.get("/", response_model=List[schemas.SensorReadingOut])
def list_all_readings(db: Session = Depends(get_db)):
    return db.query(models.SensorReading).all()
