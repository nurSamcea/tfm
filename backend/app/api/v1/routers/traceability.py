from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List, Dict, Any, Optional
from datetime import datetime

from backend.app import schemas, database, models
from backend.app.database import get_db
from backend.app.algorithms.traceability_service import TraceabilityService
from backend.app.models.blockchain_traceability import TraceabilityEventType
from backend.app.api.v1.routers.dependencies import get_current_user
from backend.app.models.user import User

router = APIRouter(prefix="/traceability", tags=["Trazabilidad Blockchain"])

@router.post("/products/{product_id}/create-chain")
def create_product_traceability_chain(
    product_id: int,
    blockchain_private_key: str = "",
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Crea una nueva cadena de trazabilidad para un producto
    """
    try:
        traceability_service = TraceabilityService(db)
        
        result = traceability_service.create_product_traceability_chain(
            product_id=product_id,
            producer_id=current_user.id,
            blockchain_private_key=blockchain_private_key
        )
        
        return {
            "success": True,
            "message": "Cadena de trazabilidad creada exitosamente",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error creando cadena de trazabilidad: {str(e)}"
        )

@router.post("/products/{product_id}/sensor-reading")
def add_sensor_reading_to_traceability(
    product_id: int,
    sensor_reading_data: schemas.SensorTraceabilityDataCreate,
    blockchain_private_key: str = "",
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Añade una lectura de sensor a la trazabilidad del producto
    """
    try:
        traceability_service = TraceabilityService(db)
        
        result = traceability_service.add_sensor_reading_to_traceability(
            product_id=product_id,
            sensor_id=sensor_reading_data.sensor_id,
            sensor_reading_data=sensor_reading_data.dict(),
            blockchain_private_key=blockchain_private_key
        )
        
        return {
            "success": True,
            "message": "Lectura de sensor añadida a la trazabilidad",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error añadiendo lectura de sensor: {str(e)}"
        )

@router.post("/products/{product_id}/transport-event")
def add_transport_event(
    product_id: int,
    event_type: str,
    transport_data: schemas.TransportLogCreate,
    blockchain_private_key: str = "",
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Añade un evento de transporte a la trazabilidad
    """
    try:
        # Validar el tipo de evento
        if event_type not in ["transport_start", "transport_checkpoint", "transport_end"]:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Tipo de evento de transporte inválido"
            )
        
        traceability_service = TraceabilityService(db)
        
        result = traceability_service.add_transport_event(
            product_id=product_id,
            event_type=TraceabilityEventType(event_type),
            transport_data=transport_data.dict(),
            actor_id=current_user.id,
            blockchain_private_key=blockchain_private_key
        )
        
        return {
            "success": True,
            "message": "Evento de transporte añadido a la trazabilidad",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error añadiendo evento de transporte: {str(e)}"
        )

@router.post("/transactions/{transaction_id}/add-to-traceability")
def add_transaction_to_traceability(
    transaction_id: int,
    blockchain_private_key: str = "",
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Añade una transacción a la trazabilidad
    """
    try:
        traceability_service = TraceabilityService(db)
        
        result = traceability_service.add_transaction_to_traceability(
            transaction_id=transaction_id,
            blockchain_private_key=blockchain_private_key
        )
        
        return {
            "success": True,
            "message": "Transacción añadida a la trazabilidad",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error añadiendo transacción a la trazabilidad: {str(e)}"
        )

@router.post("/products/{product_id}/quality-check")
def add_quality_check(
    product_id: int,
    check_data: schemas.QualityCheckCreate,
    blockchain_private_key: str = "",
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Añade un control de calidad a la trazabilidad
    """
    try:
        traceability_service = TraceabilityService(db)
        
        result = traceability_service.add_quality_check(
            product_id=product_id,
            check_data=check_data.dict(),
            inspector_id=current_user.id,
            blockchain_private_key=blockchain_private_key
        )
        
        return {
            "success": True,
            "message": "Control de calidad añadido a la trazabilidad",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error añadiendo control de calidad: {str(e)}"
        )

@router.get("/products/{product_id}/summary")
def get_product_traceability_summary(
    product_id: int,
    consumer_lat: Optional[float] = None,
    consumer_lon: Optional[float] = None,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene un resumen completo de la trazabilidad de un producto con datos falsificados
    """
    try:
        traceability_service = TraceabilityService(db)
        
        # Preparar ubicación del consumidor
        consumer_location = None
        if consumer_lat is not None and consumer_lon is not None:
            consumer_location = (consumer_lat, consumer_lon)
        
        summary = traceability_service.get_product_traceability_summary(
            product_id=product_id,
            consumer_location=consumer_location
        )
        
        return {
            "success": True,
            "data": summary
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo resumen de trazabilidad: {str(e)}"
        )

@router.post("/products/{product_id}/verify")
def verify_traceability_authenticity(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Verifica la autenticidad de la trazabilidad de un producto
    """
    try:
        traceability_service = TraceabilityService(db)
        
        verification_result = traceability_service.verify_traceability_authenticity(product_id)
        
        return {
            "success": True,
            "data": verification_result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error verificando autenticidad: {str(e)}"
        )

@router.get("/products/{product_id}/blockchain-data")
def get_product_blockchain_data(
    product_id: int,
    consumer_lat: Optional[float] = None,
    consumer_lon: Optional[float] = None,
    db: Session = Depends(get_db)
):
    """
    Obtiene datos falsificados de blockchain para un producto específico
    Incluye distancia al farmer, sostenibilidad, calidad y tiempo transcurrido
    """
    try:
        from backend.app.algorithms.fake_blockchain_data import FakeBlockchainDataGenerator
        
        # Obtener el producto
        product = db.query(models.Product).filter(models.Product.id == product_id).first()
        if not product:
            raise HTTPException(status_code=404, detail="Producto no encontrado")
        
        # Obtener información del productor
        producer = db.query(models.User).filter(models.User.id == product.provider_id).first()
        farmer_location = None
        producer_name = "Productor Desconocido"
        
        if producer:
            farmer_location = (producer.location_lat, producer.location_lon) if producer.location_lat else None
            producer_name = producer.name
        
        # Preparar ubicación del consumidor
        consumer_location = None
        if consumer_lat is not None and consumer_lon is not None:
            consumer_location = (consumer_lat, consumer_lon)
        
        # Generar datos falsificados
        fake_generator = FakeBlockchainDataGenerator()
        
        fake_product_data = fake_generator.generate_fake_product_data(
            product_category=product.category.value if product.category else "default",
            product_id=product_id,
            consumer_location=consumer_location,
            farmer_location=farmer_location,
            publication_date=product.created_at
        )
        
        fake_sustainability = fake_generator.generate_fake_sustainability_metrics(
            product_id=product_id,
            product_category=product.category.value if product.category else "default"
        )
        
        fake_quality = fake_generator.generate_fake_quality_metrics(
            product_id=product_id,
            product_category=product.category.value if product.category else "default"
        )
        
        fake_supply_events = fake_generator.generate_fake_supply_chain_events(
            product_id=product_id,
            product_category=product.category.value if product.category else "default",
            farmer_location=farmer_location or (40.4168, -3.7038),  # Madrid por defecto
            consumer_location=consumer_location or (40.4168, -3.7038)
        )
        
        return {
            "success": True,
            "product_id": product_id,
            "product_name": product.name,
            "product_category": product.category.value if product.category else "default",
            "producer_info": {
                "id": product.provider_id,
                "name": producer_name,
                "location": {
                    "lat": farmer_location[0] if farmer_location else None,
                    "lon": farmer_location[1] if farmer_location else None
                }
            },
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
                "environmental_details": fake_sustainability["environmental_impact"],
                "social_impact": fake_sustainability["social_impact"],
                "economic_impact": fake_sustainability["economic_impact"],
                "certifications": fake_sustainability["certifications"],
                "eco_friendly_practices": fake_sustainability["eco_friendly_practices"]
            },
            "quality_details": {
                "overall_quality_score": fake_quality["overall_quality_score"],
                "freshness_indicators": fake_quality["freshness_indicators"],
                "safety_standards": fake_quality["safety_standards"],
                "nutritional_quality": fake_quality["nutritional_quality"],
                "sensory_quality": fake_quality["sensory_quality"]
            },
            "supply_chain_events": fake_supply_events,
            "blockchain_info": {
                "is_verified": True,
                "blockchain_hash": f"0x{hash(str(product_id))[:64]}",
                "verification_timestamp": datetime.utcnow().isoformat()
            }
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error generando datos de blockchain: {str(e)}"
        )

@router.get("/products/{product_id}/basic-info")
def get_product_basic_blockchain_info(
    product_id: int,
    consumer_lat: Optional[float] = None,
    consumer_lon: Optional[float] = None,
    db: Session = Depends(get_db)
):
    """
    Obtiene información básica falsificada de blockchain para un producto
    Incluye solo los datos más importantes: distancia, sostenibilidad, calidad y tiempo
    """
    try:
        from backend.app.algorithms.fake_blockchain_data import FakeBlockchainDataGenerator
        
        # Obtener el producto
        product = db.query(models.Product).filter(models.Product.id == product_id).first()
        if not product:
            raise HTTPException(status_code=404, detail="Producto no encontrado")
        
        # Obtener información del productor
        producer = db.query(models.User).filter(models.User.id == product.provider_id).first()
        farmer_location = None
        producer_name = "Productor Desconocido"
        
        if producer:
            farmer_location = (producer.location_lat, producer.location_lon) if producer.location_lat else None
            producer_name = producer.name
        
        # Preparar ubicación del consumidor
        consumer_location = None
        if consumer_lat is not None and consumer_lon is not None:
            consumer_location = (consumer_lat, consumer_lon)
        
        # Generar datos falsificados básicos
        fake_generator = FakeBlockchainDataGenerator()
        
        fake_product_data = fake_generator.generate_fake_product_data(
            product_category=product.category.value if product.category else "default",
            product_id=product_id,
            consumer_location=consumer_location,
            farmer_location=farmer_location,
            publication_date=product.created_at
        )
        
        return {
            "product_id": product_id,
            "product_name": product.name,
            "producer_name": producer_name,
            "distance_to_farmer_km": fake_product_data.distance_to_consumer_km,
            "time_since_publication_hours": fake_product_data.time_since_publication_hours,
            "sustainability_score": fake_product_data.sustainability_score,
            "quality_score": fake_product_data.quality_score,
            "freshness_score": fake_product_data.freshness_score,
            "carbon_footprint_kg": fake_product_data.carbon_footprint_kg,
            "local_sourcing_score": fake_product_data.local_sourcing_score,
            "blockchain_verified": True,
            "last_updated": datetime.utcnow().isoformat()
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error generando información básica: {str(e)}"
        )

@router.get("/products/{product_id}/events")
def get_product_traceability_events(
    product_id: int,
    event_type: Optional[str] = None,
    limit: int = 100,
    offset: int = 0,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene los eventos de trazabilidad de un producto
    """
    try:
        query = db.query(models.TraceabilityEvent).filter(
            models.TraceabilityEvent.product_id == product_id
        )
        
        if event_type:
            query = query.filter(models.TraceabilityEvent.event_type == event_type)
        
        events = query.order_by(models.TraceabilityEvent.timestamp.desc()).offset(offset).limit(limit).all()
        
        return {
            "success": True,
            "data": [
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
            ]
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo eventos de trazabilidad: {str(e)}"
        )

@router.get("/products/{product_id}/sensor-data")
def get_product_sensor_data(
    product_id: int,
    sensor_id: Optional[int] = None,
    limit: int = 100,
    offset: int = 0,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene los datos de sensores asociados a la trazabilidad de un producto
    """
    try:
        query = db.query(models.SensorTraceabilityData).join(
            models.TraceabilityEvent
        ).filter(
            models.TraceabilityEvent.product_id == product_id
        )
        
        if sensor_id:
            query = query.filter(models.SensorTraceabilityData.sensor_id == sensor_id)
        
        sensor_data = query.order_by(models.SensorTraceabilityData.id.desc()).offset(offset).limit(limit).all()
        
        return {
            "success": True,
            "data": [
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
                    "is_processed": data.is_processed,
                    "extra_data": data.extra_data
                }
                for data in sensor_data
            ]
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo datos de sensores: {str(e)}"
        )

@router.get("/products/{product_id}/transport-logs")
def get_product_transport_logs(
    product_id: int,
    limit: int = 100,
    offset: int = 0,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene los logs de transporte de un producto
    """
    try:
        transport_logs = db.query(models.TransportLog).join(
            models.TraceabilityEvent
        ).filter(
            models.TraceabilityEvent.product_id == product_id
        ).order_by(models.TransportLog.id.desc()).offset(offset).limit(limit).all()
        
        return {
            "success": True,
            "data": [
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
                    },
                    "created_at": log.created_at.isoformat()
                }
                for log in transport_logs
            ]
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo logs de transporte: {str(e)}"
        )

@router.get("/products/{product_id}/quality-checks")
def get_product_quality_checks(
    product_id: int,
    limit: int = 100,
    offset: int = 0,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene los controles de calidad de un producto
    """
    try:
        quality_checks = db.query(models.QualityCheck).join(
            models.TraceabilityEvent
        ).filter(
            models.TraceabilityEvent.product_id == product_id
        ).order_by(models.QualityCheck.id.desc()).offset(offset).limit(limit).all()
        
        return {
            "success": True,
            "data": [
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
            ]
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo controles de calidad: {str(e)}"
        )

@router.get("/products/{product_id}/chain-status")
def get_product_chain_status(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene el estado de la cadena de trazabilidad de un producto
    """
    try:
        chain = db.query(models.ProductTraceabilityChain).filter(
            models.ProductTraceabilityChain.product_id == product_id
        ).first()
        
        if not chain:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="No se encontró cadena de trazabilidad para el producto"
            )
        
        return {
            "success": True,
            "data": {
                "product_id": chain.product_id,
                "product_name": chain.product_name,
                "is_complete": chain.is_complete,
                "is_verified": chain.is_verified,
                "total_distance_km": chain.total_distance_km,
                "total_time_hours": chain.total_time_hours,
                "temperature_violations": chain.temperature_violations,
                "quality_score": chain.quality_score,
                "created_at": chain.created_at.isoformat(),
                "completed_at": chain.completed_at.isoformat() if chain.completed_at else None,
                "verified_at": chain.verified_at.isoformat() if chain.verified_at else None
            }
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo estado de la cadena: {str(e)}"
        )

@router.post("/products/{product_id}/complete-chain")
def complete_traceability_chain(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Marca la cadena de trazabilidad como completa
    """
    try:
        chain = db.query(models.ProductTraceabilityChain).filter(
            models.ProductTraceabilityChain.product_id == product_id
        ).first()
        
        if not chain:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="No se encontró cadena de trazabilidad para el producto"
            )
        
        chain.is_complete = True
        chain.completed_at = datetime.utcnow()
        
        db.commit()
        
        return {
            "success": True,
            "message": "Cadena de trazabilidad marcada como completa"
        }
        
    except Exception as e:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error completando cadena de trazabilidad: {str(e)}"
        )

@router.post("/products/{product_id}/verify-chain")
def verify_traceability_chain(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Marca la cadena de trazabilidad como verificada
    """
    try:
        chain = db.query(models.ProductTraceabilityChain).filter(
            models.ProductTraceabilityChain.product_id == product_id
        ).first()
        
        if not chain:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="No se encontró cadena de trazabilidad para el producto"
            )
        
        chain.is_verified = True
        chain.verified_at = datetime.utcnow()
        
        db.commit()
        
        return {
            "success": True,
            "message": "Cadena de trazabilidad marcada como verificada"
        }
        
    except Exception as e:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error verificando cadena de trazabilidad: {str(e)}"
        )