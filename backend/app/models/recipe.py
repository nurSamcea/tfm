from sqlalchemy import Column, Integer, Text, Boolean, ForeignKey, TIMESTAMP
from backend.app.database import Base

class Recipe(Base):
    __tablename__ = "recipes"
    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    description = Column(Text)
    image_url = Column(Text)
    is_vegan = Column(Boolean)
    created_by = Column(Integer, ForeignKey("users.id"))
    created_at = Column(TIMESTAMP)

class RecipeIngredient(Base):
    __tablename__ = "recipe_ingredients"
    id = Column(Integer, primary_key=True)
    recipe_id = Column(Integer, ForeignKey("recipes.id", ondelete="CASCADE"))
    name = Column(Text)
    quantity = Column(Text)
