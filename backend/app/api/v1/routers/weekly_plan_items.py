from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from backend.app import schemas, database, models
from backend.app.database import get_db

router = APIRouter(prefix="/weekly_plan_items", tags=["Weekly Plan Items"])

@router.post("/", response_model=schemas.WeeklyPlanItemOut)
def create_weekly_plan_item(item: schemas.WeeklyPlanItemCreate, db: Session = Depends(get_db)):
    db_item = models.WeeklyPlanItem(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item

@router.get("/", response_model=list[schemas.WeeklyPlanItemOut])
def read_weekly_plan_items(db: Session = Depends(get_db)):
    return db.query(models.WeeklyPlanItem).all()
