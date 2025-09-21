from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime

class SensorReadingBase(BaseModel):
    sensor_id: int = Field(..., description="ID del sensor")
    product_id: Optional[int] = Field(None, description="ID del producto (opcional para compatibilidad)")
    temperature: Optional[float] = None
    humidity: Optional[float] = None
    gas_level: Optional[float] = None
    light_level: Optional[float] = None
    shock_detected: Optional[bool] = None
    soil_moisture: Optional[float] = None
    ph_level: Optional[float] = None
    source_device: Optional[str] = Field(None, description="ID del dispositivo (opcional para compatibilidad)")
    reading_quality: Optional[float] = Field(1.0, description="Calidad de la señal (0-1)")
    is_processed: Optional[bool] = Field(False, description="Si fue procesado por edge computing")
    extra_data: Optional[dict] = None

class SensorReadingCreate(SensorReadingBase):
    pass

class SensorReadingUpdate(BaseModel):
    temperature: Optional[float] = None
    humidity: Optional[float] = None
    gas_level: Optional[float] = None
    light_level: Optional[float] = None
    shock_detected: Optional[bool] = None
    soil_moisture: Optional[float] = None
    ph_level: Optional[float] = None
    reading_quality: Optional[float] = None
    is_processed: Optional[bool] = None
    extra_data: Optional[dict] = None

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