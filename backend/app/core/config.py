from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    # Variables de entorno básicas
    ENVIRONMENT: str
    DATABASE_URL: str
    SECRET_KEY: str
    DEBUG: str
    ALGORITHM: str
    ACCESS_TOKEN_EXPIRE_MINUTES: int
    
    # Variables específicas de la base de datos (para Railway)
    DB_HOST: Optional[str] = None
    DB_PORT: Optional[str] = None
    DB_USER: Optional[str] = None
    DB_PASSWORD: Optional[str] = None
    DB_NAME: Optional[str] = None
    DB_SSL_MODE: Optional[str] = None
    DB_CONNECT_TIMEOUT: Optional[str] = None
    
    # Variables de blockchain
    BLOCKCHAIN_URL: str = "http://localhost:8545"  # URL por defecto para desarrollo local
    CONTRACT_ADDRESS: str = "0x0000000000000000000000000000000000000000"  # Reemplazar con la dirección real

    class Config:
        case_sensitive = True
        env_file = ".env"
        extra = "allow"  # Permitir variables adicionales


settings = Settings()

