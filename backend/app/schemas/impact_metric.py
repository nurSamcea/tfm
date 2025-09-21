from typing import Dict, List, Optional
from pydantic import BaseModel


class ProductImpactDetail(BaseModel):
    co2_saved_kg: float
    local_support_eur: float
    waste_prevented_kg: float
    sustainability_score: float


class ProductImpact(BaseModel):
    product_id: int
    product_name: str
    impact: ProductImpactDetail


class TotalImpact(BaseModel):
    co2_saved_kg: float
    local_support_eur: float
    waste_prevented_kg: float
    average_sustainability_score: float


class ImpactMetrics(BaseModel):
    total_impact: TotalImpact
    product_impacts: List[ProductImpact]


class ImpactMetricBase(BaseModel):
    user_id: int
    co2_saved_kg: float
    local_support_eur: float
    waste_prevented_kg: float
    sustainability_score: float
    transaction_id: Optional[int] = None


class ImpactMetricCreate(ImpactMetricBase):
    pass


class ImpactMetricRead(ImpactMetricBase):
    id: int

    class Config:
        from_attributes = True


class ImpactMetricOut(ImpactMetricRead):
    class Config:
        from_attributes = True
        json_encoders = {
            float: lambda v: round(v, 2) if isinstance(v, float) else v
        }