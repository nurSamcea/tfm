from sqlalchemy import Column, Integer, Text, DateTime, ForeignKey, DECIMAL, Enum, JSON
from sqlalchemy.orm import relationship
from backend.app.database import Base
from datetime import datetime
import enum

class TransactionStatusEnum(enum.Enum):
    in_progress = "in_progress"  # En curso - Agricultor preparando
    delivered = "delivered"  # Entregado - Listo para venta en supermercado
    cancelled = "cancelled"  # Cancelado

class Transaction(Base):
    """Modelo de transacción para pedidos entre vendedores y compradores."""
    __tablename__ = "transactions"

    id = Column(Integer, primary_key=True)
    buyer_id = Column(Integer, ForeignKey("users.id"), nullable=False)        # Comprador (supermercado o consumidor)
    seller_id = Column(Integer, ForeignKey("users.id"), nullable=False)       # Vendedor (agricultor o supermercado)
    buyer_type = Column(Text, nullable=False)  # "supermarket" o "consumer"
    seller_type = Column(Text, nullable=False)  # "farmer" o "supermarket"
    total_price = Column(DECIMAL, nullable=False)
    currency = Column(Text, default="EUR")
    status = Column(Enum(TransactionStatusEnum), nullable=False, default=TransactionStatusEnum.in_progress)
    created_at = Column(DateTime, default=datetime.utcnow)
    confirmed_at = Column(DateTime)
    delivered_at = Column(DateTime)
    
    # Detalles del pedido (productos y cantidades)
    order_details = Column(JSON, nullable=False)  # Lista de productos con cantidades
    
    # Relaciones
    buyer = relationship("User", foreign_keys=[buyer_id])
    seller = relationship("User", foreign_keys=[seller_id])
