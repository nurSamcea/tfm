from sqlalchemy import Column, Integer, Text, Boolean, DateTime, ForeignKey, JSON
from backend.app.database import Base
from datetime import datetime

class Recipe(Base):
    __tablename__ = "recipes"

    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    description = Column(Text)
    author_id = Column(Integer, ForeignKey("users.id"))
    image_url = Column(Text)
    steps = Column(JSON)
    time_minutes = Column(Integer)
    difficulty = Column(Text)
    is_vegan = Column(Boolean)
    is_gluten_free = Column(Boolean)
    created_at = Column(DateTime, default=datetime.utcnow)
    tags = Column(JSON)
    nutrition_total = Column(JSON)
