import os
import sys
import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
from psycopg2.extras import RealDictCursor
from pathlib import Path
import argparse
from typing import Optional, List, Dict, Any
from abc import ABC, abstractmethod
from datetime import datetime
import json
import logging
from dotenv import load_dotenv

# Cargar variables de entorno desde .env
load_dotenv()

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout)
    ]
)
logger = logging.getLogger(__name__)


class DatabaseConnection:    
    def __init__(self):
        self.connection = None
        self.cursor = None
    
    def connect(self) -> bool:
        try:
            self._load_environment()
            
            database_url = os.getenv('DATABASE_URL')
            if not database_url:
                logger.error("DATABASE_URL no encontrada en variables de entorno")
                return False
            
            logger.info("Conectando a la base de datos...")
            self.connection = psycopg2.connect(database_url)
            self.connection.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
            self.cursor = self.connection.cursor(cursor_factory=RealDictCursor)
            logger.info("Conexión establecida exitosamente")
            return True
            
        except Exception as e:
            logger.error(f"Error conectando a la base de datos: {e}")
            return False
    
    def disconnect(self):
        if self.cursor:
            self.cursor.close()
        if self.connection:
            self.connection.close()
        logger.info("Conexión cerrada")
    
    def _load_environment(self):
        root_dir = Path(__file__).parent.parent
        env_file = root_dir / '.env'
        
        if env_file.exists():
            with open(env_file, 'r') as f:
                for line in f:
                    line = line.strip()
                    if line and not line.startswith('#') and '=' in line:
                        key, value = line.split('=', 1)
                        os.environ[key] = value

