from sqlalchemy import Column, Integer, Text, DateTime, ForeignKey, DECIMAL, Enum, JSON, Boolean, Float
from sqlalchemy.orm import relationship
from backend.app.database import Base
from datetime import datetime
import enum

class TraceabilityEventType(enum.Enum):
    """Tipos de eventos en la cadena de trazabilidad"""
    product_created = "product_created"  # Producto creado por el agricultor
    sensor_reading = "sensor_reading"    # Lectura de sensor IoT
    harvest = "harvest"                  # Cosecha del producto
    packaging = "packaging"              # Empaquetado
    transport_start = "transport_start"  # Inicio del transporte
    transport_checkpoint = "transport_checkpoint"  # Punto de control en transporte
    transport_end = "transport_end"      # Fin del transporte
    storage = "storage"                  # Almacenamiento
    sale_farmer_supermarket = "sale_farmer_supermarket"  # Venta agricultor a supermercado
    sale_supermarket_consumer = "sale_supermarket_consumer"  # Venta supermercado a consumidor
    delivery = "delivery"                # Entrega final
    quality_check = "quality_check"      # Control de calidad
    certification = "certification"      # Certificación (eco, etc.)

class TraceabilityEvent(Base):
    """Evento individual en la cadena de trazabilidad"""
    __tablename__ = "traceability_events"

    id = Column(Integer, primary_key=True)
    product_id = Column(Integer, ForeignKey("products.id"), nullable=False)
    event_type = Column(Enum(TraceabilityEventType), nullable=False)
    
    # Información del evento
    timestamp = Column(DateTime, default=datetime.utcnow, nullable=False)
    location_lat = Column(Float)  # Latitud del evento
    location_lon = Column(Float)  # Longitud del evento
    location_description = Column(Text)  # Descripción de la ubicación
    
    # Participantes en el evento
    actor_id = Column(Integer, ForeignKey("users.id"))  # Quien realizó el evento
    actor_type = Column(Text)  # Tipo de actor: farmer, supermarket, consumer, system
    
    # Datos específicos del evento
    event_data = Column(JSON)  # Datos específicos según el tipo de evento
    
    # Información de blockchain
    blockchain_hash = Column(Text)  # Hash de la transacción en blockchain
    blockchain_block_number = Column(Integer)  # Número de bloque
    blockchain_tx_hash = Column(Text)  # Hash de la transacción
    
    # Metadatos
    created_at = Column(DateTime, default=datetime.utcnow)
    is_verified = Column(Boolean, default=False)  # Si el evento está verificado
    
    # Relaciones
    product = relationship("Product", back_populates="traceability_events")
    actor = relationship("User", foreign_keys=[actor_id])

class ProductTraceabilityChain(Base):
    """Cadena completa de trazabilidad de un producto"""
    __tablename__ = "product_traceability_chains"

    id = Column(Integer, primary_key=True)
    product_id = Column(Integer, ForeignKey("products.id"), nullable=False, unique=True)
    
    # Información del producto
    product_name = Column(Text, nullable=False)
    product_category = Column(Text)
    is_eco = Column(Boolean, default=False)
    
    # Información del productor original
    original_producer_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    original_producer_name = Column(Text, nullable=False)
    original_producer_location_lat = Column(Float)
    original_producer_location_lon = Column(Float)
    
    # Estado de la cadena
    is_complete = Column(Boolean, default=False)  # Si la cadena está completa
    is_verified = Column(Boolean, default=False)  # Si toda la cadena está verificada
    
    # Métricas de la cadena
    total_distance_km = Column(Float, default=0.0)  # Distancia total recorrida
    total_time_hours = Column(Float, default=0.0)   # Tiempo total en la cadena
    temperature_violations = Column(Integer, default=0)  # Violaciones de temperatura
    quality_score = Column(Float, default=1.0)  # Puntuación de calidad (0-1)
    
    # Timestamps
    created_at = Column(DateTime, default=datetime.utcnow)
    completed_at = Column(DateTime)
    verified_at = Column(DateTime)
    
    # Relaciones
    product = relationship("Product", back_populates="traceability_chain")
    original_producer = relationship("User", foreign_keys=[original_producer_id])

class SensorTraceabilityData(Base):
    """Datos de sensores asociados a la trazabilidad"""
    __tablename__ = "sensor_traceability_data"

    id = Column(Integer, primary_key=True)
    traceability_event_id = Column(Integer, ForeignKey("traceability_events.id"), nullable=False)
    sensor_id = Column(Integer, ForeignKey("sensors.id"), nullable=False)
    
    # Datos del sensor
    temperature = Column(Float)
    humidity = Column(Float)
    gas_level = Column(Float)
    light_level = Column(Float)
    shock_detected = Column(Boolean)
    soil_moisture = Column(Float)
    ph_level = Column(Float)
    
    # Metadatos
    reading_quality = Column(Float, default=1.0)  # Calidad de la lectura (0-1)
    is_processed = Column(Boolean, default=False)
    extra_data = Column(JSON)
    
    # Relaciones
    traceability_event = relationship("TraceabilityEvent")
    sensor = relationship("Sensor")

class TransportLog(Base):
    """Registro detallado del transporte"""
    __tablename__ = "transport_logs"

    id = Column(Integer, primary_key=True)
    traceability_event_id = Column(Integer, ForeignKey("traceability_events.id"), nullable=False)
    
    # Información del transporte
    transport_type = Column(Text)  # truck, van, bike, etc.
    driver_id = Column(Integer, ForeignKey("users.id"))
    vehicle_id = Column(Text)  # ID del vehículo
    
    # Rutas y distancias
    start_location_lat = Column(Float)
    start_location_lon = Column(Float)
    end_location_lat = Column(Float)
    end_location_lon = Column(Float)
    distance_km = Column(Float)
    estimated_time_hours = Column(Float)
    actual_time_hours = Column(Float)
    
    # Condiciones de transporte
    temperature_min = Column(Float)
    temperature_max = Column(Float)
    humidity_min = Column(Float)
    humidity_max = Column(Float)
    
    # Metadatos
    created_at = Column(DateTime, default=datetime.utcnow)
    
    # Relaciones
    traceability_event = relationship("TraceabilityEvent")
    driver = relationship("User", foreign_keys=[driver_id])

class QualityCheck(Base):
    """Registro de controles de calidad"""
    __tablename__ = "quality_checks"

    id = Column(Integer, primary_key=True)
    traceability_event_id = Column(Integer, ForeignKey("traceability_events.id"), nullable=False)
    
    # Información del control
    check_type = Column(Text)  # visual, temperature, weight, etc.
    inspector_id = Column(Integer, ForeignKey("users.id"))
    
    # Resultados
    passed = Column(Boolean, nullable=False)
    score = Column(Float)  # Puntuación del control (0-100)
    notes = Column(Text)
    
    # Datos específicos del control
    check_data = Column(JSON)
    
    # Metadatos
    created_at = Column(DateTime, default=datetime.utcnow)
    
    # Relaciones
    traceability_event = relationship("TraceabilityEvent")
    inspector = relationship("User", foreign_keys=[inspector_id])
