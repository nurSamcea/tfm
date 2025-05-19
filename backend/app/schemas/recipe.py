from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class RecipeBase(BaseModel):
    name: str
    description: Optional[str]
    image_url: Optional[str]
    is_vegan: Optional[bool] = False

class RecipeCreate(RecipeBase):
    created_by: int

class RecipeOut(RecipeBase):
    id: int
    created_at: datetime
    class Config:
        orm_mode = True

class RecipeIngredient(BaseModel):
    id: int
    name: str
    quantity: str
    recipe_id: int
    class Config:
        orm_mode = True
