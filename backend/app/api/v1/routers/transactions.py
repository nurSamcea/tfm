from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from sqlalchemy import and_
from datetime import datetime
from typing import List

from backend.app import schemas, database, models
from backend.app.database import get_db
from backend.app.models.transaction import TransactionStatusEnum

router = APIRouter(prefix="/transactions", tags=["Transactions"])


@router.post("/create-order", response_model=schemas.TransactionOut)
def create_order_from_cart(
    order_data: schemas.TransactionCreate,
    buyer_id: int,
    buyer_type: str,
    db: Session = Depends(get_db)
):
    """
    Crea una transacción desde el carrito (supermercado o consumidor).
    Valida stock del vendedor y crea el pedido.
    """
    try:
        # 1. Validar que el comprador existe
        buyer = db.query(models.User).filter(
            and_(models.User.id == buyer_id, models.User.role == buyer_type)
        ).first()
        if not buyer:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"{buyer_type.capitalize()} no encontrado"
            )

        # 2. Validar que el vendedor existe
        seller = db.query(models.User).filter(
            and_(models.User.id == order_data.seller_id, models.User.role == order_data.seller_type)
        ).first()
        if not seller:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"{order_data.seller_type.capitalize()} no encontrado"
            )

        # 3. Validar stock disponible para cada producto
        products_to_update = []
        for item in order_data.order_details:
            product = db.query(models.Product).filter(
                and_(
                    models.Product.id == item.product_id,
                    models.Product.provider_id == order_data.seller_id
                )
            ).first()
            
            if not product:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail=f"Producto {item.product_id} no encontrado para el {order_data.seller_type} {order_data.seller_id}"
                )
            
            if product.stock_available < item.quantity:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail=f"Stock insuficiente para {product.name}. Disponible: {product.stock_available}, Solicitado: {item.quantity}"
                )
            
            products_to_update.append((product, item.quantity))

        # 4. Crear la transacción
        db_tx = models.Transaction(
            buyer_id=buyer_id,
            seller_id=order_data.seller_id,
            buyer_type=buyer_type,
            seller_type=order_data.seller_type,
            total_price=order_data.total_price,
            currency=order_data.currency,
            status=TransactionStatusEnum.in_progress,
            order_details=[item.dict() for item in order_data.order_details],
            created_at=datetime.utcnow()
        )
        db.add(db_tx)
        db.flush()  # Para obtener el ID de la transacción

        # 5. Actualizar stock del vendedor (reservar productos)
        for product, quantity in products_to_update:
            product.stock_available -= quantity
            if product.stock_available < 0:
                product.stock_available = 0

        # 6. Confirmar todos los cambios
        db.commit()
        db.refresh(db_tx)

        # 7. Agregar información adicional para la respuesta
        response_data = db_tx.__dict__.copy()
        response_data['seller_name'] = seller.name
        response_data['buyer_name'] = buyer.name

        return response_data

    except HTTPException:
        db.rollback()
        raise
    except Exception as e:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )


@router.get("/buyer/{buyer_id}/{buyer_type}", response_model=List[schemas.TransactionOut])
def get_buyer_orders(buyer_id: int, buyer_type: str, db: Session = Depends(get_db)):
    """Obtener todas las transacciones de un comprador (supermercado o consumidor)"""
    transactions = db.query(models.Transaction).filter(
        and_(
            models.Transaction.buyer_id == buyer_id,
            models.Transaction.buyer_type == buyer_type
        )
    ).order_by(models.Transaction.created_at.desc()).all()
    
    # Agregar información adicional
    result = []
    for tx in transactions:
        seller = db.query(models.User).filter(models.User.id == tx.seller_id).first()
        buyer = db.query(models.User).filter(models.User.id == tx.buyer_id).first()
        
        # Crear diccionario con solo los campos necesarios
        # Asegurar que order_details sea siempre una lista
        order_details = tx.order_details
        if isinstance(order_details, dict):
            order_details = [] if not order_details else [order_details]
        elif not isinstance(order_details, list):
            order_details = []
        
        tx_data = {
            'id': tx.id,
            'buyer_id': tx.buyer_id,
            'seller_id': tx.seller_id,
            'buyer_type': tx.buyer_type,
            'seller_type': tx.seller_type,
            'total_price': float(tx.total_price) if tx.total_price else 0.0,
            'currency': tx.currency,
            'status': tx.status,
            'created_at': tx.created_at,
            'confirmed_at': tx.confirmed_at,
            'delivered_at': tx.delivered_at,
            'order_details': order_details,
            'seller_name': seller.name if seller else None,
            'buyer_name': buyer.name if buyer else None
        }
        result.append(tx_data)
    
    return result


