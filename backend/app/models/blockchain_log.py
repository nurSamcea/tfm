from sqlalchemy import Column, Integer, Text, DateTime, Enum
from backend.app.database import Base
import enum

class BlockchainEntityType(enum.Enum):
    product = 'product'
    transaction = 'transaction'
    sensor = 'sensor'

class BlockchainLog(Base):
    """Registro de eventos en blockchain para trazabilidad."""
    __tablename__ = "blockchain_logs"

    id = Column(Integer, primary_key=True)
    entity_type = Column(Enum(BlockchainEntityType, name="blockchainentitytypeenum"))
    entity_id = Column(Integer)
    hash = Column(Text)
    timestamp = Column(DateTime)
