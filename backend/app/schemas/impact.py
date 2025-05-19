from pydantic import BaseModel
from datetime import datetime

class ImpactMetricBase(BaseModel):
    user_id: int
    co2_saved: float
    km_saved: float
    local_support_pct: float
    zero_waste_score: float

class ImpactMetricOut(ImpactMetricBase):
    id: int
    calculated_at: datetime
    class Config:
        orm_mode = True
