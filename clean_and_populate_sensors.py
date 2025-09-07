#!/usr/bin/env python3
"""
Script para limpiar y poblar las tablas de sensores con datos correctos.
Primero limpia todas las tablas relacionadas con sensores y luego las rellena.
"""

import os
import sys
import random
import datetime
from datetime import timedelta

# Agregar el directorio backend al path
sys.path.append(os.path.join(os.path.dirname(__file__), 'backend'))

from backend.app.database import SessionLocal
from backend.app.models import user, sensor, sensor_reading, sensor_alert

def clean_sensor_tables(db):
    """Limpiar todas las tablas relacionadas con sensores."""
    print("🧹 Limpiando tablas de sensores...")
    
    try:
        # Limpiar en orden correcto para evitar conflictos de foreign key
        print("   🗑️  Eliminando alertas de sensores...")
        db.query(sensor_alert.SensorAlert).delete()
        
        print("   🗑️  Eliminando lecturas de sensores...")
        db.query(sensor_reading.SensorReading).delete()
        
        print("   🗑️  Eliminando sensores...")
        db.query(sensor.Sensor).delete()
        
        print("   🗑️  Eliminando zonas de sensores...")
        db.query(sensor.SensorZone).delete()
        
        db.commit()
        print("   ✅ Tablas de sensores limpiadas correctamente")
        
    except Exception as e:
        print(f"   ❌ Error limpiando tablas: {e}")
        db.rollback()
        raise

def create_sensor_zones_for_farmer(db, farmer_id, farmer_name):
    """Crear zonas de sensores específicas para un farmer."""
    zones = []
    
    if farmer_id == 2:  # María García - Huerta Ecológica
        zones_data = [
            {"name": "Invernadero Principal", "description": "Invernadero para tomates y lechugas", "lat": 40.4168, "lon": -3.7038},
            {"name": "Campo Abierto", "description": "Campo para cultivos al aire libre", "lat": 40.4170, "lon": -3.7040},
            {"name": "Almacén", "description": "Almacén de productos cosechados", "lat": 40.4165, "lon": -3.7035},
            {"name": "Vivero", "description": "Vivero de plantas jóvenes", "lat": 40.4169, "lon": -3.7039},
        ]
    elif farmer_id == 6:  # Pedro Sánchez - Huerta Pedro
        zones_data = [
            {"name": "Invernadero Fresas", "description": "Invernadero especializado en fresas", "lat": 40.4300, "lon": -3.7100},
            {"name": "Campo Zanahorias", "description": "Campo de zanahorias orgánicas", "lat": 40.4305, "lon": -3.7105},
            {"name": "Gallinero", "description": "Gallinero para huevos camperos", "lat": 40.4295, "lon": -3.7095},
            {"name": "Almacén Frío", "description": "Almacén refrigerado", "lat": 40.4302, "lon": -3.7102},
            {"name": "Campo Naranjas", "description": "Campo de naranjas valencianas", "lat": 40.4308, "lon": -3.7108},
        ]
    elif farmer_id == 13:  # Carlos López - Granja Carlos
        zones_data = [
            {"name": "Campo Principal", "description": "Campo principal de cultivos", "lat": 40.4200, "lon": -3.7000},
            {"name": "Invernadero", "description": "Invernadero de cultivos", "lat": 40.4205, "lon": -3.7005},
            {"name": "Almacén", "description": "Almacén de productos", "lat": 40.4195, "lon": -3.6995},
            {"name": "Establo", "description": "Establo para ganado", "lat": 40.4202, "lon": -3.7002},
            {"name": "Silo", "description": "Silo de almacenamiento", "lat": 40.4198, "lon": -3.6998},
            {"name": "Campo Secundario", "description": "Campo secundario de cultivos", "lat": 40.4208, "lon": -3.7008},
        ]
    else:
        # Zonas genéricas
        zones_data = [
            {"name": f"Zona Principal {farmer_name}", "description": "Zona principal de cultivos", "lat": 40.4168, "lon": -3.7038},
            {"name": f"Almacén {farmer_name}", "description": "Almacén de productos", "lat": 40.4165, "lon": -3.7035},
        ]
    
    for zone_data in zones_data:
        zone = sensor.SensorZone(
            name=zone_data["name"],
            description=zone_data["description"],
            location_lat=zone_data["lat"],
            location_lon=zone_data["lon"],
            farmer_id=farmer_id,  # ¡IMPORTANTE! Asociar zona al farmer
            is_active=True,
            created_at=datetime.datetime.now()
        )
        db.add(zone)
        zones.append(zone)
    
    db.commit()
    print(f"   ✅ Creadas {len(zones)} zonas para {farmer_name}")
    return zones

