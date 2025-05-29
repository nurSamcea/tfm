from datetime import datetime

from pydantic import BaseModel


class BlockchainLogBase(BaseModel):
    entity_type: str
    entity_id: int
    hash: str


class BlockchainLogCreate(BlockchainLogBase):
    timestamp: datetime


class BlockchainLogRead(BlockchainLogBase):
    id: int
    timestamp: datetime

    class Config:
        orm_mode = True


class BlockchainLogOut(BlockchainLogRead):
    class Config:
        orm_mode = True
        json_encoders = {
            datetime: lambda v: v.isoformat() if isinstance(v, datetime) else v
        }
