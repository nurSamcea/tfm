from sqlalchemy import Column, Integer, Text, ForeignKey, JSON
from backend.app.database import Base

class WeeklyPlanItem(Base):
    __tablename__ = "weekly_plan_items"

    id = Column(Integer, primary_key=True)
    weekly_plan_id = Column(Integer, ForeignKey("weekly_plans.id"))
    day_of_week = Column(Text)
    meal_type = Column(Text)
    recipe_id = Column(Integer, ForeignKey("recipes.id"))
    portions = Column(Integer)
    nutrition_total = Column(JSON)
