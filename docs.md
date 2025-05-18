# ✅ Checklist del Proyecto - Plataforma de Alimentación Inteligente

**Fecha de actualización:** 16/05/2025

---

## 🌱 FASE 1: VISIÓN ESTRATÉGICA Y PREPARACIÓN

### 🎯 Objetivo y planteamiento
#### PROPÓSITO CENTRAL DEL PROYECTO
Crear una plataforma de alimentación inteligente, eficiente y humana que:
#### 1. **Facilite la vida al consumidor**

* Le diga qué tiene en casa, qué puede cocinar y qué necesita comprar.
* Le permita comprar todo en un solo clic, sin fricción, sin comparar precios ni pensar.
* Le muestre el impacto positivo de sus elecciones: salud, sostenibilidad y ahorro.

#### 2. **Empodere a pequeños productores y comercios**

* Les dé una vía directa para llegar al consumidor final, sin intermediarios abusivos.
* Les proporcione herramientas tecnológicas para digitalizar sus ventas, gestionar stock y prever demanda.
* Les garantice pagos justos y visibilidad real frente a los grandes supermercados.

#### 3. **Optimice la logística y evite el desperdicio**

* Gracias a IA, rutas eficientes y planificación, la plataforma minimiza desplazamientos y stock sobrante.
* Detecta productos próximos a caducar en tiempo real y los redirige o rebaja automáticamente (Zero Waste).
* Organiza pedidos de múltiples fuentes como si fueran de una sola, para el consumidor es invisible.

#### 4. **Conecte tecnología con humanidad**

* Usa tecnología puntera (IoT, Blockchain, Edge, AI) al servicio del bienestar común.
* No solo es una app para comprar comida: es una red de alimentación consciente, sostenible y accesible.


#### Manifiesto
En un mundo saturado de opciones, prisas y desperdicio, creemos en volver a lo esencial:
alimentarse bien, sin esfuerzo y con sentido.

Creamos esta plataforma para que cada persona tenga el control de su cocina, su salud y su impacto sin tener que pensar demasiado.

Para que comprar no sea un laberinto, sino un gesto simple, justo y poderoso.

Para que pequeños productores y comercios tengan las mismas oportunidades que las grandes cadenas.

Para que cada receta cocinada, cada compra realizada, cada producto salvado del desperdicio…
sea parte de algo más grande.

Porque comer es un acto cotidiano, pero también una declaración de intenciones.

Y tú puedes decidir, sin esfuerzo, hacer que ese acto transforme tu mundo 🌍




#### **MVP con Traza Completa — Ciclo Real del Usuario**

#### **1. Registro de inventario**

* El usuario escanea o introduce manualmente un producto (ej. “Tomate”).
* Se almacena en el backend con:

  * Fecha de caducidad
  * Categoría
  * Cantidad
* Endpoint usado: `POST /inventory/add`

#### **2. Consulta del inventario**

* El usuario puede ver lo que tiene en casa.
* Productos con colores tipo semáforo (verde/amarillo/rojo).
* Puede marcar como usado, tirar, etc.
* Endpoint: `GET /user/inventory`

#### **3. Recetas recomendadas**

* El sistema sugiere recetas que puede hacer con lo que ya tiene.
* Si faltan ingredientes, aparecen con un botón para añadirlos a la lista.
* Endpoint: `GET /recipes/suggested?from_inventory=true`

#### **4. Lista de la compra**

* Se genera con lo que falta para las recetas seleccionadas o por productos usados.
* Puede optimizarse: “1 solo supermercado” o “mejor precio”.
* Endpoint: `POST /shopping-list/from-recipes`, `POST /cart/optimize`

#### **5. Simulación de compra**

* El usuario “compra” todo lo que necesita (simulado).
* El sistema agrupa productos por supermercado, optimiza y devuelve resumen.
* Endpoint: `GET /cart/summary`, `POST /order/confirm`

#### **6. Impacto generado**

* Una vez comprado, el usuario ve:

  * Cuánto CO₂ ha ahorrado
  * Cuánto ha apoyado al comercio local
  * Si ha aprovechado productos que iban a caducar
* Endpoint: `GET /user/stats`


- [ ] Confirmar público objetivo y necesidades reales.

#### Público Objetivo

1. Consumidor final urbano (25-45 años). Perfil: Jóvenes o adultos con vida ocupada, conscientes del medio ambiente, que valoran la alimentación saludable y el tiempo.

Necesidades reales:
- No perder tiempo pensando qué comprar o cocinar. 
- Ahorrar dinero sin renunciar a la calidad. 
- Evitar tirar comida y gestionar mejor lo que tiene en casa. 
- Comprar productos frescos y de confianza sin complicaciones. 
- Vivir de forma más sostenible sin tener que hacer un máster en ello.

2. Pequeño productor local. Perfil: Agricultores, panaderos, apicultores, etc. con buena materia prima pero poca digitalización.

Necesidades reales:
- Vender sin intermediarios y sin complicarse con tecnología. 
- Saber qué productos tienen más demanda y cuándo. 
- Aumentar sus ingresos sin pagar comisiones altas como en otras plataformas. 
- Tener una red logística que les permita entregar pedidos con eficiencia.

3. Comercios locales o supermercados colaborativos. Perfil: Tiendas físicas que quieren digitalizar parte de su venta sin crear su propia app.

Necesidades reales:
- Gestionar pedidos online sin tener que aprender a programar. 
- No perder clientes frente a Amazon o grandes cadenas. 
- Unirse a una red logística que les dé escala. 
- Controlar su stock de forma más eficiente.

4. La sociedad y el planeta

Necesidades reales:
- Menos desperdicio alimentario. 
- Menos emisiones por transporte de productos. 
- Más apoyo a la economía local.

#### ¿Por qué pagarían los usuarios?

- Consumidor: Ahorro de tiempo, dinero, estrés y comida. Porque la app le resuelve la vida en la cocina y ahorra en la compra 
- Comercio local: Visibilidad y ventas automatizadas. Porque les trae ventas sin esfuerzo, con reparto y sin comisiones altas 
- Marca/inversor: Sostenibilidad, innovación, impacto. Porque promueve economía local y sostenibilidad real

[ ] Mapa de pantallas e interacciones.

#### Mapa de navegación e interacciones simplificado

1. **Supermercado inteligente**

   * Buscar productos (por nombre, categoría, preferencia).
   * Filtros: cercanía, precio, ecológico, local.
   * Botón “Añadir al carrito”.
   * Ver carrito optimizado.
   * Finalizar compra (transparente al usuario).

2. **Inventario y lista de la compra**

   * Escáner de productos.
   * Ver lo que hay en casa (con alertas de caducidad).
   * Sugerencias automáticas para reponer.
   * Generar lista de compra con 1 clic.

3. **Recetas y planning**

   * Ver recetas por lo que hay en el inventario.
   * Añadir ingredientes faltantes a la lista.
   * Arrastrar recetas al planificador semanal (desayuno, comida, cena).
   * Ver impacto nutricional y reutilización.

4. **Perfil de usuario**

   * Preferencias (veggie, sin gluten, local...).
   * Historial de compras.
   * Objetivos (ahorro, impacto, no desperdiciar).
   * Estadísticas personales (ahorro, CO₂, compras locales...).


- [ ] Esquematizar flujo backend ↔ frontend.

