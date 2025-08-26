# Frontend Android (Java)

## Estructura
- `MainActivity`: contenedor y navegación por rol
- Paquetes UI por rol: `ui/auth`, `ui/consumer`, `ui/farmer`, `ui/supermarket`
- Fragments reutilizables: `ProductDetailFragment`, `AddProductFragment`
- Adapters: `ProductAdapter`, `CartAdapter`, `Farmer*Adapter`, `Supermarket*Adapter`

## Flujo
- Autenticación simple → selección de rol → `BottomNavigationView`
- Consumo de API REST del backend para catálogo, listas, transacciones, métricas y trazabilidad

## Integración
- Clientes HTTP (Retrofit/OkHttp o equivalente) y modelos POJO
- Manejo de estado con ViewModel/LiveData

## Próximos pasos
- Endurecer autenticación (JWT)
- Añadir tests instrumentados por pantallas clave
