from sqlalchemy import Column, Integer, Float, Text, DateTime, ForeignKey, DECIMAL
from backend.app.database import Base
from datetime import datetime

class ShoppingList(Base):
    __tablename__ = "shopping_lists"

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    created_at = Column(DateTime, default=datetime.utcnow)
    total_price = Column(DECIMAL)
    currency = Column(Text)
    status = Column(Text)
