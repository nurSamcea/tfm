from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from backend.app import schemas, database, models
from backend.app.database import get_db

router = APIRouter(prefix="/weekly_plans", tags=["Weekly Plans"])

@router.post("/", response_model=schemas.WeeklyPlanOut)
def create_weekly_plan(plan: schemas.WeeklyPlanCreate, db: Session = Depends(get_db)):
    db_plan = models.WeeklyPlan(**plan.dict())
    db.add(db_plan)
    db.commit()
    db.refresh(db_plan)
    return db_plan

@router.get("/", response_model=list[schemas.WeeklyPlanOut])
def read_weekly_plans(db: Session = Depends(get_db)):
    return db.query(models.WeeklyPlan).all()
