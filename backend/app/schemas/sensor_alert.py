from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime
from enum import Enum

class AlertTypeEnum(str, Enum):
    temperature_high = "temperature_high"
    temperature_low = "temperature_low"
    humidity_high = "humidity_high"
    humidity_low = "humidity_low"
    sensor_offline = "sensor_offline"
    sensor_error = "sensor_error"
    threshold_exceeded = "threshold_exceeded"

class AlertStatusEnum(str, Enum):
    active = "active"
    acknowledged = "acknowledged"
    resolved = "resolved"
    dismissed = "dismissed"

class SensorAlertBase(BaseModel):
    sensor_id: int
    alert_type: AlertTypeEnum
    title: str = Field(..., description="Título de la alerta")
    message: str = Field(..., description="Mensaje descriptivo")
    severity: str = Field(default="medium", description="Severidad: low, medium, high, critical")
    threshold_value: Optional[float] = None
    actual_value: Optional[float] = None
    unit: Optional[str] = None
    extra_data: Optional[dict] = None

class SensorAlertCreate(SensorAlertBase):
    pass

class SensorAlertUpdate(BaseModel):
    status: Optional[AlertStatusEnum] = None
    acknowledged_by: Optional[int] = None

class SensorAlertRead(SensorAlertBase):
    id: int
    status: AlertStatusEnum
    created_at: datetime
    acknowledged_at: Optional[datetime] = None
    resolved_at: Optional[datetime] = None
    acknowledged_by: Optional[int] = None

    class Config:
        from_attributes = True
