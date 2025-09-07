# app/dependencies.py

from backend.app.database import get_db
from backend.app.models import User
from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session
import logging

from backend.app.core.security import decode_access_token

logger = logging.getLogger(__name__)

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/auth/login")


def get_current_user(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)) -> User:
    logger.info(f"=== AUTHENTICATION DEBUG ===")
    logger.info(f"Token received: {token[:20]}..." if token else "No token")
    
    payload = decode_access_token(token)
    logger.info(f"Decoded payload: {payload}")
    
    if not payload or "sub" not in payload:
        logger.error("Invalid token or missing 'sub' field")
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid authentication")
    
    user_id = int(payload["sub"])
    logger.info(f"Looking for user ID: {user_id}")
    
    user = db.query(User).filter(User.id == user_id).first()
    if not user:
        logger.error(f"User not found with ID: {user_id}")
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="User not found")
    
    logger.info(f"User found: {user.name} (ID: {user.id}, Role: {user.role})")
    logger.info(f"========================")
    return user
