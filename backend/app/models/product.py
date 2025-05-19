from sqlalchemy import Column, Integer, String, Text, Numeric, Date, Boolean, ForeignKey, JSON, ARRAY, TIMESTAMP
from backend.app.database import Base

class ProductTemplate(Base):
    __tablename__ = "product_templates"
    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    category = Column(Text)
    subcategory = Column(Text)
    default_image_url = Column(Text)
    nutrition = Column(JSON)
    allergens = Column(ARRAY(Text))
    labels = Column(ARRAY(Text))

class Product(Base):
    __tablename__ = "products"
    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    description = Column(Text)
    price = Column(Numeric(10, 2))
    stock = Column(Integer)
    unit = Column(Text)
    expiration_date = Column(Date)
    image_url = Column(Text)
    is_local = Column(Boolean, default=True)
    owner_id = Column(Integer, ForeignKey("users.id"))
    template_id = Column(Integer, ForeignKey("product_templates.id"))
    is_active = Column(Boolean, default=True)
    created_at = Column(TIMESTAMP)
