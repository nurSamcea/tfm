from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from backend.app import schemas, database, models
from backend.app.database import get_db

router = APIRouter(prefix="/recipes", tags=["Recipes"])

@router.post("/", response_model=schemas.RecipeOut)
def create_recipe(recipe: schemas.RecipeCreate, db: Session = Depends(get_db)):
    db_recipe = models.Recipe(**recipe.dict())
    db.add(db_recipe)
    db.commit()
    db.refresh(db_recipe)
    return db_recipe

@router.get("/", response_model=list[schemas.RecipeOut])
def read_recipes(db: Session = Depends(get_db)):
    return db.query(models.Recipe).all()
