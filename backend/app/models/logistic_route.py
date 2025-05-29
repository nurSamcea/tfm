from sqlalchemy import Column, Integer, Float, Text, DateTime, JSON
from backend.app.database import Base

class LogisticsRoute(Base):
    __tablename__ = "logistics_routes"

    id = Column(Integer, primary_key=True)
    driver_name = Column(Text)
    distance_km = Column(Float)
    orders_ids = Column(JSON)
    created_at = Column(DateTime)
    vehicle_type = Column(Text)
    estimated_time_min = Column(Integer)