class SchemaManager:    
    def __init__(self, db_connection: DatabaseConnection):
        self.db = db_connection
        self.schema_file = Path(__file__).parent / 'db_schema.sql'

    def ensure_core_columns(self) -> bool:
        """Garantiza que las tablas existentes tengan las columnas core esperadas por el backend.

        Principio: esta función sólo conoce de ALTERs idempotentes (SRP), sin tocar datos.
        """
        try:
            # users
            self.db.cursor.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS entity_name TEXT;")
            self.db.cursor.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS location_lat DECIMAL;")
            self.db.cursor.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS location_lon DECIMAL;")
            self.db.cursor.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS preferences JSON;")

            # products
            self.db.cursor.execute("ALTER TABLE products ADD COLUMN IF NOT EXISTS currency VARCHAR(3) DEFAULT 'EUR';")
            self.db.cursor.execute("ALTER TABLE products ADD COLUMN IF NOT EXISTS unit VARCHAR(20);")
            self.db.cursor.execute("ALTER TABLE products ADD COLUMN IF NOT EXISTS expiration_date DATE;")
            self.db.cursor.execute("ALTER TABLE products ADD COLUMN IF NOT EXISTS certifications JSON;")
            self.db.cursor.execute("ALTER TABLE products ADD COLUMN IF NOT EXISTS category VARCHAR(50);")

            # transactions
            self.db.cursor.execute("ALTER TABLE transactions ADD COLUMN IF NOT EXISTS order_details JSON NOT NULL DEFAULT '[]';")

            return True
        except Exception as e:
            logger.error(f"Error asegurando columnas core: {e}")
            return False

    def fix_sequences(self) -> bool:
        """Corrige las secuencias de auto-incremento que se han desincronizado con los datos existentes."""
        try:
            logger.info("Corrigiendo secuencias de auto-incremento...")
            
            # Solo corregir secuencias de las tablas principales que realmente las necesitan
            main_tables = ['users', 'products', 'sensors', 'sensor_readings', 'transactions']
            
            for table in main_tables:
                try:
                    # Verificar si la tabla existe
                    self.db.cursor.execute("""
                        SELECT EXISTS (
                            SELECT FROM information_schema.tables 
                            WHERE table_schema = 'public' 
                            AND table_name = %s
                        );
                    """, (table,))
                    
                    result = self.db.cursor.fetchone()
                    if not result or not result[0]:
                        continue
                    
                    # Obtener el nombre de la secuencia
                    self.db.cursor.execute("""
                        SELECT pg_get_serial_sequence(%s, 'id');
                    """, (table,))
                    sequence_result = self.db.cursor.fetchone()
                    
                    if sequence_result and sequence_result[0]:
                        sequence_name = sequence_result[0]
                        # Obtener el máximo ID actual
                        self.db.cursor.execute(f"SELECT COALESCE(MAX(id), 0) FROM {table};")
                        result = self.db.cursor.fetchone()
                        max_id = result[0] if result else 0
                        
                        # Corregir la secuencia
                        new_value = max_id + 1
                        self.db.cursor.execute(f"SELECT setval(%s, %s, false);", (sequence_name, new_value))
                        logger.info(f"✓ Tabla '{table}': secuencia corregida a {new_value}")
                        
                except Exception as e:
                    logger.debug(f"Tabla '{table}': {e}")
                    continue
            
            logger.info("Corrección de secuencias completada")
            return True
            
        except Exception as e:
            logger.error(f"Error corrigiendo secuencias: {e}")
            return False
    def create_all_tables(self) -> bool:
        if not self.schema_file.exists():
            logger.error(f"Archivo de esquema no encontrado: {self.schema_file}")
            return False
        
        try:
            logger.info("Creando esquema completo de la base de datos...")
            with open(self.schema_file, 'r', encoding='utf-8') as f:
                schema_sql = f.read()
            
            self.db.cursor.execute(schema_sql)
            logger.info("Esquema completo creado exitosamente")
            return True
            
        except Exception as e:
            logger.error(f"Error creando esquema: {e}")
            return False
    
    def create_missing_tables(self, tables: Optional[List[str]] = None) -> bool:
        """Crea sólo las tablas que falten ejecutando fragmentos del schema.

        Busca bloques CREATE TABLE IF NOT EXISTS <tabla> y ejecuta sólo los seleccionados.
        """
        if not self.schema_file.exists():
            logger.error(f"Archivo de esquema no encontrado: {self.schema_file}")
            return False
        try:
            with open(self.schema_file, 'r', encoding='utf-8') as f:
                schema_sql = f.read()

            # Obtener existentes
            self.db.cursor.execute("""
                SELECT table_name FROM information_schema.tables
                WHERE table_schema = 'public'
            """)
            existing = {row['table_name'] for row in self.db.cursor.fetchall()}

            # Tablas objetivo
            target = set(tables) if tables else set()

            created_any = False

            def exec_block(name: str, sql_block: str):
                nonlocal created_any
                if name in existing:
                    return
                self.db.cursor.execute(sql_block)
                logger.info(f"Tabla creada: {name}")
                created_any = True

            # Extraer bloques simples por nombre
            blocks = {
                'users': "CREATE TABLE IF NOT EXISTS users (",
                'sensor_zones': "CREATE TABLE IF NOT EXISTS sensor_zones (",
                'sensors': "CREATE TABLE IF NOT EXISTS sensors (",
                'products': "CREATE TABLE IF NOT EXISTS products (",
                'sensor_readings': "CREATE TABLE IF NOT EXISTS sensor_readings (",
                'sensor_alerts': "CREATE TABLE IF NOT EXISTS sensor_alerts (",
                'transactions': "CREATE TABLE IF NOT EXISTS transactions (",
            }

            for name, marker in blocks.items():
                if target and name not in target:
                    continue
                start = schema_sql.find(marker)
                if start == -1:
                    continue
                # Buscar fin del bloque por ");\n" dos saltos después opcionales
                end = schema_sql.find(")\n", start)
                # Avanzar hasta el primer punto y coma posterior al paréntesis de cierre
                semicolon = schema_sql.find(";", end)
                block_sql = schema_sql[start:semicolon+1]
                exec_block(name, block_sql)

            return created_any
        except Exception as e:
            logger.error(f"Error creando tablas faltantes: {e}")
            return False

    def drop_all_tables(self) -> bool:
        try:
            logger.info("Eliminando todas las tablas...")
            
            tables_to_drop = [
                'sensor_readings',
                'sensor_alerts', 
                'sensors',
                'sensor_zones',
                'transactions',
                'shopping_list_items',
                'shopping_list_groups',
                'shopping_lists',
                'products',
                'qrs',
                'impact_metrics',
                'blockchain_logs',
                'users'
                'product_traceability_chains',
                'alembic_version',
                'quality_checks',
                'sensor_traceability',
                'traceability_events',
                'transport_logs',
            ]
            
            for table in tables_to_drop:
                try:
                    self.db.cursor.execute(f"DROP TABLE IF EXISTS {table} CASCADE;")
                    logger.info(f"Tabla {table} eliminada")
                except Exception as e:
                    logger.warning(f"No se pudo eliminar la tabla {table}: {e}")
            
            logger.info("Todas las tablas eliminadas exitosamente")
            return True
            
        except Exception as e:
            logger.error(f"Error eliminando tablas: {e}")
            return False

