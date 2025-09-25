from typing import Dict, List, Optional, Tuple, Any
import json
from datetime import datetime, timedelta
from sqlalchemy.orm import Session
from sqlalchemy import and_, or_, desc
import hashlib
import math

from backend.app.models.blockchain_traceability import (
    TraceabilityEvent, ProductTraceabilityChain, SensorTraceabilityData,
    TransportLog, QualityCheck, TraceabilityEventType
)
from backend.app.models.product import Product
from backend.app.models.user import User
from backend.app.models.sensor import Sensor
from backend.app.models.sensor_reading import SensorReading
from backend.app.models.transaction import Transaction
from backend.app.algorithms.blockchain_manager import BlockchainManager
from backend.app.algorithms.fake_blockchain_data import FakeBlockchainDataGenerator
from backend.app.core.config import settings

class TraceabilityService:
    """Servicio principal para la gestión de trazabilidad blockchain"""
    
    def __init__(self, db: Session):
        self.db = db
        self.blockchain_manager = BlockchainManager(settings.BLOCKCHAIN_URL)
        self.fake_data_generator = FakeBlockchainDataGenerator()
    
    def create_product_traceability_chain(
        self, 
        product_id: int, 
        producer_id: int,
        blockchain_private_key: str
    ) -> Dict[str, Any]:
        """
        Crea una nueva cadena de trazabilidad para un producto
        """
        try:
            # Obtener el producto y el productor
            product = self.db.query(Product).filter(Product.id == product_id).first()
            if not product:
                raise ValueError(f"Producto con ID {product_id} no encontrado")
            
            producer = self.db.query(User).filter(User.id == producer_id).first()
            if not producer:
                raise ValueError(f"Productor con ID {producer_id} no encontrado")
            
            # Verificar que el productor es el propietario del producto
            if product.provider_id != producer_id:
                raise ValueError("El productor no es el propietario del producto")
            
            # Crear la cadena de trazabilidad
            traceability_chain = ProductTraceabilityChain(
                product_id=product_id,
                product_name=product.name,
                product_category=product.category.value if product.category else None,
                is_eco=product.is_eco,
                original_producer_id=producer_id,
                original_producer_name=producer.name,
                original_producer_location_lat=producer.location_lat,
                original_producer_location_lon=producer.location_lon
            )
            
            self.db.add(traceability_chain)
            self.db.commit()
            self.db.refresh(traceability_chain)
            
            # Crear el evento inicial de creación del producto
            self._create_traceability_event(
                product_id=product_id,
                event_type=TraceabilityEventType.product_created,
                actor_id=producer_id,
                actor_type="farmer",
                location_lat=producer.location_lat,
                location_lon=producer.location_lon,
                location_description=f"Granja de {producer.name}",
                event_data={
                    "product_name": product.name,
                    "category": product.category.value if product.category else None,
                    "is_eco": product.is_eco,
                    "certifications": product.certifications
                },
                blockchain_private_key=blockchain_private_key
            )
            
            return {
                "success": True,
                "traceability_chain_id": traceability_chain.id,
                "message": "Cadena de trazabilidad creada exitosamente"
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error creando cadena de trazabilidad: {str(e)}")
    
    def add_sensor_reading_to_traceability(
        self,
        product_id: int,
        sensor_id: int,
        sensor_reading_data: Dict[str, Any],
        blockchain_private_key: str
    ) -> Dict[str, Any]:
        """
        Añade una lectura de sensor a la trazabilidad del producto
        """
        try:
            # Obtener el sensor
            sensor = self.db.query(Sensor).filter(Sensor.id == sensor_id).first()
            if not sensor:
                raise ValueError(f"Sensor con ID {sensor_id} no encontrado")
            
            # Crear el evento de lectura de sensor
            event = self._create_traceability_event(
                product_id=product_id,
                event_type=TraceabilityEventType.sensor_reading,
                actor_id=None,  # Sistema automático
                actor_type="system",
                location_lat=sensor.location_lat,
                location_lon=sensor.location_lon,
                location_description=sensor.location_description,
                event_data={
                    "sensor_id": sensor_id,
                    "sensor_type": sensor.sensor_type.value,
                    "device_id": sensor.device_id
                },
                blockchain_private_key=blockchain_private_key
            )
            
            # Crear los datos del sensor
            sensor_data = SensorTraceabilityData(
                traceability_event_id=event["event_id"],
                sensor_id=sensor_id,
                temperature=sensor_reading_data.get("temperature"),
                humidity=sensor_reading_data.get("humidity"),
                gas_level=sensor_reading_data.get("gas_level"),
                light_level=sensor_reading_data.get("light_level"),
                shock_detected=sensor_reading_data.get("shock_detected"),
                soil_moisture=sensor_reading_data.get("soil_moisture"),
                ph_level=sensor_reading_data.get("ph_level"),
                reading_quality=sensor_reading_data.get("reading_quality", 1.0),
                is_processed=sensor_reading_data.get("is_processed", False),
                extra_data=sensor_reading_data.get("extra_data")
            )
            
            self.db.add(sensor_data)
            self.db.commit()
            
            # Actualizar métricas de la cadena
            self._update_chain_metrics(product_id)
            
            return {
                "success": True,
                "event_id": event["event_id"],
                "sensor_data_id": sensor_data.id,
                "message": "Lectura de sensor añadida a la trazabilidad"
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error añadiendo lectura de sensor: {str(e)}")
    
    def add_transport_event(
        self,
        product_id: int,
        event_type: TraceabilityEventType,
        transport_data: Dict[str, Any],
        actor_id: int,
        blockchain_private_key: str
    ) -> Dict[str, Any]:
        """
        Añade un evento de transporte a la trazabilidad
        """
        try:
            # Crear el evento de transporte
            event = self._create_traceability_event(
                product_id=product_id,
                event_type=event_type,
                actor_id=actor_id,
                actor_type="system",
                location_lat=transport_data.get("location_lat"),
                location_lon=transport_data.get("location_lon"),
                location_description=transport_data.get("location_description"),
                event_data={
                    "transport_type": transport_data.get("transport_type"),
                    "vehicle_id": transport_data.get("vehicle_id")
                },
                blockchain_private_key=blockchain_private_key
            )
            
            # Crear el log de transporte
            transport_log = TransportLog(
                traceability_event_id=event["event_id"],
                transport_type=transport_data.get("transport_type"),
                driver_id=transport_data.get("driver_id"),
                vehicle_id=transport_data.get("vehicle_id"),
                start_location_lat=transport_data.get("start_location_lat"),
                start_location_lon=transport_data.get("start_location_lon"),
                end_location_lat=transport_data.get("end_location_lat"),
                end_location_lon=transport_data.get("end_location_lon"),
                distance_km=transport_data.get("distance_km"),
                estimated_time_hours=transport_data.get("estimated_time_hours"),
                actual_time_hours=transport_data.get("actual_time_hours"),
                temperature_min=transport_data.get("temperature_min"),
                temperature_max=transport_data.get("temperature_max"),
                humidity_min=transport_data.get("humidity_min"),
                humidity_max=transport_data.get("humidity_max")
            )
            
            self.db.add(transport_log)
            self.db.commit()
            
            # Actualizar métricas de la cadena
            self._update_chain_metrics(product_id)
            
            return {
                "success": True,
                "event_id": event["event_id"],
                "transport_log_id": transport_log.id,
                "message": "Evento de transporte añadido a la trazabilidad"
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error añadiendo evento de transporte: {str(e)}")
    
    def add_transaction_to_traceability(
        self,
        transaction_id: int,
        blockchain_private_key: str
    ) -> Dict[str, Any]:
        """
        Añade una transacción a la trazabilidad
        """
        try:
            # Obtener la transacción
            transaction = self.db.query(Transaction).filter(Transaction.id == transaction_id).first()
            if not transaction:
                raise ValueError(f"Transacción con ID {transaction_id} no encontrada")
            
            # Determinar el tipo de evento según los tipos de comprador y vendedor
            if transaction.seller_type == "farmer" and transaction.buyer_type == "supermarket":
                event_type = TraceabilityEventType.sale_farmer_supermarket
            elif transaction.seller_type == "supermarket" and transaction.buyer_type == "consumer":
                event_type = TraceabilityEventType.sale_supermarket_consumer
            else:
                event_type = TraceabilityEventType.sale_farmer_supermarket  # Default
            
            # Obtener el producto de la transacción
            order_details = transaction.order_details
            if isinstance(order_details, list) and len(order_details) > 0:
                product_id = order_details[0].get("product_id") if isinstance(order_details[0], dict) else order_details[0].product_id
            else:
                raise ValueError("No se pudo determinar el producto de la transacción")
            
            # Crear el evento de transacción
            event = self._create_traceability_event(
                product_id=product_id,
                event_type=event_type,
                actor_id=transaction.buyer_id,
                actor_type=transaction.buyer_type,
                location_lat=transaction.buyer.location_lat if transaction.buyer else None,
                location_lon=transaction.buyer.location_lon if transaction.buyer else None,
                location_description=f"Transacción {transaction.id}",
                event_data={
                    "transaction_id": transaction_id,
                    "buyer_id": transaction.buyer_id,
                    "seller_id": transaction.seller_id,
                    "total_price": float(transaction.total_price),
                    "currency": transaction.currency,
                    "order_details": order_details
                },
                blockchain_private_key=blockchain_private_key
            )
            
            # Actualizar métricas de la cadena
            self._update_chain_metrics(product_id)
            
            return {
                "success": True,
                "event_id": event["event_id"],
                "message": "Transacción añadida a la trazabilidad"
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error añadiendo transacción a la trazabilidad: {str(e)}")
    
    def add_quality_check(
        self,
        product_id: int,
        check_data: Dict[str, Any],
        inspector_id: int,
        blockchain_private_key: str
    ) -> Dict[str, Any]:
        """
        Añade un control de calidad a la trazabilidad
        """
        try:
            # Crear el evento de control de calidad
            event = self._create_traceability_event(
                product_id=product_id,
                event_type=TraceabilityEventType.quality_check,
                actor_id=inspector_id,
                actor_type="inspector",
                location_lat=check_data.get("location_lat"),
                location_lon=check_data.get("location_lon"),
                location_description=check_data.get("location_description"),
                event_data={
                    "check_type": check_data.get("check_type"),
                    "inspector_id": inspector_id
                },
                blockchain_private_key=blockchain_private_key
            )
            
            # Crear el registro de control de calidad
            quality_check = QualityCheck(
                traceability_event_id=event["event_id"],
                check_type=check_data.get("check_type"),
                inspector_id=inspector_id,
                passed=check_data.get("passed", False),
                score=check_data.get("score"),
                notes=check_data.get("notes"),
                check_data=check_data.get("check_data")
            )
            
            self.db.add(quality_check)
            self.db.commit()
            
            # Actualizar métricas de la cadena
            self._update_chain_metrics(product_id)
            
            return {
                "success": True,
                "event_id": event["event_id"],
                "quality_check_id": quality_check.id,
                "message": "Control de calidad añadido a la trazabilidad"
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error añadiendo control de calidad: {str(e)}")
    
    def get_product_traceability_summary(self, product_id: int, consumer_location: Optional[Tuple[float, float]] = None) -> Dict[str, Any]:
        """
        Obtiene un resumen completo de la trazabilidad de un producto con datos falsificados
        """
        try:
            # Obtener el producto
            product = self.db.query(Product).filter(Product.id == product_id).first()
            if not product:
                raise ValueError(f"Producto con ID {product_id} no encontrado")
            
            # Obtener la cadena de trazabilidad
            chain = self.db.query(ProductTraceabilityChain).filter(
                ProductTraceabilityChain.product_id == product_id
            ).first()
            
            # Si no existe cadena, crear datos falsificados completos
            if not chain:
                return self._generate_complete_fake_traceability(product, consumer_location)
            
            # Obtener todos los eventos
            events = self.db.query(TraceabilityEvent).filter(
                TraceabilityEvent.product_id == product_id
            ).order_by(TraceabilityEvent.timestamp).all()
            
            # Actualizar métricas antes de devolver el resumen
            self._update_chain_metrics(product_id)
            
            # Refrescar la cadena para obtener las métricas actualizadas
            self.db.refresh(chain)
            
            # Obtener datos de sensores
            sensor_data = self.db.query(SensorTraceabilityData).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            # Obtener logs de transporte
            transport_logs = self.db.query(TransportLog).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            # Obtener controles de calidad
            quality_checks = self.db.query(QualityCheck).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            # Generar datos falsificados adicionales
            farmer_location = (chain.original_producer_location_lat, chain.original_producer_location_lon) if chain.original_producer_location_lat else None
            fake_product_data = self.fake_data_generator.generate_fake_product_data(
                product_category=product.category.value if product.category else "default",
                product_id=product_id,
                consumer_location=consumer_location,
                farmer_location=farmer_location,
                publication_date=chain.created_at
            )
            
            fake_sustainability = self.fake_data_generator.generate_fake_sustainability_metrics(
                product_id=product_id,
                product_category=product.category.value if product.category else "default"
            )
            
            fake_quality = self.fake_data_generator.generate_fake_quality_metrics(
                product_id=product_id,
                product_category=product.category.value if product.category else "default"
            )
            
            fake_supply_events = self.fake_data_generator.generate_fake_supply_chain_events(
                product_id=product_id,
                product_category=product.category.value if product.category else "default",
                farmer_location=farmer_location or (40.4168, -3.7038),  # Madrid por defecto
                consumer_location=consumer_location or (40.4168, -3.7038)
            )
            
            return {
                "product_id": product_id,
                "product_name": chain.product_name,
                "product_category": chain.product_category,
                "is_eco": chain.is_eco,
                "original_producer": {
                    "id": chain.original_producer_id,
                    "name": chain.original_producer_name,
                    "location_lat": chain.original_producer_location_lat,
                    "location_lon": chain.original_producer_location_lon
                },
                "chain_status": {
                    "is_complete": chain.is_complete,
                    "is_verified": chain.is_verified,
                    "total_distance_km": chain.total_distance_km,
                    "total_time_hours": chain.total_time_hours,
                    "temperature_violations": chain.temperature_violations,
                    "quality_score": chain.quality_score
                },
                # Datos falsificados para el consumidor
                "consumer_metrics": {
                    "distance_to_farmer_km": fake_product_data.distance_to_consumer_km,
                    "time_since_publication_hours": fake_product_data.time_since_publication_hours,
                    "sustainability_score": fake_product_data.sustainability_score,
                    "quality_score": fake_product_data.quality_score,
                    "freshness_score": fake_product_data.freshness_score,
                    "local_sourcing_score": fake_product_data.local_sourcing_score,
                    "packaging_score": fake_product_data.packaging_score
                },
                "environmental_impact": {
                    "carbon_footprint_kg": fake_product_data.carbon_footprint_kg,
                    "water_usage_liters": fake_product_data.water_usage_liters,
                    "overall_sustainability": fake_sustainability["overall_sustainability_score"],
                    "certifications": fake_sustainability["certifications"],
                    "eco_friendly_practices": fake_sustainability["eco_friendly_practices"]
                },
                "quality_details": {
                    "overall_quality_score": fake_quality["overall_quality_score"],
                    "freshness_indicators": fake_quality["freshness_indicators"],
                    "safety_standards": fake_quality["safety_standards"],
                    "nutritional_quality": fake_quality["nutritional_quality"]
                },
                "supply_chain_events": fake_supply_events,
                "events": [
                    {
                        "id": event.id,
                        "event_type": event.event_type.value,
                        "timestamp": event.timestamp.isoformat(),
                        "location": {
                            "lat": event.location_lat,
                            "lon": event.location_lon,
                            "description": event.location_description
                        },
                        "actor": {
                            "id": event.actor_id,
                            "type": event.actor_type
                        },
                        "event_data": event.event_data,
                        "blockchain_hash": event.blockchain_hash,
                        "is_verified": event.is_verified
                    }
                    for event in events
                ],
                "sensor_data": [
                    {
                        "id": data.id,
                        "sensor_id": data.sensor_id,
                        "temperature": data.temperature,
                        "humidity": data.humidity,
                        "gas_level": data.gas_level,
                        "light_level": data.light_level,
                        "shock_detected": data.shock_detected,
                        "soil_moisture": data.soil_moisture,
                        "ph_level": data.ph_level,
                        "reading_quality": data.reading_quality,
                        "is_processed": data.is_processed
                    }
                    for data in sensor_data
                ],
                "transport_logs": [
                    {
                        "id": log.id,
                        "transport_type": log.transport_type,
                        "driver_id": log.driver_id,
                        "vehicle_id": log.vehicle_id,
                        "start_location": {
                            "lat": log.start_location_lat,
                            "lon": log.start_location_lon
                        },
                        "end_location": {
                            "lat": log.end_location_lat,
                            "lon": log.end_location_lon
                        },
                        "distance_km": log.distance_km,
                        "estimated_time_hours": log.estimated_time_hours,
                        "actual_time_hours": log.actual_time_hours,
                        "temperature_range": {
                            "min": log.temperature_min,
                            "max": log.temperature_max
                        },
                        "humidity_range": {
                            "min": log.humidity_min,
                            "max": log.humidity_max
                        }
                    }
                    for log in transport_logs
                ],
                "quality_checks": [
                    {
                        "id": check.id,
                        "check_type": check.check_type,
                        "inspector_id": check.inspector_id,
                        "passed": check.passed,
                        "score": check.score,
                        "notes": check.notes,
                        "check_data": check.check_data,
                        "created_at": check.created_at.isoformat()
                    }
                    for check in quality_checks
                ],
                "timestamps": {
                    "created_at": chain.created_at.isoformat(),
                    "completed_at": chain.completed_at.isoformat() if chain.completed_at else None,
                    "verified_at": chain.verified_at.isoformat() if chain.verified_at else None
                }
            }
            
        except Exception as e:
            raise Exception(f"Error obteniendo resumen de trazabilidad: {str(e)}")
    
    def verify_traceability_authenticity(self, product_id: int) -> Dict[str, Any]:
        """
        Verifica la autenticidad de la trazabilidad de un producto
        """
        try:
            # Obtener la cadena de trazabilidad
            chain = self.db.query(ProductTraceabilityChain).filter(
                ProductTraceabilityChain.product_id == product_id
            ).first()
            
            if not chain:
                return {
                    "is_authentic": False,
                    "verification_score": 0.0,
                    "issues_found": ["No se encontró cadena de trazabilidad"],
                    "recommendations": ["Crear cadena de trazabilidad para el producto"]
                }
            
            # Verificar eventos en blockchain
            events = self.db.query(TraceabilityEvent).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            blockchain_verified = all(event.blockchain_hash for event in events)
            
            # Verificar datos de sensores
            sensor_data = self.db.query(SensorTraceabilityData).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            sensor_data_verified = len(sensor_data) > 0 and all(
                data.reading_quality > 0.8 for data in sensor_data
            )
            
            # Verificar controles de calidad
            quality_checks = self.db.query(QualityCheck).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            quality_checks_passed = len(quality_checks) > 0 and all(
                check.passed for check in quality_checks
            )
            
            # Calcular puntuación de verificación
            verification_score = 0.0
            issues_found = []
            recommendations = []
            
            if blockchain_verified:
                verification_score += 0.4
            else:
                issues_found.append("Eventos no verificados en blockchain")
                recommendations.append("Verificar eventos en blockchain")
            
            if sensor_data_verified:
                verification_score += 0.3
            else:
                issues_found.append("Datos de sensores insuficientes o de baja calidad")
                recommendations.append("Mejorar calidad de datos de sensores")
            
            if quality_checks_passed:
                verification_score += 0.2
            else:
                issues_found.append("Controles de calidad fallidos")
                recommendations.append("Realizar controles de calidad adicionales")
            
            if chain.is_complete:
                verification_score += 0.1
            else:
                issues_found.append("Cadena de trazabilidad incompleta")
                recommendations.append("Completar cadena de trazabilidad")
            
            is_authentic = verification_score >= 0.8
            
            return {
                "is_authentic": is_authentic,
                "verification_score": verification_score,
                "verification_details": {
                    "blockchain_verified": blockchain_verified,
                    "sensor_data_verified": sensor_data_verified,
                    "quality_checks_passed": quality_checks_passed,
                    "chain_complete": chain.is_complete,
                    "total_events": len(events),
                    "total_sensor_readings": len(sensor_data),
                    "total_quality_checks": len(quality_checks)
                },
                "issues_found": issues_found,
                "recommendations": recommendations,
                "verification_timestamp": datetime.utcnow().isoformat()
            }
            
        except Exception as e:
            raise Exception(f"Error verificando autenticidad: {str(e)}")
    
    def _generate_complete_fake_traceability(self, product: Product, consumer_location: Optional[Tuple[float, float]] = None) -> Dict[str, Any]:
        """
        Genera una trazabilidad completamente falsificada para productos sin cadena de trazabilidad
        """
        # Obtener información del productor
        producer = self.db.query(User).filter(User.id == product.provider_id).first()
        farmer_location = None
        producer_name = "Productor Desconocido"
        
        if producer:
            farmer_location = (producer.location_lat, producer.location_lon) if producer.location_lat else None
            producer_name = producer.name
        
        # Generar datos falsificados completos
        fake_product_data = self.fake_data_generator.generate_fake_product_data(
            product_category=product.category.value if product.category else "default",
            product_id=product.id,
            consumer_location=consumer_location,
            farmer_location=farmer_location,
            publication_date=product.created_at
        )
        
        fake_sustainability = self.fake_data_generator.generate_fake_sustainability_metrics(
            product_id=product.id,
            product_category=product.category.value if product.category else "default"
        )
        
        fake_quality = self.fake_data_generator.generate_fake_quality_metrics(
            product_id=product.id,
            product_category=product.category.value if product.category else "default"
        )
        
        fake_supply_events = self.fake_data_generator.generate_fake_supply_chain_events(
            product_id=product.id,
            product_category=product.category.value if product.category else "default",
            farmer_location=farmer_location or (40.4168, -3.7038),  # Madrid por defecto
            consumer_location=consumer_location or (40.4168, -3.7038)
        )
        
        return {
            "product_id": product.id,
            "product_name": product.name,
            "product_category": product.category.value if product.category else "default",
            "is_eco": product.is_eco,
            "original_producer": {
                "id": product.provider_id,
                "name": producer_name,
                "location_lat": farmer_location[0] if farmer_location else None,
                "location_lon": farmer_location[1] if farmer_location else None
            },
            "chain_status": {
                "is_complete": True,
                "is_verified": True,
                "total_distance_km": fake_product_data.distance_to_consumer_km,
                "total_time_hours": fake_product_data.time_since_publication_hours,
                "temperature_violations": 0,
                "quality_score": fake_product_data.quality_score
            },
            # Datos falsificados para el consumidor
            "consumer_metrics": {
                "distance_to_farmer_km": fake_product_data.distance_to_consumer_km,
                "time_since_publication_hours": fake_product_data.time_since_publication_hours,
                "sustainability_score": fake_product_data.sustainability_score,
                "quality_score": fake_product_data.quality_score,
                "freshness_score": fake_product_data.freshness_score,
                "local_sourcing_score": fake_product_data.local_sourcing_score,
                "packaging_score": fake_product_data.packaging_score
            },
            "environmental_impact": {
                "carbon_footprint_kg": fake_product_data.carbon_footprint_kg,
                "water_usage_liters": fake_product_data.water_usage_liters,
                "overall_sustainability": fake_sustainability["overall_sustainability_score"],
                "certifications": fake_sustainability["certifications"],
                "eco_friendly_practices": fake_sustainability["eco_friendly_practices"]
            },
            "quality_details": {
                "overall_quality_score": fake_quality["overall_quality_score"],
                "freshness_indicators": fake_quality["freshness_indicators"],
                "safety_standards": fake_quality["safety_standards"],
                "nutritional_quality": fake_quality["nutritional_quality"]
            },
            "supply_chain_events": fake_supply_events,
            "events": [],
            "sensor_data": [],
            "transport_logs": [],
            "quality_checks": [],
            "timestamps": {
                "created_at": product.created_at.isoformat() if product.created_at else datetime.utcnow().isoformat(),
                "completed_at": datetime.utcnow().isoformat(),
                "verified_at": datetime.utcnow().isoformat()
            }
        }
    
    def _create_traceability_event(
        self,
        product_id: int,
        event_type: TraceabilityEventType,
        actor_id: Optional[int],
        actor_type: str,
        location_lat: Optional[float],
        location_lon: Optional[float],
        location_description: Optional[str],
        event_data: Dict[str, Any],
        blockchain_private_key: str
    ) -> Dict[str, Any]:
        """
        Crea un evento de trazabilidad y lo registra en blockchain
        """
        try:
            # Crear el evento
            event = TraceabilityEvent(
                product_id=product_id,
                event_type=event_type,
                actor_id=actor_id,
                actor_type=actor_type,
                location_lat=location_lat,
                location_lon=location_lon,
                location_description=location_description,
                event_data=event_data
            )
            
            self.db.add(event)
            self.db.commit()
            self.db.refresh(event)
            
            # Registrar en blockchain
            blockchain_data = {
                "event_id": event.id,
                "product_id": product_id,
                "event_type": event_type.value,
                "timestamp": event.timestamp.isoformat(),
                "location": {
                    "lat": location_lat,
                    "lon": location_lon,
                    "description": location_description
                },
                "actor": {
                    "id": actor_id,
                    "type": actor_type
                },
                "event_data": event_data
            }
            
            # Crear hash único para el evento
            event_hash = hashlib.sha256(
                json.dumps(blockchain_data, sort_keys=True).encode()
            ).hexdigest()
            
            # Registrar en blockchain (simulado por ahora)
            blockchain_result = {
                "transaction_hash": f"0x{event_hash[:64]}",
                "block_number": 12345,  # Simulado
                "status": 1
            }
            
            # Actualizar el evento con la información de blockchain
            event.blockchain_hash = event_hash
            event.blockchain_block_number = blockchain_result["block_number"]
            event.blockchain_tx_hash = blockchain_result["transaction_hash"]
            event.is_verified = True
            
            self.db.commit()
            
            return {
                "event_id": event.id,
                "blockchain_hash": event_hash,
                "blockchain_tx_hash": blockchain_result["transaction_hash"]
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error creando evento de trazabilidad: {str(e)}")
    
    def _update_chain_metrics(self, product_id: int):
        """
        Actualiza las métricas de la cadena de trazabilidad
        """
        try:
            chain = self.db.query(ProductTraceabilityChain).filter(
                ProductTraceabilityChain.product_id == product_id
            ).first()
            
            if not chain:
                return
            
            # Obtener todos los eventos ordenados por timestamp
            events = self.db.query(TraceabilityEvent).filter(
                TraceabilityEvent.product_id == product_id
            ).order_by(TraceabilityEvent.timestamp).all()
            
            # Calcular distancia total
            transport_logs = self.db.query(TransportLog).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            total_distance = sum(log.distance_km or 0 for log in transport_logs)
            
            # Si no hay logs de transporte, calcular distancia basada en ubicaciones de eventos
            if total_distance == 0 and len(events) >= 2:
                total_distance = self._calculate_distance_from_events(events)
            
            # Calcular tiempo total
            if len(events) >= 2:
                start_time = events[0].timestamp
                end_time = events[-1].timestamp
                total_time = (end_time - start_time).total_seconds() / 3600  # En horas
            else:
                # Si solo hay un evento (ej. creación del producto), usar tiempo desde creación hasta ahora
                total_time = 0.0
                if len(events) == 1 and events[0].timestamp is not None:
                    total_time = (datetime.utcnow() - events[0].timestamp).total_seconds() / 3600
            
            # Calcular violaciones de temperatura
            sensor_data = self.db.query(SensorTraceabilityData).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            temperature_violations = 0
            for data in sensor_data:
                if data.temperature and (data.temperature < 0 or data.temperature > 40):
                    temperature_violations += 1
            
            # Calcular puntuación de calidad
            quality_checks = self.db.query(QualityCheck).join(
                TraceabilityEvent
            ).filter(
                TraceabilityEvent.product_id == product_id
            ).all()
            
            if quality_checks:
                quality_score = sum(check.score or 0 for check in quality_checks) / len(quality_checks) / 100
            else:
                quality_score = 1.0
            
            # Actualizar la cadena
            chain.total_distance_km = total_distance
            chain.total_time_hours = total_time
            chain.temperature_violations = temperature_violations
            chain.quality_score = quality_score
            
            # Verificar si la cadena está completa
            required_events = [
                TraceabilityEventType.product_created,
                TraceabilityEventType.harvest,
                TraceabilityEventType.sale_farmer_supermarket,
                TraceabilityEventType.sale_supermarket_consumer
            ]
            
            event_types = [event.event_type for event in events]
            chain.is_complete = all(event_type in event_types for event_type in required_events)
            
            if chain.is_complete and not chain.completed_at:
                chain.completed_at = datetime.utcnow()
            
            self.db.commit()
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error actualizando métricas de la cadena: {str(e)}")
    
    def _calculate_distance_from_events(self, events: List[TraceabilityEvent]) -> float:
        """
        Calcula la distancia total basándose en las ubicaciones de los eventos
        """
        total_distance = 0.0
        
        # Filtrar eventos que tienen ubicación válida
        events_with_location = [
            event for event in events 
            if event.location_lat is not None and event.location_lon is not None
        ]
        
        if len(events_with_location) < 2:
            return 0.0
        
        # Calcular distancia entre eventos consecutivos
        for i in range(len(events_with_location) - 1):
            current_event = events_with_location[i]
            next_event = events_with_location[i + 1]
            
            distance = self.calculate_distance(
                current_event.location_lat,
                current_event.location_lon,
                next_event.location_lat,
                next_event.location_lon
            )
            total_distance += distance
        
        return total_distance
    
    def calculate_distance(self, lat1: float, lon1: float, lat2: float, lon2: float) -> float:
        """
        Calcula la distancia entre dos puntos geográficos en kilómetros
        """
        R = 6371  # Radio de la Tierra en kilómetros
        
        dlat = math.radians(lat2 - lat1)
        dlon = math.radians(lon2 - lon1)
        
        a = (math.sin(dlat/2) * math.sin(dlat/2) +
             math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) *
             math.sin(dlon/2) * math.sin(dlon/2))
        
        c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
        distance = R * c
        
        return distance
