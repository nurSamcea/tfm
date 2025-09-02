from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List, Dict, Any
from datetime import datetime

from backend.app import schemas, database, models
from backend.app.database import get_db
from backend.app.algorithms.blockchain_manager import BlockchainManager
from backend.app.core.config import settings

router = APIRouter(prefix="/traceability", tags=["Traceability"])

# Inicializar el gestor de blockchain
blockchain_manager = BlockchainManager(settings.BLOCKCHAIN_URL)


@router.get("/product/{qr_hash}")
def get_product_traceability(qr_hash: str, db: Session = Depends(get_db)):
    """
    Obtiene la información completa de trazabilidad de un producto basado en su QR hash.
    """
    try:
        # 1. Buscar el QR en la base de datos
        qr = db.query(models.QR).filter(models.QR.qr_hash == qr_hash).first()
        if not qr:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="QR no encontrado"
            )

        # 2. Obtener el producto asociado
        product = db.query(models.Product).filter(models.Product.id == qr.product_id).first()
        if not product:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Producto no encontrado"
            )

        # 3. Obtener el productor
        producer = db.query(models.User).filter(models.User.id == product.provider_id).first()

        # 4. Obtener las últimas lecturas de sensores
        latest_sensor_readings = db.query(models.SensorReading).filter(
            models.SensorReading.product_id == product.id
        ).order_by(models.SensorReading.created_at.desc()).limit(5).all()

        # 5. Obtener logs de blockchain
        blockchain_logs = db.query(models.BlockchainLog).filter(
            models.BlockchainLog.entity_id == product.id
        ).order_by(models.BlockchainLog.timestamp.desc()).all()

        # 6. Obtener transacciones relacionadas
        transactions = db.query(models.Transaction).join(
            models.ShoppingListItem, models.Transaction.shopping_list_id == models.ShoppingListItem.shopping_list_id
        ).filter(
            models.ShoppingListItem.product_id == product.id
        ).order_by(models.Transaction.created_at.desc()).all()

        # 7. Construir eventos de trazabilidad
        traceability_events = []
        
        # Evento de creación del producto
        if product.created_at:
            traceability_events.append({
                "timestamp": product.created_at.isoformat(),
                "event": "Producto registrado en el sistema",
                "location": f"{producer.location_lat}, {producer.location_lon}" if producer and producer.location_lat else "Ubicación no disponible"
            })

        # Eventos de blockchain
        for log in blockchain_logs:
            traceability_events.append({
                "timestamp": log.timestamp.isoformat() if log.timestamp else datetime.utcnow().isoformat(),
                "event": f"Registrado en blockchain: {log.entity_type}",
                "hash": log.hash
            })

        # Eventos de transacciones
        for tx in transactions:
            traceability_events.append({
                "timestamp": tx.created_at.isoformat() if tx.created_at else datetime.utcnow().isoformat(),
                "event": f"Transacción realizada - Estado: {tx.status}",
                "amount": f"{tx.total_price} {tx.currency}"
            })

        # Ordenar eventos por timestamp
        traceability_events.sort(key=lambda x: x["timestamp"], reverse=True)

        # 8. Obtener temperatura y humedad actuales
        current_temperature = None
        current_humidity = None
        
        if latest_sensor_readings:
            latest_reading = latest_sensor_readings[0]
            current_temperature = latest_reading.temperature
            current_humidity = latest_reading.humidity

        # 9. Construir respuesta
        response = {
            "product_id": product.id,
            "product_name": product.name,
            "product_description": product.description,
            "category": product.category,
            "is_eco": product.is_eco,
            "origin": {
                "producer_name": producer.name if producer else "Productor no disponible",
                "producer_location": f"{producer.location_lat}, {producer.location_lon}" if producer and producer.location_lat else "Ubicación no disponible",
                "entity_name": producer.entity_name if producer else None
            },
            "current_conditions": {
                "temperature": current_temperature,
                "humidity": current_humidity,
                "last_updated": latest_sensor_readings[0].created_at.isoformat() if latest_sensor_readings else None
            },
            "certifications": {
                "eco_certified": product.is_eco,
                "local_product": producer and producer.role in ["farmer"] if producer else False,
                "organic": product.certifications.get("organic", False) if product.certifications else False
            },
            "traceability_events": traceability_events,
            "blockchain_verification": {
                "verified": len(blockchain_logs) > 0,
                "total_logs": len(blockchain_logs),
                "last_verification": blockchain_logs[0].timestamp.isoformat() if blockchain_logs else None
            },
            "qr_info": {
                "hash": qr.qr_hash,
                "created_at": qr.created_at.isoformat() if qr.created_at else None,
                "metadata": qr.qr_metadata
            }
        }

        return response

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )


@router.get("/product/{product_id}/history")
def get_product_history(product_id: int, db: Session = Depends(get_db)):
    """
    Obtiene el historial completo de un producto incluyendo todas las transacciones y eventos.
    """
    try:
        # Verificar que el producto existe
        product = db.query(models.Product).filter(models.Product.id == product_id).first()
        if not product:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Producto no encontrado"
            )

        # Obtener todas las transacciones relacionadas
        transactions = db.query(models.Transaction).join(
            models.ShoppingListItem, models.Transaction.shopping_list_id == models.ShoppingListItem.shopping_list_id
        ).filter(
            models.ShoppingListItem.product_id == product_id
        ).order_by(models.Transaction.created_at.desc()).all()

        # Obtener todas las lecturas de sensores
        sensor_readings = db.query(models.SensorReading).filter(
            models.SensorReading.product_id == product_id
        ).order_by(models.SensorReading.created_at.desc()).all()

        # Obtener logs de blockchain
        blockchain_logs = db.query(models.BlockchainLog).filter(
            models.BlockchainLog.entity_id == product_id
        ).order_by(models.BlockchainLog.timestamp.desc()).all()

        return {
            "product_id": product_id,
            "product_name": product.name,
            "transactions": [
                {
                    "id": tx.id,
                    "status": tx.status,
                    "total_price": float(tx.total_price) if tx.total_price else 0,
                    "currency": tx.currency,
                    "created_at": tx.created_at.isoformat() if tx.created_at else None,
                    "confirmed_at": tx.confirmed_at.isoformat() if tx.confirmed_at else None
                }
                for tx in transactions
            ],
            "sensor_readings": [
                {
                    "id": reading.id,
                    "temperature": reading.temperature,
                    "humidity": reading.humidity,
                    "sensor_type": reading.sensor_type.value if reading.sensor_type else None,
                    "created_at": reading.created_at.isoformat() if reading.created_at else None,
                    "source_device": reading.source_device
                }
                for reading in sensor_readings
            ],
            "blockchain_logs": [
                {
                    "id": log.id,
                    "entity_type": log.entity_type,
                    "hash": log.hash,
                    "timestamp": log.timestamp.isoformat() if log.timestamp else None
                }
                for log in blockchain_logs
            ]
        }

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )
