from sqlalchemy import Column, Integer, Float, Text, Boolean, ForeignKey, JSON
from backend.app.database import Base

class RecipeIngredient(Base):
    __tablename__ = "recipe_ingredients"

    id = Column(Integer, primary_key=True)
    recipe_id = Column(Integer, ForeignKey("recipes.id"))
    product_id = Column(Integer, ForeignKey("products.id"))
    name = Column(Text)
    quantity = Column(Float)
    unit = Column(Text)
    nutritional_info = Column(JSON)
    optional = Column(Boolean)
