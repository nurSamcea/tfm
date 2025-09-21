from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime, ForeignKey, Enum, Text, JSON
from sqlalchemy.orm import relationship
from backend.app.database import Base
import enum
from datetime import datetime

class SensorTypeEnum(enum.Enum):
    temperature = "temperature"
    humidity = "humidity"
    gas = "gas"
    light = "light"
    shock = "shock"
    gps = "gps"
    soil_moisture = "soil_moisture"
    ph = "ph"

class SensorStatusEnum(enum.Enum):
    active = "active"
    inactive = "inactive"
    error = "error"
    maintenance = "maintenance"

class Sensor(Base):
    """Modelo de sensor/dispositivo IoT."""
    __tablename__ = "sensors"

    id = Column(Integer, primary_key=True)
    device_id = Column(String(100), unique=True, nullable=False)  # ID único del dispositivo
    name = Column(String(200), nullable=False)  # Nombre descriptivo
    sensor_type = Column(Enum(SensorTypeEnum), nullable=False)
    status = Column(Enum(SensorStatusEnum), default=SensorStatusEnum.active)
    
    # Ubicación y configuración
    zone_id = Column(Integer, ForeignKey("sensor_zones.id"))
    location_lat = Column(Float)  # Latitud
    location_lon = Column(Float)  # Longitud
    location_description = Column(Text)  # Descripción de la ubicación
    
    # Configuración del sensor
    min_threshold = Column(Float)  # Umbral mínimo
    max_threshold = Column(Float)  # Umbral máximo
    alert_enabled = Column(Boolean, default=True)
    reading_interval = Column(Integer, default=30)  # Intervalo en segundos
    
    # Metadatos
    firmware_version = Column(String(50))
    last_seen = Column(DateTime)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    # Configuración adicional (JSON)
    config = Column(JSON)
    
    # Relaciones
    zone = relationship("SensorZone", back_populates="sensors")
    readings = relationship("SensorReading", back_populates="sensor")

class SensorZone(Base):
    """Modelo de zona/área donde se ubican los sensores."""
    __tablename__ = "sensor_zones"

    id = Column(Integer, primary_key=True)
    name = Column(String(200), nullable=False)  # Nombre de la zona
    description = Column(Text)
    
    # Ubicación de la zona
    location_lat = Column(Float)
    location_lon = Column(Float)
    location_description = Column(Text)
    
    # Configuración de la zona
    farmer_id = Column(Integer, ForeignKey("users.id"), nullable=False)
    is_active = Column(Boolean, default=True)
    
    # Metadatos
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    # Relaciones
    farmer = relationship("User", back_populates="sensor_zones")
    sensors = relationship("Sensor", back_populates="zone")
