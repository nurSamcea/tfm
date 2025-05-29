from sqlalchemy import Column, Integer, Text, DateTime
from backend.app.database import Base

class BlockchainLog(Base):
    __tablename__ = "blockchain_logs"

    id = Column(Integer, primary_key=True)
    entity_type = Column(Text)
    entity_id = Column(Integer)
    hash = Column(Text)
    timestamp = Column(DateTime)
