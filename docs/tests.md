# Guía de pruebas

## Ubicación
- Backend: `backend/tests/` (p. ej., `backend/tests/algorithms/test_blockchain_manager.py`)
- Algoritmos: `tests/unit/` (optimización, VRP, logística)

## Ejecutar
```bash
# Recomendado: entorno virtual activado
pytest -q
```

## Cobertura sugerida
- Algoritmos: `optimize_products`, `impact_calculator`, `blockchain_manager`
- Modelos y esquemas: validación básica de Pydantic
- Routers: respuestas 200/201 y validaciones 4xx

## Notas
- Mockear dependencias externas (Web3, geocoding) para tests deterministas.
