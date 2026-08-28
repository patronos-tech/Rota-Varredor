import os
from pydantic_settings import BaseSettings
from dotenv import load_dotenv

load_dotenv()

class Settings(BaseSettings):
    # App
    APP_NAME: str = "Rota Varredor"
    DEBUG: bool = os.getenv("DEBUG", "False") == "True"
    VERSION: str = "0.1.0"

    # Database
    MONGODB_URL: str = os.getenv("MONGODB_URL", "mongodb://localhost:27017")
    MONGODB_DATABASE: str = os.getenv("MONGODB_DATABASE", "rota-varredor")

    # Server
    HOST: str = os.getenv("HOST", "0.0.0.0")
    PORT: int = int(os.getenv("PORT", "8000"))

    # Security
    JWT_SECRET: str = os.getenv("JWT_SECRET", "your-secret-key-change-in-production")
    JWT_ALGORITHM: str = "HS256"
    JWT_EXPIRATION_HOURS: int = 168

    # Logging
    LOG_LEVEL: str = os.getenv("LOG_LEVEL", "info")

    class Config:
        env_file = ".env"
        case_sensitive = True

settings = Settings()