### ✅ **Esquema de flujo Backend ↔ Frontend (por pantalla)**

#### 🛒 1. Supermercado inteligente

**Frontend**: Pantalla de búsqueda de productos con filtros + carrito.

**Backend endpoints:**

* `GET /products`: obtener productos filtrados.
* `GET /products/{id}`: detalle del producto.
* `POST /cart/add`: añadir producto al carrito.
* `POST /cart/optimize`: optimizar el carrito por precio/cercanía.
* `GET /cart/summary`: vista unificada del carrito.
* `POST /order/confirm`: confirmar pedido.
* `GET /order/status`: estado de la compra.

**Lógica extra:**

* Algoritmo de optimización de rutas/logística (interno).
* Control de stock y orígenes.

---

#### 📦 2. Inventario + lista de la compra

**Frontend**: Escáner + lista editable + semáforos de caducidad.

**Backend endpoints:**

* `GET /user/inventory`: obtener inventario del usuario.
* `POST /inventory/add`: añadir producto escaneado.
* `POST /inventory/update`: actualizar cantidad o fecha.
* `GET /shopping-list`: obtener lista actual.
* `POST /shopping-list/from-inventory`: autogenerar desde inventario.
* `POST /shopping-list/add`: añadir producto manualmente.
* `POST /shopping-list/remove`: eliminar producto de la lista.

**Lógica extra:**

* Algoritmo de caducidad / sugerencias.
* Alertas de productos críticos.

---

#### 🍽️ 3. Recetas y planning

**Frontend**: Vista Pinterest con botón “planear” + calendario de comidas.

**Backend endpoints:**

* `GET /recipes/suggested`: recetas con lo que hay en casa.
* `GET /recipes/favorites`: favoritas del usuario.
* `POST /planner/add`: añadir receta a un día/hora.
* `GET /planner`: consultar planning semanal.
* `POST /shopping-list/add-missing`: autocompletar desde receta.

**Lógica extra:**

* Sugerencias basadas en inventario.
* Nutrición (si se conecta con perfil).
* Generación de lista de compra desde recetas.

---

#### 👤 4. Perfil del usuario

**Frontend**: Vista con ajustes, historial, impacto y preferencias.

**Backend endpoints:**

* `GET /user/preferences`
* `POST /user/preferences/update`
* `GET /user/history`: historial de pedidos.
* `GET /user/stats`: ahorro, CO₂, comercios locales apoyados.
* `GET /user/goals`: progreso en metas.

**Lógica extra:**

* Cálculo de impacto sostenible.
* Visualización de métricas.

---

### 🔄 Flujo general de conexión

```mermaid
graph TD
UI[Frontend App (Android)] -->|HTTP requests| API[FastAPI Backend]
API --> DB[(Base de datos PostgreSQL)]
API --> Logic[Lógica avanzada (recomendaciones, rutas, etc)]
API --> Blockchain[Blockchain trazabilidad]
API --> IoT[IoT: sensores e inventario inteligente]
API --> IA[IA: predicción y optimización]
```

- [ ] Crear mocks de datos realistas (productos, rutas, recetas...).

### ✅ Crear mocks de datos realistas

#### 🛒 Productos

**Variables clave:**

* `id`, `name`, `description`, `price`, `unit`, `stock`, `expiration_date`, `origin`, `category`, `image_url`, `is_local`

**Ejemplo:**

```json
{
  "id": 101,
  "name": "Manzana Fuji",
  "description": "Manzana dulce y crujiente",
  "price": 1.99,
  "unit": "kg",
  "stock": 50,
  "expiration_date": "2025-06-01",
  "origin": "Segovia",
  "category": "Frutas",
  "image_url": "https://cdn.example.com/img/manzana.jpg",
  "is_local": true
}
```

#### 🚚 Rutas logísticas

**Variables clave:**

* `id`, `from` (productor/supermercado), `to` (usuario), `distance_km`, `estimated_time_min`, `products`, `vehicle_type`

**Ejemplo:**

```json
{
  "id": "route_001",
  "from": "Supermercado EcoMadrid",
  "to": "Usuario Nuria",
  "distance_km": 4.7,
  "estimated_time_min": 15,
  "products": ["Tomate", "Lechuga", "Pan"],
  "vehicle_type": "bici eléctrica"
}
```

#### 🍽️ Recetas

**Variables clave:**

* `id`, `name`, `description`, `ingredients`, `steps`, `image_url`, `is_vegan`, `nutrition`

**Ejemplo:**

```json
{
  "id": "recipe_12",
  "name": "Ensalada de Quinoa",
  "description": "Refrescante y rica en proteínas",
  "ingredients": [
    {"name": "quinoa", "quantity": "100g"},
    {"name": "aguacate", "quantity": "1 unidad"},
    {"name": "tomate cherry", "quantity": "150g"}
  ],
  "steps": [
    "Cocer la quinoa 15 minutos.",
    "Trocear los ingredientes.",
    "Mezclar todo con aceite y sal."
  ],
  "image_url": "https://cdn.example.com/img/ensalada_quinoa.jpg",
  "is_vegan": true,
  "nutrition": {
    "kcal": 320,
    "protein": 9,
    "fat": 14,
    "carbs": 30
  }
}
```

#### 🧍 Usuarios

**Variables clave:**

* `id`, `name`, `email`, `role` (consumer, farmer, supermarket), `location`, `preferences`

**Ejemplo:**

```json
{
  "id": 1,
  "name": "Nuria",
  "email": "nuria@example.com",
  "role": "consumer",
  "location": {"lat": 40.416775, "lon": -3.703790},
  "preferences": {
    "vegetarian": true,
    "local_only": true,
    "allergens": ["gluten"]
  }
}
```
### 📦 ¿Cómo usarlos?

* Puedes guardar estos mocks en JSON o CSV y cargarlos en tu backend como fixtures o en una base de datos SQLite para pruebas.
* También puedes usar `Faker` en Python para generar automáticamente más ejemplos variados.
* Para Android, puedes integrarlos directamente como archivos JSON en `assets/`.


## 🧠 FASE 2: BACKEND – Lógica, API y base de datos

### 📦 Modelado de datos (DB)
- [x] Tablas básicas: `users`, `products`, `transactions`, `qrs`, `blockchain_logs`
- [ ] Añadir: `recetas`, `ingredientes`, `categorías`, `historial`, `rutas`, etc.
- [ ] Soporte para unidades y formatos variados.
- [ ] Datos logísticos (ubicación, rutas, temperatura, trazabilidad...).

### ⚙️ API REST (FastAPI)
- [x] CRUDs básicos: usuarios, productos, transacciones, trazabilidad.
- [ ] Endpoints avanzados:
  - [ ] Recetas y recomendaciones
  - [ ] Lista de la compra desde inventario/recetas
  - [ ] Logística: rutas, optimización
- [ ] Autenticación y roles con JWT
- [ ] Seguridad: control de acceso, expiración, renovación de tokens
- [ ] Documentación Swagger / OpenAPI

---

## ☁️ FASE 3: INFRAESTRUCTURA Y DEVOPS

### 🚀 Despliegue y entorno
- [x] Estructura profesional de proyecto (`src/backend/...`)
- [x] `.env` + acceso a Railway
- [ ] Docker básico (opcional)
- [ ] CI/CD con GitHub Actions
- [ ] Railway desplegado (producción)

