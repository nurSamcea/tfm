from typing import Optional

from pydantic import BaseModel


class VehicleBase(BaseModel):
    type: Optional[str]
    name: Optional[str]
    plate_number: Optional[str]
    capacity_kg: Optional[float]
    capacity_m3: Optional[float]
    speed_kmph: Optional[float]
    emissions_factor: Optional[float]


class VehicleCreate(VehicleBase):
    pass


class VehicleOut(VehicleBase):
    id: int

    class Config:
        orm_mode = True
