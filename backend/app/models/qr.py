from sqlalchemy import Column, Integer, Text, DateTime, JSON, ForeignKey
from sqlalchemy.orm import relationship
from backend.app.database import Base

class QR(Base):
    """Modelo para códigos QR asociados a productos para trazabilidad y auditoría."""
    __tablename__ = "qrs"

    id = Column(Integer, primary_key=True)
    product_id = Column(Integer, ForeignKey("products.id"))
    qr_hash = Column(Text)
    created_at = Column(DateTime)
    qr_metadata = Column(JSON)
    qr_type = Column(Text, nullable=True)  # Opcional: tipo de QR

    # Relaciones
    product = relationship("Product", back_populates="qrs")
