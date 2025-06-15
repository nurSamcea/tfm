from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    ENVIRONMENT: str
    DATABASE_URL: str
    SECRET_KEY: str
    DEBUG: str
    ALGORITHM: str
    ACCESS_TOKEN_EXPIRE_MINUTES: int
    BLOCKCHAIN_URL: str = "http://localhost:8545"  # URL por defecto para desarrollo local

    class Config:
        env_file = ".env"


settings = Settings()

