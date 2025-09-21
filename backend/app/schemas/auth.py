from pydantic import BaseModel, EmailStr, validator
from typing import Optional


class Token(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user_id: int
    user_email: str
    user_role: str
    user_name: str


class TokenPayload(BaseModel):
    sub: str


class LoginRequest(BaseModel):
    email: EmailStr
    password: str


class RegisterRequest(BaseModel):
    name: str
    email: EmailStr
    password: str
    role: str
    entity_name: Optional[str] = None
    location_lat: Optional[float] = None
    location_lon: Optional[float] = None
    preferences: Optional[dict] = None
    
    @validator('password')
    def validate_password(cls, v):
        if len(v) < 6:
            raise ValueError('La contraseña debe tener al menos 6 caracteres')
        return v
    
    @validator('role')
    def validate_role(cls, v):
        allowed_roles = ['consumer', 'farmer', 'supermarket']
        if v not in allowed_roles:
            raise ValueError(f'El rol debe ser uno de: {", ".join(allowed_roles)}')
        return v


class RegisterResponse(BaseModel):
    id: int
    name: str
    email: str
    role: str
    entity_name: Optional[str]
    message: str = "Usuario registrado exitosamente"
