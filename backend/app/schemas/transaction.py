from pydantic import BaseModel, Field
from typing import Optional, List, Dict, Any, Union
from datetime import datetime
from backend.app.models.transaction import TransactionStatusEnum

class OrderItem(BaseModel):
    """Item individual del pedido"""
    product_id: int
    product_name: str
    quantity: float
    unit_price: float
    total_price: float

class TransactionBase(BaseModel):
    buyer_id: int
    seller_id: int
    buyer_type: str
    seller_type: str
    total_price: float
    currency: str = "EUR"
    status: TransactionStatusEnum
    order_details: Union[List[OrderItem], List[Dict[str, Any]], Dict[str, Any]] = Field(description="Lista de items del pedido")

class TransactionCreate(BaseModel):
    """Esquema para crear una nueva transacción desde el carrito"""
    seller_id: int
    seller_type: str  # "farmer" o "supermarket"
    order_details: List[OrderItem]
    total_price: float
    currency: str = "EUR"

class TransactionUpdate(BaseModel):
    """Esquema para actualizar el estado de una transacción"""
    status: TransactionStatusEnum

class TransactionRead(TransactionBase):
    id: int
    created_at: datetime
    confirmed_at: Optional[datetime] = None
    delivered_at: Optional[datetime] = None

    class Config:
        from_attributes = True

class TransactionOut(TransactionRead):
    """Esquema de salida con información adicional"""
    buyer_name: Optional[str] = None
    seller_name: Optional[str] = None

    class Config:
        from_attributes = True