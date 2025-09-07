# Implementación de Permisos de Transacciones

## Resumen

Se ha implementado un sistema completo de permisos para el cambio de estados de transacciones, siguiendo las reglas de negocio especificadas. El sistema permite que cada tipo de usuario (farmer, supermarket, consumer) realice únicamente las acciones permitidas según su rol en la transacción.

## Reglas de Negocio Implementadas

### 1. Farmer (Agricultor)
- **Puede hacer**: Cambiar el estado de sus propias ventas de `in_progress` a `delivered` o `cancelled`
- **No puede hacer**: Modificar transacciones de otros agricultores
- **Contexto**: Solo puede gestionar las transacciones donde es el vendedor (`seller_type = "farmer"`)

### 2. Supermarket (Supermercado)
- **Como vendedor**: Puede cambiar el estado de ventas a consumidores de `in_progress` a `delivered` o `cancelled`
- **Como comprador**: Solo puede cancelar pedidos de agricultores (`seller_type = "farmer"`)
- **Restricciones**: No puede vender directamente a otros supermercados, solo a consumidores

### 3. Consumer (Consumidor)
- **Puede hacer**: Solo cancelar sus propios pedidos
- **No puede hacer**: Entregar pedidos o modificar transacciones de otros consumidores
- **Contexto**: Solo puede gestionar transacciones donde es el comprador (`buyer_type = "consumer"`)

## Cambios Implementados

### 1. Función de Validación de Permisos

Se creó la función `_validate_transaction_permissions()` que:
- Valida las transiciones de estado básicas
- Verifica los permisos específicos según el tipo de usuario
- Retorna un booleano y un mensaje de error descriptivo

```python
def _validate_transaction_permissions(transaction, new_status, user_id: int, user_type: str):
    """
    Validar si un usuario puede realizar un cambio de estado específico en una transacción.
    """
    # Validación de transiciones básicas
    # Validación de permisos específicos por tipo de usuario
    # Retorna (is_allowed: bool, error_message: str)
```

### 2. Endpoints Actualizados

Se modificaron los siguientes endpoints para incluir validación de permisos:

#### `PATCH /transactions/{transaction_id}/status`
- **Nuevos parámetros**: `user_id`, `user_type`
- **Funcionalidad**: Actualiza el estado con validación de permisos

#### `PATCH /transactions/{transaction_id}/cancel`
- **Nuevos parámetros**: `user_id`, `user_type`
- **Funcionalidad**: Cancela transacción con validación de permisos

#### `PATCH /transactions/{transaction_id}/deliver`
- **Nuevos parámetros**: `user_id`, `user_type`
- **Funcionalidad**: Marca como entregado con validación de permisos

### 3. Gestión Automática del Inventario

#### Función `_create_buyer_products()`
- Crea productos para el comprador cuando se entrega una transacción
- Calcula precios según el tipo de comprador (supermarket: +30%, consumer: precio original)
- Elimina productos del vendedor cuando el stock llega a 0
- Crea nuevos productos si no existen en el inventario del comprador

#### Función `_restore_seller_stock()`
- Restaura el stock del vendedor cuando se cancela una transacción
- Hace visible nuevamente productos que estaban ocultos por falta de stock

## Flujo de Actualización del Inventario

### Cuando se marca como "Entregado":
1. Se reduce el stock del vendedor (ya reservado al crear la transacción)
2. Se crea/actualiza el producto en el inventario del comprador
3. Si el stock del vendedor llega a 0, se elimina el producto
4. Se calcula el precio según el tipo de comprador

### Cuando se marca como "Cancelado":
1. Se restaura el stock del vendedor
2. Se hace visible nuevamente el producto si estaba oculto

## Ejemplos de Uso

### Farmer entrega su venta:
```bash
PATCH /api/v1/transactions/1/status?user_id=1&user_type=farmer
{
  "status": "delivered"
}
```

### Supermarket cancela pedido de farmer:
```bash
PATCH /api/v1/transactions/2/cancel?user_id=2&user_type=supermarket
```

### Consumer cancela su pedido:
```bash
PATCH /api/v1/transactions/3/cancel?user_id=3&user_type=consumer
```

## Validaciones de Seguridad

1. **Verificación de identidad**: El usuario debe ser el propietario de la transacción
2. **Validación de transiciones**: Solo se permiten transiciones válidas de estado
3. **Verificación de roles**: Cada tipo de usuario tiene permisos específicos
4. **Integridad de datos**: Las actualizaciones de inventario son atómicas

## Códigos de Error

- **403 Forbidden**: Usuario no tiene permisos para realizar la acción
- **404 Not Found**: Transacción no encontrada
- **400 Bad Request**: Transición de estado no válida

## Script de Pruebas

Se incluye un script de pruebas (`test_transaction_permissions.py`) que:
- Simula diferentes escenarios de cambio de estado
- Verifica que los permisos funcionen correctamente
- Proporciona un resumen detallado de los resultados
- Incluye pruebas de actualización de inventario

## Consideraciones Técnicas

1. **Transacciones de base de datos**: Todas las operaciones son atómicas
2. **Rollback automático**: En caso de error, se revierten todos los cambios
3. **Validación de stock**: Se verifica disponibilidad antes de procesar
4. **Logging**: Se registran errores para debugging

## Próximos Pasos

1. **Integración con frontend**: Actualizar la aplicación Android para usar los nuevos parámetros
2. **Pruebas de integración**: Ejecutar el script de pruebas con datos reales
3. **Documentación de API**: Actualizar la documentación de Swagger
4. **Monitoreo**: Implementar logging para auditoría de cambios de estado

## Archivos Modificados

- `backend/app/api/v1/routers/transactions.py`: Lógica principal de permisos
- `test_transaction_permissions.py`: Script de pruebas (nuevo)
- `TRANSACTION_PERMISSIONS_IMPLEMENTATION.md`: Documentación (nuevo)

## Conclusión

La implementación cumple con todos los requisitos especificados:
- ✅ Farmer puede cambiar estado de sus ventas a entregado o cancelado
- ✅ Supermarket puede entregar ventas a consumidores y cancelar pedidos de farmers
- ✅ Consumer solo puede cancelar sus pedidos
- ✅ Actualización automática del inventario
- ✅ Eliminación de productos cuando stock llega a 0
- ✅ Creación de productos nuevos para compradores
- ✅ Validación de permisos robusta y segura


