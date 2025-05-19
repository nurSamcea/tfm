from pydantic import BaseModel
from typing import Literal
from datetime import date

class WeeklyPlanCreate(BaseModel):
    user_id: int
    week_start: date

class WeeklyPlanOut(WeeklyPlanCreate):
    id: int
    class Config:
        orm_mode = True

class WeeklyPlanItemCreate(BaseModel):
    plan_id: int
    recipe_id: int
    day: Literal['monday','tuesday','wednesday','thursday','friday','saturday','sunday']
    meal: Literal['breakfast','lunch','dinner']

class WeeklyPlanItemOut(WeeklyPlanItemCreate):
    id: int
    class Config:
        orm_mode = True
