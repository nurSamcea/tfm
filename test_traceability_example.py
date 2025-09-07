#!/usr/bin/env python3
"""
Script de ejemplo para probar el sistema de trazabilidad blockchain
"""

import requests
import json
from datetime import datetime

# Configuración
BASE_URL = "http://localhost:8000"
API_BASE = f"{BASE_URL}/api/v1"

def test_traceability_system():
    """Función para probar el sistema de trazabilidad"""
    
    print("🔍 Probando Sistema de Trazabilidad Blockchain")
    print("=" * 50)
    
    # 1. Crear una cadena de trazabilidad para un producto
    product_id = 1  # Cambia por un ID de producto existente
    
    print(f"\n1️⃣ Creando cadena de trazabilidad para producto {product_id}...")
    
    chain_data = {
        "product_name": "Tomates Ecológicos",
        "product_category": "vegetables",
        "is_eco": True,
        "original_producer_name": "Granja Ecológica María",
        "original_producer_location_lat": 40.4168,
        "original_producer_location_lon": -3.7038
    }
    
    try:
        response = requests.post(
            f"{API_BASE}/traceability/products/{product_id}/create-chain",
            json=chain_data
        )
        
        if response.status_code == 200:
            print("✅ Cadena de trazabilidad creada exitosamente")
            print(f"📋 Datos: {response.json()}")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except requests.exceptions.ConnectionError:
        print("❌ Error: No se puede conectar al servidor. ¿Está corriendo?")
        return
    
    # 2. Agregar evento de cosecha
    print(f"\n2️⃣ Agregando evento de cosecha...")
    
    harvest_event = {
        "event_type": "harvest",
        "timestamp": datetime.now().isoformat(),
        "location_lat": 40.4168,
        "location_lon": -3.7038,
        "location_description": "Campo de tomates, zona A",
        "actor_type": "farmer",
        "event_data": {
            "harvest_date": datetime.now().isoformat(),
            "quantity_kg": 150.5,
            "quality_grade": "A"
        }
    }
    
    try:
        response = requests.post(
            f"{API_BASE}/traceability/products/{product_id}/add-event",
            json=harvest_event
        )
        
        if response.status_code == 200:
            print("✅ Evento de cosecha agregado")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Error: {e}")
    
    # 3. Agregar evento de transporte
    print(f"\n3️⃣ Agregando evento de transporte...")
    
    transport_event = {
        "event_type": "transport_start",
        "timestamp": datetime.now().isoformat(),
        "location_lat": 40.4168,
        "location_lon": -3.7038,
        "location_description": "Granja Ecológica María",
        "actor_type": "driver",
        "event_data": {
            "transport_type": "refrigerated_truck",
            "driver_name": "Juan Pérez",
            "vehicle_id": "TRK-001",
            "destination": "Mercado Central Madrid"
        }
    }
    
    try:
        response = requests.post(
            f"{API_BASE}/traceability/products/{product_id}/add-event",
            json=transport_event
        )
        
        if response.status_code == 200:
            print("✅ Evento de transporte agregado")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Error: {e}")
    
    # 4. Ver trazabilidad completa
    print(f"\n4️⃣ Obteniendo trazabilidad completa...")
    
    try:
        response = requests.get(
            f"{API_BASE}/traceability/products/{product_id}/full-trace"
        )
        
        if response.status_code == 200:
            trace_data = response.json()
            print("✅ Trazabilidad completa obtenida:")
            print(f"📊 Producto: {trace_data.get('product_name', 'N/A')}")
            print(f"🏭 Productor: {trace_data.get('original_producer_name', 'N/A')}")
            print(f"📅 Creado: {trace_data.get('created_at', 'N/A')}")
            print(f"✅ Completado: {trace_data.get('is_complete', False)}")
            print(f"🔍 Verificado: {trace_data.get('is_verified', False)}")
            
            events = trace_data.get('events', [])
            print(f"\n📋 Eventos registrados ({len(events)}):")
            for i, event in enumerate(events, 1):
                print(f"  {i}. {event.get('event_type', 'N/A')} - {event.get('timestamp', 'N/A')}")
                print(f"     📍 {event.get('location_description', 'Sin ubicación')}")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Error: {e}")
    
    # 5. Generar código QR
    print(f"\n5️⃣ Generando código QR...")
    
    try:
        response = requests.post(
            f"{API_BASE}/qr-traceability/products/{product_id}/generate-qr"
        )
        
        if response.status_code == 200:
            qr_data = response.json()
            print("✅ Código QR generado:")
            print(f"🔗 URL: {qr_data.get('qr_url', 'N/A')}")
            print(f"📱 Código: {qr_data.get('qr_code', 'N/A')}")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Error: {e}")
    
    # 6. Ver puntuación de trazabilidad
    print(f"\n6️⃣ Obteniendo puntuación de trazabilidad...")
    
    try:
        response = requests.get(
            f"{API_BASE}/traceability/products/{product_id}/traceability-score"
        )
        
        if response.status_code == 200:
            score_data = response.json()
            print("✅ Puntuación de trazabilidad:")
            print(f"📊 Puntuación total: {score_data.get('total_score', 0):.2f}/100")
            print(f"🔍 Completitud: {score_data.get('completeness_score', 0):.2f}/100")
            print(f"✅ Verificación: {score_data.get('verification_score', 0):.2f}/100")
            print(f"🌡️ Calidad: {score_data.get('quality_score', 0):.2f}/100")
        else:
            print(f"❌ Error: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ Error: {e}")
    
    print("\n" + "=" * 50)
    print("🎉 Prueba de trazabilidad completada!")
    print("\n💡 Para ver más detalles, visita:")
    print(f"   📱 Swagger UI: {BASE_URL}/docs")
    print(f"   🔍 Trazabilidad: {API_BASE}/traceability/")

if __name__ == "__main__":
    test_traceability_system()


