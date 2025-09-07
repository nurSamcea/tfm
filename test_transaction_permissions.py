#!/usr/bin/env python3
"""
Script de prueba para verificar la funcionalidad de permisos de transacciones.
Este script simula diferentes escenarios de cambio de estado según el tipo de usuario.
"""

import requests
import json
from datetime import datetime

# Configuración del servidor
BASE_URL = "http://127.0.0.1:8000"
API_BASE = f"{BASE_URL}/api/v1"

def test_transaction_permissions():
    """Probar la funcionalidad de permisos de transacciones"""
    
    print("🧪 Iniciando pruebas de permisos de transacciones...")
    print("=" * 60)
    
    # Datos de prueba
    test_scenarios = [
        {
            "name": "Farmer intenta entregar su propia venta",
            "user_id": 1,  # Farmer
            "user_type": "farmer",
            "transaction_id": 1,
            "new_status": "delivered",
            "expected_result": "success"
        },
        {
            "name": "Farmer intenta cancelar su propia venta",
            "user_id": 1,  # Farmer
            "user_type": "farmer", 
            "transaction_id": 1,
            "new_status": "cancelled",
            "expected_result": "success"
        },
        {
            "name": "Farmer intenta modificar venta de otro farmer",
            "user_id": 1,  # Farmer
            "user_type": "farmer",
            "transaction_id": 2,  # Transacción de otro farmer
            "new_status": "delivered",
            "expected_result": "forbidden"
        },
        {
            "name": "Supermarket intenta entregar venta a consumidor",
            "user_id": 2,  # Supermarket
            "user_type": "supermarket",
            "transaction_id": 3,  # Venta de supermarket a consumer
            "new_status": "delivered",
            "expected_result": "success"
        },
        {
            "name": "Supermarket intenta cancelar pedido de farmer",
            "user_id": 2,  # Supermarket
            "user_type": "supermarket",
            "transaction_id": 4,  # Compra de supermarket a farmer
            "new_status": "cancelled",
            "expected_result": "success"
        },
        {
            "name": "Supermarket intenta entregar pedido de farmer (no permitido)",
            "user_id": 2,  # Supermarket
            "user_type": "supermarket",
            "transaction_id": 4,  # Compra de supermarket a farmer
            "new_status": "delivered",
            "expected_result": "forbidden"
        },
        {
            "name": "Consumer intenta cancelar su pedido",
            "user_id": 3,  # Consumer
            "user_type": "consumer",
            "transaction_id": 5,  # Pedido del consumer
            "new_status": "cancelled",
            "expected_result": "success"
        },
        {
            "name": "Consumer intenta entregar su pedido (no permitido)",
            "user_id": 3,  # Consumer
            "user_type": "consumer",
            "transaction_id": 5,  # Pedido del consumer
            "new_status": "delivered",
            "expected_result": "forbidden"
        },
        {
            "name": "Consumer intenta cancelar pedido de otro consumer",
            "user_id": 3,  # Consumer
            "user_type": "consumer",
            "transaction_id": 6,  # Pedido de otro consumer
            "new_status": "cancelled",
            "expected_result": "forbidden"
        }
    ]
    
    results = []
    
    for scenario in test_scenarios:
        print(f"\n📋 Probando: {scenario['name']}")
        print(f"   Usuario: {scenario['user_type']} (ID: {scenario['user_id']})")
        print(f"   Transacción: {scenario['transaction_id']}")
        print(f"   Nuevo estado: {scenario['new_status']}")
        print(f"   Resultado esperado: {scenario['expected_result']}")
        
        try:
            # Realizar la petición
            response = requests.patch(
                f"{API_BASE}/transactions/{scenario['transaction_id']}/status",
                params={
                    "user_id": scenario['user_id'],
                    "user_type": scenario['user_type']
                },
                json={"status": scenario['new_status']},
                headers={"Content-Type": "application/json"}
            )
            
            # Analizar resultado
            if response.status_code == 200:
                actual_result = "success"
                print(f"   ✅ ÉXITO: {response.status_code}")
            elif response.status_code == 403:
                actual_result = "forbidden"
                print(f"   🚫 PROHIBIDO: {response.status_code}")
                try:
                    error_detail = response.json().get("detail", "Sin detalles")
                    print(f"   📝 Detalle: {error_detail}")
                except:
                    pass
            elif response.status_code == 404:
                actual_result = "not_found"
                print(f"   ❓ NO ENCONTRADO: {response.status_code}")
            else:
                actual_result = f"error_{response.status_code}"
                print(f"   ❌ ERROR: {response.status_code}")
                try:
                    error_detail = response.json().get("detail", "Sin detalles")
                    print(f"   📝 Detalle: {error_detail}")
                except:
                    pass
            
            # Verificar si el resultado es el esperado
            if actual_result == scenario['expected_result']:
                print(f"   ✅ RESULTADO CORRECTO")
                results.append({"scenario": scenario['name'], "status": "PASS"})
            else:
                print(f"   ❌ RESULTADO INCORRECTO (esperado: {scenario['expected_result']}, obtenido: {actual_result})")
                results.append({"scenario": scenario['name'], "status": "FAIL"})
                
        except requests.exceptions.ConnectionError:
            print(f"   🔌 ERROR DE CONEXIÓN: No se pudo conectar al servidor en {BASE_URL}")
            print(f"   💡 Asegúrate de que el servidor esté ejecutándose")
            results.append({"scenario": scenario['name'], "status": "CONNECTION_ERROR"})
        except Exception as e:
            print(f"   💥 ERROR INESPERADO: {str(e)}")
            results.append({"scenario": scenario['name'], "status": "ERROR"})
    
    # Resumen de resultados
    print("\n" + "=" * 60)
    print("📊 RESUMEN DE RESULTADOS")
    print("=" * 60)
    
    passed = sum(1 for r in results if r["status"] == "PASS")
    failed = sum(1 for r in results if r["status"] == "FAIL")
    errors = sum(1 for r in results if r["status"] in ["CONNECTION_ERROR", "ERROR"])
    
    print(f"✅ Pruebas exitosas: {passed}")
    print(f"❌ Pruebas fallidas: {failed}")
    print(f"💥 Errores: {errors}")
    print(f"📈 Total: {len(results)}")
    
    if failed == 0 and errors == 0:
        print("\n🎉 ¡Todas las pruebas pasaron correctamente!")
        print("🔒 El sistema de permisos está funcionando como se esperaba.")
    else:
        print(f"\n⚠️  Se encontraron {failed + errors} problemas que requieren atención.")
    
    # Mostrar detalles de fallos
    if failed > 0 or errors > 0:
        print("\n📋 DETALLES DE PROBLEMAS:")
        for result in results:
            if result["status"] != "PASS":
                print(f"   - {result['scenario']}: {result['status']}")
    
    return results

