#!/usr/bin/env python3
"""
Script para poblar la base de datos con datos de sensores para farmers.
Crea sensores, zonas y lecturas de ejemplo para los farmers especificados.
"""

import os
import sys
import random
import datetime
import time
from datetime import timedelta

# Agregar el directorio backend al path
sys.path.append(os.path.join(os.path.dirname(__file__), 'backend'))

from backend.app.database import SessionLocal
from backend.app.models import user, sensor, sensor_reading

def create_sensor_zones(db, farmer_id, farmer_name):
    """Crear zonas de sensores para un farmer."""
    zones = []
    
    # Primero obtener todas las zonas existentes para este farmer
    existing_zones = db.query(sensor.SensorZone).filter(
        sensor.SensorZone.farmer_id == farmer_id
    ).all()
    
    existing_zone_names = {zone.name for zone in existing_zones}
    print(f"   📍 Zonas existentes para farmer {farmer_id}: {list(existing_zone_names)}")
    
    if farmer_id == 2:  # María García - Huerta Ecológica
        zones_data = [
            {"name": "Invernadero Principal", "description": "Invernadero para tomates y lechugas", "lat": 40.4168, "lon": -3.7038},
            {"name": "Campo Abierto", "description": "Campo para cultivos al aire libre", "lat": 40.4170, "lon": -3.7040},
            {"name": "Almacén", "description": "Almacén de productos cosechados", "lat": 40.4165, "lon": -3.7035},
        ]
    elif farmer_id == 6:  # Pedro Sánchez - Huerta Pedro
        zones_data = [
            {"name": "Invernadero Fresas", "description": "Invernadero especializado en fresas", "lat": 40.4300, "lon": -3.7100},
            {"name": "Campo Zanahorias", "description": "Campo de zanahorias orgánicas", "lat": 40.4305, "lon": -3.7105},
            {"name": "Gallinero", "description": "Gallinero para huevos camperos", "lat": 40.4295, "lon": -3.7095},
            {"name": "Almacén Frío", "description": "Almacén refrigerado", "lat": 40.4302, "lon": -3.7102},
        ]
    elif farmer_id == 13:  # Asumiendo que es otro farmer
        zones_data = [
            {"name": "Campo Principal", "description": "Campo principal de cultivos", "lat": 40.4200, "lon": -3.7000},
            {"name": "Invernadero", "description": "Invernadero de cultivos", "lat": 40.4205, "lon": -3.7005},
            {"name": "Almacén", "description": "Almacén de productos", "lat": 40.4195, "lon": -3.6995},
        ]
    else:
        # Zonas genéricas
        zones_data = [
            {"name": f"Zona Principal {farmer_name}", "description": "Zona principal de cultivos", "lat": 40.4168, "lon": -3.7038},
            {"name": f"Almacén {farmer_name}", "description": "Almacén de productos", "lat": 40.4165, "lon": -3.7035},
        ]
    
    # Agregar zonas existentes a la lista
    for existing_zone in existing_zones:
        zones.append(existing_zone)
    
    # Crear solo las zonas que no existen
    for zone_data in zones_data:
        if zone_data["name"] in existing_zone_names:
            print(f"   ⚠️  Zona {zone_data['name']} ya existe, saltando...")
            continue
            
        print(f"   ➕ Creando nueva zona: {zone_data['name']}")
        zone = sensor.SensorZone(
            name=zone_data["name"],
            description=zone_data["description"],
            location_lat=zone_data["lat"],
            location_lon=zone_data["lon"],
            farmer_id=farmer_id,
            is_active=True,
            created_at=datetime.datetime.now()
        )
        db.add(zone)
        zones.append(zone)
    
    db.commit()
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
        
        # Verificar si el sensor ya existe
        existing_sensor = db.query(sensor.Sensor).filter(sensor.Sensor.device_id == device_id).first()
        if existing_sensor:
            print(f"   ⚠️  Sensor {device_id} ya existe, saltando...")
            sensors.append(existing_sensor)
            continue
        
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
            zone_id=zone.id,
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
    return sensors

