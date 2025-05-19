from typing import List

from backend.app import models, schemas
from backend.app.database import get_db
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

router = APIRouter(prefix="/blockchain", tags=["blockchain"])


# Obtener todos los logs blockchain (admin/debug)
@router.get("/", response_model=List[schemas.BlockchainLogOut])
def get_all_logs(db: Session = Depends(get_db)):
    return db.query(models.BlockchainLog).all()


# Obtener logs por transacción
@router.get("/by_transaction/{transaction_id}", response_model=List[schemas.BlockchainLogOut])
def get_logs_by_transaction(transaction_id: int, db: Session = Depends(get_db)):
    return db.query(models.BlockchainLog).filter(models.BlockchainLog.transaction_id == transaction_id).all()
