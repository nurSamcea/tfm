from sqlalchemy import Column, Integer, Date, Text, ForeignKey, CheckConstraint
from backend.app.database import Base

class WeeklyPlan(Base):
    __tablename__ = "weekly_plans"
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    week_start = Column(Date, nullable=False)

class WeeklyPlanItem(Base):
    __tablename__ = "weekly_plan_items"
    id = Column(Integer, primary_key=True)
    plan_id = Column(Integer, ForeignKey("weekly_plans.id", ondelete="CASCADE"))
    recipe_id = Column(Integer, ForeignKey("recipes.id"))
    day = Column(Text)
    meal = Column(Text)

    __table_args__ = (
        CheckConstraint("day IN ('monday','tuesday','wednesday','thursday','friday','saturday','sunday')", name="check_day"),
        CheckConstraint("meal IN ('breakfast','lunch','dinner')", name="check_meal"),
    )
