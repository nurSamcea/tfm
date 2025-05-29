from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class LogisticsRouteBase(BaseModel):
    driver_name: Optional[str]
    distance_km: Optional[float]
    orders_ids: Optional[dict]
    vehicle_type: Optional[str]
    estimated_time_min: Optional[int]

class LogisticsRouteCreate(LogisticsRouteBase):
    pass

class LogisticsRouteRead(LogisticsRouteBase):
    id: int
    created_at: datetime

    class Config:
        orm_mode = True


class LogisticsRouteOut(LogisticsRouteRead):
    pass
