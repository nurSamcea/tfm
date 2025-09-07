#!/usr/bin/env python3
"""
Script para poblar la base de datos con sensores de prueba
"""
import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from backend.app.database import SessionLocal
from backend.app.models import Sensor, SensorZone, User
from backend.app.schemas.sensor import SensorCreate, SensorZoneCreate
from sqlalchemy.orm import Session
from datetime import datetime
import random

def create_test_sensors():
    """Crear sensores de prueba para el simulador"""
    db = SessionLocal()
    
    try:
        # Buscar un farmer existente
        farmer = db.query(User).filter(User.role == "farmer").first()
        if not farmer:
            print("❌ No se encontró ningún farmer en la base de datos")
            return False
            
        print(f"✅ Usando farmer: {farmer.email}")
        
        # Crear múltiples zonas de sensores (10 zonas para 100 sensores)
        zones = []
        zone_names = [
            "Zona Norte", "Zona Centro", "Zona Este", "Zona Sur", "Zona Oeste",
            "Zona Montaña", "Zona Costera", "Zona Interior", "Zona Mediterránea", "Zona Atlántica"
        ]
        
        zone_coords = [
            {"lat": 43.2, "lon": -2.9, "description": "Región Norte de España"},
            {"lat": 40.4, "lon": -3.7, "description": "Región Centro de España"},
            {"lat": 39.5, "lon": -0.4, "description": "Región Este de España"},
            {"lat": 37.4, "lon": -5.9, "description": "Región Sur de España"},
            {"lat": 38.7, "lon": -9.1, "description": "Región Oeste de España"},
            {"lat": 42.6, "lon": -1.6, "description": "Región Montañosa de España"},
            {"lat": 36.7, "lon": -4.4, "description": "Región Costera de España"},
            {"lat": 41.6, "lon": -4.7, "description": "Región Interior de España"},
            {"lat": 38.0, "lon": 1.1, "description": "Región Mediterránea de España"},
            {"lat": 43.5, "lon": -8.2, "description": "Región Atlántica de España"}
        ]
        
        for i, (zone_name, coord) in enumerate(zip(zone_names, zone_coords)):
            # Verificar si la zona ya existe
            existing_zone = db.query(SensorZone).filter(
                SensorZone.farmer_id == farmer.id,
                SensorZone.name == zone_name
            ).first()
            
            if existing_zone:
                zones.append(existing_zone)
                print(f"✅ Zona existente: {zone_name}")
            else:
                zone_data = {
                    "name": zone_name,
                    "description": coord["description"],
                    "location_lat": coord["lat"],
                    "location_lon": coord["lon"],
                    "location_description": coord["description"],
                    "farmer_id": farmer.id,
                    "is_active": True,
                    "created_at": datetime.utcnow(),
                    "updated_at": datetime.utcnow()
                }
                zone = SensorZone(**zone_data)
                db.add(zone)
                db.commit()
                db.refresh(zone)
                zones.append(zone)
                print(f"✅ Zona creada: {zone_name}")
        
        # Crear sensores de prueba (100 sensores para modo full)
        sensors_data = []
        
        # Generar 100 sensores distribuidos en 10 zonas
        for sensor_id in range(1, 101):
            zone_index = (sensor_id - 1) // 10  # 10 sensores por zona (0-9)
            zone = zones[zone_index]  # Usar la zona real creada
            
            # Coordenadas base por zona (para variación de temperatura/humedad)
            zone_coords = [
                {"lat": 43.2, "lon": -2.9, "temp_base": 12.0, "humidity_base": 75.0},
                {"lat": 40.4, "lon": -3.7, "temp_base": 15.0, "humidity_base": 60.0},
                {"lat": 39.5, "lon": -0.4, "temp_base": 18.0, "humidity_base": 65.0},
                {"lat": 37.4, "lon": -5.9, "temp_base": 20.0, "humidity_base": 55.0},
                {"lat": 38.7, "lon": -9.1, "temp_base": 16.0, "humidity_base": 70.0},
                {"lat": 42.6, "lon": -1.6, "temp_base": 10.0, "humidity_base": 80.0},
                {"lat": 36.7, "lon": -4.4, "temp_base": 19.0, "humidity_base": 70.0},
                {"lat": 41.6, "lon": -4.7, "temp_base": 14.0, "humidity_base": 65.0},
                {"lat": 38.0, "lon": 1.1, "temp_base": 17.0, "humidity_base": 60.0},
                {"lat": 43.5, "lon": -8.2, "temp_base": 13.0, "humidity_base": 75.0}
            ]
            
            coord = zone_coords[zone_index]
            
            # Variar ligeramente la ubicación dentro de la zona
            lat_offset = random.uniform(-0.01, 0.01)
            lon_offset = random.uniform(-0.01, 0.01)
            
            # Determinar tipo de sensor (alternar entre temperatura y humedad)
            sensor_type = "temperature" if sensor_id % 2 == 1 else "humidity"
            
            # Configurar umbrales según el tipo
            if sensor_type == "temperature":
                min_threshold = 10.0
                max_threshold = 30.0
            else:  # humidity
                min_threshold = 30.0
                max_threshold = 80.0
            
            sensor_data = {
                "device_id": f"ESP32_SIM_{sensor_id:03d}",
                "name": f"Sensor {sensor_type.title()} {sensor_id}",
                "sensor_type": sensor_type,
                "status": "active",
                "zone_id": zone.id,
                "location_lat": coord["lat"] + lat_offset,
                "location_lon": coord["lon"] + lon_offset,
                "location_description": f"{zone.name} - Sensor {sensor_id}",
                "min_threshold": min_threshold,
                "max_threshold": max_threshold,
                "alert_enabled": True,
                "reading_interval": random.randint(20, 60),
                "firmware_version": "3.0_sim",
                "last_seen": datetime.utcnow(),
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow(),
                "config": {"calibration_offset": 0.0}
            }
            sensors_data.append(sensor_data)
        
        # Crear sensores originales (6 sensores para modo test) - comentado
        """
        sensors_data = [
            {
                "device_id": "ESP32_SIM_001",
                "name": "Sensor Temperatura 1",
                "sensor_type": "temperature",
                "status": "active",
                "zone_id": zone.id,
                "location_lat": 40.4168,
                "location_lon": -3.7038,
                "location_description": "Invernadero A",
                "min_threshold": 15.0,
                "max_threshold": 30.0,
                "alert_enabled": True,
                "reading_interval": 60,
                "firmware_version": "1.0.0",
                "last_seen": datetime.utcnow(),
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow(),
                "config": {"calibration_offset": 0.0}
            },
            {
                "device_id": "ESP32_SIM_002", 
                "name": "Sensor Humedad 1",
                "sensor_type": "humidity",
                "status": "active",
                "zone_id": zone.id,
                "location_lat": 40.4168,
                "location_lon": -3.7038,
                "location_description": "Invernadero A",
                "min_threshold": 40.0,
                "max_threshold": 80.0,
                "alert_enabled": True,
                "reading_interval": 60,
                "firmware_version": "1.0.0",
                "last_seen": datetime.utcnow(),
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow(),
                "config": {"calibration_offset": 0.0}
            },
            {
                "device_id": "ESP32_SIM_003",
                "name": "Sensor Temperatura 2", 
                "sensor_type": "temperature",
                "status": "active",
                "zone_id": zone.id,
                "location_lat": 40.4168,
                "location_lon": -3.7038,
                "location_description": "Invernadero B",
                "min_threshold": 15.0,
                "max_threshold": 30.0,
                "alert_enabled": True,
                "reading_interval": 60,
                "firmware_version": "1.0.0",
                "last_seen": datetime.utcnow(),
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow(),
                "config": {"calibration_offset": 0.0}
            },
            {
                "device_id": "ESP32_SIM_004",
                "name": "Sensor Humedad 2",
                "sensor_type": "humidity", 
                "status": "active",
                "zone_id": zone.id,
                "location_lat": 40.4168,
                "location_lon": -3.7038,
                "location_description": "Invernadero B",
                "min_threshold": 40.0,
                "max_threshold": 80.0,
                "alert_enabled": True,
                "reading_interval": 60,
                "firmware_version": "1.0.0",
                "last_seen": datetime.utcnow(),
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow(),
                "config": {"calibration_offset": 0.0}
            },
            {
                "device_id": "ESP32_SIM_005",
                "name": "Sensor Temperatura 3",
                "sensor_type": "temperature",
                "status": "active", 
                "zone_id": zone.id,
                "location_lat": 40.4168,
                "location_lon": -3.7038,
                "location_description": "Almacén",
                "min_threshold": 10.0,
                "max_threshold": 25.0,
                "alert_enabled": True,
                "reading_interval": 60,
                "firmware_version": "1.0.0",
                "last_seen": datetime.utcnow(),
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow(),
                "config": {"calibration_offset": 0.0}
            },
            {
                "device_id": "ESP32_SIM_006",
                "name": "Sensor Humedad 3",
                "sensor_type": "humidity",
                "status": "active",
                "zone_id": zone.id,
                "location_lat": 40.4168,
                "location_lon": -3.7038,
                "location_description": "Almacén",
                "min_threshold": 30.0,
                "max_threshold": 70.0,
                "alert_enabled": True,
                "reading_interval": 60,
                "firmware_version": "1.0.0",
                "last_seen": datetime.utcnow(),
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow(),
                "config": {"calibration_offset": 0.0}
            }
        ]
        """
        
        # Crear sensores
        created_sensors = []
        for sensor_data in sensors_data:
            # Verificar si el sensor ya existe
            existing_sensor = db.query(Sensor).filter(Sensor.device_id == sensor_data["device_id"]).first()
            if existing_sensor:
                print(f"⚠️  Sensor {sensor_data['device_id']} ya existe")
                created_sensors.append(existing_sensor)
            else:
                sensor = Sensor(**sensor_data)
                db.add(sensor)
                db.commit()
                db.refresh(sensor)
                created_sensors.append(sensor)
                print(f"✅ Sensor creado: {sensor.device_id} - {sensor.name}")
        
        print(f"\n🎉 Total de sensores disponibles: {len(created_sensors)}")
        for sensor in created_sensors:
            print(f"   - ID: {sensor.id}, Device: {sensor.device_id}, Tipo: {sensor.sensor_type}")
            
        return True
        
    except Exception as e:
        print(f"❌ Error creando sensores: {e}")
        db.rollback()
        return False
    finally:
        db.close()

if __name__ == "__main__":
    create_test_sensors()
