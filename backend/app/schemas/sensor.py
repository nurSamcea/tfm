from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime
from enum import Enum

class SensorTypeEnum(str, Enum):
    temperature = "temperature"
    humidity = "humidity"
    gas = "gas"
    light = "light"
    shock = "shock"
    gps = "gps"
    soil_moisture = "soil_moisture"
    ph = "ph"

class SensorStatusEnum(str, Enum):
    active = "active"
    inactive = "inactive"
    error = "error"
    maintenance = "maintenance"

class SensorBase(BaseModel):
    device_id: str = Field(..., description="ID único del dispositivo")
    name: str = Field(..., description="Nombre descriptivo del sensor")
    sensor_type: SensorTypeEnum
    zone_id: Optional[int] = None
    location_lat: Optional[float] = None
    location_lon: Optional[float] = None
    location_description: Optional[str] = None
    min_threshold: Optional[float] = None
    max_threshold: Optional[float] = None
    alert_enabled: bool = True
    reading_interval: int = 30
    config: Optional[dict] = None

class SensorCreate(SensorBase):
    pass

class SensorUpdate(BaseModel):
    name: Optional[str] = None
    status: Optional[SensorStatusEnum] = None
    zone_id: Optional[int] = None
    location_lat: Optional[float] = None
    location_lon: Optional[float] = None
    location_description: Optional[str] = None
    min_threshold: Optional[float] = None
    max_threshold: Optional[float] = None
    alert_enabled: Optional[bool] = None
    reading_interval: Optional[int] = None
    config: Optional[dict] = None

class SensorRead(SensorBase):
    id: int
    status: SensorStatusEnum
    firmware_version: Optional[str] = None
    last_seen: Optional[datetime] = None
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True

class SensorZoneBase(BaseModel):
    name: str = Field(..., description="Nombre de la zona")
    description: Optional[str] = None
    location_lat: Optional[float] = None
    location_lon: Optional[float] = None
    location_description: Optional[str] = None

class SensorZoneCreate(SensorZoneBase):
    farmer_id: int

class SensorZoneUpdate(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    location_lat: Optional[float] = None
    location_lon: Optional[float] = None
    location_description: Optional[str] = None
    is_active: Optional[bool] = None

class SensorZoneRead(SensorZoneBase):
    id: int
    farmer_id: int
    is_active: bool
    created_at: datetime
    updated_at: datetime
    sensors: List[SensorRead] = []

    class Config:
        from_attributes = True
