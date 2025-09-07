from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List

from backend.app import schemas, database, models
from backend.app.database import get_db
from backend.app.api.v1.routers.dependencies import get_current_user

router = APIRouter(prefix="/sensor-zones", tags=["Sensor Zones"])

@router.post("/", response_model=schemas.SensorZoneRead)
def create_sensor_zone(
    zone: schemas.SensorZoneCreate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Crear una nueva zona de sensores."""
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden crear zonas")
    
    # Verificar que el farmer_id coincida con el usuario actual
    if zone.farmer_id != current_user.id:
        raise HTTPException(status_code=403, detail="No puedes crear zonas para otros farmers")
    
    db_zone = models.SensorZone(**zone.dict())
    db.add(db_zone)
    db.commit()
    db.refresh(db_zone)
    return db_zone

@router.get("/", response_model=List[schemas.SensorZoneRead])
def get_sensor_zones(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Obtener todas las zonas del farmer actual."""
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden ver zonas")
    
    zones = db.query(models.SensorZone).filter(
        models.SensorZone.farmer_id == current_user.id
    ).all()
    
    return zones

@router.get("/{zone_id}", response_model=schemas.SensorZoneRead)
def get_sensor_zone(
    zone_id: int,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Obtener una zona específica."""
    zone = db.query(models.SensorZone).filter(
        models.SensorZone.id == zone_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not zone:
        raise HTTPException(status_code=404, detail="Zona no encontrada")
    
    return zone

@router.put("/{zone_id}", response_model=schemas.SensorZoneRead)
def update_sensor_zone(
    zone_id: int,
    zone_update: schemas.SensorZoneUpdate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Actualizar una zona."""
    zone = db.query(models.SensorZone).filter(
        models.SensorZone.id == zone_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not zone:
        raise HTTPException(status_code=404, detail="Zona no encontrada")
    
    # Actualizar campos
    update_data = zone_update.dict(exclude_unset=True)
    for field, value in update_data.items():
        setattr(zone, field, value)
    
    db.commit()
    db.refresh(zone)
    return zone

@router.delete("/{zone_id}")
def delete_sensor_zone(
    zone_id: int,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Eliminar una zona."""
    zone = db.query(models.SensorZone).filter(
        models.SensorZone.id == zone_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not zone:
        raise HTTPException(status_code=404, detail="Zona no encontrada")
    
    # Verificar que no tenga sensores asociados
    sensors_count = db.query(models.Sensor).filter(models.Sensor.zone_id == zone_id).count()
    if sensors_count > 0:
        raise HTTPException(
            status_code=400, 
            detail=f"No se puede eliminar la zona. Tiene {sensors_count} sensores asociados"
        )
    
    db.delete(zone)
    db.commit()
    return {"message": "Zona eliminada correctamente"}
