¡Genial! Aquí tienes un esquema general y organizado de **todos los endpoints REST** que deberías tener por cada módulo de tu backend FastAPI. Este esquema es ideal como guía para implementarlos uno a uno, modularmente.

---

## 🔐 AUTH

| Método | Endpoint         | Descripción                      |
| ------ | ---------------- | -------------------------------- |
| POST   | `/auth/register` | Registro de nuevo usuario        |
| POST   | `/auth/login`    | Login con email y password       |
| GET    | `/auth/me`       | Obtener perfil autenticado (JWT) |

---

## 👤 USERS

| Método | Endpoint      | Descripción                       |
| ------ | ------------- | --------------------------------- |
| GET    | `/users/`     | Listar todos los usuarios (admin) |
| GET    | `/users/{id}` | Obtener un usuario por ID         |
| PUT    | `/users/{id}` | Actualizar datos de usuario       |
| DELETE | `/users/{id}` | Eliminar un usuario               |

---

## 🛒 PRODUCTS

| Método | Endpoint                  | Descripción                            |
| ------ | ------------------------- | -------------------------------------- |
| GET    | `/products/`              | Listar todos los productos activos     |
| GET    | `/products/{id}`          | Obtener un producto específico         |
| GET    | `/products/by_owner/{id}` | Productos publicados por un usuario    |
| POST   | `/products/`              | Crear nuevo producto (farmer/retailer) |
| PUT    | `/products/{id}`          | Actualizar producto                    |
| DELETE | `/products/{id}`          | Eliminar producto                      |

---

## 🧺 SHOPPING LISTS

| Método | Endpoint                        | Descripción                              |
| ------ | ------------------------------- | ---------------------------------------- |
| POST   | `/shopping/lists/`              | Crear lista de compra                    |
| GET    | `/shopping/lists/{user_id}`     | Ver lista de un usuario                  |
| POST   | `/shopping/lists/{list_id}/add` | Añadir producto a lista                  |
| PUT    | `/shopping/items/{item_id}`     | Cambiar cantidad o estado (bought, etc.) |
| DELETE | `/shopping/items/{item_id}`     | Eliminar producto de lista               |

---

## 📦 TRANSACTIONS (PEDIDOS)

| Método | Endpoint                     | Descripción                                |
| ------ | ---------------------------- | ------------------------------------------ |
| POST   | `/transactions/`             | Crear una transacción/pedido nuevo         |
| GET    | `/transactions/{id}`         | Ver pedido por ID                          |
| GET    | `/transactions/by_user/{id}` | Ver pedidos de un usuario                  |
| PUT    | `/transactions/{id}/status`  | Cambiar estado (`paid`, `delivered`, etc.) |

---

## 🍽 RECIPES

| Método | Endpoint                | Descripción               |
| ------ | ----------------------- | ------------------------- |
| POST   | `/recipes/`             | Crear nueva receta        |
| GET    | `/recipes/`             | Listar todas las recetas  |
| GET    | `/recipes/{id}`         | Ver receta por ID         |
| GET    | `/recipes/by_user/{id}` | Ver recetas de un usuario |
| PUT    | `/recipes/{id}`         | Actualizar receta         |
| DELETE | `/recipes/{id}`         | Eliminar receta           |

---

## 📅 WEEKLY PLANS

| Método | Endpoint              | Descripción                                |
| ------ | --------------------- | ------------------------------------------ |
| POST   | `/planning/`          | Crear planificación semanal                |
| GET    | `/planning/{user_id}` | Ver planificación por usuario              |
| PUT    | `/planning/item/{id}` | Actualizar una celda (receta, día, comida) |
| DELETE | `/planning/item/{id}` | Eliminar receta de un día                  |

---

## 📲 QR CODES & TRACEABILITY

| Método | Endpoint                    | Descripción                     |
| ------ | --------------------------- | ------------------------------- |
| POST   | `/qrs/`                     | Generar QR y hash para producto |
| GET    | `/qrs/{qr_code}`            | Ver datos de trazabilidad       |
| GET    | `/qrs/product/{product_id}` | QR por producto                 |

---

## 🌡 SENSORS

| Método | Endpoint                           | Descripción                      |
| ------ | ---------------------------------- | -------------------------------- |
| POST   | `/sensors/`                        | Insertar nueva lectura de sensor |
| GET    | `/sensors/by_product/{product_id}` | Ver lecturas por producto        |
| GET    | `/sensors/`                        | Ver todas las lecturas           |

---

## ⛓ BLOCKCHAIN LOGS

| Método | Endpoint                          | Descripción             |
| ------ | --------------------------------- | ----------------------- |
| GET    | `/blockchain/`                    | Ver todos los logs      |
| GET    | `/blockchain/by_transaction/{id}` | Ver log por transacción |

---

## 🌍 IMPACT METRICS

| Método | Endpoint                 | Descripción                    |
| ------ | ------------------------ | ------------------------------ |
| GET    | `/impact/user/{user_id}` | Métricas de impacto personal   |
| GET    | `/impact/`               | Ver todas las métricas (admin) |
| PUT    | `/impact/{id}`           | Actualizar métricas            |

---

## 🚚 LOGISTICS ROUTES

| Método | Endpoint                      | Descripción                |
| ------ | ----------------------------- | -------------------------- |
| POST   | `/logistics/`                 | Crear nueva ruta           |
| GET    | `/logistics/`                 | Ver todas las rutas        |
| GET    | `/logistics/{route_id}`       | Ver detalles de una ruta   |
| GET    | `/logistics/by_driver/{name}` | Ver rutas de un repartidor |

---

## 🤖 RECOMMENDATIONS

| Método | Endpoint                          | Descripción                           |
| ------ | --------------------------------- | ------------------------------------- |
| GET    | `/recommendations/user/{user_id}` | Ver recomendaciones personalizadas    |
| POST   | `/recommendations/context/`       | Enviar contexto y recibir sugerencias |
| GET    | `/recommendations/`               | Ver todas (admin o debug)             |

---

¿Quieres que ahora elijamos uno y lo implementemos por completo (modelo, schema, router, lógica)? ¿O prefieres que te cree plantillas base de todos con funciones stub?
