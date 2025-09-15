from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy import inspect
from typing import Iterable, List, Set, Tuple
import logging

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


# ==============================================================
# Utilidades de gestión de esquema (creación selectiva y limpieza)
# ==============================================================
logger = logging.getLogger(__name__)


def _get_declared_tables() -> Set[str]:
    """Devuelve los nombres de tablas declaradas en los modelos.

    Principio de responsabilidad única (SRP): esta función sólo conoce de metadatos.
    """
    return set(Base.metadata.tables.keys())


def _get_existing_tables() -> Set[str]:
    """Lista las tablas existentes en la BD actual."""
    inspector = inspect(engine)
    return set(inspector.get_table_names())


def ensure_tables_exist(table_names: Iterable[str]) -> List[str]:
    """Crea de forma idempotente las tablas indicadas si faltan.

    - No toca otras tablas.
    - Devuelve la lista de tablas que ha creado.
    """
    created: List[str] = []
    existing = _get_existing_tables()
    for table_name in table_names:
        if table_name in existing:
            continue
        table_obj = Base.metadata.tables.get(table_name)
        if table_obj is None:
            logger.warning("Tabla '%s' no está declarada en los modelos; se omite", table_name)
            continue
        table_obj.create(bind=engine, checkfirst=True)
        created.append(table_name)
        logger.info("Tabla creada: %s", table_name)
    return created


def compute_unused_tables(keep_tables: Iterable[str], also_keep: Iterable[str] | None = None) -> Set[str]:
    """Calcula qué tablas sobran comparando BD vs. modelos.

    - keep_tables: conjunto permitido (p.ej., tablas de modelos en uso).
    - also_keep: extras que nunca deben borrarse (p.ej., 'alembic_version').
    """
    keep: Set[str] = set(keep_tables)
    if also_keep:
        keep.update(set(also_keep))
    existing = _get_existing_tables()
    return existing - keep


def drop_tables(table_names: Iterable[str]) -> List[str]:
    """Elimina las tablas especificadas si existen. Devuelve las eliminadas.

    Cumple OCP: no asume nada de qué se considera "sobra"; sólo ejecuta.
    """
    dropped: List[str] = []
    inspector = inspect(engine)
    existing = set(inspector.get_table_names())
    for table_name in table_names:
        if table_name not in existing:
            continue
        table_obj = Base.metadata.tables.get(table_name)
        if table_obj is None:
            # Construir objeto Table dinámico cuando no está en metadatos (p.ej., legacy)
            from sqlalchemy import Table, MetaData
            metadata = MetaData()
            table_obj = Table(table_name, metadata, autoload_with=engine)
        table_obj.drop(bind=engine, checkfirst=True)
        dropped.append(table_name)
        logger.info("Tabla eliminada: %s", table_name)
    return dropped


def sync_schema(create_only: bool = True, drop_unused: bool = False) -> Tuple[List[str], List[str]]:
    """Sincroniza el esquema de forma segura y minimalista.

    - create_only: si True, sólo crea tablas faltantes según los modelos.
    - drop_unused: si True, elimina tablas existentes que no estén en los modelos
      ni en la lista de preservación (alembic_version).

    Devuelve (created, dropped).
    """
    declared = _get_declared_tables()
    # Crear únicamente lo que falte (idempotente)
    created = ensure_tables_exist(declared) if create_only else []

    dropped: List[str] = []
    if drop_unused:
        preserve = {"alembic_version"}
        extras = compute_unused_tables(declared, also_keep=preserve)
        if extras:
            dropped = drop_tables(extras)
    return created, dropped


def ensure_sensor_alerts_table() -> bool:
    """Crea únicamente la tabla 'sensor_alerts' si falta. Devuelve True si la creó."""
    created = ensure_tables_exist(["sensor_alerts"]) 
    return "sensor_alerts" in created
