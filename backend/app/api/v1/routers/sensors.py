from datetime import datetime, timedelta
import os
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from typing import List, Optional

from backend.app import schemas, models
from backend.app.database import get_db
from backend.app.api.v1.routers.dependencies import get_current_user

router = APIRouter(prefix="/sensors", tags=["Sensors"])

@router.post("/", response_model=schemas.SensorRead)
def create_sensor(
    sensor: schemas.SensorCreate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Crear un nuevo sensor."""
    # Verificar que el usuario sea farmer
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden crear sensores")
    
    # Verificar que el device_id sea único
    existing_sensor = db.query(models.Sensor).filter(models.Sensor.device_id == sensor.device_id).first()
    if existing_sensor:
        raise HTTPException(status_code=400, detail="El device_id ya existe")
    
    # Verificar que la zona pertenezca al farmer
    if sensor.zone_id:
        zone = db.query(models.SensorZone).filter(
            models.SensorZone.id == sensor.zone_id,
            models.SensorZone.farmer_id == current_user.id
        ).first()
        if not zone:
            raise HTTPException(status_code=404, detail="Zona no encontrada o no pertenece al farmer")
    
    db_sensor = models.Sensor(**sensor.dict())
    db.add(db_sensor)
    db.commit()
    db.refresh(db_sensor)
    return db_sensor

@router.get("/", response_model=List[schemas.SensorRead])
def get_sensors(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
    zone_id: Optional[int] = Query(None),
    status: Optional[str] = Query(None)
):
    """Obtener sensores del farmer actual."""
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden ver sensores")
    
    query = db.query(models.Sensor).join(models.SensorZone).filter(
        models.SensorZone.farmer_id == current_user.id
    )
    
    if zone_id:
        query = query.filter(models.Sensor.zone_id == zone_id)
    
    if status:
        query = query.filter(models.Sensor.status == status)
    
    return query.all()

# Endpoint público de solo lectura para modo demo
@router.get("/public", response_model=List[schemas.SensorRead])
def get_sensors_public(
    db: Session = Depends(get_db),
):
    """Obtener sensores en modo demo sin autenticación.

    Requiere DEMO_MODE=true en variables de entorno. Devuelve todos los sensores.
    """
    if os.getenv("DEMO_MODE", "false").lower() != "true":
        raise HTTPException(status_code=403, detail="Endpoint deshabilitado")
    return db.query(models.Sensor).all()

@router.get("/{sensor_id}", response_model=schemas.SensorRead)
def get_sensor(
    sensor_id: int,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Obtener un sensor específico."""
    sensor = db.query(models.Sensor).join(models.SensorZone).filter(
        models.Sensor.id == sensor_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not sensor:
        raise HTTPException(status_code=404, detail="Sensor no encontrado")
    
    return sensor

@router.put("/{sensor_id}", response_model=schemas.SensorRead)
def update_sensor(
    sensor_id: int,
    sensor_update: schemas.SensorUpdate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Actualizar un sensor."""
    sensor = db.query(models.Sensor).join(models.SensorZone).filter(
        models.Sensor.id == sensor_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not sensor:
        raise HTTPException(status_code=404, detail="Sensor no encontrado")
    
    # Actualizar campos
    update_data = sensor_update.dict(exclude_unset=True)
    for field, value in update_data.items():
        setattr(sensor, field, value)
    
    sensor.updated_at = datetime.utcnow()
    db.commit()
    db.refresh(sensor)
    return sensor

@router.delete("/{sensor_id}")
def delete_sensor(
    sensor_id: int,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Eliminar un sensor."""
    sensor = db.query(models.Sensor).join(models.SensorZone).filter(
        models.Sensor.id == sensor_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not sensor:
        raise HTTPException(status_code=404, detail="Sensor no encontrado")
    
    db.delete(sensor)
    db.commit()
    return {"message": "Sensor eliminado correctamente"}

@router.get("/{sensor_id}/readings", response_model=List[schemas.SensorReadingOut])
def get_sensor_readings(
    sensor_id: int,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
    limit: int = Query(100, le=1000),
    hours: int = Query(24, le=168)  # Máximo 1 semana
):
    """Obtener lecturas de un sensor específico."""
    # Verificar que el sensor pertenezca al farmer
    sensor = db.query(models.Sensor).join(models.SensorZone).filter(
        models.Sensor.id == sensor_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not sensor:
        raise HTTPException(status_code=404, detail="Sensor no encontrado")
    
    # Obtener lecturas de las últimas N horas
    since = datetime.utcnow() - timedelta(hours=hours)
    readings = db.query(models.SensorReading).filter(
        models.SensorReading.sensor_id == sensor_id,
        models.SensorReading.created_at >= since
    ).order_by(models.SensorReading.created_at.desc()).limit(limit).all()
    
    return readings

@router.get("/{sensor_id}/status")
def get_sensor_status(
    sensor_id: int,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Obtener estado de un sensor (online/offline)."""
    sensor = db.query(models.Sensor).join(models.SensorZone).filter(
        models.Sensor.id == sensor_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not sensor:
        raise HTTPException(status_code=404, detail="Sensor no encontrado")
    
    # Considerar offline si no ha enviado datos en los últimos 5 minutos
    offline_threshold = datetime.utcnow() - timedelta(minutes=5)
    is_online = sensor.last_seen and sensor.last_seen > offline_threshold
    
    return {
        "sensor_id": sensor_id,
        "device_id": sensor.device_id,
        "is_online": is_online,
        "last_seen": sensor.last_seen,
        "status": sensor.status
    }
