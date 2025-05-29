

### 1. **Usuarios (`users`)**

* `POST /users/`: Crear usuario
* `GET /users/{id}`: Obtener usuario por ID
* `GET /users/`: Listar todos los usuarios
* `PUT /users/{id}`: Actualizar usuario
* `DELETE /users/{id}`: Eliminar usuario
* 🔐 **Auth extra**: login y registro, usando JWT

---

### 2. **Perfiles de ingesta (`intake_profiles`)**

* `POST /intake-profiles/`: Crear perfil de ingesta
* `GET /intake-profiles/{id}`: Obtener perfil
* `GET /intake-profiles/`: Listar perfiles
* `PUT /intake-profiles/{id}`: Actualizar
* `DELETE /intake-profiles/{id}`: Eliminar

---

### 3. **Productos (`products`)**

* `POST /products/`: Crear producto
* `GET /products/{id}`: Obtener producto
* `GET /products/`: Listar todos o filtrar por categoría/proveedor
* `PUT /products/{id}`: Actualizar producto
* `DELETE /products/{id}`: Eliminar producto

---

### 4. **Listas de compra (`shopping_lists`, `shopping_list_groups`, `shopping_list_items`)**

* `POST /shopping-lists/`: Crear una lista nueva
* `GET /shopping-lists/{id}`: Ver lista completa
* `POST /shopping-list-groups/`: Crear grupo por proveedor
* `POST /shopping-list-items/`: Añadir ítem al grupo
* `GET /shopping-list-items/{id}`: Ver ítem
* 🧠 Extra: endpoint para **generar cesta óptima** o importar desde planificación semanal

---

### 5. **Recetas (`recipes`, `recipe_ingredients`)**

* `POST /recipes/`: Crear receta
* `GET /recipes/{id}`: Ver receta
* `GET /recipes/`: Buscar/filtrar recetas
* `POST /recipe-ingredients/`: Añadir ingrediente
* 🧠 Extra: endpoint para calcular nutrición total o convertir receta en lista de compra

---

### 6. **Planificación semanal (`weekly_plans`, `weekly_plan_items`)**

* `POST /weekly-plans/`: Crear planificación para la semana
* `GET /weekly-plans/{id}`: Ver
* `POST /weekly-plan-items/`: Añadir receta a un día y comida
* 🧠 Extra: validar si cumple objetivos nutricionales

---

### 7. **Transacciones (`transactions`)**

* `POST /transactions/`: Confirmar compra
* `GET /transactions/{id}`: Ver transacción
* `GET /transactions/`: Listar compras del usuario

---

### 8. **Rutas logísticas (`logistics_routes`)**

* `POST /logistics-routes/`: Registrar ruta de entrega
* `GET /logistics-routes/{id}`: Ver detalles de ruta
* 🧠 Extra: endpoint para generar rutas óptimas según compras agrupadas

---

### 9. **Sensores (`sensor_readings`)**

* `POST /sensor-readings/`: Registrar lectura de sensor
* `GET /sensor-readings/{product_id}`: Ver todas las lecturas de un producto

---

### 10. **Códigos QR (`qrs`)**

* `POST /qrs/`: Asociar QR a un producto
* `GET /qrs/{product_id}`: Obtener info del QR y trazabilidad

---

### 11. **Blockchain (`blockchain_logs`)**

* `POST /blockchain-logs/`: Registrar acción blockchain
* `GET /blockchain-logs/{entity_type}/{entity_id}`: Ver logs de una entidad

---

### 12. **Impacto ecológico/social (`impact_metrics`)**

* `POST /impact-metrics/`: Registrar métrica (automática tras transacción)
* `GET /impact-metrics/{user_id}`: Ver impacto del usuario

