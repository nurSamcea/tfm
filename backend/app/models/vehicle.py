from sqlalchemy import Column, Integer, String, Float, Boolean, ForeignKey, JSON, DateTime
from sqlalchemy.orm import relationship
from datetime import datetime
from backend.app.database import Base


class Vehicle(Base):
    __tablename__ = "vehicles"

    id = Column(Integer, primary_key=True, index=True)
    type = Column(String)
    name = Column(String)
    plate_number = Column(String)
    capacity_kg = Column(Float)
    capacity_m3 = Column(Float)
    speed_kmph = Column(Float)
    emissions_factor = Column(Float)
    created_at = Column(DateTime, default=datetime.utcnow)

    drivers = relationship("Driver", back_populates="vehicle")