def test_inventory_updates():
    """Probar que las actualizaciones de inventario funcionan correctamente"""
    
    print("\n" + "=" * 60)
    print("📦 PROBANDO ACTUALIZACIONES DE INVENTARIO")
    print("=" * 60)
    
    try:
        # Obtener transacciones existentes
        response = requests.get(f"{API_BASE}/transactions/")
        if response.status_code == 200:
            transactions = response.json()
            print(f"📊 Se encontraron {len(transactions)} transacciones en el sistema")
            
            # Mostrar algunas transacciones como ejemplo
            for i, tx in enumerate(transactions[:3]):
                print(f"\n📋 Transacción {tx['id']}:")
                print(f"   Vendedor: {tx.get('seller_name', 'N/A')} ({tx.get('seller_type', 'N/A')})")
                print(f"   Comprador: {tx.get('buyer_name', 'N/A')} ({tx.get('buyer_type', 'N/A')})")
                print(f"   Estado: {tx.get('status', 'N/A')}")
                print(f"   Precio total: {tx.get('total_price', 'N/A')} {tx.get('currency', 'EUR')}")
                
        else:
            print(f"❌ Error al obtener transacciones: {response.status_code}")
            
    except requests.exceptions.ConnectionError:
        print("🔌 Error de conexión al probar inventario")
    except Exception as e:
        print(f"💥 Error inesperado: {str(e)}")

def main():
    """Función principal"""
    print("🚀 SCRIPT DE PRUEBA DE PERMISOS DE TRANSACCIONES")
    print("=" * 60)
    print(f"🌐 Servidor: {BASE_URL}")
    print(f"⏰ Fecha: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # Ejecutar pruebas de permisos
    results = test_transaction_permissions()
    
    # Ejecutar pruebas de inventario
    test_inventory_updates()
    
    print("\n" + "=" * 60)
    print("🏁 PRUEBAS COMPLETADAS")
    print("=" * 60)
    
    # Instrucciones para el usuario
    print("\n💡 INSTRUCCIONES:")
    print("1. Asegúrate de que el servidor backend esté ejecutándose")
    print("2. Verifica que existan transacciones de prueba en la base de datos")
    print("3. Si hay errores de conexión, verifica la URL del servidor")
    print("4. Si hay fallos en las pruebas, revisa la lógica de permisos implementada")

if __name__ == "__main__":
    main()


