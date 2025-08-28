from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from sqlalchemy import and_
from datetime import datetime

from backend.app import schemas, database, models
from backend.app.database import get_db
from backend.app.algorithms.impact_calculator import ImpactCalculator

router = APIRouter(prefix="/transactions", tags=["Transactions"])


@router.post("/", response_model=schemas.TransactionOut)
def create_transaction(tx: schemas.TransactionCreate, db: Session = Depends(get_db)):
    """
    Crea una transacción y actualiza el stock de productos.
    Valida disponibilidad antes de procesar la compra.
    """
    try:
        # 1. Validar que el usuario existe
        user = db.query(models.User).filter(models.User.id == tx.user_id).first()
        if not user:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Usuario no encontrado"
            )

        # 2. Validar que la lista de compra existe
        shopping_list = db.query(models.ShoppingList).filter(
            models.ShoppingList.id == tx.shopping_list_id
        ).first()
        if not shopping_list:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Lista de compra no encontrada"
            )

        # 3. Obtener items de la lista de compra
        shopping_items = db.query(models.ShoppingListItem).filter(
            models.ShoppingListItem.shopping_list_id == tx.shopping_list_id
        ).all()

        if not shopping_items:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="La lista de compra está vacía"
            )

        # 4. Validar stock disponible para cada producto
        products_to_update = []
        for item in shopping_items:
            product = db.query(models.Product).filter(
                models.Product.id == item.product_id
            ).first()
            
            if not product:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail=f"Producto {item.product_id} no encontrado"
                )
            
            if product.stock_available < item.quantity:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail=f"Stock insuficiente para {product.name}. Disponible: {product.stock_available}, Solicitado: {item.quantity}"
                )
            
            products_to_update.append((product, item.quantity))

        # 5. Crear la transacción
        db_tx = models.Transaction(
            user_id=tx.user_id,
            shopping_list_id=tx.shopping_list_id,
            total_price=tx.total_price,
            currency=tx.currency or "EUR",
            status="paid",
            created_at=datetime.utcnow(),
            confirmed_at=datetime.utcnow()
        )
        db.add(db_tx)
        db.flush()  # Para obtener el ID de la transacción

        # 6. Actualizar stock de productos
        for product, quantity in products_to_update:
            product.stock_available -= quantity
            if product.stock_available < 0:
                product.stock_available = 0

        # 7. Calcular y registrar métricas de impacto
        try:
            total_co2_saved = 0.0
            total_local_support = 0.0
            total_waste_prevented = 0.0
            
            for product, quantity in products_to_update:
                distance_km = 0.0
                if (product.provider and product.provider.location_lat and 
                    product.provider.location_lon and user.location_lat and user.location_lon):
                    # Aquí podrías calcular la distancia real usando geopy
                    distance_km = 0.0
                
                impact = ImpactCalculator.calculate_product_impact(
                    product={
                        "category": product.category or "otros",
                        "price": float(product.price or 0)
                    },
                    quantity=float(quantity),
                    distance_km=distance_km,
                    provider_type=str(getattr(product.provider, "role", "distribuidor"))
                )
                total_co2_saved += impact.co2_saved_kg
                total_local_support += impact.local_support_eur
                total_waste_prevented += impact.waste_prevented_kg

            # Crear métrica de impacto
            metric = models.ImpactMetric(
                user_id=tx.user_id,
                product_id=None,  # Métrica general de la transacción
                co2_saved_kg=round(total_co2_saved, 2),
                local_support_eur=round(total_local_support, 2),
                waste_prevented_kg=round(total_waste_prevented, 2),
                sustainability_score=0.0,
            )
            db.add(metric)
            
        except Exception as e:
            # No fallar la transacción si el cálculo de impacto falla
            print(f"Error calculando impacto: {e}")

        # 8. Confirmar todos los cambios
        db.commit()
        db.refresh(db_tx)

        return db_tx

    except HTTPException:
        db.rollback()
        raise
    except Exception as e:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )


@router.get("/", response_model=list[schemas.TransactionOut])
def read_transactions(db: Session = Depends(get_db)):
    return db.query(models.Transaction).all()
