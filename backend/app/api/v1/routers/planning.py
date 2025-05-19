from typing import List

from backend.app import models, schemas
from backend.app.database import get_db
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

router = APIRouter(prefix="/planning", tags=["planning"])


# Crear un nuevo plan semanal
@router.post("/", response_model=schemas.WeeklyPlanOut)
def create_plan(plan: schemas.WeeklyPlanCreate, db: Session = Depends(get_db)):
    db_plan = models.WeeklyPlan(**plan.dict())
    db.add(db_plan)
    db.commit()
    db.refresh(db_plan)
    return db_plan


# Obtener el plan de un usuario
@router.get("/{user_id}", response_model=List[schemas.WeeklyPlanItemOut])
def get_user_plan(user_id: int, db: Session = Depends(get_db)):
    plans = db.query(models.WeeklyPlan).filter(models.WeeklyPlan.user_id == user_id).all()
    if not plans:
        return []
    plan_ids = [p.id for p in plans]
    return db.query(models.WeeklyPlanItem).filter(models.WeeklyPlanItem.plan_id.in_(plan_ids)).all()


# Añadir receta a día/meal
@router.post("/items/", response_model=schemas.WeeklyPlanItemOut)
def add_plan_item(item: schemas.WeeklyPlanItemCreate, db: Session = Depends(get_db)):
    db_item = models.WeeklyPlanItem(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item


# Actualizar item (día, receta, comida)
@router.put("/item/{item_id}", response_model=schemas.WeeklyPlanItemOut)
def update_plan_item(item_id: int, updated: schemas.WeeklyPlanItemCreate, db: Session = Depends(get_db)):
    item = db.query(models.WeeklyPlanItem).get(item_id)
    if not item:
        raise HTTPException(status_code=404, detail="Item no encontrado")
    for key, value in updated.dict().items():
        setattr(item, key, value)
    db.commit()
    db.refresh(item)
    return item


# Eliminar item del plan
@router.delete("/item/{item_id}")
def delete_plan_item(item_id: int, db: Session = Depends(get_db)):
    item = db.query(models.WeeklyPlanItem).get(item_id)
    if not item:
        raise HTTPException(status_code=404, detail="Item no encontrado")
    db.delete(item)
    db.commit()
    return {"message": "Item eliminado del plan"}
