from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class TransactionBase(BaseModel):
    user_id: int
    shopping_list_id: int
    total_price: float
    currency: str
    status: str

class TransactionCreate(TransactionBase):
    created_at: Optional[datetime] = None
    confirmed_at: Optional[datetime] = None

class TransactionRead(TransactionBase):
    id: int
    created_at: Optional[datetime]
    confirmed_at: Optional[datetime]

    class Config:
        orm_mode = True

class TransactionOut(TransactionBase):
    id: int
    created_at: Optional[datetime]
    confirmed_at: Optional[datetime]

    class Config:
        orm_mode = True