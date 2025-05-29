from pydantic import BaseModel
from typing import Optional, List
from datetime import datetime

class RecipeBase(BaseModel):
    name: str
    description: Optional[str]
    author_id: int
    image_url: Optional[str]
    steps: Optional[dict]
    time_minutes: Optional[int]
    difficulty: Optional[str]
    is_vegan: Optional[bool]
    is_gluten_free: Optional[bool]
    tags: Optional[dict]
    nutrition_total: Optional[dict]

class RecipeCreate(RecipeBase):
    pass

class RecipeRead(RecipeBase):
    id: int
    created_at: datetime

    class Config:
        orm_mode = True

class RecipeOut(RecipeRead):
    ingredients: Optional[List[dict]] = []

    class Config:
        orm_mode = True
        json_encoders = {
            datetime: lambda v: v.isoformat() if isinstance(v, datetime) else v,
            dict: lambda v: v if isinstance(v, dict) else {}
        }