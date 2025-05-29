from sqlalchemy import Column, Integer, Float, Text, Boolean, DateTime, ForeignKey
from backend.app.database import Base

class SensorReading(Base):
    __tablename__ = "sensor_readings"

    id = Column(Integer, primary_key=True)
    product_id = Column(Integer, ForeignKey("products.id"))
    temperature = Column(Float)
    humidity = Column(Float)
    gas_level = Column(Float)
    light_level = Column(Float)
    shock_detected = Column(Boolean)
    created_at = Column(DateTime)
    source_device = Column(Text)