@router.get("/seller/{seller_id}/{seller_type}", response_model=List[schemas.TransactionOut])
def get_seller_orders(seller_id: int, seller_type: str, db: Session = Depends(get_db)):
    """Obtener todas las transacciones de un vendedor (agricultor o supermercado)"""
    transactions = db.query(models.Transaction).filter(
        and_(
            models.Transaction.seller_id == seller_id,
            models.Transaction.seller_type == seller_type
        )
    ).order_by(models.Transaction.created_at.desc()).all()
    
    # Agregar información adicional
    result = []
    for tx in transactions:
        seller = db.query(models.User).filter(models.User.id == tx.seller_id).first()
        buyer = db.query(models.User).filter(models.User.id == tx.buyer_id).first()
        
        # Crear diccionario con solo los campos necesarios
        # Asegurar que order_details sea siempre una lista
        order_details = tx.order_details
        if isinstance(order_details, dict):
            order_details = [] if not order_details else [order_details]
        elif not isinstance(order_details, list):
            order_details = []
        
        tx_data = {
            'id': tx.id,
            'buyer_id': tx.buyer_id,
            'seller_id': tx.seller_id,
            'buyer_type': tx.buyer_type,
            'seller_type': tx.seller_type,
            'total_price': float(tx.total_price) if tx.total_price else 0.0,
            'currency': tx.currency,
            'status': tx.status,
            'created_at': tx.created_at,
            'confirmed_at': tx.confirmed_at,
            'delivered_at': tx.delivered_at,
            'order_details': order_details,
            'seller_name': seller.name if seller else None,
            'buyer_name': buyer.name if buyer else None
        }
        result.append(tx_data)
    
    return result


# Endpoints específicos para mantener compatibilidad
@router.get("/supermarket/{supermarket_id}", response_model=List[schemas.TransactionOut])
def get_supermarket_orders(supermarket_id: int, db: Session = Depends(get_db)):
    """Obtener todas las transacciones de un supermercado (como comprador)"""
    return get_buyer_orders(supermarket_id, "supermarket", db)


@router.get("/farmer/{farmer_id}", response_model=List[schemas.TransactionOut])
def get_farmer_orders(farmer_id: int, db: Session = Depends(get_db)):
    """Obtener todas las transacciones de un agricultor (como vendedor)"""
    return get_seller_orders(farmer_id, "farmer", db)


@router.get("/consumer/{consumer_id}", response_model=List[schemas.TransactionOut])
def get_consumer_orders(consumer_id: int, db: Session = Depends(get_db)):
    """Obtener todas las transacciones de un consumidor (como comprador)"""
    return get_buyer_orders(consumer_id, "consumer", db)