def create_sensors_for_farmer(db, farmer_id, farmer_name, num_sensors, zones):
    """Crear sensores para un farmer específico."""
    sensors = []
    sensor_types = [
        sensor.SensorTypeEnum.temperature,
        sensor.SensorTypeEnum.humidity,
        sensor.SensorTypeEnum.soil_moisture,
        sensor.SensorTypeEnum.light,
        sensor.SensorTypeEnum.ph,
        sensor.SensorTypeEnum.gas
    ]
    
    for i in range(num_sensors):
        # Seleccionar zona aleatoria
        zone = random.choice(zones)
        sensor_type = random.choice(sensor_types)
        
        # Crear device_id único
        device_id = f"SENSOR_{farmer_id:02d}_{sensor_type.value.upper()}_{i+1:03d}"
        
        # Configurar umbrales según el tipo de sensor
        if sensor_type == sensor.SensorTypeEnum.temperature:
            min_threshold = 15.0
            max_threshold = 35.0
        elif sensor_type == sensor.SensorTypeEnum.humidity:
            min_threshold = 30.0
            max_threshold = 80.0
        elif sensor_type == sensor.SensorTypeEnum.soil_moisture:
            min_threshold = 20.0
            max_threshold = 80.0
        elif sensor_type == sensor.SensorTypeEnum.light:
            min_threshold = 1000.0
            max_threshold = 100000.0
        elif sensor_type == sensor.SensorTypeEnum.ph:
            min_threshold = 5.5
            max_threshold = 7.5
        else:  # gas
            min_threshold = 0.0
            max_threshold = 100.0
        
        # Crear sensor
        new_sensor = sensor.Sensor(
            device_id=device_id,
            name=f"Sensor {sensor_type.value.title()} {i+1} - {zone.name}",
            sensor_type=sensor_type,
            status=sensor.SensorStatusEnum.active,
            zone_id=zone.id,  # ¡IMPORTANTE! Asociar sensor a la zona
            location_lat=zone.location_lat + random.uniform(-0.001, 0.001),
            location_lon=zone.location_lon + random.uniform(-0.001, 0.001),
            location_description=f"Sensor ubicado en {zone.name}",
            min_threshold=min_threshold,
            max_threshold=max_threshold,
            alert_enabled=True,
            reading_interval=random.choice([30, 60, 120, 300]),  # 30s, 1min, 2min, 5min
            firmware_version="1.2.3",
            last_seen=datetime.datetime.now() - timedelta(minutes=random.randint(1, 60)),
            created_at=datetime.datetime.now() - timedelta(days=random.randint(1, 30)),
            config={
                "calibration_date": (datetime.datetime.now() - timedelta(days=random.randint(1, 30))).isoformat(),
                "battery_level": random.randint(20, 100),
                "signal_strength": random.randint(60, 100)
            }
        )
        
        db.add(new_sensor)
        sensors.append(new_sensor)
    
    db.commit()
    print(f"   ✅ Creados {len(sensors)} sensores para {farmer_name}")
    return sensors

