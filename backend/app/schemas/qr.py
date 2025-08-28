from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class QRBase(BaseModel):
    product_id: int
    qr_hash: str
    qr_metadata: Optional[dict]

class QRCreate(QRBase):
    pass

class QRRead(QRBase):
    id: int
    created_at: datetime

    class Config:
        from_attributes = True

class QrOut(QRRead):
    class Config:
        from_attributes = True