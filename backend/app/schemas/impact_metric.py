from pydantic import BaseModel

class ImpactMetricBase(BaseModel):
    user_id: int
    co2_saved_kg: float
    local_support_eur: float
    waste_prevented_kg: float

class ImpactMetricCreate(ImpactMetricBase):
    pass

class ImpactMetricRead(ImpactMetricBase):
    id: int

    class Config:
        orm_mode = True

class ImpactMetricOut(ImpactMetricRead):
    class Config:
        orm_mode = True
        json_encoders = {
            float: lambda v: round(v, 2) if isinstance(v, float) else v
        }