from sqlalchemy import Column, Integer, ForeignKey, Numeric, Text, TIMESTAMP, CheckConstraint
from backend.app.database import Base

class Transaction(Base):
    __tablename__ = "transactions"
    id = Column(Integer, primary_key=True)
    buyer_id = Column(Integer, ForeignKey("users.id"))
    seller_id = Column(Integer, ForeignKey("users.id"))
    total_price = Column(Numeric(10, 2))
    payment_method = Column(Text)
    status = Column(Text, default='pending')
    delivery_method = Column(Text)
    delivery_estimated = Column(TIMESTAMP)
    created_at = Column(TIMESTAMP)

    __table_args__ = (
        CheckConstraint("status IN ('pending','paid','delivered','cancelled')", name="check_transaction_status"),
    )

class TransactionItem(Base):
    __tablename__ = "transaction_items"
    id = Column(Integer, primary_key=True)
    transaction_id = Column(Integer, ForeignKey("transactions.id"))
    product_id = Column(Integer, ForeignKey("products.id"))
    quantity = Column(Integer)
    unit_price = Column(Numeric(10,2))
    qr_id = Column(Integer, ForeignKey("product_qrs.id"))
