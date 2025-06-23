from typing import List, Dict, Optional
from pydantic import BaseModel, Field
from datetime import datetime


class ProductRegistration(BaseModel):
    id: int
    name: str
    category: str
    is_eco: bool = False
    provider_lat: float
    provider_lon: float
    impact_metrics: Optional[Dict] = None


class TransactionItem(BaseModel):
    product_id: int
    quantity: int
    price: float


class TransactionRegistration(BaseModel):
    id: int
    items: List[TransactionItem]
    total_amount: float = Field(..., description="Monto total de la transacción")


class ProductVerification(BaseModel):
    is_authentic: bool
    details: Dict
    verification_date: datetime = Field(default_factory=datetime.utcnow)


class Transaction(BaseModel):
    id: int
    buyer: str
    product_id: int
    quantity: int
    price: float
    timestamp: datetime


class ProductHistory(BaseModel):
    product_id: int
    creation_date: datetime
    transactions: List[Transaction]
    previous_owners: List[str] 