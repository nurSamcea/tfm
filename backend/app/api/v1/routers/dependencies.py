# app/dependencies.py

from backend.app.database import get_db
from backend.app.models import User  # tu modelo real
from fastapi import Depends, HTTPException
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")  # o tu endpoint real


def get_current_user(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)) -> User:
    # Aquí deberías validar el token y extraer el usuario real
    user = db.query(User).filter(User.id == 1).first()  # Simulación
    if not user:
        raise HTTPException(status_code=401, detail="Invalid authentication")
    return user
