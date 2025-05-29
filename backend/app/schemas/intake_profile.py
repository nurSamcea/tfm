from pydantic import BaseModel
from typing import Optional

class IntakeProfileBase(BaseModel):
    name: Optional[str]
    gender: Optional[str]
    age_range: Optional[str]
    activity: Optional[str]
    kcal: Optional[int]
    protein: Optional[float]
    carbohydrates: Optional[float]
    sugars: Optional[float]
    fat: Optional[float]
    saturated_fat: Optional[float]
    fiber: Optional[float]
    salt: Optional[float]
    cholesterol: Optional[float]
    calcium: Optional[float]
    iron: Optional[float]
    vitamin_c: Optional[float]
    vitamin_d: Optional[float]
    vitamin_b12: Optional[float]
    potassium: Optional[float]
    magnesium: Optional[float]

class IntakeProfileCreate(IntakeProfileBase):
    pass

class IntakeProfileRead(IntakeProfileBase):
    id: int

    class Config:
        orm_mode = True
