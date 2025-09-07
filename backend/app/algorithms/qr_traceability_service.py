from typing import Dict, List, Optional, Any
import qrcode
import io
import base64
import json
import hashlib
from datetime import datetime
from sqlalchemy.orm import Session

from backend.app.models.blockchain_traceability import ProductTraceabilityChain, TraceabilityEvent
from backend.app.models.product import Product
from backend.app.models.qr import QR
from backend.app.algorithms.traceability_service import TraceabilityService

class QRTraceabilityService:
    """Servicio para generar y gestionar códigos QR de trazabilidad"""
    
    def __init__(self, db: Session):
        self.db = db
        self.traceability_service = TraceabilityService(db)
    
    def generate_traceability_qr(
        self, 
        product_id: int, 
        qr_type: str = "full_traceability"
    ) -> Dict[str, Any]:
        """
        Genera un código QR para la trazabilidad de un producto
        """
        try:
            # Obtener el producto
            product = self.db.query(Product).filter(Product.id == product_id).first()
            if not product:
                raise ValueError(f"Producto con ID {product_id} no encontrado")
            
            # Obtener la cadena de trazabilidad
            chain = self.db.query(ProductTraceabilityChain).filter(
                ProductTraceabilityChain.product_id == product_id
            ).first()
            
            if not chain:
                raise ValueError(f"No se encontró cadena de trazabilidad para el producto {product_id}")
            
            # Crear los datos del QR
            qr_data = {
                "product_id": product_id,
                "product_name": product.name,
                "product_category": product.category.value if product.category else None,
                "is_eco": product.is_eco,
                "producer": {
                    "id": chain.original_producer_id,
                    "name": chain.original_producer_name,
                    "location": {
                        "lat": chain.original_producer_location_lat,
                        "lon": chain.original_producer_location_lon
                    }
                },
                "traceability_chain_id": chain.id,
                "qr_type": qr_type,
                "generated_at": datetime.utcnow().isoformat(),
                "verification_url": f"https://api.tuapp.com/traceability/products/{product_id}/verify"
            }
            
            # Generar hash único para el QR
            qr_hash = hashlib.sha256(
                json.dumps(qr_data, sort_keys=True).encode()
            ).hexdigest()
            
            # Crear el código QR
            qr = qrcode.QRCode(
                version=1,
                error_correction=qrcode.constants.ERROR_CORRECT_L,
                box_size=10,
                border=4,
            )
            
            qr.add_data(json.dumps(qr_data))
            qr.make(fit=True)
            
            # Crear la imagen del QR
            img = qr.make_image(fill_color="black", back_color="white")
            
            # Convertir a base64
            buffer = io.BytesIO()
            img.save(buffer, format='PNG')
            img_str = base64.b64encode(buffer.getvalue()).decode()
            
            # Guardar en la base de datos
            qr_record = QR(
                product_id=product_id,
                qr_hash=qr_hash,
                qr_metadata=qr_data,
                qr_type=qr_type
            )
            
            self.db.add(qr_record)
            self.db.commit()
            self.db.refresh(qr_record)
            
            return {
                "success": True,
                "qr_id": qr_record.id,
                "qr_hash": qr_hash,
                "qr_image_base64": img_str,
                "qr_data": qr_data,
                "verification_url": qr_data["verification_url"]
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error generando código QR: {str(e)}")
    
    def generate_simple_traceability_qr(
        self, 
        product_id: int
    ) -> Dict[str, Any]:
        """
        Genera un código QR simple con solo la información básica
        """
        try:
            # Obtener el producto
            product = self.db.query(Product).filter(Product.id == product_id).first()
            if not product:
                raise ValueError(f"Producto con ID {product_id} no encontrado")
            
            # Crear datos simples del QR
            qr_data = {
                "product_id": product_id,
                "product_name": product.name,
                "producer_name": product.provider.name if product.provider else "Desconocido",
                "verification_url": f"https://api.tuapp.com/traceability/products/{product_id}/verify"
            }
            
            # Generar hash único
            qr_hash = hashlib.sha256(
                json.dumps(qr_data, sort_keys=True).encode()
            ).hexdigest()
            
            # Crear el código QR
            qr = qrcode.QRCode(
                version=1,
                error_correction=qrcode.constants.ERROR_CORRECT_L,
                box_size=10,
                border=4,
            )
            
            qr.add_data(json.dumps(qr_data))
            qr.make(fit=True)
            
            # Crear la imagen del QR
            img = qr.make_image(fill_color="black", back_color="white")
            
            # Convertir a base64
            buffer = io.BytesIO()
            img.save(buffer, format='PNG')
            img_str = base64.b64encode(buffer.getvalue()).decode()
            
            # Guardar en la base de datos
            qr_record = QR(
                product_id=product_id,
                qr_hash=qr_hash,
                qr_metadata=qr_data,
                qr_type="simple"
            )
            
            self.db.add(qr_record)
            self.db.commit()
            self.db.refresh(qr_record)
            
            return {
                "success": True,
                "qr_id": qr_record.id,
                "qr_hash": qr_hash,
                "qr_image_base64": img_str,
                "qr_data": qr_data
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error generando código QR simple: {str(e)}")
    
    def verify_qr_traceability(
        self, 
        qr_hash: str
    ) -> Dict[str, Any]:
        """
        Verifica la trazabilidad a partir de un código QR
        """
        try:
            # Buscar el QR en la base de datos
            qr_record = self.db.query(QR).filter(QR.qr_hash == qr_hash).first()
            
            if not qr_record:
                return {
                    "is_valid": False,
                    "error": "Código QR no encontrado"
                }
            
            # Obtener la información del producto
            product = self.db.query(Product).filter(Product.id == qr_record.product_id).first()
            if not product:
                return {
                    "is_valid": False,
                    "error": "Producto no encontrado"
                }
            
            # Obtener la cadena de trazabilidad
            chain = self.db.query(ProductTraceabilityChain).filter(
                ProductTraceabilityChain.product_id == qr_record.product_id
            ).first()
            
            if not chain:
                return {
                    "is_valid": False,
                    "error": "Cadena de trazabilidad no encontrada"
                }
            
            # Obtener el resumen de trazabilidad
            traceability_summary = self.traceability_service.get_product_traceability_summary(
                qr_record.product_id
            )
            
            # Verificar la autenticidad
            verification_result = self.traceability_service.verify_traceability_authenticity(
                qr_record.product_id
            )
            
            return {
                "is_valid": True,
                "product_info": {
                    "id": product.id,
                    "name": product.name,
                    "category": product.category.value if product.category else None,
                    "is_eco": product.is_eco,
                    "producer": {
                        "id": chain.original_producer_id,
                        "name": chain.original_producer_name,
                        "location": {
                            "lat": chain.original_producer_location_lat,
                            "lon": chain.original_producer_location_lon
                        }
                    }
                },
                "traceability_summary": traceability_summary,
                "verification_result": verification_result,
                "qr_info": {
                    "qr_id": qr_record.id,
                    "qr_hash": qr_record.qr_hash,
                    "qr_type": qr_record.qr_type,
                    "generated_at": qr_record.created_at.isoformat()
                }
            }
            
        except Exception as e:
            return {
                "is_valid": False,
                "error": f"Error verificando código QR: {str(e)}"
            }
    
    def get_product_qr_codes(self, product_id: int) -> List[Dict[str, Any]]:
        """
        Obtiene todos los códigos QR de un producto
        """
        try:
            qr_codes = self.db.query(QR).filter(QR.product_id == product_id).all()
            
            return [
                {
                    "id": qr.id,
                    "qr_hash": qr.qr_hash,
                    "qr_type": qr.qr_type,
                    "qr_metadata": qr.qr_metadata,
                    "created_at": qr.created_at.isoformat()
                }
                for qr in qr_codes
            ]
            
        except Exception as e:
            raise Exception(f"Error obteniendo códigos QR: {str(e)}")
    
    def generate_batch_qr_codes(
        self, 
        product_ids: List[int], 
        qr_type: str = "full_traceability"
    ) -> Dict[str, Any]:
        """
        Genera códigos QR para múltiples productos
        """
        try:
            results = []
            errors = []
            
            for product_id in product_ids:
                try:
                    if qr_type == "simple":
                        result = self.generate_simple_traceability_qr(product_id)
                    else:
                        result = self.generate_traceability_qr(product_id, qr_type)
                    
                    results.append({
                        "product_id": product_id,
                        "success": True,
                        "qr_id": result["qr_id"],
                        "qr_hash": result["qr_hash"]
                    })
                    
                except Exception as e:
                    errors.append({
                        "product_id": product_id,
                        "error": str(e)
                    })
            
            return {
                "success": True,
                "total_processed": len(product_ids),
                "successful": len(results),
                "failed": len(errors),
                "results": results,
                "errors": errors
            }
            
        except Exception as e:
            raise Exception(f"Error generando códigos QR en lote: {str(e)}")
    
    def update_qr_metadata(
        self, 
        qr_id: int, 
        new_metadata: Dict[str, Any]
    ) -> Dict[str, Any]:
        """
        Actualiza los metadatos de un código QR
        """
        try:
            qr_record = self.db.query(QR).filter(QR.id == qr_id).first()
            
            if not qr_record:
                raise ValueError(f"Código QR con ID {qr_id} no encontrado")
            
            # Actualizar metadatos
            qr_record.qr_metadata.update(new_metadata)
            
            # Regenerar hash
            qr_hash = hashlib.sha256(
                json.dumps(qr_record.qr_metadata, sort_keys=True).encode()
            ).hexdigest()
            
            qr_record.qr_hash = qr_hash
            
            self.db.commit()
            
            return {
                "success": True,
                "message": "Metadatos del código QR actualizados",
                "new_hash": qr_hash
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error actualizando metadatos del código QR: {str(e)}")
    
    def delete_qr_code(self, qr_id: int) -> Dict[str, Any]:
        """
        Elimina un código QR
        """
        try:
            qr_record = self.db.query(QR).filter(QR.id == qr_id).first()
            
            if not qr_record:
                raise ValueError(f"Código QR con ID {qr_id} no encontrado")
            
            self.db.delete(qr_record)
            self.db.commit()
            
            return {
                "success": True,
                "message": "Código QR eliminado exitosamente"
            }
            
        except Exception as e:
            self.db.rollback()
            raise Exception(f"Error eliminando código QR: {str(e)}")
