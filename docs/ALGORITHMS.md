# Algoritmos del Sistema

## 1. Sistema de Métricas de Impacto

### 1.1 Cálculo de Impacto Ambiental
- **Huella de Carbono**: Calcula las emisiones de CO2 basadas en:
  - Distancia de transporte
  - Tipo de transporte
  - Embalaje utilizado
  - Proceso de producción

- **Consumo de Agua**: Evalúa el uso de agua en:
  - Producción
  - Procesamiento
  - Limpieza

- **Gestión de Residuos**: Analiza:
  - Cantidad de residuos generados
  - Tipo de residuos
  - Método de disposición

### 1.2 Cálculo de Impacto Social
- **Condiciones Laborales**: Evalúa:
  - Salarios justos
  - Horarios de trabajo
  - Seguridad laboral
  - Beneficios sociales

- **Comunidad Local**: Considera:
  - Contribución a la economía local
  - Programas sociales
  - Desarrollo comunitario

### 1.3 Puntuación de Sostenibilidad
- Sistema de puntuación de 0 a 100
- Ponderación de factores ambientales y sociales
- Actualización en tiempo real
- Historial de puntuaciones

## 2. Sistema de Blockchain

### 2.1 Conceptos Básicos
- **¿Qué es Blockchain?**
  - Base de datos distribuida y descentralizada
  - Registros inmutables y transparentes
  - Seguridad mediante criptografía
  - Sin necesidad de intermediarios

- **¿Por qué usar Blockchain?**
  - Trazabilidad completa de productos
  - Prevención de fraudes
  - Transparencia en la cadena de suministro
  - Confianza entre participantes

### 2.2 Implementación en el Sistema

#### 2.2.1 Contrato Inteligente (ProductTraceability.sol)
- **Estructuras de Datos**:
  ```solidity
  struct Product {
      uint256 id;
      string name;
      string category;
      bool isEco;
      string location;
      address provider;
      uint256 timestamp;
  }

  struct Transaction {
      uint256 id;
      address buyer;
      uint256 productId;
      uint256 quantity;
      uint256 price;
      uint256 timestamp;
  }
  ```

- **Funcionalidades Principales**:
  - Registro de productos
  - Registro de transacciones
  - Verificación de autenticidad
  - Historial de productos

#### 2.2.2 Gestor de Blockchain (BlockchainManager)
- **Conexión con la Red**:
  ```python
  self.w3 = Web3(Web3.HTTPProvider(blockchain_url))
  self.contract = self.w3.eth.contract(
      address=self.contract_address,
      abi=self.contract_abi
  )
  ```

- **Operaciones Principales**:
  - Registro de productos
  - Registro de transacciones
  - Verificación de autenticidad
  - Consulta de historial

### 2.3 Flujo de Trabajo

1. **Registro de Producto**:
   - Proveedor registra producto en blockchain
   - Se genera hash único
   - Se almacena información completa

2. **Transacción**:
   - Comprador realiza transacción
   - Se registra en blockchain
   - Se actualiza historial

3. **Verificación**:
   - Sistema verifica autenticidad
   - Consulta historial completo
   - Valida procedencia

### 2.4 Configuración

1. **Requisitos**:
   ```bash
   web3==6.11.1
   eth-account==0.10.0
   eth-typing==4.0.0
   eth-utils==2.3.1
   ```

2. **Variables de Entorno**:
   ```
   BLOCKCHAIN_URL=http://localhost:8545
   CONTRACT_ADDRESS=0x...  # Dirección del contrato desplegado
   ```

3. **Despliegue**:
   - Compilar contrato
   - Desplegar en red Ethereum
   - Configurar dirección del contrato

## 3. Integración de Sistemas

### 3.1 Métricas + Blockchain
- Registro automático de impactos
- Trazabilidad de mejoras
- Verificación de sostenibilidad

### 3.2 API REST
- Endpoints para blockchain
- Consultas de historial
- Verificación de productos

### 3.3 Seguridad
- Firma de transacciones
- Validación de datos
- Control de acceso 