---

## 📱 FASE 4: APP DEL CONSUMIDOR (Android - Java)

### 🧩 Estructura del proyecto
- [ ] Paquetes: `ui/`, `data/`, `domain/`, `di/`, `util/`, etc.
- [ ] Configurar Retrofit, ViewModel, Repository por pantalla

### 🖼️ Pantallas principales (máximo 5)
1. **Home**
   - [ ] Botones de acceso rápido: inventario, recetas, compra, lista
2. **Inventario**
   - [ ] Lista tipo checklist + colores de caducidad
   - [ ] Escanear producto / Añadir / Ver recetas posibles
3. **Recetas**
   - [ ] Sugerencias según inventario
   - [ ] Añadir faltantes a lista de compra
   - [ ] Estilo Pinterest
4. **Lista de la compra**
   - [ ] Auto-generada desde inventario o recetas
   - [ ] Comprar ahora (opciones: 1 solo sitio o precio óptimo)
5. **Compra mágica**
   - [ ] Carrito + supermercados elegidos
   - [ ] Impacto social / ecológico
6. **Perfil (Extra)**
   - [ ] Preferencias, historial, impacto, objetivos

---

FASE 5: CONEXIÓN BACKEND + APP

¿Objetivo? Que la app móvil pueda pedir y enviar datos al backend, por ejemplo:
- Consultar recetas. 
- Ver el inventario. 
- Hacer una compra. 
- Obtener estadísticas del perfil del usuario.

✅ 1. Implementar Retrofit + Models

¿Qué es Retrofit? Es una librería de Android que te permite hacer peticiones HTTP (como GET, POST, etc.) de forma sencilla. Conecta tu app con el backend.

¿Qué tienes que hacer?
- Añadir la dependencia de Retrofit en build.gradle. 
- Crear interfaces Java con anotaciones como @GET("/products"). 
- Crear modelos Java (POJOs) que representen la respuesta JSON del backend.

✅ 2. Repositorio por feature (Inventory, Recipes, Cart…)

¿Qué es un repositorio en Android? Es una clase que se encarga de decidir de dónde sacar los datos: del backend, de la base de datos local, de memoria…

Ventaja: encapsulas toda la lógica de red en un solo lugar.

✅ 3. ViewModel + UseCase conectado a backend

¿Qué es ViewModel?  Es una clase de Android que gestiona los datos que ve la UI y sobrevive a los cambios de configuración (como rotación de pantalla).

¿Qué es UseCase?
Es una clase que encapsula una acción de negocio: "obtener inventario", "añadir a carrito", "planificar receta"... Es buena práctica para organizar tu lógica.

✅ 4. Pruebas de conexión con mock y con API real

Primero mock (simulación):
- Crear carpetas mock/ o usar archivos .json en assets/. 
- Cargar los datos localmente sin necesidad de internet o backend en desarrollo.

Después API real:
- Llamar al backend FastAPI desplegado.

Verificar que:
- Las respuestas llegan bien. 
- La UI se actualiza. 
- No hay errores de red.

💡 ¿Por qué hacerlo así?
- Separas responsabilidades (Networking, UI, lógica de negocio)
- Tu código será mantenible, escalable y limpio 
- Podrás hacer pruebas más fácilmente 
- Es el patrón MVVM moderno (Model-View-ViewModel) usado en apps profesionales


## 🚛 FASE 6: LÓGICA DE NEGOCIO Y LOGÍSTICA

### 🚚 Lógica logística
- [ ] Algoritmo de optimización de rutas (mock mínimo)

---

## ✅ ¿Qué factores impredecibles hay en la logística diaria?

1. **Tráfico en tiempo real** Puede hacer que una ruta más corta en km sea más lenta.
2. **Disponibilidad real del stock** Un productor puede quedarse sin stock a última hora.
3. **Cambios de última hora del cliente** Cancelaciones, nuevas órdenes, reubicaciones.
4. **Capacidad de los vehículos y tiempos de carga/descarga** No todos los pedidos caben en una furgoneta.
5. **Condiciones meteorológicas** Lluvia, nieve o calor extremo pueden afectar las rutas.
6. **Preferencias de entrega (ventana horaria del cliente)** Algunos clientes quieren recibir a una hora específica.
7. **Sostenibilidad y reparto justo** Equilibrar los kilómetros recorridos entre productores y consumidores.

---

## 🤖 ¿Qué enfoques inteligentes se pueden usar para optimizar rutas?

### 1. **Algoritmos de optimización clásicos**

* **TSP (Traveling Salesman Problem):** recorrer puntos minimizando distancia.
* **VRP (Vehicle Routing Problem):** extensión del TSP considerando múltiples vehículos y capacidades.
* **Google OR-Tools:** muy potente, ya incluye restricciones de tiempo, carga, etc.

👉 Útil para rutas planificadas con cierta antelación, sobre todo si no hay muchos cambios en tiempo real.

---

### 2. **Rutas adaptativas en tiempo real**

* Utiliza APIs de mapas (Google Maps, OpenRouteService, OSRM...) con tráfico en vivo.
* Cada vez que cambia algo (un pedido, un atasco…), se recalculan rutas.
* Puede usar técnicas de **reoptimización incremental**, en lugar de recalcular todo desde cero.

👉 Esto se parece más a cómo funcionan **Glovo, Uber Eats o Amazon Flex**.

---

### 3. **Machine Learning + datos históricos**

* Analizas las entregas anteriores y aprendes patrones:

  * ¿Qué zonas generan más retrasos?
  * ¿Qué días/hours son más lentos?
  * ¿Qué conductores son más eficientes?
* Puedes ajustar la ruta en base a predicciones, no solo al mapa.

👉 Esto permite que el sistema mejore con el tiempo.

---

### 4. **Sistema híbrido (ideal para tu app)**

💡 **Propuesta estratégica para ti:**

1. **Rutas iniciales planificadas con Google OR-Tools + restricciones básicas**
   (capacidad, tiempo de entrega, agrupación por zona).

2. **Ajustes en tiempo real con Google Maps API**
   (para tráfico, incidentes o anulaciones de pedidos).

3. **Feed de sensores IoT o check-ins del transportista**

   * El sistema confirma el paso por cada punto y se adapta si algo falla.

4. **Capas de sostenibilidad**

   * Rutas que priorizan menor huella de CO₂ (menos kms, más agrupación).
   * Beneficio compartido: si aceptas recibir tu pedido un poco más tarde, se optimiza más la ruta y te da un descuento.

## 🚀 ¿Qué aporta a tu plataforma esto?

* **Rapidez y ahorro**: menos km, menos gasto.
* **Sostenibilidad real**: agrupas pedidos en una lógica de “viaje compartido de comida”.
* **Flexibilidad**: puedes combinar pedidos directos del productor y de supermercados.
* **Escalabilidad**: funciona con 10 o con 1000 pedidos diarios.


-----

- [ ] Agrupación por zonas / camiones ficticios

Al agrupar por zonas:
- Cada camión cubre una zona geográfica cercana. 
- Los pedidos de varios clientes se agrupan en un mismo viaje. 
- Se reducen los cálculos complejos (subproblemas más fáciles). 
- Se pueden prever ventanas de entrega más fiables.

#### Opción 2: Clustering automático. Usar K-Means o DBSCAN con las coordenadas GPS de pedidos.

