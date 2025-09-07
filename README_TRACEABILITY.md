# Sistema de Trazabilidad Blockchain

## 🚀 Descripción

El sistema de trazabilidad blockchain implementa una cadena de suministro completa y transparente para productos alimentarios, registrando cada paso desde la producción hasta el consumidor final. Utiliza tecnología blockchain para garantizar la inmutabilidad y verificación de todos los eventos.

## ✨ Características Principales

### 🔗 Trazabilidad Completa
- **Datos del productor**: Información del agricultor, ubicación, certificaciones
- **Datos de sensores IoT**: Lecturas en tiempo real durante cultivo y transporte
- **Transacciones**: Cada cambio de propiedad (agricultor → supermercado → consumidor)
- **Información de transporte**: Distancias, tiempos, condiciones ambientales
- **Controles de calidad**: Verificaciones en cada etapa
- **Verificación de autenticidad**: Validación de la cadena de suministro

### 📱 Códigos QR de Trazabilidad
- Generación automática de códigos QR
- Verificación instantánea de autenticidad
- Acceso a información completa del producto
- Integración con aplicaciones móviles

### 🌡️ Integración con Sensores IoT
- Monitoreo automático de temperatura, humedad, pH, etc.
- Detección de anomalías en tiempo real
- Cálculo automático de puntuaciones de calidad
- Alertas por violaciones de condiciones

## 🏗️ Arquitectura

### Modelos de Datos

#### TraceabilityEvent
Registra cada evento individual en la cadena de trazabilidad:
- Tipo de evento (creación, cosecha, transporte, venta, etc.)
- Timestamp y ubicación
- Actor responsable
- Datos específicos del evento
- Hash de blockchain para verificación

#### ProductTraceabilityChain
Mantiene el estado general de la cadena de trazabilidad:
- Información del producto y productor
- Estado de completitud y verificación
- Métricas agregadas (distancia, tiempo, violaciones)
- Puntuación de calidad

#### SensorTraceabilityData
Almacena datos de sensores asociados a eventos:
- Lecturas de temperatura, humedad, pH, etc.
- Calidad de la lectura
- Metadatos adicionales

#### TransportLog
Registra información detallada del transporte:
- Tipo de vehículo y conductor
- Rutas y distancias
- Condiciones ambientales durante el transporte
- Tiempos estimados vs reales

#### QualityCheck
Registra controles de calidad:
- Tipo de control realizado
- Inspector responsable
- Resultados y puntuación
- Notas y observaciones

## 🚀 Instalación y Configuración

### 1. Requisitos
- Python 3.8+
- PostgreSQL
- Servicio de blockchain (Ethereum, Polygon, etc.)
- Sensores IoT conectados

### 2. Instalación
```bash
# Clonar el repositorio
git clone <repository-url>
cd tfm

# Instalar dependencias
pip install -r requirements.txt

# Configurar variables de entorno
cp .env.example .env
# Editar .env con tus configuraciones
```

### 3. Configuración de Base de Datos
```bash
# Ejecutar migraciones
alembic upgrade head

# Poblar base de datos con datos de ejemplo
python populate_db.py
```

### 4. Configuración de Blockchain
```bash
# Configurar variables de entorno para blockchain
export TRACEABILITY_BLOCKCHAIN_URL="http://localhost:8545"
export TRACEABILITY_CONTRACT_ADDRESS="0x..."
export TRACEABILITY_PRIVATE_KEY="0x..."
```

## 📚 Uso del Sistema

### 1. Crear Cadena de Trazabilidad
```python
from backend.app.algorithms.traceability_service import TraceabilityService

# Crear servicio
traceability_service = TraceabilityService(db)

# Crear cadena de trazabilidad
result = traceability_service.create_product_traceability_chain(
    product_id=1,
    producer_id=1,
    blockchain_private_key="0x..."
)
```

### 2. Añadir Eventos de Trazabilidad
```python
# Evento de cosecha
traceability_service.add_traceability_event(
    product_id=1,
    event_type=TraceabilityEventType.harvest,
    actor_id=1,
    location_data={"lat": 40.4168, "lon": -3.7038},
    event_data={"quantity": 100, "quality": "excellent"}
)

# Evento de transporte
traceability_service.add_transport_event(
    product_id=1,
    event_type=TraceabilityEventType.transport_start,
    transport_data={
        "transport_type": "refrigerated_truck",
        "driver_id": 2,
        "distance_km": 15.5
    },
    actor_id=2
)
```

### 3. Integrar Datos de Sensores
```python
from backend.app.algorithms.iot_traceability_integration import IOTTraceabilityIntegration

# Crear servicio IoT
iot_service = IOTTraceabilityIntegration(db)

# Crear eventos automáticos desde sensores
result = iot_service.auto_create_traceability_events_from_sensors(
    product_id=1,
    blockchain_private_key="0x..."
)

# Monitorear violaciones de temperatura
violations = iot_service.monitor_temperature_violations(
    product_id=1,
    min_temp=0.0,
    max_temp=40.0
)
```

