from sqlalchemy import Column, Integer, String, Text, DateTime, JSON, DECIMAL, ForeignKey, CheckConstraint, Index, Enum
from sqlalchemy.orm import relationship
from datetime import datetime
from backend.app.database import Base
import enum

class UserRoleEnum(enum.Enum):
    consumer = "consumer"
    farmer = "farmer"
    supermarket = "supermarket"

class User(Base):
    """Modelo de usuario para productores, minoristas, supermercados y consumidores."""
    __tablename__ = "users"
    __table_args__ = (
        Index('ix_users_email', 'email'),
    )

    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    email = Column(Text, unique=True, nullable=False, index=True)
    password_hash = Column(Text, nullable=False)
    role = Column(Enum(UserRoleEnum), nullable=False)
    entity_name = Column(Text)
    location_lat = Column(DECIMAL)
    location_lon = Column(DECIMAL)
    preferences = Column(JSON)
    created_at = Column(DateTime, default=datetime.utcnow)

    # Relaciones
    # intake_profile = relationship("IntakeProfile", back_populates="users")
    products = relationship("Product", back_populates="provider")
    shopping_lists = relationship("ShoppingList", back_populates="user")
    impact_metrics = relationship("ImpactMetric", back_populates="user")
    sensor_zones = relationship("SensorZone", back_populates="farmer")