Ejemplo: con scikit-learn puedes agrupar 100 pedidos en 5 zonas óptimas.

#### Opción 3: Red de nodos inteligente. Crear "centros de reparto" virtuales (o reales) como nodos base.
- A cada pedido se le asigna el nodo más cercano (menor distancia). 
- Cada zona tiene asignado un camión o repartidor. 
- Se puede modelar la capacidad máxima (en peso o unidades). 
- Se pueden agrupar múltiples pedidos hasta llegar al límite.

-----------
- [ ] Tiempo estimado de entrega

Necesidades: 
- El usuario necesita saber cuándo llega su compra. 
- La empresa necesita optimizar rutas y cumplir promesas. 
- Ayuda a organizar la carga, saber cuándo sale cada pedido

Formas de estimar tiempo de entrega

1. Estimación base por distancia (mock rápido)

Fórmula:

```python
tiempo_estimado = distancia_km / velocidad_media_kmh + margen_operativo
```
Velocidad media: 25 km/h en ciudad (en bici o furgoneta).

Margen operativo: 10-20 minutos por carga/espera/tráfico.


Ejemplo:
Un pedido a 5 km → 5 / 25 = 0.2h ≈ 12 minutos + 15 min de margen = 27 min estimados

Por APIs reales (más exacto)
Usar:
- Google Maps Directions API 
- OpenRouteService (ORS) → gratuito y open source

Devuelven:
- Ruta completa (con coordenadas)
- Tiempo estimado real (en segundos o minutos)
- Consideran tráfico (Google)
- Tiempo estimado por turnos de reparto (modo Glovo)

En lugar de dar una hora exacta, das una franja: "Recibirás tu pedido entre las 12:00 y 13:00"

```python
def estimar_tiempo_entrega(distancia_km: float) -> str:
    velocidad_media = 25  # km/h
    margen_operativo = 15  # minutos
    tiempo = (distancia_km / velocidad_media) * 60 + margen_operativo
    return f"{int(tiempo)} minutos"
```

----

- [ ] Visualización (Google Maps API / marcador estático)

---

### 🧠 Funciones inteligentes
- [ ] Predicción de demanda

Predicción de la demanda:
- Evitas sobrestock y desperdicio de alimentos. 
- Preparas mejor la logística y los turnos de reparto. 
- Ayudas a los productores a planificar mejor su cosecha. 
- Puedes automatizar reposiciones en supermercados.

#### Versión 1: MVP (reglas básicas + estacionalidad)

Usas un sistema sencillo de reglas + histórico:

```python
def predecir_demanda_basica(historial_semanal: list[int], factor_estacional: float) -> int:
    media = sum(historial_semanal[-4:]) / 4  # media últimas 4 semanas
    prediccion = media * factor_estacional
    return round(prediccion)
```
historial_semanal = [120, 135, 110, 140] → unidades vendidas

factor_estacional = 1.2 si es temporada alta

💡 Puedes tener factores por tipo de producto:
- Tomates en verano: 1.4 
- Calabazas en otoño: 1.3 
- Verduras todo el año: 1.0

#### Versión 2: IA simple con scikit-learn o Prophet
Si tienes más datos (ventas por día, clima, eventos…):

📦 Usa Facebook Prophet o un modelo de regresión.

Input: días, ventas, clima, tipo de producto, día de la semana, promociones…

Output: predicción por día o semana.

```python
from prophet import Prophet
import pandas as pd

df = pd.DataFrame({
    'ds': ['2024-05-01', '2024-05-02', ...],
    'y': [120, 130, ...]  # ventas reales
})
m = Prophet()
m.fit(df)
future = m.make_future_dataframe(periods=7)
forecast = m.predict(future)
```

🔁 Esto te da una predicción incluso visual.

✅ Conexión con tu sistema
El backend puede correr esta predicción cada X días (cron job).

Almacenar predicciones en tabla predicted_demand(product_id, week, amount)

El frontend de supermercados/agricultores puede mostrar recomendaciones tipo:

🧠 “Se espera vender 320 unidades la próxima semana. ¿Deseas preparar esa cantidad?”

----
- [ ] Recomendaciones personalizadas

Mostrar al usuario lo que más le interesa o necesita sin que lo busque:
- Productos que suele comprar 
- Recetas según su dieta e inventario 
- Promociones según ubicación o historial 
- Alternativas cuando algo no está disponible 
- Recomendaciones de temporada o éticas (local, menos CO₂...)

#### MVP: Sistema basado en reglas simples

Ejemplo de lógica básica:
```python
def recomendar_productos(user, historial, inventario, productos_disponibles):
    favoritos = historial.get("productos_favoritos", [])
    faltan = [p for p in favoritos if p not in inventario]
    recomendados = []

    for producto in productos_disponibles:
        if producto.id in faltan:
            recomendados.append(producto)
        elif producto.temporada == "primavera":
            recomendados.append(producto)

    return recomendados[:10]
```
¿Qué necesitas?
- Tabla user_preferences 
- Tabla user_history (productos más comprados, descartados, etc.)
- Campo temporada, eco, vegano en productos

#### Nivel intermedio: Sistema basado en hábitos + perfil

Puedes segmentar por:
- Horas de uso → sugiere cena si entra de noche 
- Perfil nutricional (veggie, sin gluten, etc.)
- Recetas similares a las favoritas 
- Reposición automática de productos de uso recurrente

```sql
SELECT * FROM products
WHERE categoria IN (SELECT categoria FROM favoritos WHERE user_id = X)
AND disponible = true
ORDER BY popularidad DESC
LIMIT 10
```

#### Nivel avanzado: Recomendador con Machine Learning
Algoritmos típicos:
- Collaborative filtering: “usuarios similares también compraron...” 
- Content-based filtering: “este producto es similar a lo que te gusta” 
- Híbrido: mezcla ambos

Puedes usar librerías como:
- Surprise (fácil de usar)
- LightFM 
- TensorFlow Recommenders (para escalar)

Ejemplo visual (en tu app):

En la lista de la compra:

    "¿Te gustaría añadir lo de la semana pasada?"

En la pantalla de compra:

    "¿Y si pruebas esta receta con lo que tienes en casa?"

En el perfil:

    “Tu consumo esta semana fue 80% local 🌱 ¡Buen trabajo!”

----

- [ ] Generador automático de menú y lista

ayudando al usuario a:
- Comer bien sin planificar manualmente. 
- Comprar lo justo (sin olvidos ni excesos). 
- Usar lo que ya tiene en casa. 
- Adaptarse a sus preferencias, tiempo y presupuesto.

Objetivos:Generar un menú semanal personalizado (desayuno, comida, cena)

✔️ Crear automáticamente la lista de la compra con los ingredientes faltantes
✔️ Optimizarla según filtros: precio, cercanía, supermercados seleccionados

🔁 Flujo general
El usuario indica sus preferencias (veggie, sin gluten, nº de personas…)

La app revisa el inventario actual

Se eligen recetas que usen lo que ya tiene y completen lo que falta

Se genera el menú semanal completo

Se crea la lista de compra automática y optimizada


⚡ Botón “Generar menú rápido” → 1 clic, todo listo

📆 Vista calendario editable → arrastrar recetas a días

💡 Recomendación según clima/localización

