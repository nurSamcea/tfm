from datetime import datetime, timedelta
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from sqlalchemy import func, and_, or_
from typing import List, Optional, Dict, Any
import logging

from backend.app import schemas, database, models
from backend.app.database import get_db
from backend.app.api.v1.routers.dependencies import get_current_user
from backend.app.models.user import UserRoleEnum

logger = logging.getLogger(__name__)

router = APIRouter(prefix="/farmer-metrics", tags=["Farmer Metrics"])

@router.get("/dashboard/{farmer_id}")
def get_farmer_dashboard(
    farmer_id: int,
    db: Session = Depends(get_db)
):
    """Obtener dashboard completo del farmer con métricas principales."""
    
    logger.info(f"=== FARMER METRICS DASHBOARD ===")
    logger.info(f"Farmer ID: {farmer_id}")
    
    # Verificar que el farmer existe
    farmer = db.query(models.User).filter(
        models.User.id == farmer_id,
        models.User.role == UserRoleEnum.farmer
    ).first()
    
    if not farmer:
        logger.error(f"Farmer {farmer_id} not found or not a farmer")
        raise HTTPException(status_code=404, detail="Farmer no encontrado")
    
    logger.info(f"Farmer found: {farmer.name} (ID: {farmer.id})")
    
    # Obtener zonas del farmer
    zones = db.query(models.SensorZone).filter(
        models.SensorZone.farmer_id == farmer_id,
        models.SensorZone.is_active == True
    ).all()
    
    logger.info(f"Found {len(zones)} zones for farmer {farmer_id}")
    for zone in zones:
        logger.info(f"Zone: {zone.name} (ID: {zone.id})")
    
    if not zones:
        logger.warning(f"No zones found for farmer {farmer_id}")
        return {
            "farmer_id": farmer_id,
            "farmer_name": farmer.name,
            "total_zones": 0,
            "total_sensors": 0,
            "online_sensors": 0,
            "offline_sensors": 0,
            "zones": [],
            "summary": {
                "avg_temperature": 0,
                "avg_humidity": 0,
                "avg_soil_moisture": 0,
                "active_alerts": 0
            }
        }
    
    zone_ids = [zone.id for zone in zones]
    
    # Obtener sensores del farmer
    sensors = db.query(models.Sensor).filter(
        models.Sensor.zone_id.in_(zone_ids)
    ).all()
    
    sensor_ids = [sensor.id for sensor in sensors]
    
    # Calcular sensores online/offline (última lectura en las últimas 2 horas)
    two_hours_ago = datetime.utcnow() - timedelta(hours=2)
    
    # Contar sensores online de manera más simple
    online_sensors = db.query(models.Sensor.id).join(models.SensorReading).filter(
        models.Sensor.id.in_(sensor_ids),
        models.SensorReading.created_at >= two_hours_ago
    ).distinct().count()
    
    offline_sensors = len(sensors) - online_sensors
    
    # Obtener lecturas recientes (últimas 24 horas)
    one_day_ago = datetime.utcnow() - timedelta(days=1)
    
    recent_readings = db.query(models.SensorReading).filter(
        models.SensorReading.sensor_id.in_(sensor_ids),
        models.SensorReading.created_at >= one_day_ago
    ).all()
    
    # Calcular promedios
    temperatures = [r.temperature for r in recent_readings if r.temperature is not None]
    humidities = [r.humidity for r in recent_readings if r.humidity is not None]
    soil_moistures = [r.soil_moisture for r in recent_readings if r.soil_moisture is not None]
    
    avg_temperature = sum(temperatures) / len(temperatures) if temperatures else 0
    avg_humidity = sum(humidities) / len(humidities) if humidities else 0
    avg_soil_moisture = sum(soil_moistures) / len(soil_moistures) if soil_moistures else 0
    
    # Contar alertas activas
    active_alerts = db.query(models.SensorAlert).filter(
        models.SensorAlert.sensor_id.in_(sensor_ids),
        models.SensorAlert.status == models.AlertStatusEnum.active
    ).count()
    
    # Obtener datos por zona
    zones_data = []
    for zone in zones:
        zone_sensors = [s for s in sensors if s.zone_id == zone.id]
        zone_sensor_ids = [s.id for s in zone_sensors]
        
        # Lecturas de la zona
        zone_readings = [r for r in recent_readings if r.sensor_id in zone_sensor_ids]
        
        # Sensores online en la zona
        zone_online = db.query(models.Sensor.id).join(models.SensorReading).filter(
            models.Sensor.id.in_(zone_sensor_ids),
            models.SensorReading.created_at >= two_hours_ago
        ).distinct().count()
        
        # Alertas de la zona
        zone_alerts = db.query(models.SensorAlert).filter(
            models.SensorAlert.sensor_id.in_(zone_sensor_ids),
            models.SensorAlert.status == models.AlertStatusEnum.active
        ).count()
        
        # Calcular promedios ambientales por zona
        zone_temperatures = [r.temperature for r in zone_readings if r.temperature is not None]
        zone_humidities = [r.humidity for r in zone_readings if r.humidity is not None]
        zone_soil_moistures = [r.soil_moisture for r in zone_readings if r.soil_moisture is not None]
        
        zone_avg_temperature = sum(zone_temperatures) / len(zone_temperatures) if zone_temperatures else 0
        zone_avg_humidity = sum(zone_humidities) / len(zone_humidities) if zone_humidities else 0
        zone_avg_soil_moisture = sum(zone_soil_moistures) / len(zone_soil_moistures) if zone_soil_moistures else 0
        
        zones_data.append({
            "zone_id": zone.id,
            "zone_name": zone.name,
            "description": zone.description,
            "total_sensors": len(zone_sensors),
            "online_sensors": zone_online,
            "offline_sensors": len(zone_sensors) - zone_online,
            "active_alerts": zone_alerts,
            "avg_temperature": round(zone_avg_temperature, 2),
            "avg_humidity": round(zone_avg_humidity, 2),
            "avg_soil_moisture": round(zone_avg_soil_moisture, 2),
            "location": {
                "lat": zone.location_lat,
                "lon": zone.location_lon
            }
        })
    
    return {
        "farmer_id": farmer_id,
        "farmer_name": farmer.name,
        "total_zones": len(zones),
        "total_sensors": len(sensors),
        "online_sensors": online_sensors,
        "offline_sensors": offline_sensors,
        "zones": zones_data,
        "summary": {
            "avg_temperature": round(avg_temperature, 2),
            "avg_humidity": round(avg_humidity, 2),
            "avg_soil_moisture": round(avg_soil_moisture, 2),
            "active_alerts": active_alerts
        }
    }

