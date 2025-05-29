from pydantic import BaseModel
from typing import Optional
from datetime import date, datetime

class ProductBase(BaseModel):
    name: str
    description: Optional[str]
    price: Optional[float]
    currency: Optional[str]
    unit: Optional[str]
    category: Optional[str]
    nutritional_info: Optional[dict]
    stock_available: Optional[float]
    expiration_date: Optional[date]
    is_eco: Optional[bool]
    image_url: Optional[str]
    provider_id: int

class ProductCreate(ProductBase):
    pass

class ProductRead(ProductBase):
    id: int
    created_at: datetime

    class Config:
        orm_mode = True
