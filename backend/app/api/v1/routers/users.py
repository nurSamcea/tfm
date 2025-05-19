from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List

from backend.app import models, schemas
from backend.app.database import get_db

router = APIRouter(prefix="/users", tags=["users"])

# Obtener todos los usuarios (admin)
@router.get("/", response_model=List[schemas.UserOut])
def get_users(db: Session = Depends(get_db)):
    return db.query(models.User).all()

# Obtener un usuario por ID
@router.get("/{user_id}", response_model=schemas.UserOut)
def get_user(user_id: int, db: Session = Depends(get_db)):
    user = db.query(models.User).get(user_id)
    if not user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    return user

# Actualizar un usuario
@router.put("/{user_id}", response_model=schemas.UserOut)
def update_user(user_id: int, updated_user: schemas.UserBase, db: Session = Depends(get_db)):
    user = db.query(models.User).get(user_id)
    if not user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    for key, value in updated_user.dict().items():
        setattr(user, key, value)
    db.commit()
    db.refresh(user)
    return user

# Eliminar un usuario
@router.delete("/{user_id}")
def delete_user(user_id: int, db: Session = Depends(get_db)):
    user = db.query(models.User).get(user_id)
    if not user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    db.delete(user)
    db.commit()
    return {"message": "Usuario eliminado"}