def create_sample_readings(db, sensors, days_back=2):
    """Crear lecturas de ejemplo para algunos sensores."""
    readings = []
    
    # Solo crear lecturas para algunos sensores para no sobrecargar
    selected_sensors = random.sample(sensors, min(5, len(sensors)))
    
    for sensor_obj in selected_sensors:
        # Crear lecturas para los últimos días
        for day in range(days_back):
            date = datetime.datetime.now() - timedelta(days=day)
            
            # Crear lecturas cada 4 horas
            for hour in range(0, 24, 4):
                reading_time = date.replace(hour=hour, minute=random.randint(0, 59), second=0)
                
                # Generar valores según el tipo de sensor
                if sensor_obj.sensor_type == sensor.SensorTypeEnum.temperature:
                    temperature = random.uniform(15.0, 35.0)
                    humidity = None
                    gas_level = None
                    light_level = None
                    shock_detected = None
                    soil_moisture = None
                    ph_level = None
                elif sensor_obj.sensor_type == sensor.SensorTypeEnum.humidity:
                    temperature = None
                    humidity = random.uniform(30.0, 80.0)
                    gas_level = None
                    light_level = None
                    shock_detected = None
                    soil_moisture = None
                    ph_level = None
                elif sensor_obj.sensor_type == sensor.SensorTypeEnum.soil_moisture:
                    temperature = None
                    humidity = None
                    gas_level = None
                    light_level = None
                    shock_detected = None
                    soil_moisture = random.uniform(20.0, 80.0)
                    ph_level = None
                elif sensor_obj.sensor_type == sensor.SensorTypeEnum.light:
                    temperature = None
                    humidity = None
                    gas_level = None
                    light_level = random.uniform(1000.0, 100000.0)
                    shock_detected = None
                    soil_moisture = None
                    ph_level = None
                elif sensor_obj.sensor_type == sensor.SensorTypeEnum.ph:
                    temperature = None
                    humidity = None
                    gas_level = None
                    light_level = None
                    shock_detected = None
                    soil_moisture = None
                    ph_level = random.uniform(5.5, 7.5)
                else:  # gas
                    temperature = None
                    humidity = None
                    gas_level = random.uniform(0.0, 100.0)
                    light_level = None
                    shock_detected = None
                    soil_moisture = None
                    ph_level = None
                
                reading = sensor_reading.SensorReading(
                    sensor_id=sensor_obj.id,
                    temperature=temperature,
                    humidity=humidity,
                    gas_level=gas_level,
                    light_level=light_level,
                    shock_detected=shock_detected,
                    soil_moisture=soil_moisture,
                    ph_level=ph_level,
                    created_at=reading_time,
                    source_device=sensor_obj.device_id,
                    reading_quality=random.uniform(0.8, 1.0),
                    is_processed=random.choice([True, False]),
                    extra_data={
                        "battery_level": random.randint(20, 100),
                        "signal_strength": random.randint(60, 100),
                        "firmware_version": "1.2.3"
                    }
                )
                
                db.add(reading)
                readings.append(reading)
    
    db.commit()
    return readings

def main():
    """Función principal."""
    print("🚀 LIMPIEZA Y POBLACIÓN DE SENSORES")
    print("=" * 50)
    
    db = SessionLocal()
    try:
        # 1. Limpiar tablas existentes
        clean_sensor_tables(db)
        
        # 2. Verificar que los farmers existen
        farmers = db.query(user.User).filter(user.User.role == 'farmer').all()
        print(f"\n📊 Farmers encontrados: {len(farmers)}")
        
        for farmer in farmers:
            print(f"   👨‍🌾 {farmer.name} (ID: {farmer.id})")
        
        # 3. Configuración de sensores por farmer
        farmer_configs = {
            2: {"name": "María García", "sensors": 20},  # Huerta Ecológica
            6: {"name": "Pedro Sánchez", "sensors": 50},  # Huerta Pedro
            13: {"name": "Carlos López", "sensors": 100}  # Granja Carlos
        }
        
        total_sensors = 0
        total_readings = 0
        
        for farmer_id, config in farmer_configs.items():
            # Verificar que el farmer existe
            farmer = db.query(user.User).filter(user.User.id == farmer_id, user.User.role == 'farmer').first()
            if not farmer:
                print(f"⚠️  Farmer con ID {farmer_id} no encontrado, saltando...")
                continue
            
            print(f"\n🌱 Configurando sensores para {config['name']} (ID: {farmer_id})...")
            
            # 1. Crear zonas de sensores
            zones = create_sensor_zones_for_farmer(db, farmer_id, config['name'])
            
            # 2. Crear sensores
            sensors = create_sensors_for_farmer(db, farmer_id, config['name'], config['sensors'], zones)
            total_sensors += len(sensors)
            
            # 3. Crear lecturas de ejemplo
            readings = create_sample_readings(db, sensors, days_back=2)
            total_readings += len(readings)
        
        print(f"\n🎉 ¡CONFIGURACIÓN COMPLETA!")
        print(f"📊 Resumen:")
        print(f"   - Total sensores creados: {total_sensors}")
        print(f"   - Total lecturas creadas: {total_readings}")
        print(f"   - Farmers configurados: {len(farmer_configs)}")
        
        # 4. Verificar asociaciones
        print(f"\n🔍 Verificando asociaciones:")
        for farmer_id in farmer_configs.keys():
            zones_count = db.query(sensor.SensorZone).filter(sensor.SensorZone.farmer_id == farmer_id).count()
            sensors_count = db.query(sensor.Sensor).join(sensor.SensorZone).filter(sensor.SensorZone.farmer_id == farmer_id).count()
            print(f"   - Farmer {farmer_id}: {zones_count} zonas, {sensors_count} sensores")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        db.rollback()
        raise
    finally:
        db.close()

if __name__ == "__main__":
    main()
