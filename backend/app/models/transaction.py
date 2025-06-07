from sqlalchemy import Column, Integer, Text, DateTime, ForeignKey, DECIMAL, Enum
from sqlalchemy.orm import relationship
from backend.app.database import Base
import enum

class TransactionStatusEnum(enum.Enum):
    pending = "pending"
    paid = "paid"
    delivered = "delivered"
    cancelled = "cancelled"

class Transaction(Base):
    """Modelo de transacción para compras y pagos."""
    __tablename__ = "transactions"

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    shopping_list_id = Column(Integer, ForeignKey("shopping_lists.id"))
    total_price = Column(DECIMAL)
    currency = Column(Text)
    status = Column(Enum(TransactionStatusEnum), nullable=False)
    created_at = Column(DateTime)
    confirmed_at = Column(DateTime)

    # Relaciones
    user = relationship("User")
    shopping_list = relationship("ShoppingList")