🍽️ Opción “Comer fuera” en algunos días

🔁 Recetas que rotan para evitar repeticiones

----

- [ ] Comparador de precios
ahorrar sin perder calidad.

Objetivo
- Mostrar al usuario dónde puede comprar más barato 
- Darle la opción de comprar todo en un solo sitio o optimizar dividiendo 
- Integrarlo con su lista de la compra o carrito

🔁 Flujo funcional

1. El usuario genera o edita su lista de la compra. 
2. La app consulta los precios en tiempo real de cada producto en varios comercios (pequeños y grandes). 
3. Se genera un resumen comparativo:

| Producto      | Sup A | sup B  |
|---------------|-------|---------|
| Tomates 1kg   | 1.20  | 1.10   |
| Pasta 500g    | 0.89  | 1.05    |
| leche vegetal | 1.50  | 1.20    |
| Total         | 3.59  | 3.35    |

4. El usuario puede elegir:
   - Comprar todo en uno solo (menos logística)
   - Optimizar por precio (aunque implique más de un comercio)
5. Se añade la selección final al carrito y pasa a la compra. 
6. Pantalla en la app 
   
    Vista de comparativa con opción de toggle:
    - “Ver todo en un solo lugar” 
    - “Optimizar por precio”

7. Muestra visual con tarjetas por producto (tipo Amazon o Glovo):

   Imagen + precios en varios comercios 
   Icono: carrito + añadir directamente

----

- [ ] Sustitutos por dieta / alergias

muchas personas no pueden (o no quieren) consumir ciertos productos por salud, ideología o preferencia. Si tu app les ofrece alternativas seguras, equivalentes y sabrosas, se gana su confianza y fidelidad.

Objetivo
- Mostrar automáticamente sustitutos adecuados para productos que no puede consumir el usuario. 
- Sugerir sustituciones inteligentes al seleccionar una receta o al añadir un producto. 
- Integrar las restricciones alimentarias en todo el proceso: inventario, recetas, compra.

1. El usuario define su perfil alimenticio:
   - Vegano
   - Sin gluten 
   - Sin lactosa 
   - Alergia a frutos secos, soja, etc. 
   - Bajo en azúcar, sin procesados…

    Esto se guarda en el perfil y se tiene en cuenta automáticamente en todas las pantallas.

2. Al seleccionar un producto o receta incompatible:
   - Se muestra un aviso tipo: “Este producto no es apto para tu dieta” 
   - Y se ofrecen sustitutos recomendados: Equivalentes en sabor, función o nutrición 
   - Posibilidad de ordenarlos por: más ecológico, más barato, más disponible

3. Ejemplo visual (en la app)

- Producto escaneado o elegido: “Leche de vaca” 
- El usuario es intolerante a la lactosa
- Sugerencias:
  - Leche de avena (más barata)
  - Leche de coco (más cremosa)
  - Leche de soja (más proteica)
- Cada uno con:
  - Precio por litro 
  - Disponibilidad 
- Perfil nutricional básico 
- Botón para añadir directamente

Ideas avanzadas
- IA personalizada: Aprender qué sustitutos prefiere cada persona y mejorar las sugerencias. 
- Filtro automático: Que la app nunca muestre productos incompatibles (opcional). 
- Recetas alternativas: Si una receta contiene algo que no puede tomar → sugerir la misma receta con ingredientes sustituidos.

---

## 🧊 FASE 7: IoT y Edge Computing
- [ ] Prototipo con sensores: temperatura, humedad

Crear un prototipo físico o simulado que permita capturar datos de temperatura y humedad desde un entorno (ej. almacén, camión, punto de entrega), enviarlos a tu plataforma en la nube, y permitir acciones automáticas o monitorización.


Flujo del prototipo
1. Sensor (DHT11/DHT22) mide temperatura y humedad cada X segundos. 
2. Microcontrolador (ESP32/RPi) obtiene los datos y los envía vía HTTP o MQTT. 
3. Backend (FastAPI) recibe los datos y los guarda en una tabla sensor_readings. 
4. Opcional: dispara alertas si los valores superan umbrales definidos. 
5. Visualización de datos. Dashboard con los últimos valores 
6. Historial por día / semana 
7. Alertas si supera X°C o %HR


¡Gran pregunta! 🧠 Tu proyecto va mucho más allá de una simple app de recetas o compras: **estás construyendo una red física + digital para optimizar alimentación, logística y sostenibilidad**. Y los sensores son el nexo entre lo que ocurre **en el mundo real** y lo que muestra tu plataforma digital.

---

## 🔧 Sensores útiles para tu plataforma

### 🧺 1. **Sensores para medir stock**

#### 🟢 Opción A: **Sensor de peso (Load Cell + HX711)**

* Detecta cuánto pesa una caja, bolsa o recipiente.
* Útil en:

  * Supermercados (bandejas de productos).
  * Neveras o estanterías inteligentes.
  * Puntos de recogida o vending de productos frescos.

💡 Si el peso cae por debajo de un umbral → se lanza una alerta o se repone.

---

#### 🟢 Opción B: **Sensor óptico IR / Ultrasonido**

* Detecta **si hay algo presente o no** (binario).
* Puede contar productos que entran/salen.
* Ejemplo:

  * Estantería con cajas de tomates → 4 sensores = 4 slots → sabe cuántos hay.

---

#### 🟢 Opción C: **Sensor de distancia ultrasónico**

* En cajas grandes o silos de grano.
* Mide cuánto queda (como una cisterna o bidón).

---

### 🌡️ 2. **Sensores de ambiente (conservación, calidad)**

#### ✅ **DHT11 / DHT22**: Temperatura y humedad

* Controlan condiciones en:

  * Cámaras frigoríficas.
  * Camiones de reparto.
  * Almacenes.
* Importante para trazabilidad e integridad de los alimentos.

---

#### ✅ **Sensor de gases (MQ-x)**:

* MQ2, MQ135 → detectan gases como CO₂, etileno o amoníaco.
* ¿Para qué sirve?

  * Control de maduración (etileno en frutas).
  * Detección de putrefacción / caducidad temprana.

---

#### ✅ **Sensor de luz (BH1750, LDR):**

* Control de exposición solar o condiciones de cultivo/almacenaje.

---

### 🧠 3. **Sensores para logística y transporte**

#### 🛰️ **GPS (NEO-6M, uBlox...)**

* Para seguimiento de camiones, furgonetas o puntos móviles de reparto.
* Conectado al backend: sabes qué productos están en ruta, cuánto tardan.

---

#### 📦 **Sensor de movimiento o vibración (MPU6050)**

* Detecta si un paquete ha sido golpeado o mal manipulado.
* Útil para productos delicados como huevos, frutas, o congelados.

---

### 🔌 4. **Sensores de energía o estado**

#### 🔋 **Voltímetro / Medidor de consumo (INA219)**

* Monitoriza el estado energético de una nevera o equipo conectado.
* Previene fallos → si baja voltaje, alerta y trazabilidad del incidente.

---

### 💡 5. **Otros sensores que podrías explorar**

