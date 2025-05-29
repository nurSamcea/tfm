from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class ShoppingListGroupBase(BaseModel):
    shopping_list_id: int
    provider_id: int
    subtotal_price: float
    delivery_estimate: Optional[datetime]
    logistics_route_id: Optional[int]

class ShoppingListGroupCreate(ShoppingListGroupBase):
    pass

class ShoppingListGroupRead(ShoppingListGroupBase):
    id: int

    class Config:
        orm_mode = True
