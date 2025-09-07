from datetime import datetime, timedelta
from fastapi import APIRouter, Depends, Query, HTTPException
from sqlalchemy.orm import Session
from typing import List, Optional

from backend.app import schemas, models
from backend.app.database import get_db
from backend.app.api.v1.routers.dependencies import get_current_user

router = APIRouter(prefix="/sensor_readings", tags=["Sensor Readings"])

@router.post("/", response_model=schemas.SensorReadingOut)
def create_sensor_reading(
    item: schemas.SensorReadingCreate, 
    db: Session = Depends(get_db)
):
    """Crear una nueva lectura de sensor (endpoint público para sensores IoT)."""
    # Resolver sensor por id o, en su defecto, por device_id en extra_data
    sensor = db.query(models.Sensor).filter(models.Sensor.id == item.sensor_id).first()
    if not sensor and item.extra_data and isinstance(item.extra_data, dict):
        device_id = item.extra_data.get("device_id")
        if device_id:
            sensor = db.query(models.Sensor).filter(models.Sensor.device_id == device_id).first()
            if sensor:
                # Ajustar sensor_id para almacenar la lectura correctamente
                item.sensor_id = sensor.id
    if not sensor:
        raise HTTPException(status_code=404, detail="Sensor no encontrado")
    
    # Actualizar last_seen del sensor
    sensor.last_seen = datetime.utcnow()
    
    # Crear la lectura
    db_item = models.SensorReading(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    
    # Verificar umbrales y crear alertas si es necesario
    _check_sensor_thresholds(sensor, db_item, db)
    
    return db_item

@router.get("/", response_model=List[schemas.SensorReadingOut])
def read_sensor_readings(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
    sensor_id: Optional[int] = Query(default=None),
    product_id: Optional[int] = Query(default=None),
    date_from: Optional[datetime] = Query(default=None),
    date_to: Optional[datetime] = Query(default=None),
    limit: int = Query(100, le=1000)
):
    """Obtener lecturas de sensores (solo para farmers)."""
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden ver lecturas")
    
    query = db.query(models.SensorReading)
    
    # Si se especifica sensor_id, verificar que pertenezca al farmer
    if sensor_id:
        sensor = db.query(models.Sensor).join(models.SensorZone).filter(
            models.Sensor.id == sensor_id,
            models.SensorZone.farmer_id == current_user.id
        ).first()
        if not sensor:
            raise HTTPException(status_code=404, detail="Sensor no encontrado")
        query = query.filter(models.SensorReading.sensor_id == sensor_id)
    
    if product_id is not None:
        query = query.filter(models.SensorReading.product_id == product_id)
    if date_from is not None:
        query = query.filter(models.SensorReading.created_at >= date_from)
    if date_to is not None:
        query = query.filter(models.SensorReading.created_at <= date_to)
    
    query = query.order_by(models.SensorReading.created_at.desc()).limit(limit)
    return query.all()

def _check_sensor_thresholds(sensor: models.Sensor, reading: models.SensorReading, db: Session):
    """Verificar umbrales del sensor y crear alertas si es necesario."""
    if not sensor.alert_enabled:
        return
    
    alerts_to_create = []
    
    # Verificar temperatura
    if reading.temperature is not None:
        if sensor.min_threshold and reading.temperature < sensor.min_threshold:
            alerts_to_create.append({
                "sensor_id": sensor.id,
                "alert_type": models.AlertTypeEnum.temperature_low,
                "title": f"Temperatura baja en {sensor.name}",
                "message": f"Temperatura: {reading.temperature}°C (umbral: {sensor.min_threshold}°C)",
                "severity": "high",
                "threshold_value": sensor.min_threshold,
                "actual_value": reading.temperature,
                "unit": "°C"
            })
        elif sensor.max_threshold and reading.temperature > sensor.max_threshold:
            alerts_to_create.append({
                "sensor_id": sensor.id,
                "alert_type": models.AlertTypeEnum.temperature_high,
                "title": f"Temperatura alta en {sensor.name}",
                "message": f"Temperatura: {reading.temperature}°C (umbral: {sensor.max_threshold}°C)",
                "severity": "high",
                "threshold_value": sensor.max_threshold,
                "actual_value": reading.temperature,
                "unit": "°C"
            })
    
    # Verificar humedad
    if reading.humidity is not None:
        if sensor.min_threshold and reading.humidity < sensor.min_threshold:
            alerts_to_create.append({
                "sensor_id": sensor.id,
                "alert_type": models.AlertTypeEnum.humidity_low,
                "title": f"Humedad baja en {sensor.name}",
                "message": f"Humedad: {reading.humidity}% (umbral: {sensor.min_threshold}%)",
                "severity": "medium",
                "threshold_value": sensor.min_threshold,
                "actual_value": reading.humidity,
                "unit": "%"
            })
        elif sensor.max_threshold and reading.humidity > sensor.max_threshold:
            alerts_to_create.append({
                "sensor_id": sensor.id,
                "alert_type": models.AlertTypeEnum.humidity_high,
                "title": f"Humedad alta en {sensor.name}",
                "message": f"Humedad: {reading.humidity}% (umbral: {sensor.max_threshold}%)",
                "severity": "medium",
                "threshold_value": sensor.max_threshold,
                "actual_value": reading.humidity,
                "unit": "%"
            })
    
    # Crear alertas
    for alert_data in alerts_to_create:
        # Verificar si ya existe una alerta activa del mismo tipo
        existing_alert = db.query(models.SensorAlert).filter(
            models.SensorAlert.sensor_id == sensor.id,
            models.SensorAlert.alert_type == alert_data["alert_type"],
            models.SensorAlert.status == models.AlertStatusEnum.active
        ).first()
        
        if not existing_alert:
            alert = models.SensorAlert(**alert_data)
            db.add(alert)
    
    db.commit()
