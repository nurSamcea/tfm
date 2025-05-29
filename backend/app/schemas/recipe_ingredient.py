from pydantic import BaseModel
from typing import Optional

class RecipeIngredientBase(BaseModel):
    recipe_id: int
    product_id: int
    name: str
    quantity: float
    unit: str
    nutritional_info: Optional[dict]
    optional: bool

class RecipeIngredientCreate(RecipeIngredientBase):
    pass

class RecipeIngredientRead(RecipeIngredientBase):
    id: int

    class Config:
        orm_mode = True

class RecipeIngredientOut(RecipeIngredientRead):
    class Config:
        orm_mode = True
        json_encoders = {
            dict: lambda v: v if isinstance(v, dict) else {}
        }