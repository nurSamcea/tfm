from sqlalchemy import Column, Integer, Float, Text, Boolean, Date, JSON, ForeignKey, DateTime, DECIMAL, Enum
from sqlalchemy.orm import relationship
from backend.app.database import Base
from datetime import datetime
import enum

class ProductCategory(enum.Enum):
    """Categorías predefinidas para productos alimentarios."""
    verduras = "verduras"
    frutas = "frutas"
    cereales = "cereales"
    legumbres = "legumbres"
    frutos_secos = "frutos_secos"
    lacteos = "lacteos"
    carnes = "carnes"
    pescados = "pescados"
    huevos = "huevos"
    hierbas = "hierbas"
    especias = "especias"
    otros = "otros"

class Product(Base):
    """Modelo de producto fresco, con información de trazabilidad y sostenibilidad."""
    __tablename__ = "products"

    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    description = Column(Text)
    price = Column(DECIMAL)
    currency = Column(Text)
    unit = Column(Text)
    category = Column(Enum(ProductCategory))
    stock_available = Column(Float)
    expiration_date = Column(Date)
    is_eco = Column(Boolean)
    image_url = Column(Text)
    provider_id = Column(Integer, ForeignKey("users.id"))
    is_hidden = Column(Boolean, default=False)  # Nuevo campo para ocultar productos
    certifications = Column(JSON)  # Ejemplo: eco, local, etc.
    created_at = Column(DateTime, default=datetime.utcnow)

    # Relaciones
    provider = relationship("User", back_populates="products")
    sensor_readings = relationship("SensorReading", back_populates="product")
    qrs = relationship("QR", back_populates="product")
    traceability_events = relationship("TraceabilityEvent", back_populates="product")
    traceability_chain = relationship("ProductTraceabilityChain", back_populates="product", uselist=False)
