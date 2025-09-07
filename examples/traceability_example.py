"""
Ejemplo de uso del sistema de trazabilidad blockchain
"""

import asyncio
import json
from datetime import datetime, timedelta
from typing import Dict, Any

# Simulación de datos para el ejemplo
SAMPLE_DATA = {
    "product_id": 1,
    "producer_id": 1,
    "sensor_readings": [
        {
            "sensor_id": 1,
            "temperature": 22.5,
            "humidity": 65.0,
            "soil_moisture": 30.0,
            "ph_level": 6.5,
            "shock_detected": False,
            "reading_quality": 0.95
        },
        {
            "sensor_id": 2,
            "temperature": 23.1,
            "humidity": 63.0,
            "soil_moisture": 28.0,
            "ph_level": 6.3,
            "shock_detected": False,
            "reading_quality": 0.92
        }
    ],
    "transport_data": {
        "transport_type": "refrigerated_truck",
        "driver_id": 2,
        "vehicle_id": "TRUCK-001",
        "start_location_lat": 40.4168,
        "start_location_lon": -3.7038,
        "end_location_lat": 40.4200,
        "end_location_lon": -3.7100,
        "distance_km": 15.5,
        "estimated_time_hours": 1.5,
        "actual_time_hours": 1.2,
        "temperature_min": 2.0,
        "temperature_max": 8.0,
        "humidity_min": 60.0,
        "humidity_max": 70.0
    },
    "quality_check": {
        "check_type": "visual_inspection",
        "inspector_id": 3,
        "passed": True,
        "score": 95.0,
        "notes": "Producto en excelente estado",
        "check_data": {
            "color": "verde",
            "firmeza": "firme",
            "tamaño": "uniforme"
        }
    }
}

