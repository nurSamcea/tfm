from datetime import datetime
from typing import Optional, List

from pydantic import BaseModel


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


class ProductInput(BaseModel):
    name: str
    quantity: int


class FilterInput(BaseModel):
    eco: bool = False
    gluten_free: bool = False
    max_distance_km: float = 50.0


class OptimizationCriteria(BaseModel):
    price_weight: float = 1.0
    distance_weight: float = 0.5
    provider_weight: float = 2.0


class OptimizationRequest(BaseModel):
    user_lat: float
    user_lon: float
    requested_products: List[ProductInput]
    filters: FilterInput
    criteria: OptimizationCriteria
