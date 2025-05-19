from backend.app.database import Base
from sqlalchemy import Column, Integer, DECIMAL, ForeignKey, TIMESTAMP


class ImpactMetric(Base):
    __tablename__ = "impact_metrics"
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id"))
    co2_saved = Column(DECIMAL)
    km_saved = Column(DECIMAL)
    local_support_pct = Column(DECIMAL)
    zero_waste_score = Column(DECIMAL)
    calculated_at = Column(TIMESTAMP)
