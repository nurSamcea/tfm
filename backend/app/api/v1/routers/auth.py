from datetime import timedelta
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from backend.app.database import get_db
from backend.app import models
from backend.app.core.security import verify_password, get_password_hash, create_access_token
from backend.app.core.config import settings
from backend.app.schemas.auth import LoginRequest, RegisterRequest, Token, RegisterResponse

router = APIRouter(prefix="/auth", tags=["Auth"]) 


@router.post("/login", response_model=Token)
def login(payload: LoginRequest, db: Session = Depends(get_db)):
    """Iniciar sesión de usuario"""
    try:
        # Buscar usuario por email
        user = db.query(models.User).filter(models.User.email == payload.email).first()
        if not user:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED, 
                detail="Email o contraseña incorrectos"
            )

        # Verificar contraseña
        if not verify_password(payload.password, user.password_hash):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED, 
                detail="Email o contraseña incorrectos"
            )

        # Crear token de acceso
        access_token_expires = timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
        token = create_access_token({"sub": str(user.id)}, expires_delta=access_token_expires)
        
        # Retornar token con información del usuario
        return Token(
            access_token=token,
            user_id=user.id,
            user_email=user.email,
            user_role=user.role.value,
            user_name=user.name
        )
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )


@router.post("/register", response_model=RegisterResponse)
def register(payload: RegisterRequest, db: Session = Depends(get_db)):
    """Registrar nuevo usuario"""
    try:
        # Verificar si el email ya está registrado
        existing = db.query(models.User).filter(models.User.email == payload.email).first()
        if existing:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST, 
                detail="El email ya está registrado"
            )

        # Crear nuevo usuario
        from backend.app.models.user import UserRoleEnum
        
        try:
            role_enum = UserRoleEnum(payload.role)
        except ValueError as e:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Rol inválido: {payload.role}. Roles válidos: consumer, farmer, supermarket"
            )
        
        user_data = {
            "name": payload.name,
            "email": payload.email,
            "password_hash": get_password_hash(payload.password),
            "role": role_enum,
            "entity_name": payload.entity_name,
            "location_lat": payload.location_lat,
            "location_lon": payload.location_lon,
            "preferences": payload.preferences
        }
        
        # Filtrar valores None
        user_data = {k: v for k, v in user_data.items() if v is not None}
        
        user = models.User(**user_data)
        db.add(user)
        db.commit()
        db.refresh(user)
        
        return RegisterResponse(
            id=user.id,
            name=user.name,
            email=user.email,
            role=user.role.value,
            entity_name=user.entity_name
        )
        
    except HTTPException:
        raise
    except Exception as e:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )
