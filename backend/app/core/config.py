from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    ENVIRONMENT: str
    DATABASE_URL: str
    SECRET_KEY: str
    DEBUG: str
    ALGORITHM: str
    ACCESS_TOKEN_EXPIRE_MINUTES: int
    BLOCKCHAIN_URL: str = "http://localhost:8545"  # URL por defecto para desarrollo local
    CONTRACT_ADDRESS: str = "0x0000000000000000000000000000000000000000"  # Reemplazar con la dirección real

    class Config:
        case_sensitive = True
        env_file = ".env"


settings = Settings()

