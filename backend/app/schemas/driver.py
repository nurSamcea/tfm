from typing import Optional

from pydantic import BaseModel


class DriverBase(BaseModel):
    name: str
    phone: Optional[str]
    email: Optional[str]
    license_number: Optional[str]
    vehicle_id: Optional[int]
    location_lat: Optional[float]
    location_lon: Optional[float]
    working_hours: Optional[dict]
    available: Optional[bool] = True


class DriverCreate(DriverBase):
    pass


class DriverOut(DriverBase):
    id: int
    vehicle: Optional[VehicleOut]

    class Config:
        orm_mode = True
