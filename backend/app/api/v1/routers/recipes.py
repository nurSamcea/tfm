from typing import List

from backend.app import models, schemas
from backend.app.database import get_db
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

router = APIRouter(prefix="/recipes", tags=["recipes"])


# Crear una receta
@router.post("/", response_model=schemas.RecipeOut)
def create_recipe(recipe: schemas.RecipeCreate, db: Session = Depends(get_db)):
    db_recipe = models.Recipe(**recipe.dict())
    db.add(db_recipe)
    db.commit()
    db.refresh(db_recipe)
    return db_recipe


# Listar todas las recetas
@router.get("/", response_model=List[schemas.RecipeOut])
def list_recipes(db: Session = Depends(get_db)):
    return db.query(models.Recipe).all()


# Obtener receta por ID
@router.get("/{recipe_id}", response_model=schemas.RecipeOut)
def get_recipe(recipe_id: int, db: Session = Depends(get_db)):
    recipe = db.query(models.Recipe).get(recipe_id)
    if not recipe:
        raise HTTPException(status_code=404, detail="Receta no encontrada")
    return recipe


# Listar recetas por usuario
@router.get("/by_user/{user_id}", response_model=List[schemas.RecipeOut])
def get_recipes_by_user(user_id: int, db: Session = Depends(get_db)):
    return db.query(models.Recipe).filter(models.Recipe.created_by == user_id).all()


# Actualizar receta
@router.put("/{recipe_id}", response_model=schemas.RecipeOut)
def update_recipe(recipe_id: int, updated: schemas.RecipeCreate, db: Session = Depends(get_db)):
    recipe = db.query(models.Recipe).get(recipe_id)
    if not recipe:
        raise HTTPException(status_code=404, detail="Receta no encontrada")
    for key, value in updated.dict().items():
        setattr(recipe, key, value)
    db.commit()
    db.refresh(recipe)
    return recipe


# Eliminar receta
@router.delete("/{recipe_id}")
def delete_recipe(recipe_id: int, db: Session = Depends(get_db)):
    recipe = db.query(models.Recipe).get(recipe_id)
    if not recipe:
        raise HTTPException(status_code=404, detail="Receta no encontrada")
    db.delete(recipe)
    db.commit()
    return {"message": "Receta eliminada"}
