from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List, Dict, Optional

from backend.app import schemas, models
from backend.app.database import get_db
from backend.app.algorithms.blockchain_manager import BlockchainManager
from backend.app.core.config import settings

router = APIRouter(prefix="/blockchain", tags=["Blockchain"])

# Inicializar el gestor de blockchain
blockchain_manager = BlockchainManager(settings.BLOCKCHAIN_URL)


@router.post("/products/{product_id}/register")
def register_product(
    product_id: int,
    provider_id: int,
    private_key: str,
    db: Session = Depends(get_db)
):
    """
    Registra un producto en la blockchain.
    """
    # Obtener el producto de la base de datos
    product = db.query(models.Product).filter(models.Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    
    # Verificar que el proveedor es el propietario del producto
    if product.provider_id != provider_id:
        raise HTTPException(status_code=403, detail="No autorizado para registrar este producto")
    
    # Crear registro en la blockchain
    product_data = {
        "id": product.id,
        "name": product.name,
        "category": product.category,
        "is_eco": product.is_eco,
        "provider_lat": product.provider.location_lat,
        "provider_lon": product.provider.location_lon
    }
    
    transaction = blockchain_manager.create_product_record(
        product_data=product_data,
        provider_id=provider_id,
        private_key=private_key
    )
    
    # Guardar el log en la base de datos
    db_log = models.BlockchainLog(
        entity_type="product",
        entity_id=product_id,
        hash=transaction.hash,
        timestamp=transaction.timestamp
    )
    db.add(db_log)
    db.commit()
    db.refresh(db_log)
    
    return {
        "message": "Producto registrado exitosamente",
        "transaction": {
            "hash": transaction.hash,
            "timestamp": transaction.timestamp.isoformat()
        }
    }


@router.post("/transactions/{transaction_id}/register")
def register_transaction(
    transaction_id: int,
    user_id: int,
    private_key: str,
    db: Session = Depends(get_db)
):
    """
    Registra una transacción en la blockchain.
    """
    # Obtener la transacción de la base de datos
    transaction = db.query(models.Transaction).filter(models.Transaction.id == transaction_id).first()
    if not transaction:
        raise HTTPException(status_code=404, detail="Transacción no encontrada")
    
    # Verificar que el usuario es el propietario de la transacción
    if transaction.user_id != user_id:
        raise HTTPException(status_code=403, detail="No autorizado para registrar esta transacción")
    
    # Crear registro en la blockchain
    transaction_data = {
        "id": transaction.id,
        "items": [
            {
                "product_id": item.product_id,
                "quantity": item.quantity,
                "price": item.price
            }
            for item in transaction.items
        ],
        "total_amount": transaction.total_amount
    }
    
    blockchain_transaction = blockchain_manager.create_transaction_record(
        transaction_data=transaction_data,
        user_id=user_id,
        private_key=private_key
    )
    
    # Guardar el log en la base de datos
    db_log = models.BlockchainLog(
        entity_type="transaction",
        entity_id=transaction_id,
        hash=blockchain_transaction.hash,
        timestamp=blockchain_transaction.timestamp
    )
    db.add(db_log)
    db.commit()
    db.refresh(db_log)
    
    return {
        "message": "Transacción registrada exitosamente",
        "transaction": {
            "hash": blockchain_transaction.hash,
            "timestamp": blockchain_transaction.timestamp.isoformat()
        }
    }


@router.get("/products/{product_id}/verify")
def verify_product(
    product_id: int,
    provider_id: int,
    db: Session = Depends(get_db)
):
    """
    Verifica la autenticidad de un producto en la blockchain.
    """
    is_authentic, verification_data = blockchain_manager.verify_product_authenticity(
        product_id=product_id,
        provider_id=provider_id
    )
    
    if not is_authentic:
        raise HTTPException(
            status_code=400,
            detail=verification_data.get("error", "No se pudo verificar la autenticidad del producto")
        )
    
    return {
        "is_authentic": True,
        "verification_data": verification_data
    }


@router.get("/products/{product_id}/history")
def get_product_history(
    product_id: int,
    db: Session = Depends(get_db)
):
    """
    Obtiene el historial completo de un producto en la blockchain.
    """
    # Verificar que el producto existe
    product = db.query(models.Product).filter(models.Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    
    history = blockchain_manager.get_product_history(product_id)
    
    return {
        "product_id": product_id,
        "product_name": product.name,
        "history": history
    }


@router.get("/logs", response_model=List[schemas.BlockchainLogOut])
def read_blockchain_logs(
    entity_type: Optional[str] = None,
    entity_id: Optional[int] = None,
    db: Session = Depends(get_db)
):
    """
    Obtiene los logs de la blockchain con filtros opcionales.
    """
    query = db.query(models.BlockchainLog)
    
    if entity_type:
        query = query.filter(models.BlockchainLog.entity_type == entity_type)
    if entity_id:
        query = query.filter(models.BlockchainLog.entity_id == entity_id)
    
    return query.all()
