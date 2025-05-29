from pydantic import BaseModel
from typing import Optional
from datetime import date, datetime

class ShoppingListItemBase(BaseModel):
    shopping_list_group_id: int
    product_id: int
    quantity: float
    unit: str
    price_unit: float
    currency: str
    total_price: float
    expiration_date: Optional[date]
    trace_hash: Optional[str]
    nutritional_info: Optional[dict]

class ShoppingListItemCreate(ShoppingListItemBase):
    pass

class ShoppingListItemRead(ShoppingListItemBase):
    id: int
    added_at: datetime

    class Config:
        orm_mode = True
