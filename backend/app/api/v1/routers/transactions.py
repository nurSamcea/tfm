from typing import List

from backend.app import models, schemas
from backend.app.database import get_db
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

router = APIRouter(prefix="/transactions", tags=["transactions"])


# Crear transacción (pedido)
@router.post("/", response_model=schemas.TransactionOut)
def create_transaction(transaction: schemas.TransactionCreate, db: Session = Depends(get_db)):
    db_tx = models.Transaction(**transaction.dict())
    db.add(db_tx)
    db.commit()
    db.refresh(db_tx)
    return db_tx


# Ver transacción por ID
@router.get("/{transaction_id}", response_model=schemas.TransactionOut)
def get_transaction(transaction_id: int, db: Session = Depends(get_db)):
    tx = db.query(models.Transaction).get(transaction_id)
    if not tx:
        raise HTTPException(status_code=404, detail="Transacción no encontrada")
    return tx


# Listar transacciones por usuario
@router.get("/by_user/{user_id}", response_model=List[schemas.TransactionOut])
def get_transactions_by_user(user_id: int, db: Session = Depends(get_db)):
    return db.query(models.Transaction).filter(models.Transaction.buyer_id == user_id).all()


# Cambiar estado de una transacción
@router.put("/{transaction_id}/status", response_model=schemas.TransactionOut)
def update_transaction_status(transaction_id: int, status: str, db: Session = Depends(get_db)):
    tx = db.query(models.Transaction).get(transaction_id)
    if not tx:
        raise HTTPException(status_code=404, detail="Transacción no encontrada")
    if status not in ['pending', 'paid', 'delivered', 'cancelled']:
        raise HTTPException(status_code=400, detail="Estado no válido")
    tx.status = status
    db.commit()
    db.refresh(tx)
    return tx
