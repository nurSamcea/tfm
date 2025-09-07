from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List, Dict, Any, Optional
from pydantic import BaseModel

from backend.app import schemas, database, models
from backend.app.database import get_db
from backend.app.algorithms.qr_traceability_service import QRTraceabilityService
from backend.app.api.v1.routers.dependencies import get_current_user
from backend.app.models.user import User

router = APIRouter(prefix="/qr-traceability", tags=["Códigos QR de Trazabilidad"])

class QRGenerationRequest(BaseModel):
    product_id: int
    qr_type: str = "full_traceability"  # full_traceability, simple

class BatchQRGenerationRequest(BaseModel):
    product_ids: List[int]
    qr_type: str = "full_traceability"

class QRVerificationRequest(BaseModel):
    qr_hash: str

class QRMetadataUpdateRequest(BaseModel):
    new_metadata: Dict[str, Any]

@router.post("/generate")
def generate_traceability_qr(
    request: QRGenerationRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Genera un código QR para la trazabilidad de un producto
    """
    try:
        qr_service = QRTraceabilityService(db)
        
        if request.qr_type == "simple":
            result = qr_service.generate_simple_traceability_qr(request.product_id)
        else:
            result = qr_service.generate_traceability_qr(request.product_id, request.qr_type)
        
        return {
            "success": True,
            "message": "Código QR generado exitosamente",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error generando código QR: {str(e)}"
        )

@router.post("/generate-batch")
def generate_batch_qr_codes(
    request: BatchQRGenerationRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Genera códigos QR para múltiples productos
    """
    try:
        qr_service = QRTraceabilityService(db)
        
        result = qr_service.generate_batch_qr_codes(
            request.product_ids, 
            request.qr_type
        )
        
        return {
            "success": True,
            "message": "Códigos QR generados en lote",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error generando códigos QR en lote: {str(e)}"
        )

@router.post("/verify")
def verify_qr_traceability(
    request: QRVerificationRequest,
    db: Session = Depends(get_db)
):
    """
    Verifica la trazabilidad a partir de un código QR
    """
    try:
        qr_service = QRTraceabilityService(db)
        
        result = qr_service.verify_qr_traceability(request.qr_hash)
        
        if not result.get("is_valid", False):
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=result.get("error", "Código QR no válido")
            )
        
        return {
            "success": True,
            "data": result
        }
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error verificando código QR: {str(e)}"
        )

@router.get("/products/{product_id}/qr-codes")
def get_product_qr_codes(
    product_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene todos los códigos QR de un producto
    """
    try:
        qr_service = QRTraceabilityService(db)
        
        qr_codes = qr_service.get_product_qr_codes(product_id)
        
        return {
            "success": True,
            "data": qr_codes
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo códigos QR: {str(e)}"
        )

@router.put("/qr-codes/{qr_id}/metadata")
def update_qr_metadata(
    qr_id: int,
    request: QRMetadataUpdateRequest,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Actualiza los metadatos de un código QR
    """
    try:
        qr_service = QRTraceabilityService(db)
        
        result = qr_service.update_qr_metadata(qr_id, request.new_metadata)
        
        return {
            "success": True,
            "message": "Metadatos del código QR actualizados",
            "data": result
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error actualizando metadatos: {str(e)}"
        )

@router.delete("/qr-codes/{qr_id}")
def delete_qr_code(
    qr_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Elimina un código QR
    """
    try:
        qr_service = QRTraceabilityService(db)
        
        result = qr_service.delete_qr_code(qr_id)
        
        return {
            "success": True,
            "message": "Código QR eliminado exitosamente"
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error eliminando código QR: {str(e)}"
        )

@router.get("/qr-codes/{qr_id}")
def get_qr_code_details(
    qr_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene los detalles de un código QR específico
    """
    try:
        qr_record = db.query(models.QR).filter(models.QR.id == qr_id).first()
        
        if not qr_record:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Código QR no encontrado"
            )
        
        return {
            "success": True,
            "data": {
                "id": qr_record.id,
                "product_id": qr_record.product_id,
                "qr_hash": qr_record.qr_hash,
                "qr_type": qr_record.qr_type,
                "qr_metadata": qr_record.qr_metadata,
                "created_at": qr_record.created_at.isoformat()
            }
        }
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo detalles del código QR: {str(e)}"
        )

@router.get("/products/{product_id}/qr-image")
def get_qr_image(
    product_id: int,
    qr_type: str = "full_traceability",
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Obtiene la imagen del código QR de un producto
    """
    try:
        qr_service = QRTraceabilityService(db)
        
        if qr_type == "simple":
            result = qr_service.generate_simple_traceability_qr(product_id)
        else:
            result = qr_service.generate_traceability_qr(product_id, qr_type)
        
        return {
            "success": True,
            "data": {
                "qr_image_base64": result["qr_image_base64"],
                "qr_hash": result["qr_hash"],
                "verification_url": result.get("verification_url")
            }
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error obteniendo imagen del código QR: {str(e)}"
        )

@router.get("/qr-codes")
def list_all_qr_codes(
    limit: int = 100,
    offset: int = 0,
    qr_type: Optional[str] = None,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Lista todos los códigos QR con filtros opcionales
    """
    try:
        query = db.query(models.QR)
        
        if qr_type:
            query = query.filter(models.QR.qr_type == qr_type)
        
        qr_codes = query.order_by(models.QR.created_at.desc()).offset(offset).limit(limit).all()
        
        return {
            "success": True,
            "data": [
                {
                    "id": qr.id,
                    "product_id": qr.product_id,
                    "qr_hash": qr.qr_hash,
                    "qr_type": qr.qr_type,
                    "created_at": qr.created_at.isoformat()
                }
                for qr in qr_codes
            ]
        }
        
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error listando códigos QR: {str(e)}"
        )

@router.post("/qr-codes/{qr_id}/regenerate")
def regenerate_qr_code(
    qr_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Regenera un código QR existente
    """
    try:
        # Obtener el código QR existente
        qr_record = db.query(models.QR).filter(models.QR.id == qr_id).first()
        
        if not qr_record:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Código QR no encontrado"
            )
        
        qr_service = QRTraceabilityService(db)
        
        # Regenerar el código QR
        if qr_record.qr_type == "simple":
            result = qr_service.generate_simple_traceability_qr(qr_record.product_id)
        else:
            result = qr_service.generate_traceability_qr(qr_record.product_id, qr_record.qr_type)
        
        return {
            "success": True,
            "message": "Código QR regenerado exitosamente",
            "data": result
        }
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Error regenerando código QR: {str(e)}"
        )