### 4. Generar Códigos QR
```python
from backend.app.algorithms.qr_traceability_service import QRTraceabilityService

# Crear servicio QR
qr_service = QRTraceabilityService(db)

# Generar código QR completo
result = qr_service.generate_traceability_qr(
    product_id=1,
    qr_type="full_traceability"
)

# Verificar código QR
verification = qr_service.verify_qr_traceability("abc123def456")
```

## 🔌 API Endpoints

### Trazabilidad
- `POST /traceability/products/{product_id}/create-chain` - Crear cadena de trazabilidad
- `POST /traceability/products/{product_id}/sensor-reading` - Añadir lectura de sensor
- `POST /traceability/products/{product_id}/transport-event` - Añadir evento de transporte
- `POST /traceability/transactions/{transaction_id}/add-to-traceability` - Añadir transacción
- `GET /traceability/products/{product_id}/summary` - Obtener resumen completo
- `POST /traceability/products/{product_id}/verify` - Verificar autenticidad

### Códigos QR
- `POST /qr-traceability/generate` - Generar código QR
- `POST /qr-traceability/verify` - Verificar código QR
- `GET /qr-traceability/products/{product_id}/qr-codes` - Obtener códigos QR del producto

### Integración IoT
- `POST /iot-traceability/products/{product_id}/auto-create-events` - Crear eventos automáticos
- `POST /iot-traceability/products/{product_id}/monitor-temperature` - Monitorear temperatura
- `POST /iot-traceability/products/{product_id}/detect-anomalies` - Detectar anomalías
- `GET /iot-traceability/products/{product_id}/sensor-report` - Generar reporte de sensores

## 🔐 Verificación de Autenticidad

El sistema implementa múltiples capas de verificación:

### 1. Verificación Blockchain
- Cada evento se registra en blockchain
- Hash único para cada evento
- Verificación de integridad de la cadena

### 2. Verificación de Sensores
- Consistencia de datos de sensores
- Calidad de las lecturas
- Detección de anomalías

### 3. Verificación de Transporte
- Validación de rutas y tiempos
- Verificación de condiciones ambientales
- Control de violaciones de temperatura

### 4. Verificación de Calidad
- Controles de calidad en cada etapa
- Puntuaciones de calidad
- Verificación de certificaciones

## 📊 Métricas y Reportes

### Métricas de la Cadena
- **Distancia total**: Kilómetros recorridos
- **Tiempo total**: Horas en la cadena de suministro
- **Violaciones de temperatura**: Número de incumplimientos
- **Puntuación de calidad**: Score de 0-1 basado en múltiples factores

### Reportes Disponibles
- Resumen completo de trazabilidad
- Reporte de datos de sensores
- Análisis de anomalías
- Verificación de autenticidad
- Línea de tiempo de eventos

## 🎯 Casos de Uso

### 👨‍🌾 Agricultor
- Crear cadena de trazabilidad para productos
- Registrar eventos de cosecha y empaquetado
- Monitorear condiciones de cultivo
- Generar códigos QR para productos

### 🏪 Supermercado
- Verificar autenticidad de productos
- Registrar transacciones de compra
- Monitorear condiciones de almacenamiento
- Realizar controles de calidad

### 👤 Consumidor
- Escanear códigos QR para verificar autenticidad
- Acceder a información completa del producto
- Verificar origen y calidad
- Reportar problemas o incidencias

### 🔍 Inspector/Regulador
- Verificar cumplimiento de normativas
- Auditar cadenas de suministro
- Detectar fraudes o adulteraciones
- Generar reportes de cumplimiento

## 🛡️ Seguridad y Privacidad

### Blockchain
- Transacciones firmadas digitalmente
- Hashes únicos para cada evento
- Inmutabilidad de registros
- Verificación de integridad

### Datos Sensibles
- Encriptación de datos personales
- Control de acceso basado en roles
- Auditoría de accesos
- Cumplimiento con GDPR

## 🚀 Ejecutar Ejemplo

```bash
# Ejecutar ejemplo completo
python examples/traceability_example.py
```

## 📖 Documentación Adicional

- [Documentación del Sistema](docs/blockchain_traceability_system.md)
- [API Reference](docs/api_reference.md)
- [Guía de Implementación](docs/implementation_guide.md)

## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## 📞 Soporte

Para soporte técnico o preguntas:
- Email: support@tuapp.com
- Documentación: [docs.tuapp.com](https://docs.tuapp.com)
- Issues: [GitHub Issues](https://github.com/tuapp/issues)

## 🔮 Roadmap

### Próximas Funcionalidades
- [ ] Integración con más tipos de sensores
- [ ] Análisis predictivo de calidad
- [ ] Optimización automática de rutas
- [ ] Integración con sistemas de pago
- [ ] Aplicación móvil nativa
- [ ] Dashboard de analytics en tiempo real

### Tecnologías Emergentes
- [ ] Inteligencia artificial para detección de anomalías
- [ ] Machine learning para predicción de calidad
- [ ] IoT edge computing para procesamiento local
- [ ] Blockchain híbrido para mejor escalabilidad

---

**¡Gracias por usar el Sistema de Trazabilidad Blockchain! 🚀**
