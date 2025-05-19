from sqlalchemy import Column, Integer, String, Text, JSON, TIMESTAMP, CheckConstraint
from backend.app.database import Base

class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    email = Column(Text, unique=True, nullable=False)
    password = Column(Text, nullable=False)
    role = Column(Text, nullable=False)
    entity_name = Column(Text)
    location = Column(JSON)
    preferences = Column(JSON)
    created_at = Column(TIMESTAMP)

    __table_args__ = (
        CheckConstraint("role IN ('consumer', 'farmer', 'retailer', 'restaurant', 'admin')", name="check_user_role"),
    )
