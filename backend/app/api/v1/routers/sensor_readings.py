from fastapi import APIRouter, Depends
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
def read_sensor_readings(db: Session = Depends(get_db)):
    return db.query(models.SensorReading).all()
