from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class LogisticsRouteBase(BaseModel):
    route_id: str
    driver_name: Optional[str]
    from_point: str
    to_point: str
    distance_km: float
    estimated_time_min: int
    vehicle_type: Optional[str]

class LogisticsRouteOut(LogisticsRouteBase):
    id: int
    created_at: datetime
    class Config:
        orm_mode = True
