1. Optimización de cesta de compra
- Programación lineal entera (ILP) con pulp u ortools
Entrada del usuario:
  - Lista de productos deseados
  - Filtros: [eco, sin gluten, proveedor único, distancia máxima, etc.]
  - Criterio de optimización: [mínimo precio, mínima distancia, mixto]

Pasos:
1. Filtrar productos según criterios nutricionales y sostenibles.
2. Para cada producto deseado, buscar candidatos que cumplan:
     - stock suficiente
     - filtros ecológicos o nutricionales
3. Construir todas las combinaciones posibles de proveedor-producto.
4. Evaluar cada combinación con una función de coste:
     - precio total
     - distancia total
     - número de proveedores
     - penalizaciones según el modo
5. Devolver la cesta óptima o cercana al óptimo.

------------
3. Rutas logísticas óptimas
- Vehicle Routing Problem (VRP) con Google OR-Tools

------------
4. Visualización y navegación logística
- OSRM / OpenRouteService (API de routing sobre OSM)
- Folium / Leaflet para dibujar rutas frontend.

------------
5. Predicción de demanda: prever qué productos serán más demandados para mejorar stock y logística.
- Modelos de series temporales (ARIMA, Prophet)
- En el futuro: Redes neuronales recurrentes (RNN, LSTM)

------------
6. Planificación semanal de recetas
- Greedy heurístico + filtros nutricionales
- Alternativa: Knapsack multidimensional si hay que ajustar cantidades con precisión.

------------
7. Recomendación de productos o recetas
- Filtrado colaborativo o basado en contenido
- Alternativa: Clustering (k-means) para agrupar hábitos similares
