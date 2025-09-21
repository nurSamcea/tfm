from pydantic import BaseModel, validator
from backend.app.models.product import ProductCategory
from datetime import date, datetime
from typing import Optional, Dict, List, Union

class ProductBase(BaseModel):
    name: str
    description: Optional[str]
    price: Optional[float]
    currency: Optional[str]
    unit: Optional[str]
    category: Optional[ProductCategory]
    stock_available: Optional[float]
    expiration_date: Optional[date]
    is_eco: Optional[bool]
    image_url: Optional[str]
    provider_id: int
    is_hidden: Optional[bool] = False

class ProductCreate(ProductBase):
    pass

class ProductUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    price: Optional[float] = None
    currency: Optional[str] = None
    unit: Optional[str] = None
    category: Optional[ProductCategory] = None
    stock_available: Optional[float] = None
    expiration_date: Optional[Union[date, str]] = None
    expiration_date_string: Optional[str] = None
    is_eco: Optional[bool] = None
    image_url: Optional[str] = None
    is_hidden: Optional[bool] = None
    
    class Config:
        # Permitir que los campos None se incluyan en el dict
        exclude_none = False
    
    @validator('expiration_date', pre=True)
    def parse_expiration_date(cls, v):
        if v is None:
            return None
        if isinstance(v, str):
            try:
                # Intentar parsear como fecha ISO
                return datetime.fromisoformat(v.replace('Z', '+00:00')).date()
            except:
                try:
                    # Intentar parsear como fecha simple
                    return datetime.strptime(v, '%Y-%m-%d').date()
                except:
                    return None
        return v

class ProductRead(ProductBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True

class ProductFilterRequest(BaseModel):
    search: Optional[str]
    filters: Dict[str, bool] = {} # Ej: {"eco": true, "gluten_free": false, ...}
    weights: Dict[str, float] = {} # Ej: {"price": 0.5, "distance": 0.3, "sustainability": 0.2, ...}
    user_lat: Optional[float] = None
    user_lon: Optional[float] = None
    sort_criteria: Optional[str] = "optimal" # Criterios disponibles: "optimal", "price", "price_desc", "distance", "sustainability", "stock", "eco"

class ProductOptimizedResponse(BaseModel):
    id: int
    name: str
    price: float
    category: Optional[ProductCategory]
    is_eco: Optional[bool]
    is_gluten_free: Optional[bool]
    provider_id: int
    distance_km: Optional[float]
    score: float
    optimization_score: Optional[float] = None
    stock_available: Optional[float] = None
