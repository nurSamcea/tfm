#!/usr/bin/env python3
"""
Script para probar la funcionalidad de trazabilidad para consumidores
"""

import requests
import json
from datetime import datetime

# Configuración
BASE_URL = "http://localhost:8000"
API_BASE = f"{BASE_URL}/api/v1"

def test_consumer_traceability():
    """Función para probar la trazabilidad para consumidores"""
    
    print("🛒 Probando Trazabilidad para Consumidores")
    print("=" * 50)
    
    # 1. Crear datos de prueba
    product_id = 1  # Cambia por un ID de producto existente
    
    print(f"\n1️⃣ Creando datos de prueba para producto {product_id}...")
    
    # Crear cadena de trazabilidad
    chain_data = {
        "product_name": "Tomates Ecológicos Premium",
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
            print("✅ Cadena de trazabilidad creada")
        else:
            print(f"⚠️ Error creando cadena: {response.status_code}")
            
    except requests.exceptions.ConnectionError:
        print("❌ Error: No se puede conectar al servidor. ¿Está corriendo?")
        return
    
    # 2. Agregar eventos de trazabilidad
    events = [
        {
            "event_type": "product_created",
            "timestamp": datetime.now().isoformat(),
            "location_lat": 40.4168,
            "location_lon": -3.7038,
            "location_description": "Granja Ecológica María, Madrid",
            "actor_type": "farmer",
            "event_data": {
                "variety": "Tomate Cherry",
                "planting_date": "2024-03-15",
                "organic_certification": "ES-ECO-001"
            }
        },
        {
            "event_type": "harvest",
            "timestamp": datetime.now().isoformat(),
            "location_lat": 40.4168,
            "location_lon": -3.7038,
            "location_description": "Campo de tomates, zona A",
            "actor_type": "farmer",
            "event_data": {
                "harvest_date": datetime.now().isoformat(),
                "quantity_kg": 150.5,
                "quality_grade": "A",
                "harvest_method": "manual"
            }
        },
        {
            "event_type": "packaging",
            "timestamp": datetime.now().isoformat(),
            "location_lat": 40.4168,
            "location_lon": -3.7038,
            "location_description": "Planta de empaque, Granja María",
            "actor_type": "packer",
            "event_data": {
                "package_type": "caja de cartón",
                "weight_kg": 2.5,
                "packaging_date": datetime.now().isoformat(),
                "batch_number": "TM-2024-001"
            }
        },
        {
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
        },
        {
            "event_type": "transport_end",
            "timestamp": datetime.now().isoformat(),
            "location_lat": 40.4168,
            "location_lon": -3.7038,
            "location_description": "Mercado Central Madrid",
            "actor_type": "driver",
            "event_data": {
                "arrival_time": datetime.now().isoformat(),
                "temperature_during_transport": "4°C",
                "distance_km": 25.3,
                "transport_time_hours": 1.2
            }
        },
        {
            "event_type": "sale_supermarket_consumer",
            "timestamp": datetime.now().isoformat(),
            "location_lat": 40.4168,
            "location_lon": -3.7038,
            "location_description": "Supermercado EcoFresh, Madrid",
            "actor_type": "cashier",
            "event_data": {
                "sale_date": datetime.now().isoformat(),
                "price_per_kg": 4.50,
                "total_price": 11.25,
                "payment_method": "card"
            }
        }
    ]
    
    print(f"\n2️⃣ Agregando {len(events)} eventos de trazabilidad...")
    
    for i, event in enumerate(events, 1):
        try:
            response = requests.post(
                f"{API_BASE}/traceability/products/{product_id}/add-event",
                json=event
            )
            
            if response.status_code == 200:
                print(f"✅ Evento {i}: {event['event_type']}")
            else:
                print(f"⚠️ Error en evento {i}: {response.status_code}")
                
        except Exception as e:
            print(f"❌ Error en evento {i}: {e}")
    
    # 3. Probar endpoints para consumidores
    print(f"\n3️⃣ Probando endpoints para consumidores...")
    
    # Probar resumen JSON
    try:
        response = requests.get(
            f"{API_BASE}/consumer/products/{product_id}/trace/json"
        )
        
        if response.status_code == 200:
            data = response.json()
            print("✅ Trazabilidad JSON obtenida:")
            print(f"   📦 Producto: {data['product']['name']}")
            print(f"   🏭 Productor: {data['producer']['name']}")
            print(f"   📊 Eventos: {data['statistics']['total_events']}")
            print(f"   🚚 Distancia: {data['statistics']['total_distance_km']:.1f} km")
            print(f"   ⏱️ Tiempo: {data['statistics']['total_time_hours']:.1f} horas")
            print(f"   ✅ Calidad: {data['statistics']['quality_score']:.1f}%")
        else:
            print(f"❌ Error obteniendo JSON: {response.status_code}")
            
    except Exception as e:
        print(f"❌ Error: {e}")
    
    # Probar resumen
    try:
        response = requests.get(
            f"{API_BASE}/consumer/products/{product_id}/trace/summary"
        )
        
        if response.status_code == 200:
            summary = response.json()
            print("✅ Resumen obtenido:")
            print(f"   📦 {summary['product_name']}")
            print(f"   🏭 {summary['producer']}")
            print(f"   📊 {summary['total_events']} eventos")
            print(f"   ✅ Calidad: {summary['quality_score']:.1f}%")
        else:
            print(f"❌ Error obteniendo resumen: {response.status_code}")
            
    except Exception as e:
        print(f"❌ Error: {e}")
    
    print("\n" + "=" * 50)
    print("🎉 Prueba de trazabilidad para consumidores completada!")
    print("\n💡 URLs para probar:")
    print(f"   🏠 Página de inicio: {BASE_URL}/api/v1/consumer/")
    print(f"   📱 Trazabilidad HTML: {BASE_URL}/api/v1/consumer/products/{product_id}/trace")
    print(f"   📊 Trazabilidad JSON: {BASE_URL}/api/v1/consumer/products/{product_id}/trace/json")
    print(f"   📋 Resumen: {BASE_URL}/api/v1/consumer/products/{product_id}/trace/summary")
    print(f"   📚 Documentación: {BASE_URL}/docs")

if __name__ == "__main__":
    test_consumer_traceability()


