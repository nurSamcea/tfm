from pydantic import BaseModel
from typing import Optional
from datetime import date, datetime

class ProductQRCreate(BaseModel):
    product_id: int
    blockchain_hash: str
    qr_code: str
    origin: Optional[str]
    collected_at: Optional[date]
    certification: Optional[str]
    temperature_log: Optional[dict]

class ProductQROut(ProductQRCreate):
    id: int
    created_at: datetime
    class Config:
        orm_mode = True
