from sqlalchemy import Column, Integer, Text, DateTime, Date, ForeignKey
from backend.app.database import Base

class WeeklyPlan(Base):
    __tablename__ = "weekly_plans"

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    intake_profile_id = Column(Integer, ForeignKey("intake_profiles.id"))
    week_start = Column(Date)
    created_at = Column(DateTime)
    comment = Column(Text)
