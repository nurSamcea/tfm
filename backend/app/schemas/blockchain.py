from pydantic import BaseModel
from typing import Literal, Optional
from datetime import datetime

class BlockchainLogCreate(BaseModel):
    transaction_id: int
    block_hash: Optional[str]
    block_timestamp: Optional[datetime]
    status: Literal['initiated','validated','error']
    qr_code_url: Optional[str]

class BlockchainLogOut(BlockchainLogCreate):
    id: int
    created_at: datetime
    class Config:
        orm_mode = True
