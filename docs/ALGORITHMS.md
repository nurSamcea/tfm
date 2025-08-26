# Algoritmos del Sistema (actualizado)

## 1) Cálculo de impacto (backend/app/algorithms/impact_calculator.py)
- Factores de CO2 por categoría
- Soporte a distancia de transporte y tipo de proveedor
- Resultado: `ProductImpact` con `co2_saved_kg`, `local_support_eur`, `waste_prevented_kg`, `sustainability_score`

## 2) Gestión blockchain (backend/app/algorithms/blockchain_manager.py)
- Conexión Web3 según `BLOCKCHAIN_URL` y `CONTRACT_ADDRESS`
- Operaciones: registro de productos y transacciones, verificación, historial

## 3) Optimización de cesta (backend/app/algorithms/optimize_products.py)
- Filtros por eco, gluten, distancia máxima
- Selección de mejores candidatos por coste/distancia
- Agrupación por proveedor y penalización por paradas
- Devuelve cesta, coste total ponderado y agrupaciones/alternativas

## 4) VRP/Logística (especificación)
- Base para resolver rutas con heurísticas/OR-Tools (tests en `tests/unit/test_vrp_optimizer.py`) 