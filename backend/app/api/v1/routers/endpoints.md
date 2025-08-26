# Referencia Completa de Endpoints v1

## Autenticación (`/auth`)
- **POST** `/auth/login` - Login de usuario
- **POST** `/auth/register` - Registro de usuario

## Usuarios (`/users`)
- **GET** `/users/` - Listar todos los usuarios
- **POST** `/users/` - Crear usuario
- **GET** `/users/{user_id}` - Obtener usuario por ID
- **PUT** `/users/{user_id}` - Actualizar usuario
- **DELETE** `/users/{user_id}` - Eliminar usuario

## Productos (`/products`)
- **POST** `/products/` - Crear producto
- **GET** `/products/` - Listar productos (con filtros opcionales)
- **GET** `/products/{product_id}` - Obtener producto por ID
- **PUT** `/products/{product_id}` - Actualizar producto
- **DELETE** `/products/{product_id}` - Eliminar producto
- **POST** `/products/optimized/` - Obtener productos optimizados

## Listas de Compra (`/shopping-lists`)
- **GET** `/shopping-lists/` - Listar todas las listas
- **POST** `/shopping-lists/` - Crear lista de compra
- **GET** `/shopping-lists/{list_id}` - Obtener lista por ID
- **PUT** `/shopping-lists/{list_id}` - Actualizar lista
- **DELETE** `/shopping-lists/{list_id}` - Eliminar lista
- **POST** `/shopping-lists/optimize/` - Optimizar cesta de compra

## Grupos de Lista (`/shopping-list-groups`)
- **GET** `/shopping-list-groups/` - Listar todos los grupos
- **POST** `/shopping-list-groups/` - Crear grupo
- **GET** `/shopping-list-groups/{group_id}` - Obtener grupo por ID
- **PUT** `/shopping-list-groups/{group_id}` - Actualizar grupo
- **DELETE** `/shopping-list-groups/{group_id}` - Eliminar grupo

## Ítems de Lista (`/shopping-list-items`)
- **GET** `/shopping-list-items/` - Listar todos los ítems
- **POST** `/shopping-list-items/` - Crear ítem
- **GET** `/shopping-list-items/{item_id}` - Obtener ítem por ID
- **PUT** `/shopping-list-items/{item_id}` - Actualizar ítem
- **DELETE** `/shopping-list-items/{item_id}` - Eliminar ítem

## Transacciones (`/transactions`)
- **GET** `/transactions/` - Listar transacciones
- **POST** `/transactions/` - Crear transacción (calcula impacto automáticamente)

## Lecturas de Sensores (`/sensor_readings`)
- **POST** `/sensor_readings/` - Crear lectura de sensor
- **GET** `/sensor_readings/` - Listar lecturas (con filtros por product_id, date_from, date_to)

## Códigos QR (`/qrs`)
- **POST** `/qrs/` - Crear/adjuntar QR
- **GET** `/qrs/` - Listar QR (con filtro opcional por product_id)
- **GET** `/qrs/{qr_id}` - Obtener QR por ID

## Blockchain (`/blockchain`)
- **POST** `/blockchain/products/{product_id}/register` - Registrar producto en blockchain
- **POST** `/blockchain/transactions/{transaction_id}/register` - Registrar transacción en blockchain
- **GET** `/blockchain/products/{product_id}/verify` - Verificar autenticidad de producto
- **GET** `/blockchain/products/{product_id}/history` - Obtener historial de producto
- **GET** `/blockchain/logs` - Listar logs de blockchain (con filtros opcionales)

## Métricas de Impacto (`/impact_metrics`)
- **GET** `/impact_metrics/` - Listar métricas de impacto
- **POST** `/impact_metrics/` - Crear métrica de impacto

## Endpoint Raíz
- **GET** `/` - Mensaje de bienvenida

## Notas de Uso
- **Autenticación**: La mayoría de endpoints requieren token JWT (excepto login/register)
- **Filtros**: Varios endpoints soportan filtros por query parameters
- **Optimización**: Los endpoints de optimización usan algoritmos de impacto y distancia
- **Blockchain**: Endpoints para trazabilidad y verificación de productos
- **Documentación**: Swagger UI disponible en `/docs` cuando el servidor está ejecutándose

