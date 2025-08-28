#!/usr/bin/env python3
"""
Script de Gestión de Base de Datos para Railway - Plataforma de Gestión Alimentaria
"""

import os
import sys
import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
from pathlib import Path
import argparse

# Cargar variables de entorno desde el archivo .env de la raíz
def load_env_from_root():
    """Cargar variables de entorno desde el archivo .env de la raíz del proyecto"""
    root_dir = Path(__file__).parent.parent  # Subir un nivel desde database/
    env_file = root_dir / '.env'
    
    if env_file.exists():
        print(f"📁 Cargando variables de entorno desde: {env_file}")
        with open(env_file, 'r') as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith('#') and '=' in line:
                    key, value = line.split('=', 1)
                    os.environ[key] = value
        return True
    else:
        print(f"⚠️  Archivo .env no encontrado en: {env_file}")
        return False

def get_database_connection():
    """Obtener conexión a la base de datos de Railway"""
    
    # Cargar variables de entorno desde la raíz
    load_env_from_root()
    
    # Obtener DATABASE_URL del archivo .env de la raíz
    database_url = os.getenv('DATABASE_URL')
    
    if not database_url:
        print("❌ Error: DATABASE_URL no encontrada en el archivo .env")
        print("💡 Asegúrate de que el archivo .env en la raíz del proyecto contenga DATABASE_URL")
        return None
    
    print(f"🔗 Conectando a Railway con: {database_url.split('@')[1] if '@' in database_url else 'URL oculta'}")
    
    try:
        # Conectar directamente con la URL
        conn = psycopg2.connect(database_url)
        conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
        cursor = conn.cursor()
        print("✅ Conexión exitosa a Railway")
        return conn, cursor
    except Exception as e:
        print(f"❌ Error conectando a Railway: {e}")
        return None, None

def create_tables():
    """Crear todas las tablas del esquema"""
    conn, cursor = get_database_connection()
    if not conn:
        return False
    
    try:
        print("📋 Creando esquema de tablas...")
        schema_path = Path(__file__).parent / 'schema.sql'
        with open(schema_path, 'r', encoding='utf-8') as file:
            schema = file.read()
        
        # Ejecutar todo el esquema de una vez
        try:
            cursor.execute(schema)
            print("✅ Esquema de tablas creado exitosamente")
        except psycopg2.Error as e:
            if "already exists" in str(e):
                print("⚠️  Algunas tablas ya existen, continuando...")
            else:
                print(f"❌ Error ejecutando esquema: {e}")
                return False
        
        return True
        
    except Exception as e:
        print(f"❌ Error creando tablas: {e}")
        return False
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

def delete_tables():
    """Eliminar todas las tablas"""
    conn, cursor = get_database_connection()
    if not conn:
        return False
    
    try:
        print("🗑️  Eliminando todas las tablas...")
        
        # Obtener lista de tablas
        cursor.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'public' AND table_type = 'BASE TABLE'
        """)
        tables = cursor.fetchall()
        
        if not tables:
            print("ℹ️  No hay tablas para eliminar")
            return True
        
        # Eliminar tablas en orden correcto (respetando foreign keys)
        table_names = [table[0] for table in tables]
        print(f"📋 Tablas encontradas: {', '.join(table_names)}")
        
        # Orden de eliminación para respetar foreign keys
        delete_order = [
            'blockchain_logs', 'impact_metrics', 'qrs', 'sensor_readings',
            'transactions', 'shopping_list_items', 'shopping_list_groups',
            'shopping_lists', 'products', 'users'
        ]
        
        for table in delete_order:
            if table in table_names:
                try:
                    cursor.execute(f"DROP TABLE IF EXISTS {table} CASCADE")
                    print(f"✅ Tabla '{table}' eliminada")
                except Exception as e:
                    print(f"⚠️  Error eliminando tabla '{table}': {e}")
        
        print("✅ Todas las tablas eliminadas")
        return True
        
    except Exception as e:
        print(f"❌ Error eliminando tablas: {e}")
        return False
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

def update_data():
    """Insertar datos de ejemplo en las tablas"""
    conn, cursor = get_database_connection()
    if not conn:
        return False
    
    try:
        print("📊 Insertando datos de ejemplo...")
        data_path = Path(__file__).parent / 'sample_data.sql'
        with open(data_path, 'r', encoding='utf-8') as file:
            data = file.read()
        
        # Ejecutar todo el script de datos de una vez
        try:
            cursor.execute(data)
            print("✅ Datos insertados exitosamente")
        except psycopg2.Error as e:
            if "duplicate key" in str(e) or "already exists" in str(e):
                print("⚠️  Algunos datos ya existen, continuando...")
            else:
                print(f"❌ Error insertando datos: {e}")
                return False
        
        # Verificar datos insertados
        try:
            cursor.execute("SELECT COUNT(*) FROM users")
            users_count = cursor.fetchone()[0]
            cursor.execute("SELECT COUNT(*) FROM products")
            products_count = cursor.fetchone()[0]
            
            print(f"✅ Verificación de datos:")
            print(f"   👥 Usuarios: {users_count}")
            print(f"   🛍️  Productos: {products_count}")
        except Exception as e:
            print(f"⚠️  No se pudo verificar los datos: {e}")
        
        return True
        
    except Exception as e:
        print(f"❌ Error insertando datos: {e}")
        return False
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

def check_status():
    """Verificar el estado actual de la base de datos"""
    conn, cursor = get_database_connection()
    if not conn:
        return False
    
    try:
        print("📊 Estado actual de la base de datos:")
        
        # Verificar tablas
        cursor.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'public' AND table_type = 'BASE TABLE'
            ORDER BY table_name
        """)
        tables = cursor.fetchall()
        
        print(f"📋 Tablas encontradas: {len(tables)}")
        for table in tables:
            print(f"   - {table[0]}")
        
        # Contar registros en tablas principales
        for table_name in ['users', 'products', 'shopping_lists']:
            try:
                cursor.execute(f"SELECT COUNT(*) FROM {table_name}")
                count = cursor.fetchone()[0]
                print(f"   📝 {table_name}: {count} registros")
            except:
                print(f"   ❌ {table_name}: tabla no encontrada")
        
        return True
        
    except Exception as e:
        print(f"❌ Error verificando estado: {e}")
        return False
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

def main():
    parser = argparse.ArgumentParser(description='Gestor de Base de Datos para Railway')
    parser.add_argument('actions', nargs='+', choices=['create', 'delete', 'update', 'status'], 
                       help='Acciones a realizar (puedes especificar múltiples)')
    
    args = parser.parse_args()
    
    print("🚀 Iniciando gestión de base de datos en Railway...")
    
    # Ejecutar acciones en orden
    for action in args.actions:
        print(f"\n📋 Ejecutando acción: {action}")
        
        if action == 'create':
            success = create_tables()
            if success:
                print("🎉 ¡Tablas creadas exitosamente!")
            else:
                print("💥 Error creando tablas")
                sys.exit(1)
        
        elif action == 'delete':
            success = delete_tables()
            if success:
                print("🎉 ¡Tablas eliminadas exitosamente!")
            else:
                print("💥 Error eliminando tablas")
                sys.exit(1)
        
        elif action == 'update':
            success = update_data()
            if success:
                print("🎉 ¡Datos actualizados exitosamente!")
            else:
                print("💥 Error actualizando datos")
                sys.exit(1)
        
        elif action == 'status':
            success = check_status()
            if not success:
                print("💥 Error verificando estado")
                sys.exit(1)
    
    print(f"\n✅ Todas las acciones completadas: {', '.join(args.actions)}")

if __name__ == "__main__":
    main()