def create_sensor_readings(db, sensors, days_back=7):
    """Crear lecturas de sensores para los últimos días."""
    readings = []
    
    for sensor_obj in sensors:
        # Crear lecturas para los últimos días
        for day in range(days_back):
            date = datetime.datetime.now() - timedelta(days=day)
            
            # Crear múltiples lecturas por día (cada 5 minutos para datos más frecuentes)
            for hour in range(0, 24):
                for minute in range(0, 60, 5):  # Cada 5 minutos
                    reading_time = date.replace(hour=hour, minute=minute, second=random.randint(0, 59))
                
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

def generate_realtime_sensor_data(db, sensors, duration_minutes=60, interval_seconds=30):
    """Generar datos de sensores en tiempo real para simular actualizaciones continuas."""
    print(f"🔄 Iniciando generación de datos en tiempo real...")
    print(f"   - Duración: {duration_minutes} minutos")
    print(f"   - Intervalo: {interval_seconds} segundos")
    print(f"   - Sensores: {len(sensors)}")
    
    start_time = datetime.datetime.now()
    end_time = start_time + timedelta(minutes=duration_minutes)
    readings_count = 0
    
    try:
        while datetime.datetime.now() < end_time:
            current_time = datetime.datetime.now()
            
            # Generar lecturas para algunos sensores aleatorios
            selected_sensors = random.sample(sensors, min(5, len(sensors)))
            
            for sensor_obj in selected_sensors:
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
                    created_at=current_time,
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
                readings_count += 1
            
            db.commit()
            
            # Mostrar progreso cada 2 minutos
            elapsed = (current_time - start_time).total_seconds() / 60
            if int(elapsed) % 2 == 0 and elapsed > 0:
                print(f"   📊 Progreso: {elapsed:.1f}min - {readings_count} lecturas generadas")
            
            # Esperar antes de la siguiente iteración
            time.sleep(interval_seconds)
    
    except KeyboardInterrupt:
        print(f"\n⏹️  Generación interrumpida por el usuario")
    except Exception as e:
        print(f"\n❌ Error durante la generación: {e}")
    finally:
        print(f"\n✅ Generación completada:")
        print(f"   - Total lecturas generadas: {readings_count}")
        print(f"   - Tiempo transcurrido: {(datetime.datetime.now() - start_time).total_seconds()/60:.1f} minutos")

def clean_existing_data(db, farmer_id):
    """Limpiar datos existentes para un farmer específico."""
    print(f"🧹 Limpiando datos existentes para farmer {farmer_id}...")
    
    # Obtener zonas del farmer
    zones = db.query(sensor.SensorZone).filter(sensor.SensorZone.farmer_id == farmer_id).all()
    zone_ids = [zone.id for zone in zones]
    
    if not zone_ids:
        print(f"   ℹ️  No hay zonas para limpiar para farmer {farmer_id}")
        return
    
    print(f"   📍 Encontradas {len(zones)} zonas para limpiar")
    
    # Obtener sensores de las zonas
    sensors = db.query(sensor.Sensor).filter(sensor.Sensor.zone_id.in_(zone_ids)).all()
    sensor_ids = [s.id for s in sensors]
    
    if sensor_ids:
        print(f"   🔧 Encontrados {len(sensors)} sensores para limpiar")
        
        # Eliminar lecturas
        readings_deleted = db.query(sensor_reading.SensorReading).filter(
            sensor_reading.SensorReading.sensor_id.in_(sensor_ids)
        ).delete(synchronize_session=False)
        print(f"   🗑️  Eliminadas {readings_deleted} lecturas")
        
        # Eliminar sensores
        sensors_deleted = db.query(sensor.Sensor).filter(
            sensor.Sensor.id.in_(sensor_ids)
        ).delete(synchronize_session=False)
        print(f"   🗑️  Eliminados {sensors_deleted} sensores")
    else:
        print(f"   ℹ️  No hay sensores para limpiar")
    
    # Eliminar zonas
    zones_deleted = db.query(sensor.SensorZone).filter(
        sensor.SensorZone.farmer_id == farmer_id
    ).delete(synchronize_session=False)
    print(f"   🗑️  Eliminadas {zones_deleted} zonas")
    
    db.commit()
    print(f"✅ Limpieza completada para farmer {farmer_id}")

