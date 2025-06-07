from sqlalchemy import Column, Integer, Float, ForeignKey
from sqlalchemy.orm import relationship
from backend.app.database import Base

class ImpactMetric(Base):
    """Métricas de impacto ambiental y social por usuario."""
    __tablename__ = "impact_metrics"

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    co2_saved_kg = Column(Float)
    local_support_eur = Column(Float)
    waste_prevented_kg = Column(Float)
    nutritional_consumption = Column(Float, nullable=True)

    # Relaciones
    user = relationship("User", back_populates="impact_metrics")
