from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime, ForeignKey, Enum, Text, JSON
from sqlalchemy.orm import relationship
from backend.app.database import Base
import enum
from datetime import datetime

class AlertTypeEnum(enum.Enum):
    temperature_high = "temperature_high"
    temperature_low = "temperature_low"
    humidity_high = "humidity_high"
    humidity_low = "humidity_low"
    sensor_offline = "sensor_offline"
    sensor_error = "sensor_error"
    threshold_exceeded = "threshold_exceeded"

class AlertStatusEnum(enum.Enum):
    active = "active"
    acknowledged = "acknowledged"
    resolved = "resolved"
    dismissed = "dismissed"

class SensorAlert(Base):
    """Modelo de alertas del sistema de sensores."""
    __tablename__ = "sensor_alerts"

    id = Column(Integer, primary_key=True)
    sensor_id = Column(Integer, ForeignKey("sensors.id"), nullable=False)
    alert_type = Column(Enum(AlertTypeEnum), nullable=False)
    status = Column(Enum(AlertStatusEnum), default=AlertStatusEnum.active)
    
    # Información de la alerta
    title = Column(String(200), nullable=False)
    message = Column(Text, nullable=False)
    severity = Column(String(20), default="medium")  # low, medium, high, critical
    
    # Valores que dispararon la alerta
    threshold_value = Column(Float)
    actual_value = Column(Float)
    unit = Column(String(20))  # °C, %, etc.
    
    # Metadatos
    created_at = Column(DateTime, default=datetime.utcnow)
    acknowledged_at = Column(DateTime)
    resolved_at = Column(DateTime)
    acknowledged_by = Column(Integer, ForeignKey("users.id"))
    
    # Configuración adicional
    extra_data = Column(JSON)
    
    # Relaciones
    sensor = relationship("Sensor")
    acknowledged_user = relationship("User")