class DataManager:    
    def __init__(self, db_connection: DatabaseConnection):
        self.db = db_connection
    
    def insert_sample_data(self) -> bool:
        try:
            logger.info("Insertando datos de ejemplo...")
            
            # Insertar usuarios
            users_data = [
                (1, 'María García', 'maria@farmer.com', 'hashed_password_123', 'farmer', '123456789', 'Granja Los Olivos, Córdoba'),
                (2, 'Pedro Sánchez', 'pedro@farmer.com', 'hashed_password_456', 'farmer', '987654321', 'Finca San Pedro, Sevilla'),
                (3, 'Juan Pérez', 'juan@consumer.com', 'hashed_password_789', 'consumer', '111222333', 'Calle Mayor 123, Madrid'),
                (4, 'Ana López', 'ana@consumer.com', 'hashed_password_101', 'consumer', '444555666', 'Avenida Libertad 45, Barcelona'),
                (5, 'Carlos Ruiz', 'carlos@consumer.com', 'hashed_password_112', 'consumer', '777888999', 'Plaza España 78, Valencia'),
                (6, 'Supermercado Central', 'info@supercentral.com', 'hashed_password_131', 'supermarket', '000111222', 'Centro Comercial Plaza, Madrid')
            ]
            
            for user in users_data:
                self.db.cursor.execute("""
                    INSERT INTO users (id, name, email, password_hash, role, phone, address) 
                    VALUES (%s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO NOTHING
                """, user)
            
            # Insertar zonas de sensores
            zones_data = [
                (1, 'Invernadero Principal', 'Zona principal de cultivo de la Huerta Ecológica María', 40.4168, -3.7038, 'Invernadero principal con tomates, lechugas y hierbas aromáticas', 1, True),
                (2, 'Campo Abierto', 'Campo de cultivo al aire libre', 37.7849, -122.4094, 'Zona B - Campo', 2, True)
            ]
            
            for zone in zones_data:
                self.db.cursor.execute("""
                    INSERT INTO sensor_zones (id, name, description, location_lat, location_lon, location_description, farmer_id, is_active) 
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO NOTHING
                """, zone)
            
            # Insertar sensores
            sensors_data = [
                (1, 'maria-garcia-sensor-001', 'Sensor Real María García', 'temperature', 'active', 1, 40.4168, -3.7038, 'Invernadero Principal - Huerta Ecológica María', 15.0, 35.0, True, 30, '2.0.0', datetime.now()),
                (2, 'pedro-sanchez-sensor-01', 'Sensor Simulado Pedro Sánchez', 'humidity', 'active', 2, 37.7849, -122.4094, 'Campo Abierto', 20.0, 80.0, True, 60, '1.0.0', datetime.now())
            ]
            
            for sensor in sensors_data:
                self.db.cursor.execute("""
                    INSERT INTO sensors (id, device_id, name, sensor_type, status, zone_id, location_lat, location_lon, location_description, min_threshold, max_threshold, alert_enabled, reading_interval, firmware_version, last_seen) 
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO NOTHING
                """, sensor)
            
            # Insertar productos
            products_data = [
                (1, 'Tomates Ecológicos', 'Tomates cultivados sin pesticidas', 2.50, 'verduras', True, 50, 'product_2_Tomates_Ecológicos_1.jpg', 1, False),
                (2, 'Lechuga Fresca', 'Lechuga iceberg recién cosechada', 1.80, 'verduras', False, 30, 'product_2_Lechuga_Fresca_2.jpg', 1, False),
                (3, 'Manzanas Rojas', 'Manzanas rojas del huerto', 3.20, 'frutas', True, 25, 'product_2_Manzanas_Rojas_3.jpg', 2, False),
                (4, 'Zanahorias Orgánicas', 'Zanahorias cultivadas orgánicamente', 2.80, 'verduras', True, 40, None, 2, False),
                (5, 'Arroz Integral', 'Arroz integral de grano largo', 4.50, 'cereales', True, 20, 'product_10_Arroz_Integral_15.jpg', 6, False),
                (6, 'Yogur Griego', 'Yogur griego natural', 3.80, 'lacteos', False, 15, 'product_10_Yogur_Griego_17.jpg', 6, False)
            ]
            
            for product in products_data:
                self.db.cursor.execute("""
                    INSERT INTO products (id, name, description, price, category, is_eco, stock_available, image_url, provider_id, is_hidden) 
                    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
                    ON CONFLICT (id) DO NOTHING
                """, product)
            
            logger.info("Datos de ejemplo insertados exitosamente")
            return True
            
        except Exception as e:
            logger.error(f"Error insertando datos de ejemplo: {e}")
            return False

