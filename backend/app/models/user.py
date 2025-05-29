from sqlalchemy import Column, Integer, String, Text, DateTime, JSON, DECIMAL, ForeignKey, CheckConstraint
from sqlalchemy.orm import relationship
from datetime import datetime
from backend.app.database import Base

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    email = Column(Text, unique=True, nullable=False)
    password_hash = Column(Text, nullable=False)
    role = Column(Text, CheckConstraint("role IN ('consumer', 'farmer', 'retailer', 'restaurant', 'admin')"), nullable=False)
    entity_name = Column(Text)
    location_lat = Column(DECIMAL)
    location_lon = Column(DECIMAL)
    preferences = Column(JSON)
    intake_profile_id = Column(Integer, ForeignKey("intake_profiles.id"))
    created_at = Column(DateTime, default=datetime.utcnow)
