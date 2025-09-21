from typing import Dict, List, Optional, Any
from datetime import datetime, timedelta
from sqlalchemy.orm import Session
from sqlalchemy import and_, or_, desc

from backend.app.models.blockchain_traceability import (
    TraceabilityEvent, ProductTraceabilityChain, SensorTraceabilityData,
    TraceabilityEventType
)
from backend.app.models.product import Product
from backend.app.models.sensor import Sensor
from backend.app.models.sensor_reading import SensorReading
from backend.app.models.sensor import SensorZone
from backend.app.algorithms.traceability_service import TraceabilityService

class IOTTraceabilityIntegration:
    """Servicio para integrar automáticamente los datos de sensores IoT con la trazabilidad"""
    
    def __init__(self, db: Session):
        self.db = db
        self.traceability_service = TraceabilityService(db)
    
    def auto_create_traceability_events_from_sensors(
        self, 
        product_id: int,
        blockchain_private_key: str
    ) -> Dict[str, Any]:
        """
        Crea automáticamente eventos de trazabilidad basados en las lecturas de sensores
        """
        try:
            # Obtener el producto
            product = self.db.query(Product).filter(Product.id == product_id).first()
            if not product:
                raise ValueError(f"Producto con ID {product_id} no encontrado")
            
            # Obtener la zona del sensor del productor
            sensor_zone = self.db.query(SensorZone).filter(
                SensorZone.farmer_id == product.provider_id
            ).first()
            
            if not sensor_zone:
                return {
                    "success": False,
                    "message": "No se encontró zona de sensores para el productor"
                }
            
            # Obtener sensores de la zona
            sensors = self.db.query(Sensor).filter(
                Sensor.zone_id == sensor_zone.id
            ).all()
            
            if not sensors:
                return {
                    "success": False,
                    "message": "No se encontraron sensores en la zona del productor"
                }
            
            events_created = []
            
            # Procesar cada sensor
            for sensor in sensors:
                # Obtener lecturas recientes del sensor
                recent_readings = self.db.query(SensorReading).filter(
                    and_(
                        SensorReading.sensor_id == sensor.id,
                        SensorReading.created_at >= datetime.utcnow() - timedelta(days=7)
                    )
                ).order_by(SensorReading.created_at.desc()).limit(10).all()
                
                if not recent_readings:
                    continue
                
                # Crear evento de lectura de sensor
                for reading in recent_readings:
                    try:
                        # Crear evento de trazabilidad
                        event = self.traceability_service._create_traceability_event(
                            product_id=product_id,
                            event_type=TraceabilityEventType.sensor_reading,
                            actor_id=None,
                            actor_type="system",
                            location_lat=sensor.location_lat,
                            location_lon=sensor.location_lon,
                            location_description=sensor.location_description,
                            event_data={
                                "sensor_id": sensor.id,
                                "sensor_type": sensor.sensor_type.value,
                                "device_id": sensor.device_id,
                                "reading_id": reading.id
                            },
                            blockchain_private_key=blockchain_private_key
                        )
                        
                        # Crear datos del sensor
                        sensor_data = SensorTraceabilityData(
                            traceability_event_id=event["event_id"],
                            sensor_id=sensor.id,
                            temperature=reading.temperature,
                            humidity=reading.humidity,
                            gas_level=reading.gas_level,
                            light_level=reading.light_level,
                            shock_detected=reading.shock_detected,
                            soil_moisture=reading.soil_moisture,
                            ph_level=reading.ph_level,
                            reading_quality=reading.reading_quality,
                            is_processed=reading.is_processed,
                            extra_data=reading.extra_data
                        )
                        
                        self.db.add(sensor_data)
                        events_created.append({
                            "sensor_id": sensor.id,
                            "reading_id": reading.id,
                            "event_id": event["event_id"]
                        })
                        
                    except Exception as e:
                        print(f"Error procesando lectura {reading.id}: {str(e)}")
                        continue
            
            self.db.commit()
            
            return {
                "success": True,
                "message": f"Se crearon {len(events_created)} eventos de trazabilidad",
                "events_created": events_created
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error creando eventos automáticos: {str(e)}")
    
    def monitor_temperature_violations(
        self, 
        product_id: int,
        min_temp: float = 0.0,
        max_temp: float = 40.0
    ) -> Dict[str, Any]:
        """
        Monitorea violaciones de temperatura para un producto
        """
        try:
            # Obtener datos de sensores de temperatura
            sensor_data = self.db.query(SensorTraceabilityData).join(
                TraceabilityEvent
            ).filter(
                and_(
                    TraceabilityEvent.product_id == product_id,
                    SensorTraceabilityData.temperature.isnot(None)
                )
            ).all()
            
            violations = []
            
            for data in sensor_data:
                if data.temperature < min_temp or data.temperature > max_temp:
                    violations.append({
                        "sensor_id": data.sensor_id,
                        "temperature": data.temperature,
                        "min_threshold": min_temp,
                        "max_threshold": max_temp,
                        "violation_type": "too_low" if data.temperature < min_temp else "too_high",
                        "event_id": data.traceability_event_id
                    })
            
            # Actualizar contador de violaciones en la cadena
            chain = self.db.query(ProductTraceabilityChain).filter(
                ProductTraceabilityChain.product_id == product_id
            ).first()
            
            if chain:
                chain.temperature_violations = len(violations)
                self.db.commit()
            
            return {
                "success": True,
                "total_violations": len(violations),
                "violations": violations
            }
            
        except Exception as e:
            raise Exception(f"Error monitoreando violaciones de temperatura: {str(e)}")
    
    def calculate_quality_score_from_sensors(
        self, 
        product_id: int
    ) -> Dict[str, Any]:
        """
        Calcula la puntuación de calidad basada en los datos de sensores
        """
        try:
            # Obtener datos de sensores
            sensor_data = self.db.query(SensorTraceabilityData).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            if not sensor_data:
                return {
                    "success": False,
                    "message": "No hay datos de sensores para calcular la puntuación"
                }
            
            # Calcular puntuación basada en diferentes factores
            quality_factors = {
                "temperature_consistency": 0.0,
                "humidity_optimal": 0.0,
                "no_shock_detected": 0.0,
                "soil_conditions": 0.0,
                "overall_reading_quality": 0.0
            }
            
            # Factor 1: Consistencia de temperatura
            temperatures = [data.temperature for data in sensor_data if data.temperature is not None]
            if temperatures:
                temp_variance = max(temperatures) - min(temperatures)
                quality_factors["temperature_consistency"] = max(0, 1 - (temp_variance / 20))  # Normalizar
            
            # Factor 2: Humedad óptima
            humidities = [data.humidity for data in sensor_data if data.humidity is not None]
            if humidities:
                avg_humidity = sum(humidities) / len(humidities)
                # Humedad óptima entre 40-70%
                if 40 <= avg_humidity <= 70:
                    quality_factors["humidity_optimal"] = 1.0
                else:
                    quality_factors["humidity_optimal"] = max(0, 1 - abs(avg_humidity - 55) / 30)
            
            # Factor 3: Sin detección de golpes
            shock_count = sum(1 for data in sensor_data if data.shock_detected)
            quality_factors["no_shock_detected"] = max(0, 1 - (shock_count / len(sensor_data)))
            
            # Factor 4: Condiciones del suelo
            soil_moistures = [data.soil_moisture for data in sensor_data if data.soil_moisture is not None]
            ph_levels = [data.ph_level for data in sensor_data if data.ph_level is not None]
            
            soil_score = 0.0
            if soil_moistures:
                avg_moisture = sum(soil_moistures) / len(soil_moistures)
                # Humedad del suelo óptima entre 20-40%
                if 20 <= avg_moisture <= 40:
                    soil_score += 0.5
                else:
                    soil_score += max(0, 0.5 - abs(avg_moisture - 30) / 20)
            
            if ph_levels:
                avg_ph = sum(ph_levels) / len(ph_levels)
                # pH óptimo entre 6-7
                if 6 <= avg_ph <= 7:
                    soil_score += 0.5
                else:
                    soil_score += max(0, 0.5 - abs(avg_ph - 6.5) / 2)
            
            quality_factors["soil_conditions"] = soil_score
            
            # Factor 5: Calidad general de las lecturas
            reading_qualities = [data.reading_quality for data in sensor_data if data.reading_quality is not None]
            if reading_qualities:
                quality_factors["overall_reading_quality"] = sum(reading_qualities) / len(reading_qualities)
            
            # Calcular puntuación final
            weights = {
                "temperature_consistency": 0.25,
                "humidity_optimal": 0.20,
                "no_shock_detected": 0.20,
                "soil_conditions": 0.20,
                "overall_reading_quality": 0.15
            }
            
            final_score = sum(
                quality_factors[factor] * weights[factor] 
                for factor in quality_factors
            )
            
            # Actualizar la cadena de trazabilidad
            chain = self.db.query(ProductTraceabilityChain).filter(
                ProductTraceabilityChain.product_id == product_id
            ).first()
            
            if chain:
                chain.quality_score = final_score
                self.db.commit()
            
            return {
                "success": True,
                "quality_score": final_score,
                "quality_factors": quality_factors,
                "weights": weights,
                "total_sensor_readings": len(sensor_data)
            }
            
        except Exception as e:
            raise Exception(f"Error calculando puntuación de calidad: {str(e)}")
    
    def detect_anomalies_in_sensor_data(
        self, 
        product_id: int
    ) -> Dict[str, Any]:
        """
        Detecta anomalías en los datos de sensores
        """
        try:
            # Obtener datos de sensores
            sensor_data = self.db.query(SensorTraceabilityData).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            if not sensor_data:
                return {
                    "success": False,
                    "message": "No hay datos de sensores para analizar"
                }
            
            anomalies = []
            
            # Análisis de temperatura
            temperatures = [data.temperature for data in sensor_data if data.temperature is not None]
            if temperatures:
                avg_temp = sum(temperatures) / len(temperatures)
                temp_std = (sum((t - avg_temp) ** 2 for t in temperatures) / len(temperatures)) ** 0.5
                
                for data in sensor_data:
                    if data.temperature and abs(data.temperature - avg_temp) > 2 * temp_std:
                        anomalies.append({
                            "type": "temperature_anomaly",
                            "sensor_id": data.sensor_id,
                            "value": data.temperature,
                            "expected_range": [avg_temp - 2 * temp_std, avg_temp + 2 * temp_std],
                            "severity": "high" if abs(data.temperature - avg_temp) > 3 * temp_std else "medium"
                        })
            
            # Análisis de humedad
            humidities = [data.humidity for data in sensor_data if data.humidity is not None]
            if humidities:
                avg_humidity = sum(humidities) / len(humidities)
                humidity_std = (sum((h - avg_humidity) ** 2 for h in humidities) / len(humidities)) ** 0.5
                
                for data in sensor_data:
                    if data.humidity and abs(data.humidity - avg_humidity) > 2 * humidity_std:
                        anomalies.append({
                            "type": "humidity_anomaly",
                            "sensor_id": data.sensor_id,
                            "value": data.humidity,
                            "expected_range": [avg_humidity - 2 * humidity_std, avg_humidity + 2 * humidity_std],
                            "severity": "high" if abs(data.humidity - avg_humidity) > 3 * humidity_std else "medium"
                        })
            
            # Análisis de golpes
            shock_events = [data for data in sensor_data if data.shock_detected]
            if shock_events:
                for data in shock_events:
                    anomalies.append({
                        "type": "shock_detected",
                        "sensor_id": data.sensor_id,
                        "value": True,
                        "severity": "high"
                    })
            
            # Análisis de calidad de lectura
            low_quality_readings = [data for data in sensor_data if data.reading_quality and data.reading_quality < 0.8]
            if low_quality_readings:
                for data in low_quality_readings:
                    anomalies.append({
                        "type": "low_reading_quality",
                        "sensor_id": data.sensor_id,
                        "value": data.reading_quality,
                        "expected_range": [0.8, 1.0],
                        "severity": "medium"
                    })
            
            return {
                "success": True,
                "total_anomalies": len(anomalies),
                "anomalies": anomalies,
                "anomaly_summary": {
                    "temperature_anomalies": len([a for a in anomalies if a["type"] == "temperature_anomaly"]),
                    "humidity_anomalies": len([a for a in anomalies if a["type"] == "humidity_anomaly"]),
                    "shock_events": len([a for a in anomalies if a["type"] == "shock_detected"]),
                    "low_quality_readings": len([a for a in anomalies if a["type"] == "low_reading_quality"])
                }
            }
            
        except Exception as e:
            raise Exception(f"Error detectando anomalías: {str(e)}")
    
    def generate_sensor_report(
        self, 
        product_id: int
    ) -> Dict[str, Any]:
        """
        Genera un reporte completo de los datos de sensores
        """
        try:
            # Obtener datos de sensores
            sensor_data = self.db.query(SensorTraceabilityData).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            if not sensor_data:
                return {
                    "success": False,
                    "message": "No hay datos de sensores para generar el reporte"
                }
            
            # Calcular estadísticas
            temperatures = [data.temperature for data in sensor_data if data.temperature is not None]
            humidities = [data.humidity for data in sensor_data if data.humidity is not None]
            soil_moistures = [data.soil_moisture for data in sensor_data if data.soil_moisture is not None]
            ph_levels = [data.ph_level for data in sensor_data if data.ph_level is not None]
            
            # Detectar anomalías
            anomalies_result = self.detect_anomalies_in_sensor_data(product_id)
            
            # Calcular puntuación de calidad
            quality_result = self.calculate_quality_score_from_sensors(product_id)
            
            # Monitorear violaciones de temperatura
            temp_violations = self.monitor_temperature_violations(product_id)
            
            report = {
                "product_id": product_id,
                "total_sensor_readings": len(sensor_data),
                "sensor_statistics": {
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
                },
                "quality_assessment": quality_result,
                "anomaly_detection": anomalies_result,
                "temperature_violations": temp_violations,
                "report_generated_at": datetime.utcnow().isoformat()
            }
            
            return {
                "success": True,
                "data": report
            }
            
        except Exception as e:
            raise Exception(f"Error generando reporte de sensores: {str(e)}")
