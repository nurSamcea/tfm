
## ✅ CHECKLIST GENERAL POR ÁREAS

### 1. **Infraestructura y despliegue**

* [ ] Crear repositorio limpio (API + frontend + docs)
* [ ] Entorno `.env` + Railway deploy
* [ ] CI/CD con GitHub Actions
* [ ] Docker (opcional)
* [ ] Base de datos (PostgreSQL)

---

### 2. **Backend (FastAPI)**

* [ ] Modelado de datos (productos, recetas, usuarios, pedidos, sensores, trazabilidad)
* [ ] Endpoints básicos:

  * [ ] Inventario (`GET /inventory`, `POST /inventory/add`)
  * [ ] Recetas (`GET /recipes`, `POST /recipes/favorite`)
  * [ ] Lista compra (`POST /shopping-list/from-inventory`)
  * [ ] Impacto (`GET /user/stats`)
  * [ ] Rutas y logística (`POST /routes/optimize`)
* [ ] Lógica avanzada:

  * [ ] Algoritmo de sugerencias
  * [ ] Optimización de rutas (mock)
  * [ ] Cálculo de impacto
* [ ] Seguridad y autenticación JWT
* [ ] Logs de blockchain simulados (`product_hash`, `event`, `timestamp`)
* [ ] Endpoint de recepción de sensores (`POST /sensor-data`)

---

### 3. **APP móvil (Android - Java)**

* [ ] Configurar Retrofit + estructura base (MVVM)
* [ ] Crear models y repositories por módulo:

  * [ ] Inventario
  * [ ] Recetas
  * [ ] Lista de compra
  * [ ] Perfil
* [ ] Pantallas:

  * [ ] Inventario (checklist, escáner)
  * [ ] Recetas sugeridas
  * [ ] Lista compra inteligente
  * [ ] Compra mágica (carrito)
  * [ ] Perfil del usuario
* [ ] Funcionalidades inteligentes:

  * [ ] Sustitutos alimentarios
  * [ ] Recomendaciones personalizadas
  * [ ] Generador automático de menú semanal
  * [ ] Impacto visual (CO₂, dinero local, ahorro)

---

### 4. **Panel web (roles)**

* [ ] Agricultor:

  * [ ] Subida de productos
  * [ ] QR y trazabilidad
  * [ ] Gestión de pedidos
* [ ] Supermercado:

  * [ ] Visualizar stock
  * [ ] Hacer pedidos a agricultores
  * [ ] Predicción de demanda
* [ ] Restaurante:

  * [ ] Subir recetas propias
  * [ ] Pedidos frescos
  * [ ] Participación en economía circular

---

### 5. **IoT y sensores**

* [ ] Configurar ESP32 con:

  * [ ] Sensor de temperatura (DHT22)
  * [ ] Sensor de peso (Load Cell + HX711)
* [ ] Backend recibe lecturas y almacena
* [ ] Visualización + alertas si hay cambios
* [ ] Reposición automática (mock mínimo)

---

### 6. **Landing web y demo**

* [ ] Página de presentación (React + Tailwind o Webflow)
* [ ] Explicación del proyecto
* [ ] Formulario de interés / registro
* [ ] Demo pública enlazada al backend

---
