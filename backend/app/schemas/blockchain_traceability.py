from typing import List, Dict, Optional, Union
from pydantic import BaseModel, Field
from datetime import datetime
from enum import Enum

class TraceabilityEventTypeEnum(str, Enum):
    product_created = "product_created"
    sensor_reading = "sensor_reading"
    harvest = "harvest"
    packaging = "packaging"
    transport_start = "transport_start"
    transport_checkpoint = "transport_checkpoint"
    transport_end = "transport_end"
    storage = "storage"
    sale_farmer_supermarket = "sale_farmer_supermarket"
    sale_supermarket_consumer = "sale_supermarket_consumer"
    delivery = "delivery"
    quality_check = "quality_check"
    certification = "certification"

class TraceabilityEventBase(BaseModel):
    product_id: int
    event_type: TraceabilityEventTypeEnum
    timestamp: datetime = Field(default_factory=datetime.utcnow)
    location_lat: Optional[float] = None
    location_lon: Optional[float] = None
    location_description: Optional[str] = None
    actor_id: Optional[int] = None
    actor_type: Optional[str] = None
    event_data: Optional[Dict] = None

class TraceabilityEventCreate(TraceabilityEventBase):
    pass

class TraceabilityEventUpdate(BaseModel):
    location_lat: Optional[float] = None
    location_lon: Optional[float] = None
    location_description: Optional[str] = None
    event_data: Optional[Dict] = None
    is_verified: Optional[bool] = None

class TraceabilityEventRead(TraceabilityEventBase):
    id: int
    blockchain_hash: Optional[str] = None
    blockchain_block_number: Optional[int] = None
    blockchain_tx_hash: Optional[str] = None
    created_at: datetime
    is_verified: bool = False

    class Config:
        from_attributes = True

class ProductTraceabilityChainBase(BaseModel):
    product_id: int
    product_name: str
    product_category: Optional[str] = None
    is_eco: bool = False
    original_producer_id: int
    original_producer_name: str
    original_producer_location_lat: Optional[float] = None
    original_producer_location_lon: Optional[float] = None

class ProductTraceabilityChainCreate(ProductTraceabilityChainBase):
    pass

class ProductTraceabilityChainUpdate(BaseModel):
    is_complete: Optional[bool] = None
    is_verified: Optional[bool] = None
    total_distance_km: Optional[float] = None
    total_time_hours: Optional[float] = None
    temperature_violations: Optional[int] = None
    quality_score: Optional[float] = None

class ProductTraceabilityChainRead(ProductTraceabilityChainBase):
    id: int
    is_complete: bool = False
    is_verified: bool = False
    total_distance_km: float = 0.0
    total_time_hours: float = 0.0
    temperature_violations: int = 0
    quality_score: float = 1.0
    created_at: datetime
    completed_at: Optional[datetime] = None
    verified_at: Optional[datetime] = None

    class Config:
        from_attributes = True

class SensorTraceabilityDataBase(BaseModel):
    traceability_event_id: int
    sensor_id: int
    temperature: Optional[float] = None
    humidity: Optional[float] = None
    gas_level: Optional[float] = None
    light_level: Optional[float] = None
    shock_detected: Optional[bool] = None
    soil_moisture: Optional[float] = None
    ph_level: Optional[float] = None
    reading_quality: float = 1.0
    is_processed: bool = False
    extra_data: Optional[Dict] = None

class SensorTraceabilityDataCreate(SensorTraceabilityDataBase):
    pass

class SensorTraceabilityDataRead(SensorTraceabilityDataBase):
    id: int

    class Config:
        from_attributes = True

class TransportLogBase(BaseModel):
    traceability_event_id: int
    transport_type: Optional[str] = None
    driver_id: Optional[int] = None
    vehicle_id: Optional[str] = None
    start_location_lat: Optional[float] = None
    start_location_lon: Optional[float] = None
    end_location_lat: Optional[float] = None
    end_location_lon: Optional[float] = None
    distance_km: Optional[float] = None
    estimated_time_hours: Optional[float] = None
    actual_time_hours: Optional[float] = None
    temperature_min: Optional[float] = None
    temperature_max: Optional[float] = None
    humidity_min: Optional[float] = None
    humidity_max: Optional[float] = None

class TransportLogCreate(TransportLogBase):
    pass

class TransportLogRead(TransportLogBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True

class QualityCheckBase(BaseModel):
    traceability_event_id: int
    check_type: Optional[str] = None
    inspector_id: Optional[int] = None
    passed: bool
    score: Optional[float] = None
    notes: Optional[str] = None
    check_data: Optional[Dict] = None

class QualityCheckCreate(QualityCheckBase):
    pass

class QualityCheckRead(QualityCheckBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True

class ProductTraceabilitySummary(BaseModel):
    """Resumen completo de la trazabilidad de un producto"""
    product_id: int
    product_name: str
    product_category: Optional[str] = None
    is_eco: bool = False
    
    # Información del productor
    original_producer: Dict
    
    # Estado de la cadena
    is_complete: bool = False
    is_verified: bool = False
    
    # Métricas
    total_distance_km: float = 0.0
    total_time_hours: float = 0.0
    temperature_violations: int = 0
    quality_score: float = 1.0
    
    # Eventos de la cadena
    events: List[TraceabilityEventRead]
    
    # Timestamps
    created_at: datetime
    completed_at: Optional[datetime] = None
    verified_at: Optional[datetime] = None

class TraceabilityVerificationRequest(BaseModel):
    """Solicitud de verificación de trazabilidad"""
    product_id: int
    verification_type: str = "full_chain"  # full_chain, blockchain_only, sensors_only
    include_sensor_data: bool = True
    include_transport_data: bool = True
    include_quality_checks: bool = True

class TraceabilityVerificationResponse(BaseModel):
    """Respuesta de verificación de trazabilidad"""
    is_authentic: bool
    verification_score: float  # 0-1
    verification_details: Dict
    blockchain_verified: bool
    sensor_data_verified: bool
    transport_data_verified: bool
    quality_checks_passed: bool
    issues_found: List[str]
    recommendations: List[str]
    verification_timestamp: datetime = Field(default_factory=datetime.utcnow)
