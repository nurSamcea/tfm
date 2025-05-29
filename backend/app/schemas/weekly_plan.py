from pydantic import BaseModel
from typing import Optional
from datetime import datetime, date

class WeeklyPlanBase(BaseModel):
    user_id: int
    intake_profile_id: Optional[int]
    week_start: date
    comment: Optional[str]

class WeeklyPlanCreate(WeeklyPlanBase):
    pass

class WeeklyPlanRead(WeeklyPlanBase):
    id: int
    created_at: Optional[datetime]

    class Config:
        orm_mode = True

class WeeklyPlanOut(WeeklyPlanRead):
    class Config:
        orm_mode = True
        json_encoders = {
            datetime: lambda v: v.isoformat() if isinstance(v, datetime) else v,
            date: lambda v: v.isoformat() if isinstance(v, date) else v
        }