| Sensor                        | Uso                                         | Beneficio                                     |
| ----------------------------- | ------------------------------------------- | --------------------------------------------- |
| **Cámara + IA**               | Recuento visual, clasificación de productos | Más preciso que sensores binarios             |
| **Sensor de color (TCS3200)** | Detecta cambio de color en alimentos        | Madurez o deterioro visible                   |
| **Identificación RFID/NFC**   | Seguimiento por etiquetas inteligentes      | Trazabilidad detallada y sin escaneo manual   |
| **Sensor de sonido**          | Detección de anomalías en electrodomésticos | Alerta por ruidos anómalos (compresor dañado) |

---

## 🚀 ¿Cómo integrarlo con tu plataforma?

1. Cada dispositivo (ESP32, Raspberry Pi, etc.) actúa como **nodo Edge**.
2. Procesa datos localmente (opcional) y los envía por:

   * MQTT
   * HTTP REST
3. El backend recibe los datos y:

   * Los almacena (`sensor_readings`).
   * Genera alertas.
   * Dispara automatismos (p.ej. reposición, notificación al usuario).
4. En la app del consumidor o productor, se visualiza el estado.

---

## ¿Quieres que te prepare?

* 🧪 Un esquema de sensores para supermercado/productor.
* 📡 Código de ejemplo para ESP32 (MicroPython o Arduino).
* 🔄 Backend endpoint `POST /sensor-data`.


---

## 🧺 SENSORES PARA MEDIR STOCK

| Sensor                         | Uso                                                         | Precio estimado (€) | Comentario                                             |
| ------------------------------ | ----------------------------------------------------------- | ------------------- | ------------------------------------------------------ |
| **Load Cell + HX711**          | Peso de productos, nivel de stock en estanterías o bandejas | 3 – 8 €             | Preciso, ideal para reposición automática              |
| **Sensor infrarrojo IR**       | Detectar presencia/ausencia de objetos                      | 0,50 – 2 €          | Muy barato, ideal para detectar si una caja está vacía |
| **Sensor ultrasónico HC-SR04** | Medir distancia (nivel de cajas, bidones, etc.)             | 1 – 3 €             | Bueno para depósitos o silos                           |

---

## 🌡️ SENSORES DE AMBIENTE

| Sensor              | Uso                                     | Precio estimado (€) | Comentario                                    |
| ------------------- | --------------------------------------- | ------------------- | --------------------------------------------- |
| **DHT11**           | Temperatura y humedad (baja precisión)  | 1 – 2 €             | Suficiente para prototipos                    |
| **DHT22 (AM2302)**  | Temp/humedad (más preciso)              | 3 – 5 €             | Recomendado para entornos sensibles           |
| **BME280 / BMP280** | Temp/humedad/presión                    | 5 – 9 €             | Sensor más completo (I2C/SPI)                 |
| **MQ135 / MQ2**     | Detección de gases (CO₂, etileno, etc.) | 2 – 4 €             | Útil para caducidad / conservación            |
| **BH1750 / LDR**    | Sensor de luz ambiente                  | 1 – 3 €             | Control de exposición o refrigeración por luz |

---

## 🚚 SENSORES PARA TRANSPORTE Y LOGÍSTICA

| Sensor                    | Uso                                             | Precio estimado (€) | Comentario                                      |
| ------------------------- | ----------------------------------------------- | ------------------- | ----------------------------------------------- |
| **GPS NEO-6M**            | Ubicación de repartidores o camiones            | 6 – 12 €            | Se conecta por UART (ESP32 / Arduino)           |
| **MPU6050**               | Acelerómetro y giroscopio (vibraciones, golpes) | 2 – 4 €             | Muy útil para detectar incidencias en la ruta   |
| **RFID/NFC módulo RC522** | Identificación de paquetes/productos            | 1 – 3 €             | Para trazabilidad avanzada y sin escaneo manual |

---

## 🔌 OTROS SENSORES ÚTILES

| Sensor      | Uso                                                     | Precio estimado (€) | Comentario                                    |
| ----------- | ------------------------------------------------------- | ------------------- | --------------------------------------------- |
| **INA219**  | Medición de voltaje y consumo eléctrico                 | 2 – 4 €             | Para monitorizar frigoríficos o energía usada |
| **TCS3200** | Sensor de color (maduración de frutas, control calidad) | 4 – 8 €             | Poco usado, pero muy visual                   |

---

## 💡 EJEMPLOS DE USO EN TU PROYECTO

### 🛒 Supermercado inteligente (sala de stock)

* Load cell para saber si un producto necesita reponer.
* DHT22 para temperatura de conservación.
* INA219 para vigilar neveras.
* LEDs conectados al semáforo: verde/amarillo/rojo.

### 🧑‍🌾 Agricultor digitalizado

* GPS + sensores IR para saber qué envíos ha preparado.
* Cámara + TCS3200 (opcional) para verificar calidad.

### 🚚 Logística / furgoneta

* GPS + MPU6050 para trazabilidad y seguridad de entrega.
* RFID/NFC si usas etiquetas inteligentes por caja.

---

## 🔗 Microcontroladores recomendados para integrarlos

| Dispositivo             | Precio (€) | Características                         |
| ----------------------- | ---------- | --------------------------------------- |
| **ESP32**               | 3 – 8 €    | WiFi + Bluetooth, ideal para IoT y Edge |
| **Raspberry Pi Pico W** | 6 – 10 €   | Compacto + WiFi                         |
| **Arduino Uno / Nano**  | 3 – 12 €   | Muy usado pero menos potente (sin WiFi) |

---

## ¿Te gustaría que te monte ahora mismo?

✅ Un ejemplo completo de:

* Un inventario con Load Cell + ESP32
* Un backend simple que recibe los datos
* Una app Android que muestra si un producto necesita reponer

---

- [ ] Edge: preprocesamiento en ESP32 o Raspberry

Objetivo

Realizar un preprocesamiento local de los datos de sensores antes de enviarlos al servidor. Esto permite:
- Reducir tráfico de red. 
- Ahorrar batería y recursos. 
- Tomar decisiones rápidas sin depender de internet. 
- Filtrar datos irrelevantes o incorrectos.

¿Qué puede hacer el preprocesamiento en el dispositivo Edge?
- Filtrado de valores erráticos
- Ignorar lecturas fuera de rango esperado. Ej.: ignorar temperatura > 100 °C o < -20 °C. 
- Promediado local 
- Calcular un promedio móvil de los últimos 5-10 valores antes de enviar. 
- Condiciones de envío inteligentes. Solo enviar datos si hubo un cambio significativo. Ej.: enviar si cambia más de ±1 °C o ±5 % HR. 
- Alerta local. Si temperatura > 10 °C en cámara de frío → activar LED o buzzer sin necesidad de servidor.

📦 Ejemplo de flujo en pseudocódigo para ESP32 (MicroPython o Arduino)

```python
def loop():
    temp, hum = leer_sensor()
    
    if temp < -20 or temp > 100:
        return  # valor inválido

    if abs(temp - temp_anterior) > 1 or abs(hum - hum_anterior) > 5:
        enviar_datos(temp, hum)
        temp_anterior = temp
        hum_anterior = hum
```

🛠️ ¿Qué tipo de preprocesamiento necesitas?
- Media móvil o suavizado 
- Compresión de datos (en el buffer antes de enviar)
- Envío solo si hay cambios 
- Decisión autónoma (ej. alertas)
- Otro: ___

--- 
- [ ] Reposición automática + envío de datos a backend

Objetivo

