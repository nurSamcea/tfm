from sqlalchemy import Column, Integer, Float, Text, Boolean, DateTime, ForeignKey, Enum
from sqlalchemy.orm import relationship
from backend.app.database import Base
import enum

class SensorTypeEnum(enum.Enum):
    temperature = "temperature"
    humidity = "humidity"
    gas = "gas"
    light = "light"
    shock = "shock"
    gps = "gps"

class SensorReading(Base):
    """Lectura de sensores ambientales y de localización."""
    __tablename__ = "sensor_readings"

    id = Column(Integer, primary_key=True)
    product_id = Column(Integer, ForeignKey("products.id"))
    sensor_type = Column(Enum(SensorTypeEnum), nullable=True)
    temperature = Column(Float)
    humidity = Column(Float)
    gas_level = Column(Float)
    light_level = Column(Float)
    shock_detected = Column(Boolean)
    created_at = Column(DateTime)
    source_device = Column(Text)  # ID del sensor/dispositivo

    # Relaciones
    product = relationship("Product", back_populates="sensor_readings")
