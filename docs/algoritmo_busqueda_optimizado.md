# Algoritmo de Búsqueda Optimizado

## Descripción General

Se ha implementado un algoritmo de búsqueda inteligente que ordena los productos según criterios de priorización basados en los filtros activos del usuario. El algoritmo considera múltiples factores como precio, proximidad, sostenibilidad, características ecológicas y disponibilidad de stock.

## Funcionalidades

### 1. Criterios de Ordenación

El algoritmo soporta los siguientes criterios de ordenación:

- **`optimal`** (por defecto): Algoritmo que considera múltiples factores con pesos balanceados
- **`price`**: Prioriza productos con menor precio
- **`distance`**: Prioriza productos más cercanos al usuario
- **`sustainability`**: Prioriza productos con mayor score de sostenibilidad
- **`eco`**: Prioriza productos ecológicos
- **`stock`**: Prioriza productos con mayor disponibilidad de stock

### 2. Algoritmo Óptimo (Por Defecto)

Cuando no hay filtros específicos activos, el algoritmo usa una puntuación compuesta con los siguientes pesos:

- **Precio**: 30% - Productos más baratos obtienen mayor puntuación
- **Distancia**: 25% - Productos más cercanos obtienen mayor puntuación
- **Sostenibilidad**: 20% - Productos con mayor score de sostenibilidad
- **Ecológico**: 15% - Bonus por productos ecológicos
- **Stock**: 10% - Productos con mayor disponibilidad

### 3. Priorización por Filtros

Cuando el usuario activa filtros específicos, el algoritmo se adapta:

- **Filtro "Precio bajo"**: Ordena por precio ascendente
- **Filtro "Cerca"**: Ordena por distancia ascendente
- **Filtro "Sostenible"**: Ordena por score de sostenibilidad descendente
- **Filtro "Ecológico"**: Prioriza productos ecológicos
- **Filtro "Disponible"**: Ordena por stock disponible descendente

## Implementación

### Backend

#### Archivo: `backend/app/algorithms/optimize_products.py`

```python
def calculate_product_score(
    product: Dict, 
    user_location: Optional[Tuple[float, float]] = None,
    weights: Optional[Dict[str, float]] = None,
    filters: Optional[Dict[str, bool]] = None
) -> float:
    """
    Calcula la puntuación de un producto basada en múltiples criterios.
    Puntuación más alta = mejor producto.
    """
```

#### Archivo: `backend/app/api/v1/routers/products.py`

El endpoint `/products/optimized/` utiliza el nuevo algoritmo:

```python
@router.post("/optimized/", response_model=list[ProductOptimizedResponse])
def get_products_optimized(
    request: ProductFilterRequest = Body(...),
    db: Session = Depends(database.get_db)
):
```

### Frontend

#### ConsumerSearchProductsFragment.java
- Implementa algoritmo de puntuación local
- Determina criterios de ordenación basados en filtros activos
- Aplica ordenación en tiempo real

#### SupermarketSearchProductsFragment.java
- Misma implementación que ConsumerSearchProductsFragment
- Optimizado para búsquedas de supermercados

## Uso

### 1. Sin Filtros Activos
Los productos se ordenan usando el algoritmo óptimo que considera todos los factores.

### 2. Con Filtros Específicos
- **Precio bajo**: Productos ordenados por precio ascendente
- **Cerca**: Productos ordenados por distancia ascendente
- **Sostenible**: Productos ordenados por score de sostenibilidad
- **Ecológico**: Productos ecológicos aparecen primero
- **Disponible**: Productos con stock aparecen primero

### 3. Múltiples Filtros
Cuando se activan múltiples filtros, el algoritmo prioriza según el primer filtro activo.

## Beneficios

1. **Experiencia de Usuario Mejorada**: Los productos más relevantes aparecen primero
2. **Flexibilidad**: Se adapta a las preferencias del usuario
3. **Eficiencia**: Reduce el tiempo de búsqueda
4. **Personalización**: Considera ubicación y preferencias del usuario
5. **Sostenibilidad**: Promueve productos ecológicos y sostenibles

## Configuración

### Pesos Personalizables

Los pesos del algoritmo pueden ser personalizados a través del request:

```json
{
  "weights": {
    "price": 0.3,
    "distance": 0.25,
    "sustainability": 0.2,
    "eco": 0.15,
    "stock": 0.1
  }
}
```

### Criterio de Ordenación

```json
{
  "sort_criteria": "optimal"
}
```

## Consideraciones Técnicas

1. **Rendimiento**: El algoritmo es eficiente y se ejecuta en tiempo real
2. **Escalabilidad**: Funciona con grandes catálogos de productos
3. **Precisión**: Considera múltiples factores para una ordenación precisa
4. **Flexibilidad**: Fácil de extender con nuevos criterios

## Futuras Mejoras

1. **Machine Learning**: Implementar algoritmos de ML para personalización
2. **Historial de Usuario**: Considerar preferencias históricas
3. **Tendencias**: Incorporar tendencias de mercado
4. **Colaborativo**: Filtros basados en comportamiento de usuarios similares


