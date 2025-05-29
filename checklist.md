
## ✅ CHECKLIST GENERAL POR ÁREAS

### 1. **Infraestructura y despliegue**

* [X] Crear repositorio limpio (API + frontend + docs)
* [ ] Entorno `.env`
* [X] Railway deploy
* [X] CI/CD con GitHub Actions
* [ ] Docker (opcional). Por ahora solo he añadido ficheros Dockerfile, pensar si es necesario y se quiere añadir. “Posponer Docker hasta integración IoT o despliegue en otro entorno”
* [X] Base de datos (PostgreSQL)

---

### 2. **Backend (FastAPI)**

* [X] Modelado de datos (productos, recetas, usuarios, pedidos, sensores, trazabilidad)
  > uvicorn backend.app.main:app --reload
* [X] Endpoints básicos:
### 🔐 AUTH (Autenticación y registro)
* [ ] `POST /auth/register` – Registro de nuevo usuario
* [ ] `POST /auth/login` – Inicio de sesión con JWT
* [ ] `GET /auth/me` – Obtener perfil autenticado
### 👤 USERS (Gestión de usuarios)
* [ ] `GET /users/` – Ver todos los usuarios (admin)
* [ ] `GET /users/{id}` – Ver usuario por ID
* [ ] `PUT /users/{id}` – Actualizar usuario
* [ ] `DELETE /users/{id}` – Eliminar usuario
* [ ] `GET /users/preferences/{id}` – Ver preferencias
* [ ] `POST /users/preferences/{id}` – Actualizar preferencias

---

### 🛒 PRODUCTS (Gestión de productos)

* [ ] `GET /products/` – Ver todos los productos
* [ ] `GET /products/{id}` – Ver un producto específico
* [ ] `GET /products/by_owner/{id}` – Productos creados por usuario
* [ ] `POST /products/` – Crear nuevo producto
* [ ] `PUT /products/{id}` – Actualizar producto
* [ ] `DELETE /products/{id}` – Eliminar producto

---

### 🧺 SHOPPING LIST (Lista de la compra)

* [ ] `POST /shopping/lists/` – Crear nueva lista
* [ ] `GET /shopping/lists/{user_id}` – Ver lista por usuario
* [ ] `POST /shopping/lists/{list_id}/add` – Añadir producto a la lista
* [ ] `PUT /shopping/items/{item_id}` – Editar cantidad/estado
* [ ] `DELETE /shopping/items/{item_id}` – Eliminar producto

---

### 📦 TRANSACTIONS (Pedidos)

* [ ] `POST /transactions/` – Crear nuevo pedido
* [ ] `GET /transactions/{id}` – Ver pedido por ID
* [ ] `GET /transactions/by_user/{id}` – Ver pedidos por usuario
* [ ] `PUT /transactions/{id}/status` – Cambiar estado del pedido

---

### 🍽 RECIPES (Recetas)

* [ ] `POST /recipes/` – Crear receta
* [ ] `GET /recipes/` – Ver todas las recetas
* [ ] `GET /recipes/{id}` – Ver receta por ID
* [ ] `GET /recipes/by_user/{id}` – Ver recetas de un usuario
* [ ] `PUT /recipes/{id}` – Actualizar receta
* [ ] `DELETE /recipes/{id}` – Eliminar receta
* [ ] `GET /recipes/suggested?from_inventory=true` – Recetas según inventario
* [ ] `POST /recipes/favorite` – Marcar como favorita
* [ ] `GET /recipes/favorites` – Ver favoritas

---

### 📅 WEEKLY PLANS (Planning semanal)

* [ ] `POST /planning/` – Crear planning semanal
* [ ] `GET /planning/{user_id}` – Ver planning por usuario
* [ ] `PUT /planning/item/{id}` – Editar receta/día
* [ ] `DELETE /planning/item/{id}` – Eliminar celda

---

### 🌍 IMPACT METRICS (Impacto)

* [ ] `GET /impact/user/{user_id}` – Ver impacto personal
* [ ] `GET /impact/` – Ver métricas globales (admin)
* [ ] `PUT /impact/{id}` – Actualizar métricas

---

### 🚚 LOGISTICS (Rutas y entregas)

* [ ] `POST /logistics/` – Crear ruta logística
* [ ] `GET /logistics/` – Ver todas las rutas
* [ ] `GET /logistics/{route_id}` – Ver ruta por ID
* [ ] `GET /logistics/by_driver/{name}` – Ver rutas por conductor
* [ ] `POST /logistics/optimize` – Optimizar rutas (mock)
* [ ] `GET /logistics/summary` – Resumen de rutas simuladas

---

### 🤖 RECOMMENDATIONS (Sugerencias inteligentes)

* [ ] `GET /recommendations/user/{user_id}` – Recomendaciones para el usuario
* [ ] `POST /recommendations/context/` – Enviar contexto y recibir sugerencias
* [ ] `GET /recommendations/` – Ver todas (admin o debug)

---

### 📲 QR & TRACEABILITY (Trazabilidad con QR)

* [ ] `POST /qrs/` – Generar QR + hash
* [ ] `GET /qrs/{qr_code}` – Ver trazabilidad desde hash
* [ ] `GET /qrs/product/{product_id}` – Ver QR por producto

---

### 🌡 SENSORS (Datos de sensores IoT)

* [ ] `POST /sensors/` – Insertar nueva lectura
* [ ] `GET /sensors/` – Ver todas las lecturas
* [ ] `GET /sensors/by_product/{product_id}` – Lecturas por producto

---

### ⛓ BLOCKCHAIN LOGS (Auditoría trazable)

* [ ] `GET /blockchain/` – Ver todos los logs
* [ ] `GET /blockchain/by_transaction/{id}` – Ver log por transacción


* [ ] Lógica avanzada:

| Lógica                                            | Estado | Descripción                                          |
| ------------------------------------------------- | ------ | ---------------------------------------------------- |
| \[ ] Algoritmo de sugerencias                     | 🔄     | Recomendar productos y recetas personalizadas        |
| \[ ] Optimización de rutas (mock mínimo)          | 🔄     | Agrupar pedidos por zona y simular entregas          |
| \[ ] Cálculo de impacto                           | 🔄     | CO₂ ahorrado, compras locales, productos salvados    |
| \[ ] Generador automático de menú semanal         | ⏳      | Crear menú + lista desde preferencias e inventario   |
| \[ ] Comparador de precios por supermercado       | ⏳      | Mostrar precio óptimo por producto o grupo           |
| \[ ] Sustitución de productos por alergias/dietas | ⏳      | Reemplazo automático según restricciones del usuario |
| \[ ] Reposición automática                        | ⏳      | Detectar bajo stock y generar alerta/pedido          |
| \[ ] Planificador con arrastrar recetas           | ⏳      | Agregar recetas favoritas a calendario de comidas    |
* [ ] Seguridad y autenticación JWT
* [ ] Logs de blockchain simulados (`product_hash`, `event`, `timestamp`)
* [ ] Endpoint de recepción de sensores (`POST /sensor-data`)

---

### 3. **APP móvil (Android - Java)**
https://www.hellofresh.es/about/ingredientes-locales

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
