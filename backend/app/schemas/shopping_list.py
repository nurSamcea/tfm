from datetime import datetime
from typing import Optional, List

from pydantic import BaseModel


class ProductRequest(BaseModel):
    name: str
    quantity: int


class OptimizationFilters(BaseModel):
    eco: bool = False
    gluten_free: bool = False
    max_distance_km: float = 10.0


class OptimizationCriteria(BaseModel):
    price_weight: float = 1.0
    distance_weight: float = 1.0
    provider_weight: float = 1.0
    route_efficiency_weight: float = 0.1


class OptimizationRequest(BaseModel):
    user_lat: float
    user_lon: float
    requested_products: List[ProductRequest]
    filters: OptimizationFilters = OptimizationFilters()
    criteria: OptimizationCriteria = OptimizationCriteria()


class ProviderLocation(BaseModel):
    lat: float
    lon: float


class ProviderSummary(BaseModel):
    provider_name: str
    location: ProviderLocation
    items: List[dict]
    total_items: int
    total_price: float


class OptimizationMetrics(BaseModel):
    unique_providers: int
    average_distance_per_provider: float
    average_price_per_item: float


class OptimizationResponse(BaseModel):
    optimized_basket: List[dict]
    total_price: float
    total_distance_km: float
    total_cost: float
    provider_summary: dict[str, ProviderSummary]
    alternative_products: dict[str, List[dict]]
    metrics: OptimizationMetrics


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
        from_attributes = True


class ProductInput(BaseModel):
    name: str
    quantity: int


class FilterInput(BaseModel):
    eco: bool = False
    gluten_free: bool = False
    max_distance_km: float = 50.0
