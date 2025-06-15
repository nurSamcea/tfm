# Guía de Implementación

## 1. Configuración del Entorno

### 1.1 Requisitos Previos
- Python 3.8+
- Node.js 14+ (para desarrollo de contratos)
- Ganache o red Ethereum local
- MetaMask (opcional, para pruebas)

### 1.2 Instalación de Dependencias
```bash
# Backend
cd backend
pip install -r requirements.txt

# Contratos
cd contracts
npm install -g truffle
npm install
```

## 2. Configuración de Blockchain

### 2.1 Red Local
1. Instalar Ganache:
```bash
npm install -g ganache
```

2. Iniciar red local:
```bash
ganache --port 8545
```

### 2.2 Despliegue del Contrato
1. Compilar contrato:
```bash
cd backend/app/contracts
truffle compile
```

2. Desplegar contrato:
```bash
truffle migrate --network development
```

3. Copiar dirección del contrato desplegado al archivo `.env`:
```
CONTRACT_ADDRESS=0x...  # Dirección del contrato desplegado
```

## 3. Uso del Sistema

### 3.1 Registro de Productos
```python
from app.algorithms.blockchain_manager import BlockchainManager

# Inicializar gestor
blockchain = BlockchainManager(blockchain_url="http://localhost:8545")

# Datos del producto
product_data = {
    "id": 1,
    "name": "Producto Eco",
    "category": "Alimentación",
    "is_eco": True,
    "provider_lat": 40.4168,
    "provider_lon": -3.7038
}

# Registrar producto
result = blockchain.create_product_record(
    product_data=product_data,
    provider_id=1,
    private_key="tu_clave_privada"
)
```

### 3.2 Registro de Transacciones
```python
# Datos de la transacción
transaction_data = {
    "id": 1,
    "items": [{
        "product_id": 1,
        "quantity": 2,
        "price": 19.99
    }]
}

# Registrar transacción
result = blockchain.create_transaction_record(
    transaction_data=transaction_data,
    user_id=1,
    private_key="tu_clave_privada"
)
```

### 3.3 Verificación de Productos
```python
# Verificar autenticidad
is_authentic, details = blockchain.verify_product_authenticity(
    product_id=1,
    provider_id=1
)

# Obtener historial
history = blockchain.get_product_history(product_id=1)
```

## 4. Integración con Métricas

### 4.1 Registro Automático
```python
from app.algorithms.impact_metrics import ImpactMetrics
from app.algorithms.blockchain_manager import BlockchainManager

# Inicializar gestores
metrics = ImpactMetrics()
blockchain = BlockchainManager(blockchain_url="http://localhost:8545")

# Calcular impacto
impact = metrics.calculate_impact(product_data)

# Registrar en blockchain
blockchain.create_product_record(
    product_data={**product_data, "impact": impact},
    provider_id=1,
    private_key="tu_clave_privada"
)
```

### 4.2 Verificación de Sostenibilidad
```python
# Verificar producto y métricas
is_authentic, details = blockchain.verify_product_authenticity(
    product_id=1,
    provider_id=1
)

if is_authentic:
    impact = metrics.get_product_impact(product_id=1)
    sustainability_score = metrics.calculate_sustainability_score(impact)
```

## 5. Consideraciones de Seguridad

### 5.1 Gestión de Claves
- Nunca almacenar claves privadas en código
- Usar variables de entorno o gestores de secretos
- Implementar rotación de claves

### 5.2 Validación de Datos
- Verificar formato de datos antes de enviar a blockchain
- Implementar límites y restricciones
- Validar permisos de usuario

### 5.3 Manejo de Errores
```python
try:
    result = blockchain.create_product_record(...)
except Exception as e:
    logger.error(f"Error en registro de producto: {str(e)}")
    # Implementar lógica de recuperación
```

## 6. Monitoreo y Mantenimiento

### 6.1 Logs
- Configurar logging para operaciones blockchain
- Monitorear gas y costos
- Registrar eventos importantes

### 6.2 Actualizaciones
- Mantener dependencias actualizadas
- Revisar seguridad de contratos
- Implementar mejoras de rendimiento

### 6.3 Respaldo
- Mantener copias de contratos
- Documentar cambios
- Plan de recuperación 