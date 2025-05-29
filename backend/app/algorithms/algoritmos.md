### **algoritmo de búsqueda inteligente de productos**

Dado un conjunto de productos deseados, el sistema debe encontrar la **mejor combinación posible de comercios** (supermercados, agricultores o tiendas) para realizar la compra, cumpliendo con las preferencias y restricciones del usuario.

---

## ✅ **ETAPAS DEL ALGORITMO**

### 1. **Normalización del carrito**

Convertir el input del usuario en una lista de productos claros y estructurados:

```json
[
  { "name": "tomate", "amount": 1, "unit": "kg" },
  { "name": "leche vegetal", "amount": 2, "unit": "litros" }
]
```

* [x] Sinónimos ("tomate cherry" = "tomate")
* [x] Estándar de unidades ("1 botella" → "1 litro")
* [x] Eliminar duplicados
* [x] Ignorar productos que ya están en el inventario con stock suficiente

---

### 2. **Búsqueda de proveedores disponibles**

Consultar en la base de datos:

* ¿Qué productos están disponibles?
* ¿Qué proveedores los ofrecen?
* ¿A qué distancia están del usuario?
* ¿Tienen etiquetas como `eco`, `local`, `bio`, etc.?

---

### 3. **Filtrado por restricciones del usuario**

#### 🎚️ Restricciones funcionales:

* [ ] `un_solo_comercio`: ¿puede dividir la compra o debe hacerse en un solo lugar?
* [ ] `eco_only`: solo productos ecológicos certificados
* [ ] `local_only`: solo productores en un radio de 10 km
* [ ] `sin_alérgenos`: excluir productos con ingredientes no aptos (ej. gluten, lácteos)
* [ ] `preferencias_dietéticas`: vegan, sin azúcar, etc.
* [ ] `presupuesto_max`: no superar cierto total
* [ ] `proximidad_max`: no recorrer más de X km

---

### 4. **Evaluación de cada proveedor**

Por cada proveedor:

* [ ] `productos_cubiertos`: porcentaje de productos deseados que puede ofrecer
* [ ] `precio_total`: suma del coste de esos productos
* [ ] `distancia`: cálculo con Haversine según coordenadas GPS
* [ ] `disponibilidad`: stock actualizado
* [ ] `etiquetas`: ecológico, local, sostenible, etc.

---

### 5. **Generación de combinaciones posibles**

Si se permite más de un proveedor:

* Crear combinaciones mínimas de 1 a N proveedores que cubran todo el carrito.
* Evaluar cada combinación según:

| Combinación | Precio total | Distancia total | Nº comercios | Eco Score | Local Score |
| ----------- | ------------ | --------------- | ------------ | --------- | ----------- |
| A + B       | 13.40 €      | 5.2 km          | 2            | 0.9       | 1.0         |

---

### 6. **Cálculo de puntuación por combinación**

Una función que combine criterios con pesos ajustables:

```python
def puntuacion_final(precio, distancia, eco_score, local_score, num_proveedores):
    return (
        (precio * 0.5) +
        (distancia * 0.3) -
        (eco_score * 2.0) -
        (local_score * 1.5) +
        (num_proveedores * 0.8)
    )
```

El sistema selecciona las combinaciones con **menor puntuación**.

---

### 7. **Presentación de opciones al usuario**

Opciones sugeridas:

* ✅ Compra recomendada (óptima)
* 🛍️ Compra en un solo comercio (aunque cueste más)
* 💸 Compra por precio mínimo
* 🚲 Compra más ecológica/local

---

## 🔒 POSIBLES RESTRICCIONES A TENER EN CUENTA

### 🔐 *Restricciones duras* (no se pueden violar):

* Ingredientes excluidos (alérgenos)
* Producto imprescindible no disponible → avisar
* Límite de presupuesto máximo
* Límite de distancia máxima (ej. 5 km en bici)

### 🎯 *Restricciones suaves* (afectan la puntuación):

* Preferencias dietéticas (vegano, sin gluten…)
* Preferencia por comercio local
* Preferencia por ecológico
* Minimizar número de comercios
* Minimizar impacto (km recorridos, CO₂ estimado)
* Maximizar aprovechamiento del inventario existente

---

## 💡 EJEMPLO DE ENTRADA

```json
{
  "carrito": [
    {"producto": "tomate", "cantidad": 2, "unidad": "kg"},
    {"producto": "pan integral", "cantidad": 1, "unidad": "unidad"}
  ],
  "usuario": {
    "lat": 40.4,
    "lon": -3.7,
    "preferencias": {
      "eco_only": false,
      "local_only": false,
      "sin_gluten": true,
      "un_solo_comercio": false
    },
    "restricciones": {
      "distancia_max_km": 10,
      "presupuesto_max": 15
    }
  }
}
```

---

## 🛠️ ¿Qué necesitas para implementarlo?

1. Base de datos estructurada con:

   * Tabla `products` con etiquetas (`eco`, `local`, `vegan`, `gluten_free`)
   * Tabla `provider_products` con `precio`, `stock`, `provider_id`, `ubicación`
   * Tabla `providers` con `lat`, `lon`, `tipo` (agricultor, supermercado)
2. Módulo `logic/product_optimizer.py`
3. Endpoint `POST /magic-search` con esquema de entrada/salida claro
4. Tests automáticos con mocks de ubicaciones, productos y resultados

---

¿Quieres que ahora prepare el módulo `product_optimizer.py` con las funciones clave y el primer esqueleto del endpoint `/magic-search`?
