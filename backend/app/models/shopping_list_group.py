from sqlalchemy import Column, Integer, Float, Text, DateTime, ForeignKey, DECIMAL
from backend.app.database import Base

class ShoppingListGroup(Base):
    __tablename__ = "shopping_list_groups"

    id = Column(Integer, primary_key=True)
    shopping_list_id = Column(Integer, ForeignKey("shopping_lists.id"))
    provider_id = Column(Integer, ForeignKey("users.id"))
    subtotal_price = Column(DECIMAL)
    delivery_estimate = Column(DateTime)
    logistics_route_id = Column(Integer, ForeignKey("logistics_routes.id"))
