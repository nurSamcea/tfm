# Base de Datos - Plataforma de Gestión Alimentaria

## 📋 Descripción

Configuración simple y coherente de la base de datos PostgreSQL para la plataforma.

## 🏗️ Estructura

```
database/
├── README.md              # Este archivo
├── railway_init.py       # Script principal para gestionar la BD
├── schema.sql            # Esquema de la base de datos
├── sample_data.sql       # Datos de ejemplo
```

## 🚀 Uso Rápido

### Para Railway (Recomendado)
```bash
cd database

# Acciones individuales
python railway_init.py create    # Crear tablas
python railway_init.py delete    # Eliminar tablas
python railway_init.py update    # Insertar datos de ejemplo
python railway_init.py status    # Verificar estado

# Acciones múltiples
python railway_init.py delete create update  # Reiniciar completamente
python railway_init.py create update         # Crear tablas y datos
python railway_init.py status update         # Verificar y actualizar datos
```

### Para Desarrollo Local
```bash
# 1. Configurar variables de entorno
export DATABASE_URL="postgresql://usuario:password@localhost:5432/tfm_db"

# 2. Gestionar base de datos
cd database
python railway_init.py create update  # Crear tablas y datos
python railway_init.py status         # Verificar estado
```

## 🔧 Configuración Detallada

### Requisitos
- PostgreSQL 12+
- Python 3.8+
- psycopg2

### Instalar dependencias
```bash
pip install -r requirements.txt
```

### Crear base de datos local
```sql
CREATE DATABASE tfm_db;
```

## 📊 Tablas

El esquema incluye todas las tablas necesarias para la plataforma:
- `users` - Usuarios del sistema
- `products` - Productos alimentarios
- `shopping_lists` - Listas de compra
- `shopping_list_groups` - Agrupación por proveedor
- `shopping_list_items` - Items de las listas
- `transactions` - Transacciones
- `sensor_readings` - Lecturas IoT
- `qrs` - Códigos QR
- `impact_metrics` - Métricas de impacto
- `blockchain_logs` - Logs de blockchain

## 🔄 Migraciones

Para cambios en el esquema, usar Alembic desde el backend:

```bash
cd backend
alembic revision --autogenerate -m "Descripción del cambio"
alembic upgrade head
```

## 🛠️ Troubleshooting

### Error de conexión
- Verificar `DATABASE_URL`
- Comprobar que PostgreSQL esté ejecutándose

### Error de permisos
- Verificar que el usuario tenga permisos de CREATE, INSERT, etc.

### Error de tablas
- Ejecutar `python railway_init.py create` para recrear las tablas

