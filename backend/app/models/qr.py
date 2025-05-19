from sqlalchemy import Column, Integer, Text, Date, ForeignKey, JSON, TIMESTAMP
from backend.app.database import Base

class ProductQR(Base):
    __tablename__ = "product_qrs"
    id = Column(Integer, primary_key=True)
    product_id = Column(Integer, ForeignKey("products.id"))
    blockchain_hash = Column(Text, nullable=False)
    qr_code = Column(Text, unique=True, nullable=False)
    origin = Column(Text)
    collected_at = Column(Date)
    certification = Column(Text)
    temperature_log = Column(JSON)
    created_at = Column(TIMESTAMP)
