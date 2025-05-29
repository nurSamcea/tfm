from sqlalchemy import Column, Integer, Float, Text, Boolean, Date, JSON, ForeignKey, DateTime, DECIMAL
from backend.app.database import Base
from datetime import datetime

class Product(Base):
    __tablename__ = "products"

    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    description = Column(Text)
    price = Column(DECIMAL)
    currency = Column(Text)
    unit = Column(Text)
    category = Column(Text)
    nutritional_info = Column(JSON)
    stock_available = Column(Float)
    expiration_date = Column(Date)
    is_eco = Column(Boolean)
    image_url = Column(Text)
    provider_id = Column(Integer, ForeignKey("users.id"))
    created_at = Column(DateTime, default=datetime.utcnow)
