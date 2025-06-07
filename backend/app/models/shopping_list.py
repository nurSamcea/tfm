from sqlalchemy import Column, Integer, Float, Text, DateTime, ForeignKey, DECIMAL, Enum
from sqlalchemy.orm import relationship
from datetime import datetime
from backend.app.database import Base
import enum

class ShoppingListStatusEnum(enum.Enum):
    draft = "draft"
    pending = "pending"
    paid = "paid"
    delivered = "delivered"
    cancelled = "cancelled"

class ShoppingList(Base):
    """Lista de compra de un usuario, con agrupación por proveedor."""
    __tablename__ = "shopping_lists"

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    created_at = Column(DateTime, default=datetime.utcnow)
    total_price = Column(DECIMAL)
    currency = Column(Text)
    status = Column(Enum(ShoppingListStatusEnum), nullable=False)

    # Relaciones
    user = relationship("User", back_populates="shopping_lists")
    groups = relationship("ShoppingListGroup", back_populates="shopping_list")
