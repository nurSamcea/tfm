from sqlalchemy import Column, Integer, Float, Text, Boolean, Date, JSON, ForeignKey, DateTime, DECIMAL
from sqlalchemy.orm import relationship
from backend.app.database import Base
from datetime import datetime

class Product(Base):
    """Modelo de producto fresco, con información de trazabilidad y sostenibilidad."""
    __tablename__ = "products"

    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    description = Column(Text)
    price = Column(DECIMAL)
    currency = Column(Text)
    unit = Column(Text)
    category = Column(Text)
    stock_available = Column(Float)
    expiration_date = Column(Date)
    is_eco = Column(Boolean)
    image_url = Column(Text)
    provider_id = Column(Integer, ForeignKey("users.id"))
    certifications = Column(JSON)  # Ejemplo: eco, local, etc.
    created_at = Column(DateTime, default=datetime.utcnow)

    # Relaciones
    provider = relationship("User", back_populates="products")
    sensor_readings = relationship("SensorReading", back_populates="product")
    qrs = relationship("QR", back_populates="product")
