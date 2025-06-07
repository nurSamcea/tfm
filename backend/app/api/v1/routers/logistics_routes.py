from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from backend.app import schemas, database, models
from backend.app.algorithms.optimize_vrp import LogisticsOptimizer
from backend.app.database import get_db
from fastapi import HTTPException

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



@router.post("/optimize", summary="Generar rutas logísticas óptimas")
def optimize_logistics_routes(db: Session = Depends(get_db)):
    optimizer = LogisticsOptimizer(db)
    route = optimizer.solve_routing()
    if not route:
        return {"message": "No hay pedidos pendientes para optimizar."}
    return {"message": "Ruta generada correctamente", "route_id": route.id}