class TraceabilityExample:
    """Ejemplo de uso del sistema de trazabilidad"""
    
    def __init__(self):
        self.blockchain_private_key = "0x1234567890abcdef"  # Clave privada de ejemplo
        self.product_id = SAMPLE_DATA["product_id"]
        self.producer_id = SAMPLE_DATA["producer_id"]
    
    async def run_complete_example(self):
        """Ejecuta un ejemplo completo del sistema de trazabilidad"""
        print("🚀 Iniciando ejemplo de trazabilidad blockchain...")
        
        try:
            # 1. Crear cadena de trazabilidad
            print("\n1️⃣ Creando cadena de trazabilidad...")
            await self.create_traceability_chain()
            
            # 2. Añadir lecturas de sensores
            print("\n2️⃣ Añadiendo lecturas de sensores...")
            await self.add_sensor_readings()
            
            # 3. Añadir evento de cosecha
            print("\n3️⃣ Añadiendo evento de cosecha...")
            await self.add_harvest_event()
            
            # 4. Añadir evento de empaquetado
            print("\n4️⃣ Añadiendo evento de empaquetado...")
            await self.add_packaging_event()
            
            # 5. Añadir evento de transporte
            print("\n5️⃣ Añadiendo evento de transporte...")
            await self.add_transport_event()
            
            # 6. Añadir control de calidad
            print("\n6️⃣ Añadiendo control de calidad...")
            await self.add_quality_check()
            
            # 7. Añadir transacción de venta
            print("\n7️⃣ Añadiendo transacción de venta...")
            await self.add_sale_transaction()
            
            # 8. Generar código QR
            print("\n8️⃣ Generando código QR...")
            await self.generate_qr_code()
            
            # 9. Verificar autenticidad
            print("\n9️⃣ Verificando autenticidad...")
            await self.verify_authenticity()
            
            # 10. Obtener resumen completo
            print("\n🔟 Obteniendo resumen completo...")
            await self.get_traceability_summary()
            
            print("\n✅ Ejemplo completado exitosamente!")
            
        except Exception as e:
            print(f"❌ Error en el ejemplo: {str(e)}")
    
    async def create_traceability_chain(self):
        """Crea una cadena de trazabilidad para el producto"""
        # Simulación de llamada a la API
        print(f"   📦 Creando cadena para producto {self.product_id}")
        print(f"   👨‍🌾 Productor: {self.producer_id}")
        print(f"   🔗 Clave privada: {self.blockchain_private_key[:10]}...")
        
        # Simular respuesta
        response = {
            "success": True,
            "traceability_chain_id": 1,
            "message": "Cadena de trazabilidad creada exitosamente"
        }
        
        print(f"   ✅ {response['message']}")
        return response
    
    async def add_sensor_readings(self):
        """Añade lecturas de sensores a la trazabilidad"""
        for i, reading in enumerate(SAMPLE_DATA["sensor_readings"]):
            print(f"   📊 Añadiendo lectura de sensor {reading['sensor_id']}")
            print(f"      🌡️ Temperatura: {reading['temperature']}°C")
            print(f"      💧 Humedad: {reading['humidity']}%")
            print(f"      🌱 Humedad del suelo: {reading['soil_moisture']}%")
            print(f"      🧪 pH: {reading['ph_level']}")
            print(f"      ⚡ Calidad de lectura: {reading['reading_quality']}")
            
            # Simular respuesta
            response = {
                "success": True,
                "event_id": i + 1,
                "sensor_data_id": i + 1,
                "message": "Lectura de sensor añadida a la trazabilidad"
            }
            
            print(f"      ✅ {response['message']}")
    
    async def add_harvest_event(self):
        """Añade evento de cosecha"""
        print("   🌾 Añadiendo evento de cosecha")
        print("      📍 Ubicación: Granja del productor")
        print("      👨‍🌾 Actor: Agricultor")
        print("      📅 Timestamp: Ahora")
        
        # Simular respuesta
        response = {
            "success": True,
            "event_id": 3,
            "message": "Evento de cosecha añadido"
        }
        
        print(f"      ✅ {response['message']}")
    
    async def add_packaging_event(self):
        """Añade evento de empaquetado"""
        print("   📦 Añadiendo evento de empaquetado")
        print("      📍 Ubicación: Planta de empaquetado")
        print("      👨‍🔧 Actor: Empaquetador")
        print("      📅 Timestamp: Ahora")
        
        # Simular respuesta
        response = {
            "success": True,
            "event_id": 4,
            "message": "Evento de empaquetado añadido"
        }
        
        print(f"      ✅ {response['message']}")
    
    async def add_transport_event(self):
        """Añade evento de transporte"""
        transport_data = SAMPLE_DATA["transport_data"]
        print("   🚚 Añadiendo evento de transporte")
        print(f"      🚛 Tipo: {transport_data['transport_type']}")
        print(f"      👨‍💼 Conductor: {transport_data['driver_id']}")
        print(f"      🚗 Vehículo: {transport_data['vehicle_id']}")
        print(f"      📏 Distancia: {transport_data['distance_km']} km")
        print(f"      ⏱️ Tiempo estimado: {transport_data['estimated_time_hours']} horas")
        print(f"      ⏱️ Tiempo real: {transport_data['actual_time_hours']} horas")
        print(f"      🌡️ Temperatura: {transport_data['temperature_min']}-{transport_data['temperature_max']}°C")
        
        # Simular respuesta
        response = {
            "success": True,
            "event_id": 5,
            "transport_log_id": 1,
            "message": "Evento de transporte añadido"
        }
        
        print(f"      ✅ {response['message']}")
    
    async def add_quality_check(self):
        """Añade control de calidad"""
        quality_check = SAMPLE_DATA["quality_check"]
        print("   🔍 Añadiendo control de calidad")
        print(f"      🔬 Tipo: {quality_check['check_type']}")
        print(f"      👨‍🔬 Inspector: {quality_check['inspector_id']}")
        print(f"      ✅ Aprobado: {quality_check['passed']}")
        print(f"      📊 Puntuación: {quality_check['score']}/100")
        print(f"      📝 Notas: {quality_check['notes']}")
        
        # Simular respuesta
        response = {
            "success": True,
            "event_id": 6,
            "quality_check_id": 1,
            "message": "Control de calidad añadido"
        }
        
        print(f"      ✅ {response['message']}")
    
    async def add_sale_transaction(self):
        """Añade transacción de venta"""
        print("   💰 Añadiendo transacción de venta")
        print("      🏪 Vendedor: Supermercado")
        print("      👤 Comprador: Consumidor")
        print("      💵 Precio: 5.99 EUR")
        print("      📦 Cantidad: 1 kg")
        
        # Simular respuesta
        response = {
            "success": True,
            "event_id": 7,
            "message": "Transacción añadida a la trazabilidad"
        }
        
        print(f"      ✅ {response['message']}")
    
    async def generate_qr_code(self):
        """Genera código QR de trazabilidad"""
        print("   📱 Generando código QR de trazabilidad")
        print("      🔗 Tipo: Trazabilidad completa")
        print("      📊 Datos incluidos: Producto, productor, eventos, sensores")
        print("      🔍 URL de verificación: https://api.tuapp.com/traceability/products/1/verify")
        
        # Simular respuesta
        response = {
            "success": True,
            "qr_id": 1,
            "qr_hash": "abc123def456",
            "qr_image_base64": "iVBORw0KGgoAAAANSUhEUgAA...",  # Imagen base64 truncada
            "verification_url": "https://api.tuapp.com/traceability/products/1/verify"
        }
        
        print(f"      ✅ Código QR generado: {response['qr_hash']}")
        print(f"      🔗 URL de verificación: {response['verification_url']}")
    
    async def verify_authenticity(self):
        """Verifica la autenticidad del producto"""
        print("   🔐 Verificando autenticidad del producto")
        print("      🔗 Verificando blockchain...")
        print("      📊 Verificando datos de sensores...")
        print("      🚚 Verificando datos de transporte...")
        print("      🔍 Verificando controles de calidad...")
        
        # Simular respuesta
        response = {
            "is_authentic": True,
            "verification_score": 0.95,
            "verification_details": {
                "blockchain_verified": True,
                "sensor_data_verified": True,
                "transport_data_verified": True,
                "quality_checks_passed": True,
                "chain_complete": True,
                "total_events": 7,
                "total_sensor_readings": 2,
                "total_quality_checks": 1
            },
            "issues_found": [],
            "recommendations": [
                "Producto auténtico y de alta calidad",
                "Cadena de suministro completa y verificada",
                "Condiciones de transporte óptimas"
            ]
        }
        
        print(f"      ✅ Autenticidad verificada: {response['is_authentic']}")
        print(f"      📊 Puntuación de verificación: {response['verification_score']}")
        print(f"      🔗 Blockchain verificado: {response['verification_details']['blockchain_verified']}")
        print(f"      📊 Datos de sensores verificados: {response['verification_details']['sensor_data_verified']}")
        print(f"      🚚 Datos de transporte verificados: {response['verification_details']['transport_data_verified']}")
        print(f"      🔍 Controles de calidad aprobados: {response['verification_details']['quality_checks_passed']}")
        print(f"      📋 Total de eventos: {response['verification_details']['total_events']}")
    
    async def get_traceability_summary(self):
        """Obtiene resumen completo de la trazabilidad"""
        print("   📋 Obteniendo resumen completo de trazabilidad")
        
        # Simular respuesta
        response = {
            "product_id": self.product_id,
            "product_name": "Lechuga Fresca",
            "product_category": "verduras",
            "is_eco": True,
            "original_producer": {
                "id": self.producer_id,
                "name": "Granja Ecológica Verde",
                "location": {
                    "lat": 40.4168,
                    "lon": -3.7038
                }
            },
            "chain_status": {
                "is_complete": True,
                "is_verified": True,
                "total_distance_km": 15.5,
                "total_time_hours": 48.0,
                "temperature_violations": 0,
                "quality_score": 0.95
            },
            "events": [
                {"event_type": "product_created", "timestamp": "2024-01-15T08:00:00Z"},
                {"event_type": "sensor_reading", "timestamp": "2024-01-15T08:30:00Z"},
                {"event_type": "sensor_reading", "timestamp": "2024-01-15T09:00:00Z"},
                {"event_type": "harvest", "timestamp": "2024-01-15T10:00:00Z"},
                {"event_type": "packaging", "timestamp": "2024-01-15T11:00:00Z"},
                {"event_type": "transport_start", "timestamp": "2024-01-15T12:00:00Z"},
                {"event_type": "transport_end", "timestamp": "2024-01-15T13:30:00Z"},
                {"event_type": "quality_check", "timestamp": "2024-01-15T14:00:00Z"},
                {"event_type": "sale_supermarket_consumer", "timestamp": "2024-01-15T15:00:00Z"}
            ],
            "sensor_data": [
                {"sensor_id": 1, "temperature": 22.5, "humidity": 65.0, "quality": 0.95},
                {"sensor_id": 2, "temperature": 23.1, "humidity": 63.0, "quality": 0.92}
            ],
            "transport_logs": [
                {
                    "transport_type": "refrigerated_truck",
                    "distance_km": 15.5,
                    "temperature_range": {"min": 2.0, "max": 8.0}
                }
            ],
            "quality_checks": [
                {
                    "check_type": "visual_inspection",
                    "passed": True,
                    "score": 95.0
                }
            ]
        }
        
        print(f"      📦 Producto: {response['product_name']}")
        print(f"      🏷️ Categoría: {response['product_category']}")
        print(f"      🌱 Ecológico: {response['is_eco']}")
        print(f"      👨‍🌾 Productor: {response['original_producer']['name']}")
        print(f"      📍 Ubicación: {response['original_producer']['location']['lat']}, {response['original_producer']['location']['lon']}")
        print(f"      ✅ Cadena completa: {response['chain_status']['is_complete']}")
        print(f"      🔐 Cadena verificada: {response['chain_status']['is_verified']}")
        print(f"      📏 Distancia total: {response['chain_status']['total_distance_km']} km")
        print(f"      ⏱️ Tiempo total: {response['chain_status']['total_time_hours']} horas")
        print(f"      🌡️ Violaciones de temperatura: {response['chain_status']['temperature_violations']}")
        print(f"      📊 Puntuación de calidad: {response['chain_status']['quality_score']}")
        print(f"      📋 Total de eventos: {len(response['events'])}")
        print(f"      📊 Total de lecturas de sensores: {len(response['sensor_data'])}")
        print(f"      🚚 Total de logs de transporte: {len(response['transport_logs'])}")
        print(f"      🔍 Total de controles de calidad: {len(response['quality_checks'])}")

async def main():
    """Función principal para ejecutar el ejemplo"""
    example = TraceabilityExample()
    await example.run_complete_example()

if __name__ == "__main__":
    asyncio.run(main())
