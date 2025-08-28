#!/usr/bin/env python3
"""
Script de prueba para verificar las APIs de autenticación
"""

import requests
import json

# Configuración
BASE_URL = "http://localhost:8000"
API_BASE = f"{BASE_URL}/api/v1"

def test_backend_health():
    """Prueba si el backend está funcionando"""
    try:
        response = requests.get(f"{BASE_URL}/docs")
        if response.status_code == 200:
            print("✅ Backend está funcionando correctamente")
            return True
        else:
            print(f"❌ Backend no responde correctamente: {response.status_code}")
            return False
    except requests.exceptions.ConnectionError:
        print("❌ No se puede conectar al backend. Asegúrate de que esté corriendo en http://localhost:8000")
        return False

def test_register():
    """Prueba el registro de usuarios"""
    print("\n🔐 Probando registro de usuario...")
    
    register_data = {
        "name": "Usuario de Prueba",
        "email": "test@example.com",
        "password": "password123",
        "role": "consumer"
    }
    
    try:
        response = requests.post(f"{API_BASE}/auth/register", json=register_data)
        print(f"Status Code: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"✅ Registro exitoso: {result}")
            return True
        else:
            print(f"❌ Error en registro: {response.text}")
            return False
    except Exception as e:
        print(f"❌ Error de conexión: {e}")
        return False

def test_login():
    """Prueba el login de usuarios"""
    print("\n🔑 Probando login de usuario...")
    
    login_data = {
        "email": "test@example.com",
        "password": "password123"
    }
    
    try:
        response = requests.post(f"{API_BASE}/auth/login", json=login_data)
        print(f"Status Code: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"✅ Login exitoso: {result}")
            return result.get("access_token")
        else:
            print(f"❌ Error en login: {response.text}")
            return None
    except Exception as e:
        print(f"❌ Error de conexión: {e}")
        return None

def test_protected_endpoint(token):
    """Prueba un endpoint protegido con el token"""
    if not token:
        print("❌ No hay token para probar endpoint protegido")
        return False
    
    print("\n🛡️ Probando endpoint protegido...")
    
    headers = {
        "Authorization": f"Bearer {token}"
    }
    
    try:
        # Probamos obtener productos (endpoint protegido)
        response = requests.get(f"{API_BASE}/products", headers=headers)
        print(f"Status Code: {response.status_code}")
        
        if response.status_code == 200:
            result = response.json()
            print(f"✅ Endpoint protegido funciona: {len(result)} productos encontrados")
            return True
        else:
            print(f"❌ Error en endpoint protegido: {response.text}")
            return False
    except Exception as e:
        print(f"❌ Error de conexión: {e}")
        return False

def main():
    """Función principal de pruebas"""
    print("🚀 Iniciando pruebas del sistema de autenticación...")
    
    # Prueba 1: Verificar que el backend esté funcionando
    if not test_backend_health():
        return
    
    # Prueba 2: Registrar un usuario
    if not test_register():
        print("⚠️ Continuando con login (usuario puede ya existir)...")
    
    # Prueba 3: Hacer login
    token = test_login()
    if not token:
        print("❌ No se pudo obtener token de autenticación")
        return
    
    # Prueba 4: Probar endpoint protegido
    test_protected_endpoint(token)
    
    print("\n🎉 Pruebas completadas!")

if __name__ == "__main__":
    main()
