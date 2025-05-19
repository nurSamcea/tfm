from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class SensorReadingCreate(BaseModel):
    device_id: str
    product_id: int
    temperature: Optional[float]
    humidity: Optional[float]
    gas_level: Optional[float]
    voltage: Optional[float]

class SensorReadingOut(SensorReadingCreate):
    id: int
    timestamp: datetime
    class Config:
        orm_mode = True
