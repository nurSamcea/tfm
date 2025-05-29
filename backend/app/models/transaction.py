from sqlalchemy import Column, Integer, Text, DateTime, ForeignKey, DECIMAL
from backend.app.database import Base

class Transaction(Base):
    __tablename__ = "transactions"

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    shopping_list_id = Column(Integer, ForeignKey("shopping_lists.id"))
    total_price = Column(DECIMAL)
    currency = Column(Text)
    status = Column(Text)
    created_at = Column(DateTime)
    confirmed_at = Column(DateTime)
