from sqlalchemy import Column, Integer, Text, ForeignKey, TIMESTAMP, CheckConstraint

from backend.app.database import Base


class BlockchainLog(Base):
    __tablename__ = "blockchain_logs"
    id = Column(Integer, primary_key=True)
    transaction_id = Column(Integer, ForeignKey("transactions.id"))
    block_hash = Column(Text)
    block_timestamp = Column(TIMESTAMP)
    status = Column(Text)
    qr_code_url = Column(Text)
    created_at = Column(TIMESTAMP)

    __table_args__ = (
        CheckConstraint("status IN ('initiated','validated','error')", name="check_blockchain_status"),
    )
