"""
Router para que los consumidores puedan ver la trazabilidad de productos
"""

from fastapi import APIRouter, HTTPException, Depends
from fastapi.responses import HTMLResponse
from sqlalchemy.orm import Session
from datetime import datetime

from backend.app.database import get_db
from backend.app.algorithms.traceability_service import TraceabilityService
from backend.app.models.product import Product

router = APIRouter(prefix="/consumer", tags=["Consumer Traceability"])

def get_product_or_404(product_id: int, db: Session) -> Product:
    """Obtiene un producto por ID o lanza HTTPException 404 si no existe"""
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Producto no encontrado")
    return product

@router.get("/products/{product_id}/trace", response_class=HTMLResponse)
async def get_product_trace_for_consumer(
    product_id: int,
    db: Session = Depends(get_db)
):
    """
    Obtiene la trazabilidad completa de un producto para mostrar al consumidor
    """
    try:
        product = get_product_or_404(product_id, db)
        
        # Obtener la trazabilidad completa
        traceability_service = TraceabilityService(db)
        trace_data = traceability_service.get_product_traceability_summary(product_id)
        
        if not trace_data:
            raise HTTPException(status_code=404, detail="No se encontró trazabilidad para este producto")
        
        # Generar HTML para mostrar al consumidor
        html_content = generate_consumer_trace_html(trace_data, product)
        
        return HTMLResponse(content=html_content)
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al obtener trazabilidad: {str(e)}")

@router.get("/products/{product_id}/trace/json")
async def get_product_trace_json(
    product_id: int,
    db: Session = Depends(get_db)
):
    """
    Obtiene la trazabilidad en formato JSON para aplicaciones móviles
    """
    try:
        product = get_product_or_404(product_id, db)
        
        # Obtener la trazabilidad completa
        traceability_service = TraceabilityService(db)
        try:
            trace_data = traceability_service.get_product_traceability_summary(product_id)
            consumer_data = format_trace_for_consumer(trace_data, product)
            return consumer_data
        except Exception as e:
            # Si falla por cualquier motivo, devolver datos vacíos (mejor UX que 500)
            return format_empty_trace_for_consumer(product)
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al obtener trazabilidad: {str(e)}")

@router.get("/products/{product_id}/trace/summary")
async def get_product_trace_summary(
    product_id: int,
    db: Session = Depends(get_db)
):
    """
    Obtiene un resumen de la trazabilidad del producto
    """
    try:
        product = get_product_or_404(product_id, db)
        
        # Obtener la trazabilidad completa
        traceability_service = TraceabilityService(db)
        trace_data = traceability_service.get_product_traceability_summary(product_id)
        
        if not trace_data:
            raise HTTPException(status_code=404, detail="No se encontró trazabilidad para este producto")
        
        # Generar resumen
        summary = generate_trace_summary(trace_data, product)
        
        return summary
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al obtener resumen: {str(e)}")

@router.post("/products/{product_id}/trace/update-metrics")
async def update_traceability_metrics(
    product_id: int,
    db: Session = Depends(get_db)
):
    """
    Fuerza la actualización de las métricas de trazabilidad de un producto
    """
    try:
        product = get_product_or_404(product_id, db)
        
        # Actualizar métricas
        traceability_service = TraceabilityService(db)
        traceability_service._update_chain_metrics(product_id)
        
        # Obtener las métricas actualizadas
        trace_data = traceability_service.get_product_traceability_summary(product_id)
        
        return {
            "success": True,
            "message": "Métricas de trazabilidad actualizadas",
            "metrics": {
                "total_distance_km": trace_data.get("chain_status", {}).get("total_distance_km", 0),
                "total_time_hours": trace_data.get("chain_status", {}).get("total_time_hours", 0),
                "quality_score": trace_data.get("chain_status", {}).get("quality_score", 0),
                "temperature_violations": trace_data.get("chain_status", {}).get("temperature_violations", 0)
            }
        }
        
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al actualizar métricas: {str(e)}")