Diseñar un sistema en el dispositivo Edge (ESP32 o Raspberry Pi) que detecte cuándo el stock de un producto cae por debajo de un umbral y:
- Envíe un aviso al backend (FastAPI). 
- Opcionalmente, active una reposición automática (sugerencia, pedido o alerta).

🔁 ¿Cómo funciona este flujo en la práctica?
1. Lectura de sensor (por ejemplo, peso, presencia óptica, nivel de caja). 
2. Comparación con umbral mínimo definido (por producto).

    Si está por debajo del mínimo: Se envía un POST al backend con la información del producto. 
3. Se registra en la base de datos como “bajo stock”. 
4. Se puede mostrar en la app del usuario, agricultor o supermercado. 
5. Se puede generar automáticamente una línea en la lista de compra o pedido.

¿Qué necesitas decidir?
- ¿Qué sensores usarás para detectar bajo stock? (peso, óptico, manual…)
- ¿Cuáles son los umbrales mínimos por producto? 
- ¿Reposición automática directa o solo sugerencia en la app? 
- ¿Cada cuánto tiempo se hace el chequeo? ¿O se hace por evento?

---

## 🔗 FASE 8: Blockchain y Trazabilidad
- [] QR con hash y trazabilidad

Significa que cada producto individual o lote puede ser escaneado (con un QR) y mostrarle a cualquier usuario la historia completa del producto, con estos objetivos:
- Garantizar el origen (agricultor, zona, lote)
- Mostrar las condiciones (transporte, temperatura…)
- Generar confianza (certificaciones, registros de blockchain, etc.)

Esquema lógico general
```csharp
Agricultor / Comercio
     ↓
[Backoffice de productor]
     ↓
Crea un producto → Se genera un QR único con HASH
     ↓
Se guarda en la base de datos la relación:
  QR = Producto X + Estado + Historial + Ubicación
     ↓
Se puede escanear el QR desde:
  - App del consumidor
  - Web pública de trazabilidad
     ↓
La app muestra TODO el historial
```

Desde la app Android
Escaneas el QR (con una librería tipo ZXing)

Extraes el hash de la URL

Haces una petición GET /trazabilidad/{qr_hash}

Muestras la info en una pantalla visual (nombre, temperatura, origen...)

#### BONUS: Conexión con Blockchain
Si lo estás simulando:

Guarda en una tabla blockchain_logs un registro simple por producto

Puedes vincularlo con el qr_hash

----

- [] Logs de blockchain

Crear un registro inmutable cada vez que un producto cambia de estado, ubicación, temperatura u otro evento relevante.
Esto simula un comportamiento blockchain (incluso sin usar blockchain real en el MVP), asegurando:
- Auditoría transparente 
- Seguridad en los datos 
- Confianza para el consumidor y productor

¿Cómo se conecta con el resto?

- Cada vez que el producto se crea, se transporta, o se entrega, llamas a log_blockchain_event(...)
- Desde la app, escaneas el QR y ves el historial del producto (con tiempos, localización, eventos…)
- En el futuro, puedes migrarlo fácilmente a una blockchain real (como Ethereum, Polygon, etc.)

----

- [ ] Certificados y autenticidad

Asegurar que cada producto (o productor) puede:
- Mostrar certificados de calidad (ecológico, km 0, sin pesticidas…)
- Garantizar autenticidad (no falsificación)
- Mostrar confianza al consumidor 
- Relación con productos y productores

Un producto puede tener varias certificaciones (product_id)

Un productor también puede tener certificaciones generales (producer_id)

Lógica para verificar certificados

- Puedes añadir un campo booleano verified = True/False, y que:
- El admin revise la certificación subida (PDF, imagen, etc.)
- Se guarde su verificación manual o automática 
- También se puede usar blockchain para registrar la certificación de forma inmutable

Visualización en la app

Cuando el usuario vea un producto, puede consultar:
- Iconos de certificación (“Eco”, “Sin gluten”, etc.)
- Documento escaneado o emitido por una autoridad 
- Estado “Verificado ✅” o “Pendiente de validación ⏳ 
- Opcional: usar Blockchain 
- Guarda en el log de blockchain el certificado_hash o el link, para:
  - Inmutabilidad 
  - Auditoría completa (quién lo subió, cuándo, qué autoridad)

----

- [ ] Visualización de trazabilidad desde la app

Permitir al usuario escanear un producto (QR) o pulsar en él y ver:
- Su recorrido completo: desde origen hasta su cocina 
- Info como: lugar, fechas, transportistas, temperatura, estado 
- Certificados, impacto ecológico, alertas (si los hay)

🧠 Requisitos del backend

Ya tienes esta base:

Tabla product_qrs que asocia un QR con un producto

Tabla blockchain_logs con hash, eventos y timestamps

Modelo products, transactions, etc.

Te falta conectar y visualizar esto como una traza cronológica visual.

Lógica de backend

El QR se escanea: /trace/{product_id} o con el hash del QR

Se consultan todos los TraceEvent del producto

Se devuelven ordenados por fecha para representar la traza

Visual en la app (Android)

Cuando el usuario pulse en “Ver trazabilidad” desde el producto:

Mostrar línea de tiempo 📍 con:

🟢 Recolectado (Ciudad, Fecha)

🚚 Transportado (Temperatura, Transporte)

🏬 Llegada a almacén (Fecha, Ubicación)

🛍️ Entregado

Mostrar hash blockchain si se pulsa “Ver detalles”

Mostrar certificados (si existen)

🧪 Extra

Puedes probarlo con productos de prueba: 🍅 Tomate de Málaga

En cada evento, puedes incluir más info opcional: humedad, sensor, alertas

---

## 💻 FASE 9: PANEL WEB Y VISUALIZACIÓN

### 🌐 Landing pública
- [ ] Página de presentación del proyecto

## 🧾 Página de presentación del proyecto (Landing page)

### 🎯 **Objetivo**

Crear una página web pública que:

* Explique el propósito y beneficios del proyecto
* Atraiga usuarios, colaboradores o testers
* Sirva como carta de presentación para tu TFM / portfolio

---

### 🧱 Contenido sugerido

#### 1. **Header**

* Logo del proyecto
* Menú (Inicio | Beneficios | Cómo funciona | Registro | Contacto)

#### 2. **Hero Section**

* ✨ Frase llamativa tipo:

  > "Tu alimentación, más inteligente, local y sostenible"
* Imagen ilustrativa
* Botón: “Empieza ahora” o “Descubre cómo funciona”

#### 3. **Problema + Solución**

* Breve texto sobre:

  > El problema del desperdicio, la desconexión entre productores y consumidores
* Tu solución:

  * Compra optimizada
  * Productos frescos y locales
  * Trazabilidad transparente

#### 4. **Beneficios**

* 🛒 Compra más barata y eficiente
* 🌱 Apoyas al productor local
* 🧠 Menús y lista automática
* 🔒 Transparencia total con blockchain

#### 5. **Cómo funciona**

* 3 pasos visuales:

  1. Explora productos y recetas
  2. Compra en un clic sin preocuparte
  3. Recibe de forma local y sostenible

#### 6. **Testimonios o impacto**

* “+1.000 recetas sin desperdicio”
* “80% ahorro de tiempo al hacer la compra”
* (Puedes falsificarlos temporalmente como demo)

#### 7. **Call to action**

* Registro o dejar email
* Botón “Quiero ser parte del cambio”

