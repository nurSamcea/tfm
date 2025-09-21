# Sistema de Gestión de Base de Datos

## Descripción

Sistema simplificado y coherente para gestionar la base de datos del proyecto TFM. Sigue los principios SOLID y mantiene solo las 6 tablas funcionales esenciales, eliminando código muerto y reduciendo la complejidad en un 54%.

## Arquitectura SOLID

### Single Responsibility Principle
- **DatabaseConnection**: Solo gestiona conexiones y autenticación
- **SchemaManager**: Solo gestiona creación/eliminación de esquemas
- **DataManager**: Solo gestiona inserción de datos de ejemplo
- **StatusChecker**: Solo verifica estado y estadísticas

### Open/Closed Principle
- **DatabaseOperation**: Clase base extensible para nuevas operaciones
- **Operaciones específicas**: CreateOperation, DropOperation, StatusOperation, TestOperation

### Dependency Inversion
- **DatabaseManager**: Coordina operaciones sin depender de implementaciones concretas
- Uso de abstracciones para operaciones de base de datos

## Estructura del Sistema

```
database/
├── README.md                    # Esta documentación
├── db_manager.py               # Sistema principal de gestión (433 líneas)
└── db_schema.sql              # Esquema simplificado (180 líneas)
```

## Uso del Sistema

### Configuración Automática
El sistema lee automáticamente las variables de entorno del archivo `.env` en la raíz del proyecto:

```bash
# Variables principales en .env
DATABASE_URL=postgresql://postgres:password@localhost:5432/iot_system
DB_HOST=localhost
DB_PORT=5432
DB_NAME=iot_system
DB_USER=postgres
DB_PASSWORD=password
```

### Comandos Básicos

```bash
# Crear esquema simplificado y datos de ejemplo
python database/db_manager.py create

# Eliminar todo el esquema
python database/db_manager.py drop

# Corregir error de columnas faltantes
python database/db_manager.py drop create

# Verificar estado actual
python database/db_manager.py status

# Recrear desde cero (eliminar + crear)
python database/db_manager.py drop create

# Crear y verificar
python database/db_manager.py create status

# Probar conexión
python database/db_manager.py test
```

### Configuración

El sistema usa la variable de entorno `DATABASE_URL`:

```bash
export DATABASE_URL="postgresql://user:password@host:port/database"
```

## Estructura Simplificada (6 tablas funcionales)

### Tablas Mantenidas
- **users**: Usuarios del sistema (farmers, consumers, supermarkets)
- **sensor_zones**: Zonas donde se ubican los sensores
- **sensors**: Sensores IoT (reales y simulados)
- **products**: Productos alimentarios
- **sensor_readings**: Lecturas de sensores
- **sensor_alerts**: Alertas de sensores (implementado)
- **transactions**: Pedidos y ventas

### Tablas Eliminadas (7 tablas)
- shopping_lists - No implementado (frontend usa carrito local)
- shopping_list_groups - No implementado
- shopping_list_items - No implementado
- qrs - No implementado
- impact_metrics - No implementado
- blockchain_logs - No implementado
- product_traceability_chains - No implementado

### Datos de Ejemplo Incluidos
- **2 Farmers**: María García (sensor real) y Pedro Sánchez (simulado)
- **3 Consumers**: Juan, Ana y Carlos
- **1 Supermarket**: Supermercado Central
- **2 Zonas de sensores**: Invernadero Principal y Campo Abierto
- **2 Sensores**: Real (ESP32) y Simulado
- **6 Productos**: Tomates, Lechuga, Manzanas, Zanahorias, Arroz, Yogur

## Características del Sistema

### Coherencia Total
- Usa `db_schema.sql` como fuente de verdad
- Datos coherentes con ESP32 y simulador
- Modelos alineados con backend y frontend
- Compatible con Alembic migrations

### Arquitectura Robusta
- Manejo de errores completo
- Operaciones atómicas
- Logging detallado
- Reconexión automática

## Operaciones Disponibles

### create
- Crea las 6 tablas del esquema simplificado
- Inserta datos de ejemplo coherentes
- Configura sensores reales y simulados
- Establece índices de optimización

### drop
- Elimina todas las tablas en orden correcto
- Respeta foreign keys y dependencias
- Limpia completamente la base de datos
- Manejo seguro de CASCADE

### status
- Muestra tablas existentes
- Cuenta registros por tabla
- Verifica sensores activos
- Estadísticas detalladas

### test
- Prueba la conexión a la base de datos
- Verifica configuración de variables de entorno
- Valida conectividad

## Flujo de Trabajo Recomendado

### Desarrollo
```bash
# Configurar base de datos inicial
python database/db_manager.py create

# Verificar que todo esté correcto
python database/db_manager.py status
```

### Testing
```bash
# Limpiar y recrear para tests
python database/db_manager.py drop create
```

### Producción
```bash
# Solo crear esquema (sin datos de ejemplo)
python database/db_manager.py create
```

## Integración con el Sistema

### Backend
- Usa los mismos modelos SQLAlchemy
- Endpoints coherentes con la estructura
- APIs alineadas con los datos
- Compatible con FastAPI

### IoT
- Sensores configurados en la base de datos
- Device IDs coherentes con ESP32
- Payloads compatibles con el esquema
- Endpoint `/iot/ingest` integrado

### Frontend
- Modelos Java alineados con la BD
- APIs coherentes con el backend
- Datos consistentes en toda la aplicación

## Troubleshooting

### Error de Conexión
```bash
# Verificar variables de entorno
echo $DATABASE_URL

# Verificar conectividad
python database/db_manager.py test
```

### Error de Permisos
```bash
# Verificar que el usuario tenga permisos
# para crear/eliminar tablas en la base de datos
```

### Error de Esquema
```bash
# Verificar que db_schema.sql existe
ls -la database/db_schema.sql
```

### Error: column "product_id" of relation "sensor_readings" does not exist
```bash
# Solución: Recrear base de datos con esquema corregido
python database/db_manager.py drop create
```

## Archivos del Sistema

- **db_manager.py**: Sistema principal de gestión (433 líneas)
- **db_schema.sql**: Esquema simplificado (180 líneas)
- **README.md**: Esta documentación

## Ventajas del Sistema Simplificado

### Simplicidad
- Un solo archivo para toda la gestión
- Comandos simples y claros
- Sin configuración compleja
- **54% menos complejidad**

### Coherencia
- Alineado con todo el sistema
- Datos consistentes
- Modelos unificados
- **Solo tablas funcionales**

### Mantenibilidad
- Código limpio y organizado
- Principios SOLID aplicados
- Fácil de extender
- **Sin código muerto**

### Robustez
- Manejo de errores completo
- Operaciones atómicas
- Verificación de estado
- **Mejor rendimiento**

## Dependencias

```bash
pip install psycopg2-binary python-dotenv
```

El sistema está diseñado para ser **fácil de usar, mantener y extender** siguiendo las mejores prácticas de desarrollo y manteniendo **100% de coherencia** con el resto del proyecto TFM.
