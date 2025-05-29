from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from backend.app import schemas, database, models
from backend.app.database import get_db

router = APIRouter(prefix="/logistics_routes", tags=["Logistics Routes"])

@router.post("/", response_model=schemas.LogisticsRouteOut)
def create_logistics_route(item: schemas.LogisticsRouteCreate, db: Session = Depends(get_db)):
    db_item = models.LogisticsRoute(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item

@router.get("/", response_model=list[schemas.LogisticsRouteOut])
def read_logistics_routes(db: Session = Depends(get_db)):
    return db.query(models.LogisticsRoute).all()
