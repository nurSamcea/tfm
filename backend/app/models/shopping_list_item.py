from backend.app.database import Base
from sqlalchemy import Column, Integer, Float, Text, DateTime, Date, ForeignKey, JSON
from sqlalchemy.orm import relationship


class ShoppingListItem(Base):
    """Ítem de una lista de compra agrupada por proveedor."""
    __tablename__ = "shopping_list_items"

    id = Column(Integer, primary_key=True)
    shopping_list_group_id = Column(Integer, ForeignKey("shopping_list_groups.id"))
    product_id = Column(Integer, ForeignKey("products.id"))
    quantity = Column(Float)
    unit = Column(Text)
    price_unit = Column(Float)
    currency = Column(Text)
    total_price = Column(Float)
    expiration_date = Column(Date)
    trace_hash = Column(Text)
    nutritional_info = Column(JSON)
    added_at = Column(DateTime)

    # Relaciones
    group = relationship("ShoppingListGroup", back_populates="items")
    product = relationship("Product")