def main():
    """Función principal para poblar la base de datos con sensores."""
    print("🚀 Iniciando población de base de datos con sensores...")
    
    db = SessionLocal()
    try:
        # Verificar que los farmers existen
        farmers = db.query(user.User).filter(user.User.role == 'farmer').all()
        print(f"📊 Farmers encontrados: {len(farmers)}")
        
        for farmer in farmers:
            print(f"👨‍🌾 Farmer: {farmer.name} (ID: {farmer.id})")
        
        # Configuración de sensores por farmer
        farmer_configs = {
            2: {"name": "María García", "sensors": 20},  # Huerta Ecológica
            6: {"name": "Pedro Sánchez", "sensors": 50},  # Huerta Pedro
            13: {"name": "Farmer 13", "sensors": 100}  # Asumiendo que existe
        }
        
        total_sensors = 0
        total_readings = 0
        
        # Preguntar si se quiere limpiar datos existentes
        print(f"\n🧹 ¿Deseas limpiar datos existentes antes de crear nuevos? (s/n): ", end="")
        try:
            clean_response = input().lower().strip()
            should_clean = clean_response in ['s', 'si', 'sí', 'y', 'yes']
        except (EOFError, KeyboardInterrupt):
            should_clean = False
            print(f"\n👋 Continuando sin limpiar...")
        
        for farmer_id, config in farmer_configs.items():
            # Verificar que el farmer existe
            farmer = db.query(user.User).filter(user.User.id == farmer_id, user.User.role == 'farmer').first()
            if not farmer:
                print(f"⚠️  Farmer con ID {farmer_id} no encontrado, saltando...")
                continue
            
            print(f"\n🌱 Configurando sensores para {config['name']} (ID: {farmer_id})...")
            
            # Limpiar datos existentes si se solicita
            if should_clean:
                clean_existing_data(db, farmer_id)
            
            # 1. Crear zonas de sensores
            print(f"   📍 Creando zonas de sensores...")
            zones = create_sensor_zones(db, farmer_id, config['name'])
            print(f"   ✅ Creadas {len(zones)} zonas")
            
            # 2. Crear sensores
            print(f"   🔧 Creando {config['sensors']} sensores...")
            sensors = create_sensors_for_farmer(db, farmer_id, config['name'], config['sensors'], zones)
            print(f"   ✅ Creados {len(sensors)} sensores")
            total_sensors += len(sensors)
            
            # 3. Crear lecturas de sensores (solo para algunos sensores para no sobrecargar)
            print(f"   📊 Creando lecturas de sensores...")
            selected_sensors = random.sample(sensors, min(10, len(sensors)))  # Máximo 10 sensores con lecturas
            readings = create_sensor_readings(db, selected_sensors, days_back=3)  # Solo 3 días de datos
            print(f"   ✅ Creadas {len(readings)} lecturas")
            total_readings += len(readings)
        
        print(f"\n🎉 ¡Base de datos poblada exitosamente!")
        print(f"📊 Resumen:")
        print(f"   - Total sensores creados: {total_sensors}")
        print(f"   - Total lecturas creadas: {total_readings}")
        print(f"   - Farmers configurados: {len(farmer_configs)}")
        
        # Preguntar si se quiere generar datos en tiempo real
        print(f"\n🔄 ¿Deseas generar datos en tiempo real? (s/n): ", end="")
        try:
            response = input().lower().strip()
            if response in ['s', 'si', 'sí', 'y', 'yes']:
                # Obtener todos los sensores creados
                all_sensors = db.query(sensor.Sensor).all()
                print(f"\n🚀 Iniciando generación de datos en tiempo real...")
                print(f"   - Presiona Ctrl+C para detener")
                
                # Generar datos en tiempo real por 30 minutos con intervalos de 30 segundos
                generate_realtime_sensor_data(db, all_sensors, duration_minutes=30, interval_seconds=30)
        except (EOFError, KeyboardInterrupt):
            print(f"\n👋 Saliendo...")
        
    except Exception as e:
        print(f"❌ Error al poblar la base de datos: {e}")
        db.rollback()
        raise
    finally:
        db.close()

if __name__ == "__main__":
    main()