@router.get("/zones/{zone_id}/statistics")
def get_zone_statistics(
    zone_id: int,
    hours: int = Query(24, ge=1, le=168),  # Máximo 1 semana
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Obtener estadísticas detalladas de una zona específica."""
    
    # Verificar que el usuario sea farmer
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden acceder a estas métricas")
    
    # Verificar que la zona pertenece al farmer
    zone = db.query(models.SensorZone).filter(
        models.SensorZone.id == zone_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not zone:
        raise HTTPException(status_code=404, detail="Zona no encontrada o no pertenece al farmer")
    
    # Obtener sensores de la zona
    sensors = db.query(models.Sensor).filter(
        models.Sensor.zone_id == zone_id
    ).all()
    
    if not sensors:
        return {
            "zone_id": zone_id,
            "zone_name": zone.name,
            "sensors": [],
            "statistics": {
                "total_sensors": 0,
                "online_sensors": 0,
                "offline_sensors": 0,
                "active_alerts": 0
            }
        }
    
    sensor_ids = [sensor.id for sensor in sensors]
    
    # Obtener lecturas del período especificado
    time_threshold = datetime.utcnow() - timedelta(hours=hours)
    
    readings = db.query(models.SensorReading).filter(
        models.SensorReading.sensor_id.in_(sensor_ids),
        models.SensorReading.created_at >= time_threshold
    ).order_by(models.SensorReading.created_at.desc()).all()
    
    # Calcular estadísticas por tipo de sensor
    sensor_stats = {}
    for sensor in sensors:
        sensor_readings = [r for r in readings if r.sensor_id == sensor.id]
        
        if sensor_readings:
            if sensor.sensor_type == models.SensorTypeEnum.temperature:
                values = [r.temperature for r in sensor_readings if r.temperature is not None]
            elif sensor.sensor_type == models.SensorTypeEnum.humidity:
                values = [r.humidity for r in sensor_readings if r.humidity is not None]
            elif sensor.sensor_type == models.SensorTypeEnum.soil_moisture:
                values = [r.soil_moisture for r in sensor_readings if r.soil_moisture is not None]
            elif sensor.sensor_type == models.SensorTypeEnum.light:
                values = [r.light_level for r in sensor_readings if r.light_level is not None]
            elif sensor.sensor_type == models.SensorTypeEnum.ph:
                values = [r.ph_level for r in sensor_readings if r.ph_level is not None]
            else:
                values = []
            
            if values:
                sensor_stats[sensor.id] = {
                    "sensor_id": sensor.id,
                    "device_id": sensor.device_id,
                    "name": sensor.name,
                    "type": sensor.sensor_type.value,
                    "status": sensor.status.value,
                    "last_seen": sensor.last_seen.isoformat() if sensor.last_seen else None,
                    "statistics": {
                        "count": len(values),
                        "min": round(min(values), 2),
                        "max": round(max(values), 2),
                        "avg": round(sum(values) / len(values), 2),
                        "latest": round(values[0], 2) if values else None
                    },
                    "thresholds": {
                        "min": sensor.min_threshold,
                        "max": sensor.max_threshold
                    }
                }
    
    # Contar sensores online/offline
    two_hours_ago = datetime.utcnow() - timedelta(hours=2)
    online_sensors = db.query(models.Sensor).join(models.SensorReading).filter(
        models.Sensor.id.in_(sensor_ids),
        models.SensorReading.created_at >= two_hours_ago
    ).distinct().count()
    
    # Contar alertas activas
    active_alerts = db.query(models.SensorAlert).filter(
        models.SensorAlert.sensor_id.in_(sensor_ids),
        models.SensorAlert.status == models.AlertStatusEnum.active
    ).count()
    
    return {
        "zone_id": zone_id,
        "zone_name": zone.name,
        "description": zone.description,
        "location": {
            "lat": zone.location_lat,
            "lon": zone.location_lon
        },
        "sensors": list(sensor_stats.values()),
        "statistics": {
            "total_sensors": len(sensors),
            "online_sensors": online_sensors,
            "offline_sensors": len(sensors) - online_sensors,
            "active_alerts": active_alerts,
            "period_hours": hours,
            "total_readings": len(readings)
        }
    }

@router.get("/sensors/{sensor_id}/history")
def get_sensor_history(
    sensor_id: int,
    hours: int = Query(24, ge=1, le=168),
    limit: int = Query(100, ge=1, le=1000),
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Obtener historial de lecturas de un sensor específico."""
    
    # Verificar que el usuario sea farmer
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden acceder a estas métricas")
    
    # Verificar que el sensor pertenece al farmer
    sensor = db.query(models.Sensor).join(models.SensorZone).filter(
        models.Sensor.id == sensor_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not sensor:
        raise HTTPException(status_code=404, detail="Sensor no encontrado o no pertenece al farmer")
    
    # Obtener lecturas del período especificado
    time_threshold = datetime.utcnow() - timedelta(hours=hours)
    
    readings = db.query(models.SensorReading).filter(
        models.SensorReading.sensor_id == sensor_id,
        models.SensorReading.created_at >= time_threshold
    ).order_by(models.SensorReading.created_at.desc()).limit(limit).all()
    
    # Formatear lecturas
    formatted_readings = []
    for reading in readings:
        value = None
        if sensor.sensor_type == models.SensorTypeEnum.temperature:
            value = reading.temperature
        elif sensor.sensor_type == models.SensorTypeEnum.humidity:
            value = reading.humidity
        elif sensor.sensor_type == models.SensorTypeEnum.soil_moisture:
            value = reading.soil_moisture
        elif sensor.sensor_type == models.SensorTypeEnum.light:
            value = reading.light_level
        elif sensor.sensor_type == models.SensorTypeEnum.ph:
            value = reading.ph_level
        elif sensor.sensor_type == models.SensorTypeEnum.gas:
            value = reading.gas_level
        
        formatted_readings.append({
            "id": reading.id,
            "value": round(value, 2) if value is not None else None,
            "unit": _get_sensor_unit(sensor.sensor_type),
            "quality": reading.reading_quality,
            "created_at": reading.created_at.isoformat(),
            "extra_data": reading.extra_data
        })
    
    return {
        "sensor_id": sensor_id,
        "sensor_name": sensor.name,
        "device_id": sensor.device_id,
        "type": sensor.sensor_type.value,
        "zone_name": sensor.zone.name if sensor.zone else None,
        "thresholds": {
            "min": sensor.min_threshold,
            "max": sensor.max_threshold
        },
        "readings": formatted_readings,
        "statistics": {
            "total_readings": len(formatted_readings),
            "period_hours": hours,
            "latest_value": formatted_readings[0]["value"] if formatted_readings else None
        }
    }

def _get_sensor_unit(sensor_type):
    """Obtener unidad de medida para un tipo de sensor."""
    units = {
        models.SensorTypeEnum.temperature: "°C",
        models.SensorTypeEnum.humidity: "%",
        models.SensorTypeEnum.soil_moisture: "%",
        models.SensorTypeEnum.light: "lux",
        models.SensorTypeEnum.ph: "pH",
        models.SensorTypeEnum.gas: "ppm"
    }
    return units.get(sensor_type, "")

@router.get("/alerts")
def get_farmer_alerts(
    status: Optional[str] = Query(None),
    severity: Optional[str] = Query(None),
    hours: int = Query(24, ge=1, le=168),
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Obtener alertas del farmer."""
    
    # Verificar que el usuario sea farmer
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden acceder a estas métricas")
    
    # Obtener sensores del farmer
    sensors = db.query(models.Sensor).join(models.SensorZone).filter(
        models.SensorZone.farmer_id == current_user.id
    ).all()
    
    sensor_ids = [sensor.id for sensor in sensors]
    
    if not sensor_ids:
        return {"alerts": [], "total": 0}
    
    # Construir query de alertas
    query = db.query(models.SensorAlert).filter(
        models.SensorAlert.sensor_id.in_(sensor_ids)
    )
    
    # Filtrar por tiempo
    time_threshold = datetime.utcnow() - timedelta(hours=hours)
    query = query.filter(models.SensorAlert.created_at >= time_threshold)
    
    # Filtrar por status
    if status:
        query = query.filter(models.SensorAlert.status == status)
    
    # Filtrar por severidad
    if severity:
        query = query.filter(models.SensorAlert.severity == severity)
    
    alerts = query.order_by(models.SensorAlert.created_at.desc()).all()
    
    # Formatear alertas
    formatted_alerts = []
    for alert in alerts:
        formatted_alerts.append({
            "id": alert.id,
            "sensor_id": alert.sensor_id,
            "sensor_name": alert.sensor.name,
            "zone_name": alert.sensor.zone.name if alert.sensor.zone else None,
            "alert_type": alert.alert_type.value,
            "status": alert.status.value,
            "severity": alert.severity,
            "title": alert.title,
            "message": alert.message,
            "threshold_value": alert.threshold_value,
            "actual_value": alert.actual_value,
            "unit": alert.unit,
            "created_at": alert.created_at.isoformat(),
            "acknowledged_at": alert.acknowledged_at.isoformat() if alert.acknowledged_at else None,
            "resolved_at": alert.resolved_at.isoformat() if alert.resolved_at else None
        })
    
    return {
        "alerts": formatted_alerts,
        "total": len(formatted_alerts),
        "filters": {
            "status": status,
            "severity": severity,
            "hours": hours
        }
    }
