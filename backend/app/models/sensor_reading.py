from sqlalchemy import Column, Integer, Float, Text, Boolean, DateTime, ForeignKey, Enum, JSON
from sqlalchemy.orm import relationship
from backend.app.database import Base
import enum
from datetime import datetime

class SensorReading(Base):
    """Lectura de sensores ambientales y de localización."""
    __tablename__ = "sensor_readings"

    id = Column(Integer, primary_key=True)
    sensor_id = Column(Integer, ForeignKey("sensors.id"), nullable=False)
    product_id = Column(Integer, ForeignKey("products.id"))  # Mantener para compatibilidad
    
    # Valores de sensores
    temperature = Column(Float)
    humidity = Column(Float)
    gas_level = Column(Float)
    light_level = Column(Float)
    shock_detected = Column(Boolean)
    soil_moisture = Column(Float)
    ph_level = Column(Float)
    
    # Metadatos de la lectura
    created_at = Column(DateTime, default=datetime.utcnow)
    source_device = Column(Text)  # ID del sensor/dispositivo (mantener para compatibilidad)
    reading_quality = Column(Float)  # Calidad de la señal (0-1)
    is_processed = Column(Boolean, default=False)  # Si fue procesado por edge computing
    
    # Datos adicionales (JSON)
    extra_data = Column(JSON)

    # Relaciones
    sensor = relationship("Sensor", back_populates="readings")
    product = relationship("Product", back_populates="sensor_readings")
