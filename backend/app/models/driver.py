from sqlalchemy import Column, Integer, String, Float, Boolean, ForeignKey, JSON, DateTime
from sqlalchemy.orm import relationship
from datetime import datetime
from backend.app.database import Base


class Driver(Base):
    __tablename__ = "drivers"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    phone = Column(String)
    email = Column(String, unique=True)
    license_number = Column(String)
    vehicle_id = Column(Integer, ForeignKey("vehicles.id"))
    location_lat = Column(Float)
    location_lon = Column(Float)
    working_hours = Column(JSON)
    available = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)

    vehicle = relationship("Vehicle", back_populates="drivers")
