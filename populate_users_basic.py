#!/usr/bin/env python3
"""
Script para poblar la base de datos con usuarios básicos necesarios.
"""

import os
import sys
import datetime

# Agregar el directorio backend al path
sys.path.append(os.path.join(os.path.dirname(__file__), 'backend'))

from backend.app.database import SessionLocal
from backend.app.models import user

def create_basic_users(db):
    """Crear usuarios básicos necesarios para el sistema."""
    
    # Verificar si ya existen usuarios
    existing_users = db.query(user.User).count()
    if existing_users > 0:
        print(f"📊 Ya existen {existing_users} usuarios en la base de datos")
        return
    
    print("👥 Creando usuarios básicos...")
    
    users_data = [
        {
            "id": 2,
            "name": "María García",
            "email": "maria@huerta.com",
            "password_hash": "$2b$12$hash123",  # Hash de ejemplo
            "role": "farmer",
            "entity_name": "Huerta Ecológica María",
            "location_lat": 40.4168,
            "location_lon": -3.7038,
            "preferences": '{"certifications": ["organic", "eco_friendly"], "specialties": ["vegetables", "herbs"]}'
        },
        {
            "id": 6,
            "name": "Pedro Sánchez",
            "email": "pedro@huerta.com",
            "password_hash": "$2b$12$hash456",
            "role": "farmer",
            "entity_name": "Huerta Pedro",
            "location_lat": 40.4300,
            "location_lon": -3.7100,
            "preferences": '{"certifications": ["organic"], "specialties": ["root_vegetables", "fruits"]}'
        },
        {
            "id": 13,
            "name": "Carlos López",
            "email": "carlos@huerta.com",
            "password_hash": "$2b$12$hash789",
            "role": "farmer",
            "entity_name": "Granja Carlos",
            "location_lat": 40.4200,
            "location_lon": -3.7000,
            "preferences": '{"certifications": ["organic"], "specialties": ["dairy", "poultry"]}'
        },
        {
            "id": 1,
            "name": "Juan Pérez",
            "email": "juan@email.com",
            "password_hash": "$2b$12$hash123",
            "role": "consumer",
            "entity_name": "Juan Pérez",
            "location_lat": 40.4168,
            "location_lon": -3.7038,
            "preferences": '{"diet": "omnivore", "preferences": ["local_products"]}'
        },
        {
            "id": 3,
            "name": "SuperMercadoX",
            "email": "x@super.com",
            "password_hash": "$2b$12$hash789",
            "role": "supermarket",
            "entity_name": "SuperMercadoX",
            "location_lat": 40.4168,
            "location_lon": -3.7038,
            "preferences": '{"services": ["delivery", "pickup"], "specialties": ["dairy", "bakery"]}'
        }
    ]
    
    created_users = []
    
    for user_data in users_data:
        new_user = user.User(
            id=user_data["id"],
            name=user_data["name"],
            email=user_data["email"],
            password_hash=user_data["password_hash"],
            role=user_data["role"],
            entity_name=user_data["entity_name"],
            location_lat=user_data["location_lat"],
            location_lon=user_data["location_lon"],
            preferences=user_data["preferences"],
            created_at=datetime.datetime.now()
        )
        
        db.add(new_user)
        created_users.append(new_user)
        print(f"   ✅ Creado usuario: {user_data['name']} ({user_data['role']})")
    
    db.commit()
    print(f"🎉 Creados {len(created_users)} usuarios básicos")

def main():
    """Función principal."""
    print("🚀 Iniciando creación de usuarios básicos...")
    
    db = SessionLocal()
    try:
        create_basic_users(db)
    except Exception as e:
        print(f"❌ Error al crear usuarios: {e}")
        db.rollback()
        raise
    finally:
        db.close()

if __name__ == "__main__":
    main()
