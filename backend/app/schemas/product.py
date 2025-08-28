from pydantic import BaseModel
from typing import Optional, Dict, List
from datetime import date, datetime

class ProductBase(BaseModel):
    name: str
    description: Optional[str]
    price: Optional[float]
    currency: Optional[str]
    unit: Optional[str]
    category: Optional[str]
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
        from_attributes = True

class ProductFilterRequest(BaseModel):
    search: Optional[str]
    filters: Dict[str, bool] = {} # Ej: {"eco": true, "gluten_free": false, ...}
    weights: Dict[str, float] = {} # Ej: {"price": 0.5, "distance": 0.3, ...}
    user_lat: Optional[float] = None
    user_lon: Optional[float] = None

class ProductOptimizedResponse(BaseModel):
    id: int
    name: str
    price: float
    category: Optional[str]
    is_eco: Optional[bool]
    is_gluten_free: Optional[bool]
    provider_id: int
    distance_km: Optional[float]
    score: float
