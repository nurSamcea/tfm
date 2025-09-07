from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

from backend.app.core.config import settings

engine = create_engine(settings.DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

# Importar todos los modelos para que SQLAlchemy los reconozca
from backend.app.models import (
    user, product, transaction, qr, sensor_reading, sensor, sensor_alert,
    blockchain_log, blockchain_traceability, impact_metric, shopping_list,
    shopping_list_group, shopping_list_item
)


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
