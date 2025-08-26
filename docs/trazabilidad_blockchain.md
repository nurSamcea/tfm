# Trazabilidad con Blockchain

## Contrato inteligente
- Ruta: `backend/app/contracts/ProductTraceability.sol`
- ABI: `backend/app/contracts/ProductTraceability.json`

## Gestor en backend
- `backend/app/algorithms/blockchain_manager.py` conecta con Web3 usando:
```
BLOCKCHAIN_URL=http://localhost:8545
CONTRACT_ADDRESS=0x...  # Dirección del contrato desplegado
```

## Operaciones típicas
- Registro de producto
- Registro de transacción
- Verificación de autenticidad
- Consulta de historial

## Endpoints relacionados (v1)
- `blockchain_logs`: registro/consulta de eventos vinculados a entidades
- `qrs`: asociación de QR para facilitar la verificación

## Consideraciones
- No almacenar claves privadas en código; usar entorno/secret manager
- Manejo de errores y logging de transacciones
- Costes de gas y redes de prueba en desarrollo
