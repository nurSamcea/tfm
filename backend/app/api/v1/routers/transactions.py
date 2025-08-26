from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from backend.app import schemas, database, models
from backend.app.database import get_db
from backend.app.algorithms.impact_calculator import ImpactCalculator

router = APIRouter(prefix="/transactions", tags=["Transactions"])


@router.post("/", response_model=schemas.TransactionOut)
def create_transaction(tx: schemas.TransactionCreate, db: Session = Depends(get_db)):
    db_tx = models.Transaction(**tx.dict())
    db.add(db_tx)
    db.commit()
    db.refresh(db_tx)

    # Registrar métrica de impacto básica (ejemplo): sumar por ítems si hay datos disponibles
    try:
        total_co2_saved = 0.0
        total_local_support = 0.0
        total_waste_prevented = 0.0
        for item in getattr(db_tx, "items", []) or []:
            product = db.query(models.Product).get(item.product_id)
            if not product:
                continue
            distance_km = 0.0
            if product.provider and product.provider.location_lat and product.provider.location_lon and db_tx.user and db_tx.user.location_lat and db_tx.user.location_lon:
                # Si hay coordenadas, se podrían calcular distancias reales aquí
                distance_km = 0.0
            impact = ImpactCalculator.calculate_product_impact(
                product={"category": product.category or "otros", "price": float(product.price or 0)},
                quantity=float(getattr(item, "quantity", 1) or 1),
                distance_km=distance_km,
                provider_type=str(getattr(product.provider, "role", "distribuidor"))
            )
            total_co2_saved += impact.co2_saved_kg
            total_local_support += impact.local_support_eur
            total_waste_prevented += impact.waste_prevented_kg

        metric = models.ImpactMetric(
            user_id=getattr(db_tx, "user_id", None),
            product_id=None,
            co2_saved_kg=round(total_co2_saved, 2),
            local_support_eur=round(total_local_support, 2),
            waste_prevented_kg=round(total_waste_prevented, 2),
            sustainability_score=0.0,
        )
        db.add(metric)
        db.commit()
    except Exception:
        # Evitar romper la transacción de negocio si el cálculo de impacto falla
        db.rollback()

    return db_tx


@router.get("/", response_model=list[schemas.TransactionOut])
def read_transactions(db: Session = Depends(get_db)):
    return db.query(models.Transaction).all()
