# Modelos de datos y base de datos

## Resumen de entidades (SQLAlchemy)
- `User` (`backend/app/models/user.py`)
  - Campos: `id`, `name`, `email`, `password_hash`, `role`, `entity_name`, `location_lat`, `location_lon`, `preferences`, `created_at`
  - Relaciones: `products`, `shopping_lists`, `impact_metrics`

- `Product` (`backend/app/models/product.py`)
  - Campos: `id`, `name`, `description`, `price`, `currency`, `unit`, `category`, `stock_available`, `expiration_date`, `is_eco`, `image_url`, `provider_id`, `certifications`, `created_at`
  - Relaciones: `provider (User)`, `sensor_readings`, `qrs`

- `ShoppingList`, `ShoppingListGroup`, `ShoppingListItem`
  - Listas de compra por usuario, agrupadas por proveedor y con ítems.
  - Relaciones encadenadas: `ShoppingList.user → ShoppingList.groups → ShoppingListItem.product`

- `Transaction`
  - Estado de transacción y vínculo con `ShoppingList`.

- `SensorReading`
  - Tipos: temperatura, humedad, gps, etc. `product_id` como FK.

- `QR`
  - Asociación de códigos QR a `product_id` con metadatos.

- `BlockchainLog`
  - Registro de eventos/tx vinculados a entidades (producto, transacción, sensor, etc.).

- `ImpactMetric`
  - Métricas de impacto por usuario (y/o producto), con `breakdown` JSON opcional.

## Relaciones clave (ER simplificado)
- `User` 1—N `ShoppingList`
- `ShoppingList` 1—N `ShoppingListGroup` 1—N `ShoppingListItem`
- `Product` 1—N `ShoppingListItem`
- `Product` 1—N `SensorReading`, `QR`, `BlockchainLog`
- `User` 1—N `ImpactMetric`

## Scripts y migraciones
- SQL: `database/database_schema.sql`, `database/drop_tables.sql`
- Datos de ejemplo: `database/fake_data.sql`, `database/fake_data.py`
- Migraciones: `alembic/` (config en `alembic.ini`, versiones en `alembic/versions/`)

## Índices y rendimiento (recomendado)
- `users(email)` (ya indexado)
- `product(category)`, `product(provider_id)`
- `shopping_list_item(shopping_list_id, product_id)`
- `transaction(created_at, buyer_id)`
- `sensor_reading(product_id, recorded_at)`
- `blockchain_log(product_id, created_at)`