#### 8. **Footer**

* Contacto
* Redes sociales
* GitHub o demo del proyecto (si aplicable)

---

### ⚙️ Tecnologías recomendadas

Para algo simple:

* **Framework estático**:

  * [x] [Vite + React](https://vitejs.dev/)
  * [x] TailwindCSS para estilos
* **Otras opciones**:

  * Webflow (si no quieres picar código)
  * Notion + Super.so para versión ultra rápida

---

### 🖼️ ¿Quieres que te genere el HTML + CSS base o un diseño visual de ejemplo?

Puedo darte también el `index.html` + `style.css` directamente para que puedas modificarlo. ¿Te interesa?

- [ ] Mapa de productores, rutas, impacto generado
Perfecto, te explico cómo puedes implementar el **Mapa de productores, rutas e impacto generado**, y luego te añado la checklist correspondiente para dejarlo bien claro.

---

### 🗺️ Mapa de productores, rutas e impacto generado

#### 🎯 ¿Qué objetivo cumple?

Mostrar de forma visual:

* Los **productores activos** cerca del usuario.
* Las **rutas logísticas** activas o planificadas (entregas, pedidos).
* El **impacto generado**: CO₂ ahorrado, comercio local apoyado, etc.

---

### 🧱 ¿Qué debe incluir el mapa?

1. **Marcadores:**

   * 🟢 Productores (nombre, tipo de producto, disponibilidad)
   * 🛍️ Supermercados o puntos de recogida
   * 🧍 Usuarios (para entrega directa)
   * 🚚 Vehículos en ruta (opcional para tracking)

2. **Rutas:**

   * Líneas conectando productores con supermercados y consumidores
   * Color o estilo indicando prioridad o eficiencia

3. **Impacto:**

   * Panel informativo:

     * KM ahorrados respecto a modelo tradicional
     * Kg de CO₂ evitados
     * Dinero destinado a comercio local (%)

---

### 🛠️ ¿Cómo lo puedes hacer técnicamente?

#### 📍 Tecnologías:

* **Frontend (App o Web):**

  * Google Maps API o Mapbox (más personalizable)
  * Marcadores dinámicos con información (infowindows)
  * Dibujo de rutas (`polyline`)

* **Backend (FastAPI):**

  * Endpoint: `GET /map/data` que devuelva:

    ```json
    {
      "producers": [{ "name": "Finca Las Rosas", "lat": ..., "lng": ..., "products": [...] }],
      "supermarkets": [...],
      "consumers": [...],
      "routes": [{ "from": ..., "to": ..., "distance_km": ..., "co2_saved": ... }],
      "stats": { "co2_total": 38.2, "km_ahorrados": 123, "local_support": 86.5 }
    }
    ```

* **Impacto en tiempo real:**

  * Se actualiza tras cada pedido o semanalmente.

---

### ✅ Checklist para el mapa

#### Módulo: **Mapa de productores y rutas**

* [ ] Endpoint `/map/data` para obtener datos geolocalizados
* [ ] Marcadores de productores, supermercados y usuarios
* [ ] Trazado de rutas logísticas (con `polyline`)
* [ ] Panel con impacto generado (CO₂, KM, dinero local)
* [ ] Diseño limpio y responsive (para web o app)
* [ ] Posibilidad de filtrar por zona, tipo de productor, etc.
* [ ] Animación o refresco cada X minutos si hay rutas activas

---

¿Quieres que te prepare también el JSON de ejemplo y el código base para empezar a mostrar el mapa en Android o web?


Perfecto. Vamos a estructurar bien esta sección: los **paneles internos** para los diferentes tipos de actores (agricultores, supermercados y restaurantes), qué funcionalidades deben tener, cómo se relacionan con el backend y cómo puedes implementarlos paso a paso.

---

## 🧑‍🌾 **Paneles Internos (Roles Profesionales)**

### ✅ Objetivo

Dar a cada actor una interfaz clara, eficiente y adaptada a sus tareas para:

* Gestionar stock y pedidos
* Colaborar en la cadena logística
* Contribuir al ecosistema de recetas o abastecimiento

---

### 🔹 1. **Panel del Agricultor**

#### Funciones clave:

* [ ] 📦 Subir productos y actualizar stock → `POST /farmer/products`, `PUT /farmer/products/{id}`
* [ ] 📷 Adjuntar imágenes, info nutricional, QR → `POST /products/{id}/upload-image`
* [ ] 📊 Ver estado de sus pedidos → `GET /farmer/orders`
* [ ] 🔗 Generar código QR por producto (trazabilidad) → `GET /product/{id}/qr`
* [ ] 🛒 Confirmar o rechazar pedidos → `POST /farmer/orders/{id}/status`

#### Backend mínimo:

* CRUD de productos con usuario tipo `farmer`
* Asociar cada producto con `farmer_id`
* Sistema de pedidos asignados automáticamente o manualmente

---

### 🔹 2. **Panel del Supermercado**

#### Funciones clave:

* [ ] 📥 Hacer pedidos a agricultores → `POST /orders`
* [ ] 📊 Visualizar stock propio → `GET /supermarket/stock`
* [ ] 📈 Ver demanda y predicciones → `GET /supermarket/forecast`
* [ ] 🔄 Aprobar / rechazar entregas recibidas → `POST /orders/{id}/status`

#### Backend mínimo:

* Tabla de stock local del supermercado
* Forecast básico usando hábitos de compra + productos más vendidos
* Relación entre pedidos, almacenes y rutas asignadas

---

### 🔹 3. **Panel del Restaurante**

#### Funciones clave:

* [ ] 🍽️ Subir recetas propias (como “influencer alimentario”) → `POST /recipes`
* [ ] 🛒 Hacer pedidos de productos frescos → `POST /restaurant/orders`
* [ ] 📊 Ver productos que tienen alta rotación → `GET /restaurant/trends`
* [ ] 🌍 Participar en economía circular (opcional) → `POST /restaurant/donate`

#### Backend mínimo:

* CRUD de recetas asociadas al `restaurant_id`
* Sistema de compra de productos frescos por receta
* Conexión con ONGs o compost para sobras (mock mínimo)

---

### 🧠 Extra: lógica de roles

* Cada panel se accede tras login, el usuario tiene un campo `role` (`farmer`, `supermarket`, `restaurant`)
* Cada uno solo accede a sus endpoints personalizados
* Puedes usar `Depends(get_current_user)` + validación de rol

---

### ✅ Checklist resumida

#### Panel Agricultor

* [ ] Subida y edición de productos
* [ ] Gestión de stock y pedidos
* [ ] QR de trazabilidad

#### Panel Supermercado

* [ ] Compra de productos a agricultores
* [ ] Visualización de stock y predicción de demanda

#### Panel Restaurante

* [ ] Subida de recetas
* [ ] Compra optimizada de ingredientes frescos
* [ ] Donación o economía circular

---

¿Quieres que te cree un ejemplo visual (mockup o estructura XML/Java) de alguno de estos paneles? ¿O prefieres que prepare primero el backend con endpoints de ejemplo para cada uno?

---

## 🧪 FASE 10: TESTING Y ENTREGA FINAL
- [ ] Pruebas unitarias con pytest
- [ ] Flujos completos en app
- [ ] Presentación o demo
- [ ] Documentación técnica + README
