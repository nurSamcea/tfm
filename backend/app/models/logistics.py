from sqlalchemy import Column, Integer, Text, DECIMAL, TIMESTAMP
from backend.app.database import Base

class LogisticsRoute(Base):
    __tablename__ = "logistics_routes"
    id = Column(Integer, primary_key=True)
    route_id = Column(Text)
    driver_name = Column(Text)
    from_point = Column(Text)
    to_point = Column(Text)
    distance_km = Column(DECIMAL)
    estimated_time_min = Column(Integer)
    vehicle_type = Column(Text)
    created_at = Column(TIMESTAMP)
