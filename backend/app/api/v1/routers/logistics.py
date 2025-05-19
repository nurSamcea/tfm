from typing import List

from backend.app import models, schemas
from backend.app.database import get_db
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

router = APIRouter(prefix="/logistics", tags=["logistics"])


# Crear nueva ruta logística
@router.post("/", response_model=schemas.LogisticsRouteOut)
def create_route(route: schemas.LogisticsRouteBase, db: Session = Depends(get_db)):
    db_route = models.LogisticsRoute(**route.dict())
    db.add(db_route)
    db.commit()
    db.refresh(db_route)
    return db_route


# Obtener ruta por ID
@router.get("/{route_id}", response_model=schemas.LogisticsRouteOut)
def get_route_by_id(route_id: int, db: Session = Depends(get_db)):
    route = db.query(models.LogisticsRoute).get(route_id)
    if not route:
        raise HTTPException(status_code=404, detail="Ruta no encontrada")
    return route


# Listar todas las rutas (admin o conductor logístico)
@router.get("/", response_model=List[schemas.LogisticsRouteOut])
def list_routes(db: Session = Depends(get_db)):
    return db.query(models.LogisticsRoute).all()


# Filtrar rutas por conductor
@router.get("/by_driver/{driver_name}", response_model=List[schemas.LogisticsRouteOut])
def get_routes_by_driver(driver_name: str, db: Session = Depends(get_db)):
    return db.query(models.LogisticsRoute).filter(models.LogisticsRoute.driver_name == driver_name).all()
