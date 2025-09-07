#!/usr/bin/env python3
"""
Script para probar el endpoint de farmer metrics
"""

import requests
import json
import os
from dotenv import load_dotenv

# Cargar variables de entorno
load_dotenv()

# Configuración
BASE_URL = f"http://{os.getenv('BACKEND_IP', '192.168.68.116')}:8000"
FARMER_EMAIL = "maria@example.com"  # Usuario farmer ID 2
FARMER_PASSWORD = "password123"

def test_farmer_metrics():
    print("=== TESTING FARMER METRICS ENDPOINT ===")
    print(f"Base URL: {BASE_URL}")
    
    # 1. Login como farmer
    print("\n1. Login como farmer...")
    login_data = {
        "username": FARMER_EMAIL,
        "password": FARMER_PASSWORD
    }
    
    login_response = requests.post(f"{BASE_URL}/auth/login", data=login_data)
    print(f"Login status: {login_response.status_code}")
    
    if login_response.status_code != 200:
        print(f"Error en login: {login_response.text}")
        return
    
    login_result = login_response.json()
    token = login_result.get("access_token")
    print(f"Token obtenido: {token[:20]}..." if token else "No token")
    
    # 2. Probar endpoint de farmer metrics
    print("\n2. Probando endpoint farmer-metrics/dashboard...")
    
    # Usar el endpoint sin autenticación, pasando el ID del farmer
    farmer_id = 2  # ID de María García
    metrics_response = requests.get(f"{BASE_URL}/farmer-metrics/dashboard/{farmer_id}")
    print(f"Metrics status: {metrics_response.status_code}")
    print(f"Metrics response: {metrics_response.text}")
    
    if metrics_response.status_code == 200:
        metrics_data = metrics_response.json()
        print(f"\n✅ Dashboard cargado exitosamente!")
        print(f"Farmer: {metrics_data.get('farmer_name')}")
        print(f"Total zonas: {metrics_data.get('total_zones')}")
        print(f"Total sensores: {metrics_data.get('total_sensors')}")
        print(f"Zonas encontradas: {len(metrics_data.get('zones', []))}")
        
        for zone in metrics_data.get('zones', []):
            print(f"  - {zone.get('zone_name')}: {zone.get('total_sensors')} sensores")
    else:
        print(f"❌ Error en metrics: {metrics_response.status_code}")
        print(f"Response: {metrics_response.text}")
    
    # 3. Probar endpoint público de sensores para comparar
    print("\n3. Probando endpoint público de sensores...")
    sensors_response = requests.get(f"{BASE_URL}/sensors/public")
    print(f"Sensors public status: {sensors_response.status_code}")
    
    if sensors_response.status_code == 200:
        sensors_data = sensors_response.json()
        print(f"Sensores públicos encontrados: {len(sensors_data)}")
    else:
        print(f"Error en sensores públicos: {sensors_response.text}")

if __name__ == "__main__":
    test_farmer_metrics()