class StatusChecker:    
    def __init__(self, db_connection: DatabaseConnection):
        self.db = db_connection
    
    def check_database_status(self) -> bool:
        try:
            logger.info("Verificando estado de la base de datos...")
            
            # Verificar tablas existentes
            self.db.cursor.execute("""
                SELECT table_name 
                FROM information_schema.tables 
                WHERE table_schema = 'public' 
                ORDER BY table_name
            """)
            
            tables = [row['table_name'] for row in self.db.cursor.fetchall()]
            
            if not tables:
                logger.warning("No se encontraron tablas en la base de datos")
                return False
            
            logger.info(f"Tablas encontradas: {', '.join(tables)}")
            
            # Contar registros por tabla
            for table in tables:
                try:
                    self.db.cursor.execute(f"SELECT COUNT(*) as count FROM {table}")
                    count = self.db.cursor.fetchone()['count']
                    logger.info(f"Tabla {table}: {count} registros")
                except Exception as e:
                    logger.warning(f"No se pudo contar registros en {table}: {e}")
            
            # Verificar sensores activos
            try:
                self.db.cursor.execute("SELECT COUNT(*) as count FROM sensors WHERE status = 'active'")
                active_sensors = self.db.cursor.fetchone()['count']
                logger.info(f"Sensores activos: {active_sensors}")
            except Exception as e:
                logger.warning(f"No se pudo verificar sensores activos: {e}")
            
            logger.info("Verificación de estado completada")
            return True
            
        except Exception as e:
            logger.error(f"Error verificando estado: {e}")
            return False


class DatabaseOperation(ABC):    
    def __init__(self, db_manager):
        self.db_manager = db_manager
    
    @abstractmethod
    def execute(self) -> bool:
        pass

class CreateOperation(DatabaseOperation):    
    def execute(self) -> bool:
        logger.info("Ejecutando operación CREATE...")
        
        if not self.db_manager.schema_manager.create_all_tables():
            return False
        # Asegurar columnas core si las tablas ya existían
        if not self.db_manager.schema_manager.ensure_core_columns():
            logger.warning("Algunas columnas core no pudieron asegurarse")
        
        # Corregir secuencias de auto-incremento
        if not self.db_manager.schema_manager.fix_sequences():
            logger.warning("Error corrigiendo secuencias, pero continuando...")
        
        if not self.db_manager.data_manager.insert_sample_data():
            logger.warning("Error insertando datos de ejemplo, pero esquema creado")
        
        logger.info("Operación CREATE completada")
        return True

class DropOperation(DatabaseOperation):    
    def execute(self) -> bool:
        logger.info("Ejecutando operación DROP...")
        
        if not self.db_manager.schema_manager.drop_all_tables():
            return False
        
        logger.info("Operación DROP completada")
        return True

class StatusOperation(DatabaseOperation):    
    def execute(self) -> bool:
        logger.info("Ejecutando operación STATUS...")
        
        if not self.db_manager.status_checker.check_database_status():
            return False
        
        logger.info("Operación STATUS completada")
        return True

class TestOperation(DatabaseOperation):    
    def execute(self) -> bool:
        logger.info("Ejecutando operación TEST...")
        
        if not self.db_manager.db_connection.connect():
            return False
        
        logger.info("Operación TEST completada - Conexión exitosa")
        return True



class DatabaseManager:    
    def __init__(self):
        self.db_connection = DatabaseConnection()
        self.schema_manager = SchemaManager(self.db_connection)
        self.data_manager = DataManager(self.db_connection)
        self.status_checker = StatusChecker(self.db_connection)
    
    def execute_operations(self, operations: List[str]) -> bool:
        try:
            # Conectar a la base de datos
            if not self.db_connection.connect():
                return False
            
            # Mapear operaciones a clases
            operation_map = {
                'create': CreateOperation,
                'drop': DropOperation,
                'status': StatusOperation,
                'test': TestOperation
            }
            
            # Ejecutar operaciones
            for operation_name in operations:
                if operation_name not in operation_map:
                    logger.error(f"Operación desconocida: {operation_name}")
                    return False
                
                operation_class = operation_map[operation_name]
                operation = operation_class(self)
                
                if not operation.execute():
                    logger.error(f"Error ejecutando operación: {operation_name}")
                    return False
            
            return True
            
        except Exception as e:
            logger.error(f"Error ejecutando operaciones: {e}")
            return False
        
        finally:
            self.db_connection.disconnect()

def main():
    parser = argparse.ArgumentParser(description='Gestor de Base de Datos TFM')
    parser.add_argument('operations', nargs='+', 
                       choices=['create', 'drop', 'status', 'test'],
                       help='Operaciones a ejecutar')
    
    args = parser.parse_args()
    
    db_manager = DatabaseManager()
    
    if db_manager.execute_operations(args.operations):
        logger.info("Todas las operaciones completadas exitosamente")
        sys.exit(0)
    else:
        logger.error("Error ejecutando operaciones")
        sys.exit(1)

if __name__ == '__main__':
    main()