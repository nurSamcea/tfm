from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from backend.app import schemas, database, models
from backend.app.database import get_db

router = APIRouter(prefix="/impact_metrics", tags=["Impact Metrics"])

@router.post("/", response_model=schemas.ImpactMetricOut)
def create_impact_metric(item: schemas.ImpactMetricCreate, db: Session = Depends(get_db)):
    db_item = models.ImpactMetric(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item

@router.get("/", response_model=list[schemas.ImpactMetricOut])
def read_impact_metrics(db: Session = Depends(get_db)):
    return db.query(models.ImpactMetric).all()
