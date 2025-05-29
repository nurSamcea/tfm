from pydantic import BaseModel
from typing import Optional

class WeeklyPlanItemBase(BaseModel):
    weekly_plan_id: int
    day_of_week: str
    meal_type: str
    recipe_id: int
    portions: int
    nutrition_total: Optional[dict]

class WeeklyPlanItemCreate(WeeklyPlanItemBase):
    pass

class WeeklyPlanItemRead(WeeklyPlanItemBase):
    id: int

    class Config:
        orm_mode = True


class WeeklyPlanItemOut(WeeklyPlanItemRead):
    class Config:
        orm_mode = True
        json_encoders = {
            dict: lambda v: v if isinstance(v, dict) else {}
        }