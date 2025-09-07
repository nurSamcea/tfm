#!/usr/bin/env python3
"""
Script para limpiar específicamente los datos de María García (farmer_id = 2).
"""

import os
import sys

# Agregar el directorio backend al path
sys.path.append(os.path.join(os.path.dirname(__file__), 'backend'))

from backend.app.database import SessionLocal
from backend.app.models import sensor, sensor_reading

def clean_maria_data():
    """Limpiar datos específicos de María García (farmer_id = 2)."""
    farmer_id = 2
    print(f"🧹 Limpiando datos de María García (farmer_id = {farmer_id})...")
    
    db = SessionLocal()
    try:
        # Obtener zonas del farmer
        zones = db.query(sensor.SensorZone).filter(sensor.SensorZone.farmer_id == farmer_id).all()
        zone_ids = [zone.id for zone in zones]
        
        if not zone_ids:
            print(f"   ℹ️  No hay zonas para limpiar para farmer {farmer_id}")
            return
        
        print(f"   📍 Encontradas {len(zones)} zonas para limpiar:")
        for zone in zones:
            print(f"      - {zone.name}")
        
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
        print(f"✅ Limpieza completada para María García")
        
    except Exception as e:
        print(f"❌ Error durante la limpieza: {e}")
        db.rollback()
        raise
    finally:
        db.close()

if __name__ == "__main__":
    clean_maria_data()
