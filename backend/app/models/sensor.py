from sqlalchemy import Column, Integer, Text, DECIMAL, ForeignKey, TIMESTAMP
from backend.app.database import Base

class SensorReading(Base):
    __tablename__ = "sensor_readings"
    id = Column(Integer, primary_key=True)
    device_id = Column(Text)
    product_id = Column(Integer, ForeignKey("products.id"))
    temperature = Column(DECIMAL)
    humidity = Column(DECIMAL)
    gas_level = Column(DECIMAL)
    voltage = Column(DECIMAL)
    timestamp = Column(TIMESTAMP)
