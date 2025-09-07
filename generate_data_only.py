#!/usr/bin/env python3
"""
Script simple para generar solo datos de sensores en tiempo real.
No crea sensores ni zonas, solo genera lecturas para sensores existentes.
"""

import os
import sys
import random
import datetime
import time
import argparse
from datetime import timedelta

# Agregar el directorio backend al path
sys.path.append(os.path.join(os.path.dirname(__file__), 'backend'))

from backend.app.database import SessionLocal
from backend.app.models import sensor, sensor_reading

def generate_sensor_readings_only(duration_minutes=None, interval_seconds=30, farmer_id=None):
    """Generar solo lecturas de sensores para sensores existentes."""
    print(f"🔄 Generando lecturas de sensores en tiempo real...")
    print(f"   - Duración: {'Indefinida' if duration_minutes is None else f'{duration_minutes} minutos'}")
    print(f"   - Intervalo: {interval_seconds} segundos")
    print(f"   - Farmer ID: {'Todos' if farmer_id is None else farmer_id}")
    print(f"   - Presiona Ctrl+C para detener")
    
    db = SessionLocal()
    try:
        # Obtener sensores existentes
        query = db.query(sensor.Sensor)
        if farmer_id:
            # Obtener sensores de un farmer específico
            query = query.join(sensor.SensorZone).filter(sensor.SensorZone.farmer_id == farmer_id)
        
        sensors = query.all()
        
        if not sensors:
            print(f"❌ No se encontraron sensores para el farmer {farmer_id}")
            return
        
        print(f"   - Sensores encontrados: {len(sensors)}")
        
        start_time = datetime.datetime.now()
        end_time = start_time + timedelta(minutes=duration_minutes) if duration_minutes else None
        readings_count = 0
        
        while True:
            # Verificar si se ha alcanzado el tiempo límite
            if end_time and datetime.datetime.now() >= end_time:
                print(f"\n⏰ Tiempo límite alcanzado ({duration_minutes} minutos)")
                break
            
            current_time = datetime.datetime.now()
            
            # Generar lecturas para algunos sensores aleatorios (máximo 10 por iteración)
            selected_sensors = random.sample(sensors, min(10, len(sensors)))
            
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
        db.close()

def main():
    """Función principal."""
    parser = argparse.ArgumentParser(description='Generar solo lecturas de sensores en tiempo real')
    parser.add_argument('--duration', type=int, help='Duración en minutos (opcional, por defecto indefinida)')
    parser.add_argument('--interval', type=int, default=30, help='Intervalo entre lecturas en segundos (por defecto 30)')
    parser.add_argument('--farmer-id', type=int, help='ID del farmer específico (opcional, por defecto todos)')
    
    args = parser.parse_args()
    
    generate_sensor_readings_only(
        duration_minutes=args.duration,
        interval_seconds=args.interval,
        farmer_id=args.farmer_id
    )

if __name__ == "__main__":
    main()
