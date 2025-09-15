from pydantic import BaseModel, EmailStr
from typing import Optional, Literal
from datetime import datetime


class UserBase(BaseModel):
    name: str
    email: EmailStr
    role: Literal['consumer', 'farmer', 'supermarket']
    entity_name: Optional[str]
    location_lat: Optional[float]
    location_lon: Optional[float]
    preferences: Optional[dict]


class UserCreate(UserBase):
    password: str


class UserUpdate(BaseModel):
    name: Optional[str]
    role: Optional[Literal['consumer', 'farmer', 'supermarket']]
    entity_name: Optional[str]
    location_lat: Optional[float]
    location_lon: Optional[float]
    preferences: Optional[dict]
    password: Optional[str]


class UserRead(UserBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True
        use_enum_values = True


class UserSelect(BaseModel):
    """Esquema simplificado para selección de usuarios en la interfaz"""
    id: int
    name: str
    email: str
    entity_name: Optional[str]
    role: str
    
    class Config:
        from_attributes = True
        use_enum_values = True


class UserLocationUpdate(BaseModel):
    """Payload para actualizar la ubicación del usuario autenticado"""
    location_lat: float
    location_lon: float
