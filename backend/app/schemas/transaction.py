from pydantic import BaseModel
from typing import Literal, Optional
from datetime import datetime

class TransactionBase(BaseModel):
    buyer_id: int
    seller_id: int
    total_price: float
    payment_method: Optional[str]
    status: Optional[Literal['pending', 'paid', 'delivered', 'cancelled']] = 'pending'
    delivery_method: Optional[str]
    delivery_estimated: Optional[datetime]

class TransactionCreate(TransactionBase):
    pass

class TransactionOut(TransactionBase):
    id: int
    created_at: datetime
    class Config:
        orm_mode = True

class TransactionItemBase(BaseModel):
    product_id: int
    quantity: int
    unit_price: float
    qr_id: Optional[int]

class TransactionItem(TransactionItemBase):
    id: int
    transaction_id: int
    class Config:
        orm_mode = True
