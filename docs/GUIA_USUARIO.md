# Guía de Usuario (paso a paso)

Esta guía te lleva de cero a funcionando con Backend, App Android, Simulador IoT y (opcional) Blockchain. No necesitas saber programar: sigue los pasos tal cual.

---

## 1) Requisitos
- Windows 10/11
- Python 3.11 (no 3.13)
- Git (opcional)
- PostgreSQL 13+ (puede ser local)
- Android Studio (para la app)
- Docker Desktop (opcional)

---

## 2) Backend (API) en 10 minutos

### 2.1. Crear entorno e instalar dependencias
1. Abre PowerShell en la carpeta del proyecto (raíz del repo)
2. Crea y activa un entorno con Python 3.11:
```powershell
python -m venv venv
./venv/Scripts/activate
```
3. Instala dependencias:
```powershell
pip install -r backend/requirements.txt
```


### 2.2. Configurar la base de datos
Crea una BD PostgreSQL (por ejemplo `zero_db`) y un usuario (por ejemplo `zero_user` / `zero_pass`).

Variable de conexión (ejemplo):
```
DATABASE_URL=postgresql://zero_user:zero_pass@localhost:5432/zero_db
```
Colócala como variable de entorno o en `.env` si lo usas.

### 2.3. Migrar el esquema (Alembic)
Desde la raíz del proyecto (con el venv activo):
```powershell
alembi upgrade head
```

### 2.4. Arrancar el servidor
```powershell
uvicorn backend.app.main:app --reload --host 0.0.0.0 --port 8000
```
- API disponible en: `http://127.0.0.1:8000`
- Documentación interactiva: `http://127.0.0.1:8000/docs`

### 2.5. Opcional: Docker
- Abre Docker Desktop
- En la raíz del proyecto:
```powershell
docker-compose up -d
```
Si ves el error de conexión a Docker Desktop, ábrelo y reintenta.

---

## 3) Autenticación básica (crear usuario y login)
Puedes hacerlo desde Swagger (`/docs`) o vía comandos.

- Crear usuario (register):
```bash
POST /auth/register
{
  "name": "Demo",
  "email": "demo@example.com",
  "password": "demo1234",
  "role": "consumer"
}
```
- Login:
```bash
POST /auth/login
{
  "email": "demo@example.com",
  "password": "demo1234"
}
```
- Copia `access_token` para llamadas protegidas (Authorization: Bearer <token>).

---

## 4) App Android (instalar y probar)
1. Abre Android Studio > Open > selecciona la carpeta `frontend`.
2. Sincroniza Gradle (se descargan dependencias).
3. Compila y ejecuta en emulador o dispositivo.
   - Emulador usa `http://10.0.2.2:8000` como base (ya configurado).
   - Dispositivo físico: cambia la base a tu IP (en `ApiClient` si fuese necesario).
4. Asegúrate de permitir cámara (para escanear QR).

### Funcionalidades principales en la app
- Ver productos, añadir al carrito, ajustar cantidades.
- Pantalla de compra (checkout) con total y confirmación.
- Escáner QR (ver trazabilidad del producto).
- Estilo moderno (paleta verde-naranja) ya aplicado.

---

## 5) Simulador IoT (sin sensores reales)
Para simular lecturas de temperatura/humedad hacia el backend:

1. Activa el entorno (`./venv/Scripts/activate`)
2. Ejecuta el simulador:
```powershell
python iot/iot_simulator.py --backend http://127.0.0.1:8000 --product 1 --device ESP32_SIM_001 --interval 10
```
- Con token JWT (si el endpoint lo requiere):
```powershell
python iot/iot_simulator.py --backend http://127.0.0.1:8000 --product 1 --token "<TU_TOKEN>"
```
- Solo imprimir sin enviar:
```powershell
python iot/iot_simulator.py --print-only
```

Los datos llegan a: `POST /sensor_readings/` y los verás en la BD y en `/sensor_readings/` (Swagger).

---

## 6) Flujo de compra y stock (cómo probar)
1. Crea productos desde `/products/` (o usa los existentes si hay datos).
2. En la app: añade productos al carrito.
3. En `Checkout`: confirma la compra.
4. El backend:
   - Valida stock.
   - Crea la transacción.
   - Descuenta stock de cada producto.
   - Calcula y guarda métricas de impacto.

Puedes verificar en:
- `GET /transactions/` (transacciones creadas)
- `GET /products/{id}` (stock actualizado)
- `GET /impact_metrics/` (métricas registradas)

---

## 7) QR y trazabilidad
- La app puede escanear QR.
- Backend expone:
  - `GET /traceability/product/{qr_hash}` → resumen completo (producto, lecturas, blockchain, transacciones, etc.)
  - `GET /traceability/product/{product_id}/history` → historial detallado

Si no tienes QR reales, puedes simular usando hashes/entradas en `QR` con un `product_id` válido.

---

## 8) Blockchain (opcional)
- Contrato: `backend/app/contracts/ProductTraceability.sol`
- Integración: `algorithms/blockchain_manager.py`
- Endpoints útiles:
  - `POST /blockchain/products/{product_id}/register`
  - `GET  /blockchain/products/{product_id}/verify`
  - `GET  /blockchain/products/{product_id}/history`

Para usar red local (Ganache):
- Levanta Ganache en `http://localhost:8545`
- Ajusta `BLOCKCHAIN_URL` en configuración.

---

## 9) Problemas típicos y soluciones rápidas
- Alembic no se reconoce:
  - Usa: `python -m alembic -c alembic.ini upgrade head` (desde raíz)
- Error compilación de `ckzg`/`cl.exe` en Windows:
  - Usa Python 3.11 (no 3.13)
- Docker: error de tubería de Windows:
  - Abre Docker Desktop y reintenta `docker-compose up -d`
- Android: recursos de color/tema no encontrados:
  - Ya se añadieron colores compatibles; sincroniza Gradle y reconstruye.

---

## 10) Comandos rápidos (chuleta)
Backend:
```powershell
# Activar entorno
y ./venv/Scripts/activate

# Instalar dependencias
pip install -r backend/requirements.txt

# Migrar BD
python -m alembic -c alembic.ini upgrade head

# Arrancar API
uvicorn backend.app.main:app --reload --host 0.0.0.0 --port 8000
```

IoT simulado:
```powershell
python iot/iot_simulator.py --backend http://127.0.0.1:8000 --product 1 --device ESP32_SIM_001 --interval 10
```

---

## 11) ¿Qué puedo hacer ya?
- Crear usuarios, iniciar sesión (JWT)
- Crear/listar productos, optimizar compra
- Hacer compras desde la app y ver stock actualizarse
- Enviar lecturas de sensores simuladas al backend
- Escanear QR en la app y ver trazabilidad
- Registrar/verificar en blockchain (si lo configuras)

---

## 12) Dónde está todo
- API y modelos: `backend/app/...`
- Documentación API: `http://127.0.0.1:8000/docs`
- App Android: `frontend/app/`
- Simulador IoT: `iot/iot_simulator.py`
- Contratos/Blockchain: `backend/app/contracts/`
- Documentación extendida: `docs/` (arquitectura, endpoints, algoritmos, etc.)

---

¿Dudas? Empieza por `/docs` del backend y la pantalla principal de la app. Luego prueba el simulador IoT. Si algo falla, revisa la sección de problemas típicos arriba.
