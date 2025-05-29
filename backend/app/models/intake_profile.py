from sqlalchemy import Column, Integer, Float, Text
from backend.app.database import Base

class IntakeProfile(Base):
    __tablename__ = "intake_profiles"

    id = Column(Integer, primary_key=True)
    name = Column(Text)
    gender = Column(Text)
    age_range = Column(Text)
    activity = Column(Text)
    kcal = Column(Integer)
    protein = Column(Float)
    carbohydrates = Column(Float)
    sugars = Column(Float)
    fat = Column(Float)
    saturated_fat = Column(Float)
    fiber = Column(Float)
    salt = Column(Float)
    cholesterol = Column(Float)
    calcium = Column(Float)
    iron = Column(Float)
    vitamin_c = Column(Float)
    vitamin_d = Column(Float)
    vitamin_b12 = Column(Float)
    potassium = Column(Float)
    magnesium = Column(Float)
