from sqlalchemy import Column, Integer, Float, Text, DateTime, ForeignKey, DECIMAL
from sqlalchemy.orm import relationship
from backend.app.database import Base

class ShoppingListGroup(Base):
    """Agrupación de productos de una lista de compra por proveedor."""
    __tablename__ = "shopping_list_groups"

    id = Column(Integer, primary_key=True)
    shopping_list_id = Column(Integer, ForeignKey("shopping_lists.id"))
    provider_id = Column(Integer, ForeignKey("users.id"))
    subtotal_price = Column(DECIMAL)
    delivery_estimate = Column(DateTime)

    # Relaciones
    shopping_list = relationship("ShoppingList", back_populates="groups")
    items = relationship("ShoppingListItem", back_populates="group")
