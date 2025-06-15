from typing import List, Dict
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.api import deps
from app.algorithms.blockchain_manager import BlockchainManager
from app.schemas.blockchain import (
    ProductRegistration,
    TransactionRegistration,
    ProductVerification,
    ProductHistory
)
from app.core.config import settings

router = APIRouter()

@router.post("/products", response_model=Dict)
def register_product(
    *,
    db: Session = Depends(deps.get_db),
    product_in: ProductRegistration,
    current_user = Depends(deps.get_current_active_user)
) -> Dict:
    """
    Registra un nuevo producto en la blockchain.
    """
    try:
        blockchain = BlockchainManager(blockchain_url=settings.BLOCKCHAIN_URL)
        result = blockchain.create_product_record(
            product_data=product_in.dict(),
            provider_id=current_user.id,
            private_key=current_user.private_key
        )
        return result
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )

@router.post("/transactions", response_model=Dict)
def register_transaction(
    *,
    db: Session = Depends(deps.get_db),
    transaction_in: TransactionRegistration,
    current_user = Depends(deps.get_current_active_user)
) -> Dict:
    """
    Registra una nueva transacción en la blockchain.
    """
    try:
        blockchain = BlockchainManager(blockchain_url=settings.BLOCKCHAIN_URL)
        result = blockchain.create_transaction_record(
            transaction_data=transaction_in.dict(),
            user_id=current_user.id,
            private_key=current_user.private_key
        )
        return result
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )

@router.get("/products/{product_id}/verify", response_model=ProductVerification)
def verify_product(
    *,
    db: Session = Depends(deps.get_db),
    product_id: int,
    current_user = Depends(deps.get_current_active_user)
) -> ProductVerification:
    """
    Verifica la autenticidad de un producto en la blockchain.
    """
    try:
        blockchain = BlockchainManager(blockchain_url=settings.BLOCKCHAIN_URL)
        is_authentic, details = blockchain.verify_product_authenticity(
            product_id=product_id,
            provider_id=current_user.id
        )
        return ProductVerification(
            is_authentic=is_authentic,
            details=details
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )

@router.get("/products/{product_id}/history", response_model=ProductHistory)
def get_product_history(
    *,
    db: Session = Depends(deps.get_db),
    product_id: int,
    current_user = Depends(deps.get_current_active_user)
) -> ProductHistory:
    """
    Obtiene el historial completo de un producto en la blockchain.
    """
    try:
        blockchain = BlockchainManager(blockchain_url=settings.BLOCKCHAIN_URL)
        history = blockchain.get_product_history(product_id=product_id)
        return ProductHistory(**history)
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        ) 