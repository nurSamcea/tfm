# Arquitectura del Sistema

## Visión general
Plataforma modular para distribución sostenible de productos frescos con tres dominios principales:
- Backend API (FastAPI + SQLAlchemy + Pydantic)
- Frontend Android (Java)
- IoT (ESP32, Raspberry Pi)
- Soporte de trazabilidad en Blockchain (Web3 + contrato Solidity)

## Componentes
- `backend/app/main.py`: instancia FastAPI y registro de routers v1.
- `backend/app/api/v1/routers/`: módulos de endpoints (auth, usuarios, productos, listas de compra, transacciones, sensores, qrs, blockchain_logs, métricas de impacto).
- `backend/app/models/`: modelos SQLAlchemy (user, product, shopping_list, shopping_list_item, shopping_list_group, transaction, sensor_reading, qr, blockchain_log, impact_metric).
- `backend/app/schemas/`: esquemas Pydantic alineados con los modelos.
- `backend/app/algorithms/`: lógica de negocio (optimizador de cesta, cálculo de impacto, gestor blockchain).
- `backend/app/contracts/`: contrato `ProductTraceability.sol` y ABI `ProductTraceability.json`.
- `database/`: scripts SQL, datos de ejemplo y documentación BD.
- `frontend/`: app Android (Java) por roles (consumer, farmer, supermarket).
- `iot/`: firmwares (ESP32) y scripts (Raspberry) para sensores.

## Endpoints disponibles
- **Autenticación**: `/auth/login`, `/auth/register`
- **Usuarios**: CRUD completo (`/users`)
- **Productos**: CRUD + optimización (`/products`)
- **Listas de compra**: CRUD completo + optimización (`/shopping-lists`, `/shopping-list-groups`, `/shopping-list-items`)
- **Transacciones**: CRUD con cálculo automático de impacto (`/transactions`)
- **Sensores**: Lecturas con filtros temporales (`/sensor_readings`)
- **QR**: Trazabilidad (`/qrs`)
- **Blockchain**: Registro, verificación e historial (`/blockchain`)
- **Métricas**: Impacto ambiental y social (`/impact_metrics`)

## Integraciones clave
- IoT → Backend: lecturas registradas como `sensor_reading` mediante endpoints.
- Backend → Blockchain: operaciones a través de `algorithms/blockchain_manager.py` usando `BLOCKCHAIN_URL` y `CONTRACT_ADDRESS`.
- Frontend → Backend: consumo de endpoints REST para catálogo, compras, métricas, QR y trazabilidad.

## Despliegue local
- Python 3.11, PostgreSQL, variables en `backend/app/core/config.py` (vía `.env`).
- Arranque local del backend: `uvicorn backend.app.main:app --reload`.
- Docker opcional vía `docker-compose.yml`.

## Seguridad
- Gestión de secretos por entorno (`SECRET_KEY`, claves blockchain).
- Autenticación JWT implementada con endpoints de login/registro.
