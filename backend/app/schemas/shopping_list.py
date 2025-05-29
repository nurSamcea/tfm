from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class ShoppingListBase(BaseModel):
    user_id: int
    total_price: Optional[float]
    currency: Optional[str]
    status: Optional[str]

class ShoppingListCreate(ShoppingListBase):
    pass

class ShoppingListRead(ShoppingListBase):
    id: int
    created_at: datetime

    class Config:
        orm_mode = True
