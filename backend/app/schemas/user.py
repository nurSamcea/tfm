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