def _validate_transaction_permissions(transaction, new_status, user_id: int, user_type: str):
    """
    Validar si un usuario puede realizar un cambio de estado específico en una transacción.
    
    Reglas de negocio:
    - Farmer: puede cambiar de 'in_progress' a 'delivered' o 'cancelled' en sus ventas
    - Supermarket (como vendedor): puede cambiar de 'in_progress' a 'delivered' o 'cancelled' en ventas a consumidores
    - Supermarket (como comprador): solo puede cancelar pedidos de proveedores (farmers)
    - Consumer: solo puede cancelar sus pedidos
    """
    current_status = transaction.status
    
    # Validar transiciones básicas
    valid_transitions = {
        TransactionStatusEnum.in_progress: [TransactionStatusEnum.delivered, TransactionStatusEnum.cancelled],
        TransactionStatusEnum.delivered: [],  # Estado final
        TransactionStatusEnum.cancelled: []   # Estado final
    }
    
    if new_status not in valid_transitions.get(current_status, []):
        return False, f"Transición de estado no válida: {current_status.value} -> {new_status.value}"
    
    # Validar permisos específicos por tipo de usuario
    if user_type == "farmer":
        # Farmer solo puede modificar sus propias ventas
        if transaction.seller_id != user_id or transaction.seller_type != "farmer":
            return False, "Los agricultores solo pueden modificar sus propias ventas"
        
        # Farmer puede entregar o cancelar
        if new_status in [TransactionStatusEnum.delivered, TransactionStatusEnum.cancelled]:
            return True, "Permitido"
    
    elif user_type == "supermarket":
        # Supermarket puede actuar como vendedor o comprador
        if transaction.seller_id == user_id and transaction.seller_type == "supermarket":
            # Como vendedor: solo puede vender a consumidores, puede entregar o cancelar
            if transaction.buyer_type == "consumer":
                if new_status in [TransactionStatusEnum.delivered, TransactionStatusEnum.cancelled]:
                    return True, "Permitido"
            else:
                return False, "Los supermercados solo pueden vender directamente a consumidores"
        
        elif transaction.buyer_id == user_id and transaction.buyer_type == "supermarket":
            # Como comprador: solo puede cancelar pedidos de farmers
            if transaction.seller_type == "farmer":
                if new_status == TransactionStatusEnum.cancelled:
                    return True, "Permitido"
                else:
                    return False, "Los supermercados solo pueden cancelar pedidos de agricultores"
            else:
                return False, "Los supermercados solo pueden cancelar pedidos de agricultores"
        else:
            return False, "El supermercado no está involucrado en esta transacción"
    
    elif user_type == "consumer":
        # Consumer solo puede cancelar sus propios pedidos
        if transaction.buyer_id != user_id or transaction.buyer_type != "consumer":
            return False, "Los consumidores solo pueden cancelar sus propios pedidos"
        
        if new_status == TransactionStatusEnum.cancelled:
            return True, "Permitido"
        else:
            return False, "Los consumidores solo pueden cancelar pedidos"
    
    else:
        return False, f"Tipo de usuario no válido: {user_type}"
    
    return False, "Operación no permitida"


@router.patch("/{transaction_id}/status", response_model=schemas.TransactionOut)
def update_transaction_status(
    transaction_id: int,
    status_update: schemas.TransactionUpdate,
    user_id: int,
    user_type: str,
    db: Session = Depends(get_db)
):
    """Actualizar el estado de una transacción con validación de permisos"""
    try:
        # 1. Buscar la transacción
        transaction = db.query(models.Transaction).filter(
            models.Transaction.id == transaction_id
        ).first()
        
        if not transaction:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Transacción no encontrada"
            )

        # 2. Validar permisos del usuario
        is_allowed, error_message = _validate_transaction_permissions(
            transaction, status_update.status, user_id, user_type
        )
        
        if not is_allowed:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=error_message
            )

        # 3. Actualizar estado y fechas
        transaction.status = status_update.status
        
        if status_update.status == TransactionStatusEnum.in_progress:
            transaction.confirmed_at = datetime.utcnow()
        elif status_update.status == TransactionStatusEnum.delivered:
            transaction.delivered_at = datetime.utcnow()
            # Cuando se marca como entregado, crear productos para el comprador
            _create_buyer_products(transaction, db)
        elif status_update.status == TransactionStatusEnum.cancelled:
            # Si se cancela, devolver stock al vendedor
            _restore_seller_stock(transaction, db)

        db.commit()
        db.refresh(transaction)

        # 4. Agregar información adicional para la respuesta
        seller = db.query(models.User).filter(models.User.id == transaction.seller_id).first()
        buyer = db.query(models.User).filter(models.User.id == transaction.buyer_id).first()
        
        response_data = transaction.__dict__.copy()
        response_data['seller_name'] = seller.name if seller else None
        response_data['buyer_name'] = buyer.name if buyer else None

        return response_data

    except HTTPException:
        db.rollback()
        raise
    except Exception as e:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )


def _create_buyer_products(transaction, db):
    """Crear productos para el comprador cuando se entrega el pedido"""
    for item_data in transaction.order_details:
        # Buscar el producto original del vendedor
        original_product = db.query(models.Product).filter(
            models.Product.id == item_data['product_id']
        ).first()
        
        if not original_product:
            continue
            
        # Verificar si ya existe un producto similar en el comprador
        existing_product = db.query(models.Product).filter(
            and_(
                models.Product.provider_id == transaction.buyer_id,
                models.Product.name == original_product.name,
                models.Product.category == original_product.category
            )
        ).first()
        
        if existing_product:
            # Si existe, actualizar stock
            existing_product.stock_available += item_data['quantity']
        else:
            # Si no existe, crear nuevo producto
            # Calcular precio según el tipo de comprador
            if transaction.buyer_type == "supermarket":
                # Supermercado: margen del 30%
                new_price = original_product.price * 1.3
            else:
                # Consumidor: precio original (compra directa)
                new_price = original_product.price
                
            new_product = models.Product(
                name=original_product.name,
                description=original_product.description,
                price=new_price,
                currency=original_product.currency,
                unit=original_product.unit,
                category=original_product.category,
                stock_available=item_data['quantity'],
                expiration_date=original_product.expiration_date,
                is_eco=original_product.is_eco,
                image_url=original_product.image_url,
                provider_id=transaction.buyer_id,
                is_hidden=False,
                certifications=original_product.certifications
            )
            db.add(new_product)
        
        # Eliminar el producto del vendedor si el stock llega a 0
        # (El stock ya se redujo al crear la transacción)
        if original_product.stock_available <= 0:
            db.delete(original_product)


def _restore_seller_stock(transaction, db):
    """Restaurar stock del vendedor cuando se cancela una transacción"""
    for item_data in transaction.order_details:
        # Buscar el producto original del vendedor
        product = db.query(models.Product).filter(
            models.Product.id == item_data['product_id']
        ).first()
        
        if product:
            # Restaurar el stock
            product.stock_available += item_data['quantity']
            
            # Si el producto estaba oculto (stock = 0), hacerlo visible nuevamente
            if product.is_hidden:
                product.is_hidden = False


@router.patch("/{transaction_id}/cancel", response_model=schemas.TransactionOut)
def cancel_transaction(
    transaction_id: int,
    user_id: int,
    user_type: str,
    db: Session = Depends(get_db)
):
    """Cancelar una transacción y restaurar el stock del vendedor"""
    try:
        # 1. Buscar la transacción
        transaction = db.query(models.Transaction).filter(
            models.Transaction.id == transaction_id
        ).first()
        
        if not transaction:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Transacción no encontrada"
            )

        # 2. Validar permisos del usuario para cancelar
        is_allowed, error_message = _validate_transaction_permissions(
            transaction, TransactionStatusEnum.cancelled, user_id, user_type
        )
        
        if not is_allowed:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=error_message
            )

        # 3. Cancelar y restaurar stock
        transaction.status = TransactionStatusEnum.cancelled
        _restore_seller_stock(transaction, db)

        db.commit()
        db.refresh(transaction)

        # 4. Preparar respuesta
        seller = db.query(models.User).filter(models.User.id == transaction.seller_id).first()
        buyer = db.query(models.User).filter(models.User.id == transaction.buyer_id).first()
        
        # Asegurar que order_details sea siempre una lista
        order_details = transaction.order_details
        if isinstance(order_details, dict):
            order_details = [] if not order_details else [order_details]
        elif not isinstance(order_details, list):
            order_details = []
        
        response_data = {
            'id': transaction.id,
            'buyer_id': transaction.buyer_id,
            'seller_id': transaction.seller_id,
            'buyer_type': transaction.buyer_type,
            'seller_type': transaction.seller_type,
            'total_price': float(transaction.total_price) if transaction.total_price else 0.0,
            'currency': transaction.currency,
            'status': transaction.status,
            'created_at': transaction.created_at,
            'confirmed_at': transaction.confirmed_at,
            'delivered_at': transaction.delivered_at,
            'order_details': order_details,
            'seller_name': seller.name if seller else None,
            'buyer_name': buyer.name if buyer else None
        }

        return response_data

    except HTTPException:
        db.rollback()
        raise
    except Exception as e:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )


@router.patch("/{transaction_id}/deliver", response_model=schemas.TransactionOut)
def deliver_transaction(
    transaction_id: int,
    user_id: int,
    user_type: str,
    db: Session = Depends(get_db)
):
    """Marcar una transacción como entregada y transferir stock al comprador"""
    try:
        # 1. Buscar la transacción
        transaction = db.query(models.Transaction).filter(
            models.Transaction.id == transaction_id
        ).first()
        
        if not transaction:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Transacción no encontrada"
            )

        # 2. Validar permisos del usuario para entregar
        is_allowed, error_message = _validate_transaction_permissions(
            transaction, TransactionStatusEnum.delivered, user_id, user_type
        )
        
        if not is_allowed:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=error_message
            )

        # 3. Marcar como entregado y transferir stock
        transaction.status = TransactionStatusEnum.delivered
        transaction.delivered_at = datetime.utcnow()
        _create_buyer_products(transaction, db)

        db.commit()
        db.refresh(transaction)

        # 4. Preparar respuesta
        seller = db.query(models.User).filter(models.User.id == transaction.seller_id).first()
        buyer = db.query(models.User).filter(models.User.id == transaction.buyer_id).first()
        
        # Asegurar que order_details sea siempre una lista
        order_details = transaction.order_details
        if isinstance(order_details, dict):
            order_details = [] if not order_details else [order_details]
        elif not isinstance(order_details, list):
            order_details = []
        
        response_data = {
            'id': transaction.id,
            'buyer_id': transaction.buyer_id,
            'seller_id': transaction.seller_id,
            'buyer_type': transaction.buyer_type,
            'seller_type': transaction.seller_type,
            'total_price': float(transaction.total_price) if transaction.total_price else 0.0,
            'currency': transaction.currency,
            'status': transaction.status,
            'created_at': transaction.created_at,
            'confirmed_at': transaction.confirmed_at,
            'delivered_at': transaction.delivered_at,
            'order_details': order_details,
            'seller_name': seller.name if seller else None,
            'buyer_name': buyer.name if buyer else None
        }

        return response_data

    except HTTPException:
        db.rollback()
        raise
    except Exception as e:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error interno del servidor: {str(e)}"
        )


@router.get("/", response_model=List[schemas.TransactionOut])
def read_transactions(db: Session = Depends(get_db)):
    """Obtener todas las transacciones"""
    transactions = db.query(models.Transaction).all()
    
    # Agregar información adicional
    result = []
    for tx in transactions:
        seller = db.query(models.User).filter(models.User.id == tx.seller_id).first()
        buyer = db.query(models.User).filter(models.User.id == tx.buyer_id).first()
        
        # Crear diccionario con solo los campos necesarios
        # Asegurar que order_details sea siempre una lista
        order_details = tx.order_details
        if isinstance(order_details, dict):
            order_details = [] if not order_details else [order_details]
        elif not isinstance(order_details, list):
            order_details = []
        
        tx_data = {
            'id': tx.id,
            'buyer_id': tx.buyer_id,
            'seller_id': tx.seller_id,
            'buyer_type': tx.buyer_type,
            'seller_type': tx.seller_type,
            'total_price': float(tx.total_price) if tx.total_price else 0.0,
            'currency': tx.currency,
            'status': tx.status,
            'created_at': tx.created_at,
            'confirmed_at': tx.confirmed_at,
            'delivered_at': tx.delivered_at,
            'order_details': order_details,
            'seller_name': seller.name if seller else None,
            'buyer_name': buyer.name if buyer else None
        }
        result.append(tx_data)
    
    return result


@router.get("/{transaction_id}", response_model=schemas.TransactionOut)
def get_transaction_by_id(transaction_id: int, db: Session = Depends(get_db)):
    """Obtener una transacción específica por ID"""
    transaction = db.query(models.Transaction).filter(
        models.Transaction.id == transaction_id
    ).first()
    
    if not transaction:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Transacción no encontrada"
        )
    
    # Obtener información del vendedor y comprador
    seller = db.query(models.User).filter(models.User.id == transaction.seller_id).first()
    buyer = db.query(models.User).filter(models.User.id == transaction.buyer_id).first()
    
    # Asegurar que order_details sea siempre una lista
    order_details = transaction.order_details
    if isinstance(order_details, dict):
        order_details = [] if not order_details else [order_details]
    elif not isinstance(order_details, list):
        order_details = []
    
    # Crear diccionario con todos los campos necesarios
    tx_data = {
        'id': transaction.id,
        'buyer_id': transaction.buyer_id,
        'seller_id': transaction.seller_id,
        'buyer_type': transaction.buyer_type,
        'seller_type': transaction.seller_type,
        'total_price': float(transaction.total_price) if transaction.total_price else 0.0,
        'currency': transaction.currency,
        'status': transaction.status,
        'created_at': transaction.created_at,
        'confirmed_at': transaction.confirmed_at,
        'delivered_at': transaction.delivered_at,
        'order_details': order_details,
        'seller_name': seller.name if seller else None,
        'buyer_name': buyer.name if buyer else None
    }
    
    return tx_data