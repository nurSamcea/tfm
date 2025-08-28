from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class SensorReadingBase(BaseModel):
    product_id: int
    temperature: Optional[float]
    humidity: Optional[float]
    gas_level: Optional[float]
    light_level: Optional[float]
    shock_detected: Optional[bool]
    source_device: Optional[str]

class SensorReadingCreate(SensorReadingBase):
    pass

class SensorReadingRead(SensorReadingBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True


class SensorReadingOut(SensorReadingRead):
    class Config:
        from_attributes = True
        json_encoders = {
            datetime: lambda v: v.isoformat() if isinstance(v, datetime) else v,
            float: lambda v: round(v, 2) if isinstance(v, float) else v
        }