def generate_consumer_trace_html(trace_data: dict, product: Product) -> str:
    """Genera HTML para mostrar la trazabilidad al consumidor"""
    
    events = trace_data.get('events', [])
    chain_data = trace_data.get('chain', {})
    
    # Calcular estadísticas
    total_distance = chain_data.get('total_distance_km', 0)
    total_time = chain_data.get('total_time_hours', 0)
    quality_score = chain_data.get('quality_score', 0)
    temperature_violations = chain_data.get('temperature_violations', 0)
    
    html = f"""
    <!DOCTYPE html>
    <html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Trazabilidad - {product.name}</title>
        <style>
            body {{
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                margin: 0;
                padding: 20px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
            }}
            .container {{
                max-width: 800px;
                margin: 0 auto;
                background: white;
                border-radius: 15px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                overflow: hidden;
            }}
            .header {{
                background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
                color: white;
                padding: 30px;
                text-align: center;
            }}
            .header h1 {{
                margin: 0;
                font-size: 2.5em;
                font-weight: 300;
            }}
            .header p {{
                margin: 10px 0 0 0;
                opacity: 0.9;
                font-size: 1.1em;
            }}
            .stats {{
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
                gap: 20px;
                padding: 30px;
                background: #f8f9fa;
            }}
            .stat-card {{
                background: white;
                padding: 20px;
                border-radius: 10px;
                text-align: center;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }}
            .stat-number {{
                font-size: 2em;
                font-weight: bold;
                color: #4CAF50;
                margin-bottom: 5px;
            }}
            .stat-label {{
                color: #666;
                font-size: 0.9em;
                text-transform: uppercase;
                letter-spacing: 1px;
            }}
            .timeline {{
                padding: 30px;
            }}
            .timeline h2 {{
                color: #333;
                margin-bottom: 30px;
                text-align: center;
            }}
            .timeline-item {{
                display: flex;
                margin-bottom: 30px;
                position: relative;
            }}
            .timeline-item:not(:last-child)::before {{
                content: '';
                position: absolute;
                left: 20px;
                top: 50px;
                width: 2px;
                height: calc(100% + 20px);
                background: #e0e0e0;
            }}
            .timeline-icon {{
                width: 40px;
                height: 40px;
                border-radius: 50%;
                background: #4CAF50;
                color: white;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: bold;
                margin-right: 20px;
                flex-shrink: 0;
            }}
            .timeline-content {{
                flex: 1;
                background: #f8f9fa;
                padding: 20px;
                border-radius: 10px;
                border-left: 4px solid #4CAF50;
            }}
            .timeline-title {{
                font-weight: bold;
                color: #333;
                margin-bottom: 5px;
            }}
            .timeline-date {{
                color: #666;
                font-size: 0.9em;
                margin-bottom: 10px;
            }}
            .timeline-location {{
                color: #4CAF50;
                font-size: 0.9em;
                margin-bottom: 10px;
            }}
            .timeline-details {{
                color: #666;
                font-size: 0.9em;
            }}
            .footer {{
                background: #333;
                color: white;
                padding: 20px;
                text-align: center;
            }}
            .footer p {{
                margin: 0;
                opacity: 0.8;
            }}
            .quality-badge {{
                display: inline-block;
                padding: 5px 15px;
                border-radius: 20px;
                font-size: 0.8em;
                font-weight: bold;
                text-transform: uppercase;
            }}
            .quality-excellent {{ background: #4CAF50; color: white; }}
            .quality-good {{ background: #FFC107; color: #333; }}
            .quality-fair {{ background: #FF9800; color: white; }}
            .quality-poor {{ background: #F44336; color: white; }}
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>🌱 {product.name}</h1>
                <p>Información completa de trazabilidad</p>
            </div>
            
            <div class="stats">
                <div class="stat-card">
                    <div class="stat-number">{total_distance:.1f}</div>
                    <div class="stat-label">Kilómetros Recorridos</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{total_time:.1f}</div>
                    <div class="stat-label">Horas de Viaje</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{quality_score:.1f}%</div>
                    <div class="stat-label">Puntuación de Calidad</div>
                </div>
                <div class="stat-card">
                    <div class="stat-number">{temperature_violations}</div>
                    <div class="stat-label">Violaciones de Temperatura</div>
                </div>
            </div>
            
            <div class="timeline">
                <h2>📋 Historia del Producto</h2>
    """
    
    # Agregar eventos a la línea de tiempo
    for i, event in enumerate(events):
        event_type = event.get('event_type', 'unknown')
        timestamp = event.get('timestamp', '')
        location = event.get('location_description', 'Ubicación no especificada')
        event_data = event.get('event_data', {})
        
        # Mapear tipos de eventos a iconos y títulos
        event_icons = {
            'product_created': '🌱',
            'harvest': '🌾',
            'packaging': '📦',
            'transport_start': '🚚',
            'transport_checkpoint': '📍',
            'transport_end': '🏁',
            'storage': '🏪',
            'sale_farmer_supermarket': '🛒',
            'sale_supermarket_consumer': '💳',
            'delivery': '🚚',
            'quality_check': '✅',
            'certification': '🏆',
            'sensor_reading': '📊'
        }
        
        event_titles = {
            'product_created': 'Producto Creado',
            'harvest': 'Cosecha',
            'packaging': 'Empaque',
            'transport_start': 'Inicio de Transporte',
            'transport_checkpoint': 'Punto de Control',
            'transport_end': 'Fin de Transporte',
            'storage': 'Almacenamiento',
            'sale_farmer_supermarket': 'Venta a Supermercado',
            'sale_supermarket_consumer': 'Venta al Consumidor',
            'delivery': 'Entrega',
            'quality_check': 'Verificación de Calidad',
            'certification': 'Certificación',
            'sensor_reading': 'Lectura de Sensor'
        }
        
        icon = event_icons.get(event_type, '📋')
        title = event_titles.get(event_type, event_type.replace('_', ' ').title())
        
        # Formatear fecha
        try:
            date_obj = datetime.fromisoformat(timestamp.replace('Z', '+00:00'))
            formatted_date = date_obj.strftime('%d/%m/%Y %H:%M')
        except:
            formatted_date = timestamp
        
        # Generar detalles del evento
        details = []
        if event_data:
            for key, value in event_data.items():
                if key not in ['timestamp', 'created_at']:
                    details.append(f"{key.replace('_', ' ').title()}: {value}")
        
        details_text = '<br>'.join(details) if details else 'Sin detalles adicionales'
        
        html += f"""
                <div class="timeline-item">
                    <div class="timeline-icon">{icon}</div>
                    <div class="timeline-content">
                        <div class="timeline-title">{title}</div>
                        <div class="timeline-date">{formatted_date}</div>
                        <div class="timeline-location">📍 {location}</div>
                        <div class="timeline-details">{details_text}</div>
                    </div>
                </div>
        """
    
    # Cerrar HTML
    html += """
            </div>
            
            <div class="footer">
                <p>🔒 Información verificada por blockchain</p>
                <p>Generado el """ + datetime.now().strftime('%d/%m/%Y %H:%M') + """</p>
            </div>
        </div>
    </body>
    </html>
    """
    
    return html

