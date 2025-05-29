from sqlalchemy import Column, Integer, Text, DateTime, JSON, ForeignKey
from backend.app.database import Base

class QR(Base):
    __tablename__ = "qrs"

    id = Column(Integer, primary_key=True)
    product_id = Column(Integer, ForeignKey("products.id"))
    qr_hash = Column(Text)
    created_at = Column(DateTime)
    qr_metadata = Column(JSON)
