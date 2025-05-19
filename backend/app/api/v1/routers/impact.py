from typing import List

from backend.app import models, schemas
from backend.app.database import get_db
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

router = APIRouter(prefix="/impact", tags=["impact"])


# Obtener métricas por usuario
@router.get("/user/{user_id}", response_model=List[schemas.ImpactMetricOut])
def get_impact_by_user(user_id: int, db: Session = Depends(get_db)):
    return db.query(models.ImpactMetric).filter(models.ImpactMetric.user_id == user_id).all()


# Listar todas las métricas (admin/debug)
@router.get("/", response_model=List[schemas.ImpactMetricOut])
def list_all_metrics(db: Session = Depends(get_db)):
    return db.query(models.ImpactMetric).all()


# Actualizar métricas (si se recalcualan o corrigen)
@router.put("/{metric_id}", response_model=schemas.ImpactMetricOut)
def update_metric(metric_id: int, updated: schemas.ImpactMetricBase, db: Session = Depends(get_db)):
    metric = db.query(models.ImpactMetric).get(metric_id)
    if not metric:
        raise HTTPException(status_code=404, detail="Métrica no encontrada")
    for key, value in updated.dict().items():
        setattr(metric, key, value)
    db.commit()
    db.refresh(metric)
    return metric
