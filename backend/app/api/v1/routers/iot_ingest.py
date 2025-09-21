"""
Endpoint específico para ingesta de datos IoT con autenticación por token.
Permite que sensores reales (ESP32) y simuladores envíen datos de forma segura.
"""

from datetime import datetime
from fastapi import APIRouter, Depends, HTTPException, Header
from sqlalchemy.orm import Session
from typing import Optional
import os
import logging

from backend.app import schemas, models
from backend.app.database import get_db

logger = logging.getLogger(__name__)
router = APIRouter(prefix="/iot", tags=["IoT Ingest"])

# Token de autenticación para sensores IoT
IOT_INGEST_TOKEN = os.getenv("IOT_INGEST_TOKEN", "dev_iot_token_2024")

class IoTTelemetryData:
    """DTO para datos de telemetría IoT"""
    def __init__(self, device_id: str, temperature: Optional[float] = None, 
                 humidity: Optional[float] = None, timestamp: Optional[int] = None,
                 extra_data: Optional[dict] = None):
        self.device_id = device_id
        self.temperature = temperature
        self.humidity = humidity
        self.timestamp = timestamp
        self.extra_data = extra_data or {}

@router.post("/ingest")
async def ingest_iot_data(
    data: dict,
    x_ingest_token: Optional[str] = Header(None, alias="X-Ingest-Token"),
    db: Session = Depends(get_db)
):
    """
    Endpoint para ingesta de datos IoT desde sensores reales y simuladores.
    
    Autenticación: Header X-Ingest-Token
    Formato esperado:
    {
        "device_id": "esp32-001",
        "temperature": 24.3,
        "humidity": 61.5,
        "timestamp": 1726200000,  # opcional, Unix timestamp
        "extra_data": {...}       # opcional
    }
    """
    
    # Verificar token de autenticación
    if not x_ingest_token or x_ingest_token != IOT_INGEST_TOKEN:
        logger.warning(f"Intento de ingesta con token inválido: {x_ingest_token}")
        raise HTTPException(status_code=401, detail="Token de autenticación inválido")
    
    # Validar datos requeridos
    device_id = data.get("device_id")
    if not device_id:
        raise HTTPException(status_code=400, detail="device_id es requerido")
    
    temperature = data.get("temperature")
    humidity = data.get("humidity")
    
    if temperature is None and humidity is None:
        raise HTTPException(status_code=400, detail="Al menos temperature o humidity debe estar presente")
    
    # Buscar sensor por device_id
    sensor = db.query(models.Sensor).filter(models.Sensor.device_id == device_id).first()
    if not sensor:
        logger.warning(f"Sensor no encontrado para device_id: {device_id}")
        raise HTTPException(status_code=404, detail=f"Sensor con device_id '{device_id}' no encontrado")
    
    # Actualizar last_seen del sensor
    sensor.last_seen = datetime.utcnow()
    
    # Crear timestamp
    if data.get("timestamp"):
        try:
            created_at = datetime.fromtimestamp(data["timestamp"])
        except (ValueError, TypeError):
            created_at = datetime.utcnow()
    else:
        created_at = datetime.utcnow()
    
    # Crear lectura de sensor
    reading_data = {
        "sensor_id": sensor.id,
        "temperature": temperature,
        "humidity": humidity,
        "created_at": created_at,
        "source_device": device_id,
        "reading_quality": 1.0,  # Asumimos calidad alta para sensores reales
        "is_processed": True,    # Marcamos como procesado
        "extra_data": data.get("extra_data", {})
    }
    
    # Agregar otros campos si están presentes
    for field in ["gas_level", "light_level", "soil_moisture", "ph_level"]:
        if field in data:
            reading_data[field] = data[field]
    
    if "shock_detected" in data:
        reading_data["shock_detected"] = bool(data["shock_detected"])
    
    db_reading = models.SensorReading(**reading_data)
    db.add(db_reading)
    db.commit()
    db.refresh(db_reading)
    
    # Verificar umbrales y crear alertas si es necesario
    _check_sensor_thresholds(sensor, db_reading, db)
    
    logger.info(f"Datos IoT ingeridos exitosamente para device_id: {device_id}")
    
    return {
        "status": "success",
        "message": "Datos ingeridos exitosamente",
        "reading_id": db_reading.id,
        "device_id": device_id,
        "timestamp": created_at.isoformat()
    }

@router.get("/devices/{device_id}/telemetry")
async def get_device_telemetry(
    device_id: str,
    limit: int = 100,
    db: Session = Depends(get_db)
):
    """
    Obtener telemetría reciente de un dispositivo específico.
    Endpoint público para consulta de datos (sin autenticación).
    """
    
    # Buscar sensor
    sensor = db.query(models.Sensor).filter(models.Sensor.device_id == device_id).first()
    if not sensor:
        raise HTTPException(status_code=404, detail=f"Dispositivo '{device_id}' no encontrado")
    
    # Obtener lecturas recientes
    readings = db.query(models.SensorReading).filter(
        models.SensorReading.sensor_id == sensor.id
    ).order_by(models.SensorReading.created_at.desc()).limit(limit).all()
    
    return {
        "device_id": device_id,
        "sensor_name": sensor.name,
        "sensor_type": sensor.sensor_type.value,
        "status": sensor.status.value,
        "last_seen": sensor.last_seen.isoformat() if sensor.last_seen else None,
        "readings": [
            {
                "id": r.id,
                "temperature": r.temperature,
                "humidity": r.humidity,
                "gas_level": r.gas_level,
                "light_level": r.light_level,
                "soil_moisture": r.soil_moisture,
                "ph_level": r.ph_level,
                "shock_detected": r.shock_detected,
                "created_at": r.created_at.isoformat(),
                "reading_quality": r.reading_quality,
                "extra_data": r.extra_data
            }
            for r in readings
        ]
    }

def _check_sensor_thresholds(sensor: models.Sensor, reading: models.SensorReading, db: Session):
    """Verificar umbrales del sensor y crear alertas si es necesario"""
    
    if not sensor.alert_enabled:
        return
    
    alerts_to_create = []
    
    # Verificar temperatura
    if reading.temperature is not None and sensor.min_threshold is not None and sensor.max_threshold is not None:
        if reading.temperature < sensor.min_threshold or reading.temperature > sensor.max_threshold:
            alerts_to_create.append({
                "sensor_id": sensor.id,
                "alert_type": models.AlertTypeEnum.threshold_exceeded,
                "title": f"Temperatura fuera de rango en {sensor.name}",
                "severity": "high" if abs(reading.temperature - (sensor.min_threshold + sensor.max_threshold) / 2) > 5 else "medium",
                "message": f"Temperatura fuera de rango: {reading.temperature}°C (rango: {sensor.min_threshold}-{sensor.max_threshold}°C)",
                "actual_value": reading.temperature,
                "threshold_value": sensor.min_threshold,
                "unit": "°C"
            })
    
    # Crear alertas
    for alert_data in alerts_to_create:
        alert = models.SensorAlert(**alert_data)
        db.add(alert)
    
    if alerts_to_create:
        db.commit()
        logger.warning(f"Alertas creadas para sensor {sensor.device_id}: {len(alerts_to_create)} alertas")
