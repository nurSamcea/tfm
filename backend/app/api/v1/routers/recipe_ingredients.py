from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from backend.app import schemas, database, models
from backend.app.database import get_db

router = APIRouter(prefix="/recipe_ingredients", tags=["Recipe Ingredients"])

@router.post("/", response_model=schemas.RecipeIngredientOut)
def create_recipe_ingredient(item: schemas.RecipeIngredientCreate, db: Session = Depends(get_db)):
    db_item = models.RecipeIngredient(**item.dict())
    db.add(db_item)
    db.commit()
    db.refresh(db_item)
    return db_item

@router.get("/", response_model=list[schemas.RecipeIngredientOut])
def read_recipe_ingredients(db: Session = Depends(get_db)):
    return db.query(models.RecipeIngredient).all()
