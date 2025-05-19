from pydantic import BaseModel
from typing import Optional, List
from datetime import date, datetime

class ProductTemplateBase(BaseModel):
    name: str
    category: Optional[str]
    subcategory: Optional[str]
    default_image_url: Optional[str]
    nutrition: Optional[dict]
    allergens: Optional[List[str]]
    labels: Optional[List[str]]

class ProductTemplate(ProductTemplateBase):
    id: int
    class Config:
        orm_mode = True

class ProductBase(BaseModel):
    name: str
    description: Optional[str]
    price: float
    stock: int
    unit: str
    expiration_date: Optional[date]
    image_url: Optional[str]
    is_local: Optional[bool] = True
    template_id: Optional[int]

class ProductCreate(ProductBase):
    pass

class ProductOut(ProductBase):
    id: int
    owner_id: int
    is_active: bool
    created_at: datetime
    class Config:
        orm_mode = True