def format_trace_for_consumer(trace_data: dict, product: Product) -> dict:
    """Formatea los datos de trazabilidad para el consumidor"""
    
    events = trace_data.get('events', [])
    chain_status = trace_data.get('chain_status', {})
    original_producer = trace_data.get('original_producer', {})
    
    # Formatear eventos para el consumidor
    consumer_events = []
    for event in events:
        consumer_event = {
            'step': len(consumer_events) + 1,
            'type': event.get('event_type', 'unknown'),
            'title': get_event_title(event.get('event_type', 'unknown')),
            'timestamp': event.get('timestamp', ''),
            'location': event.get('location', {}).get('description', 'Ubicación no especificada'),
            'details': event.get('event_data', {})
        }
        consumer_events.append(consumer_event)
    
    # Calcular estadísticas
    total_distance = chain_status.get('total_distance_km', 0)
    total_time = chain_status.get('total_time_hours', 0)
    quality_score = chain_status.get('quality_score', 0)
    temperature_violations = chain_status.get('temperature_violations', 0)
    
    return {
        'product': {
            'id': product.id,
            'name': product.name,
            'category': (product.category.value if getattr(product, 'category', None) is not None and hasattr(product.category, 'value') else product.category),
            'price': product.price
        },
        'summary': {
            'total_distance_km': total_distance,
            'total_time_hours': total_time,
            'quality_score': quality_score,
            'sustainability_score': quality_score,  # Usar quality_score como sustainability_score
            'blockchain_verified': any(event.get('blockchain_hash') for event in events)
        },
        'events': consumer_events,
        'verification': {
            'is_verified': chain_status.get('is_verified', False),
            'is_complete': chain_status.get('is_complete', False),
            'blockchain_verified': any(event.get('blockchain_hash') for event in events)
        }
    }

def generate_trace_summary(trace_data: dict, product: Product) -> dict:
    """Genera un resumen de la trazabilidad del producto"""
    
    events = trace_data.get('events', [])
    chain_data = trace_data.get('chain', {})
    
    # Contar eventos por tipo
    event_counts = {}
    for event in events:
        event_type = event.get('event_type', 'unknown')
        event_counts[event_type] = event_counts.get(event_type, 0) + 1
    
    return {
        'product_name': product.name,
        'producer': chain_data.get('original_producer_name', 'Desconocido'),
        'total_events': len(events),
        'event_summary': event_counts,
        'quality_score': chain_data.get('quality_score', 0),
        'is_verified': chain_data.get('is_verified', False),
        'last_updated': max([event.get('timestamp', '') for event in events], default='')
    }

def format_empty_trace_for_consumer(product: Product) -> dict:
    """Formatea datos vacíos de trazabilidad para productos sin trazabilidad"""
    return {
        'product': {
            'id': product.id,
            'name': product.name,
            'category': (product.category.value if getattr(product, 'category', None) is not None and hasattr(product.category, 'value') else product.category),
            'price': product.price
        },
        'summary': {
            'total_distance_km': 0.0,
            'total_time_hours': 0.0,
            'quality_score': 0.0,
            'sustainability_score': 0.0,
            'blockchain_verified': False
        },
        'events': [],
        'verification': {
            'is_verified': False,
            'is_complete': False,
            'blockchain_verified': False
        }
    }

def get_event_title(event_type: str) -> str:
    """Convierte el tipo de evento a un título legible"""
    titles = {
        'product_created': 'Producto Creado',
        'harvest': 'Cosecha',
        'packaging': 'Empaque',
        'transport_start': 'Inicio de Transporte',
        'transport_checkpoint': 'Punto de Control',
        'transport_end': 'Fin de Transporte',
        'storage': 'Almacenamiento',
        'sale_farmer_supermarket': 'Venta a Supermercado',
        'sale_supermarket_consumer': 'Venta al Consumidor',
        'delivery': 'Entrega',
        'quality_check': 'Verificación de Calidad',
        'certification': 'Certificación',
        'sensor_reading': 'Lectura de Sensor'
    }
    return titles.get(event_type, event_type.replace('_', ' ').title())


