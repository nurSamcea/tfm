# Flujo Backend ↔ Frontend

## Roles y navegación (Android)
- Consumidor: catálogo, compras, historial, perfil
- Agricultor: stock, pedidos, sensores, perfil
- Supermercado: proveedores, inventario, perfil

## Interacciones principales
1) Listado de productos
- App → GET `/products`
- Respuesta: lista de `ProductRead`

2) Crear lista de compra / ítems
- App → POST `/shopping-lists` y POST `/shopping-list-items`
- Backend persiste y devuelve recurso creado

3) Confirmar transacción
- App → POST `/transactions`
- Backend crea la transacción y puede disparar cálculo de impacto

4) Lecturas IoT
- Dispositivo → POST `/sensor-readings`
- App muestra últimas lecturas asociadas al producto

5) Trazabilidad y QR
- App → POST `/qrs` para asociar; GET para consultar
- App/Backend → `blockchain_logs` para ver historial

## Validación y seguridad
- Dependencia de usuario actual simulada en `dependencies.py` (OAuth2 esquemático).
- Endurecer autenticación en producción (JWT, permisos por rol).

## Errores y estados
- Usar códigos HTTP semánticos (201 creación, 400 validación, 404 no encontrado).
- Responses tipados con Pydantic.
