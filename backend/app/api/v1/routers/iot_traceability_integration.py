from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import Dict, Any
from pydantic import BaseModel

from backend.app import schemas, database, models
from backend.app.database import get_db
from backend.app.algorithms.iot_traceability_integration import IOTTraceabilityIntegration
from backend.app.api.v1.routers.dependencies import get_current_user
from backend.app.models.user import User

router = APIRouter(prefix="/iot-traceability", tags=["Integración IoT-Trazabilidad"])

class TemperatureMonitoringRequest(BaseModel):
    product_id: int
    min_temp: float = 0.0
    max_temp: float = 40.0

@router.post("/products/{product_id}/auto-create-events")
def auto_create_traceability_events_from_sensors(
    product_id: int,
    blockchain_private_key: str,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Crea automáticamente eventos de trazabilidad basados en las lecturas de sensores
    """
    try:
        iot_service = IOTTraceabilityIntegration(db)
        
        result = iot_service.auto_create_traceability_events_from_sensors(
            product_id=product_id,
            blockchain_private_key=blockchain_private_key
        )
        
        return {
            "success": True,
            "message": "Eventos de trazabilidad creados automáticamente",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error creando eventos automáticos: {str(e)}"
        )

@router.post("/products/{product_id}/monitor-temperature")
def monitor_temperature_violations(
    product_id: int,
    request: TemperatureMonitoringRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Monitorea violaciones de temperatura para un producto
    """
    try:
        iot_service = IOTTraceabilityIntegration(db)
        
        result = iot_service.monitor_temperature_violations(
            product_id=product_id,
            min_temp=request.min_temp,
            max_temp=request.max_temp
        )
        
        return {
            "success": True,
            "message": "Monitoreo de temperatura completado",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error monitoreando temperatura: {str(e)}"
        )

@router.post("/products/{product_id}/calculate-quality-score")
def calculate_quality_score_from_sensors(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Calcula la puntuación de calidad basada en los datos de sensores
    """
    try:
        iot_service = IOTTraceabilityIntegration(db)
        
        result = iot_service.calculate_quality_score_from_sensors(product_id)
        
        return {
            "success": True,
            "message": "Puntuación de calidad calculada",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error calculando puntuación de calidad: {str(e)}"
        )

@router.post("/products/{product_id}/detect-anomalies")
def detect_anomalies_in_sensor_data(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Detecta anomalías en los datos de sensores
    """
    try:
        iot_service = IOTTraceabilityIntegration(db)
        
        result = iot_service.detect_anomalies_in_sensor_data(product_id)
        
        return {
            "success": True,
            "message": "Análisis de anomalías completado",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error detectando anomalías: {str(e)}"
        )

@router.get("/products/{product_id}/sensor-report")
def generate_sensor_report(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Genera un reporte completo de los datos de sensores
    """
    try:
        iot_service = IOTTraceabilityIntegration(db)
        
        result = iot_service.generate_sensor_report(product_id)
        
        return {
            "success": True,
            "message": "Reporte de sensores generado",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error generando reporte de sensores: {str(e)}"
        )

@router.post("/products/{product_id}/integrate-sensor-data")
def integrate_sensor_data_with_traceability(
    product_id: int,
    blockchain_private_key: str,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Integra automáticamente los datos de sensores con la trazabilidad
    """
    try:
        iot_service = IOTTraceabilityIntegration(db)
        
        # Crear eventos automáticos
        events_result = iot_service.auto_create_traceability_events_from_sensors(
            product_id=product_id,
            blockchain_private_key=blockchain_private_key
        )
        
        # Monitorear violaciones de temperatura
        temp_result = iot_service.monitor_temperature_violations(product_id)
        
        # Calcular puntuación de calidad
        quality_result = iot_service.calculate_quality_score_from_sensors(product_id)
        
        # Detectar anomalías
        anomalies_result = iot_service.detect_anomalies_in_sensor_data(product_id)
        
        return {
            "success": True,
            "message": "Integración de datos de sensores completada",
            "data": {
                "events_creation": events_result,
                "temperature_monitoring": temp_result,
                "quality_calculation": quality_result,
                "anomaly_detection": anomalies_result
            }
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error integrando datos de sensores: {str(e)}"
        )

@router.get("/products/{product_id}/sensor-summary")
def get_sensor_data_summary(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene un resumen de los datos de sensores para un producto
    """
    try:
        # Obtener datos de sensores
        sensor_data = db.query(models.SensorTraceabilityData).join(
            models.TraceabilityEvent
        ).filter(
            models.TraceabilityEvent.product_id == product_id
        ).all()
        
        if not sensor_data:
            return {
                "success": True,
                "message": "No hay datos de sensores disponibles",
                "data": {
                    "total_readings": 0,
                    "sensors_used": [],
                    "data_summary": {}
                }
            }
        
        # Obtener sensores únicos
        unique_sensors = list(set(data.sensor_id for data in sensor_data))
        
        # Calcular estadísticas básicas
        temperatures = [data.temperature for data in sensor_data if data.temperature is not None]
        humidities = [data.humidity for data in sensor_data if data.humidity is not None]
        soil_moistures = [data.soil_moisture for data in sensor_data if data.soil_moisture is not None]
        ph_levels = [data.ph_level for data in sensor_data if data.ph_level is not None]
        
        summary = {
            "total_readings": len(sensor_data),
            "sensors_used": unique_sensors,
            "data_summary": {
                "temperature": {
                    "count": len(temperatures),
                    "min": min(temperatures) if temperatures else None,
                    "max": max(temperatures) if temperatures else None,
                    "avg": sum(temperatures) / len(temperatures) if temperatures else None
                },
                "humidity": {
                    "count": len(humidities),
                    "min": min(humidities) if humidities else None,
                    "max": max(humidities) if humidities else None,
                    "avg": sum(humidities) / len(humidities) if humidities else None
                },
                "soil_moisture": {
                    "count": len(soil_moistures),
                    "min": min(soil_moistures) if soil_moistures else None,
                    "max": max(soil_moistures) if soil_moistures else None,
                    "avg": sum(soil_moistures) / len(soil_moistures) if soil_moistures else None
                },
                "ph_level": {
                    "count": len(ph_levels),
                    "min": min(ph_levels) if ph_levels else None,
                    "max": max(ph_levels) if ph_levels else None,
                    "avg": sum(ph_levels) / len(ph_levels) if ph_levels else None
                }
            }
        }
        
        return {
            "success": True,
            "data": summary
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo resumen de datos de sensores: {str(e)}"
        )

@router.get("/products/{product_id}/sensor-timeline")
def get_sensor_data_timeline(
    product_id: int,
    limit: int = 100,
    offset: int = 0,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene la línea de tiempo de los datos de sensores para un producto
    """
    try:
        # Obtener eventos de trazabilidad con datos de sensores
        events = db.query(models.TraceabilityEvent).filter(
            and_(
                models.TraceabilityEvent.product_id == product_id,
                models.TraceabilityEvent.event_type == "sensor_reading"
            )
        ).order_by(models.TraceabilityEvent.timestamp.desc()).offset(offset).limit(limit).all()
        
        timeline = []
        
        for event in events:
            # Obtener datos del sensor para este evento
            sensor_data = db.query(models.SensorTraceabilityData).filter(
                models.SensorTraceabilityData.traceability_event_id == event.id
            ).first()
            
            if sensor_data:
                timeline.append({
                    "event_id": event.id,
                    "timestamp": event.timestamp.isoformat(),
                    "sensor_id": sensor_data.sensor_id,
                    "location": {
                        "lat": event.location_lat,
                        "lon": event.location_lon,
                        "description": event.location_description
                    },
                    "sensor_data": {
                        "temperature": sensor_data.temperature,
                        "humidity": sensor_data.humidity,
                        "gas_level": sensor_data.gas_level,
                        "light_level": sensor_data.light_level,
                        "shock_detected": sensor_data.shock_detected,
                        "soil_moisture": sensor_data.soil_moisture,
                        "ph_level": sensor_data.ph_level,
                        "reading_quality": sensor_data.reading_quality,
                        "is_processed": sensor_data.is_processed
                    }
                })
        
        return {
            "success": True,
            "data": {
                "timeline": timeline,
                "total_events": len(timeline)
            }
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo línea de tiempo de sensores: {str(e)}"
        )
