# Sistema de Trazabilidad Blockchain

## Descripción General

El sistema de trazabilidad blockchain implementa una cadena de suministro completa y transparente para productos alimentarios, registrando cada paso desde la producción hasta el consumidor final. Utiliza tecnología blockchain para garantizar la inmutabilidad y verificación de todos los eventos.

## Características Principales

### 1. Trazabilidad Completa
- **Datos del productor**: Información del agricultor, ubicación, certificaciones
- **Datos de sensores IoT**: Lecturas en tiempo real durante cultivo y transporte
- **Transacciones**: Cada cambio de propiedad (agricultor → supermercado → consumidor)
- **Información de transporte**: Distancias, tiempos, condiciones ambientales
- **Controles de calidad**: Verificaciones en cada etapa
- **Verificación de autenticidad**: Validación de la cadena de suministro

### 2. Integración con Sensores IoT
- Monitoreo automático de temperatura, humedad, pH, etc.
- Detección de anomalías en tiempo real
- Cálculo automático de puntuaciones de calidad
- Alertas por violaciones de condiciones

### 3. Códigos QR de Trazabilidad
- Generación automática de códigos QR
- Verificación instantánea de autenticidad
- Acceso a información completa del producto
- Integración con aplicaciones móviles

## Arquitectura del Sistema

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

### Servicios

#### TraceabilityService
Servicio principal para la gestión de trazabilidad:
- Creación de cadenas de trazabilidad
- Registro de eventos
- Cálculo de métricas
- Verificación de autenticidad

#### QRTraceabilityService
Servicio para códigos QR:
- Generación de códigos QR
- Verificación de autenticidad
- Gestión de metadatos

#### IOTTraceabilityIntegration
Servicio de integración con sensores IoT:
- Creación automática de eventos
- Monitoreo de violaciones
- Detección de anomalías
- Generación de reportes

## Flujo de Trabajo

### 1. Creación de Producto
```python
# Crear cadena de trazabilidad
traceability_service.create_product_traceability_chain(
    product_id=product_id,
    producer_id=producer_id,
    blockchain_private_key=private_key
)
```

### 2. Registro de Eventos
```python
# Evento de cosecha
traceability_service.add_traceability_event(
    product_id=product_id,
    event_type=TraceabilityEventType.harvest,
    actor_id=farmer_id,
    location_data=location_data,
    event_data=harvest_data
)

# Evento de transporte
traceability_service.add_transport_event(
    product_id=product_id,
    event_type=TraceabilityEventType.transport_start,
    transport_data=transport_data,
    actor_id=driver_id
)
```

### 3. Integración con Sensores
```python
# Crear eventos automáticos desde sensores
iot_service.auto_create_traceability_events_from_sensors(
    product_id=product_id,
    blockchain_private_key=private_key
)

# Monitorear violaciones de temperatura
iot_service.monitor_temperature_violations(
    product_id=product_id,
    min_temp=0.0,
    max_temp=40.0
)
```

### 4. Generación de Códigos QR
```python
# Generar código QR completo
qr_service.generate_traceability_qr(
    product_id=product_id,
    qr_type="full_traceability"
)

# Verificar código QR
qr_service.verify_qr_traceability(qr_hash)
```

## Endpoints de API

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

## Verificación de Autenticidad

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

## Métricas y Reportes

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

## Seguridad y Privacidad

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

## Casos de Uso

### 1. Agricultor
- Crear cadena de trazabilidad para productos
- Registrar eventos de cosecha y empaquetado
- Monitorear condiciones de cultivo
- Generar códigos QR para productos

### 2. Supermercado
- Verificar autenticidad de productos
- Registrar transacciones de compra
- Monitorear condiciones de almacenamiento
- Realizar controles de calidad

### 3. Consumidor
- Escanear códigos QR para verificar autenticidad
- Acceder a información completa del producto
- Verificar origen y calidad
- Reportar problemas o incidencias

### 4. Inspector/Regulador
- Verificar cumplimiento de normativas
- Auditar cadenas de suministro
- Detectar fraudes o adulteraciones
- Generar reportes de cumplimiento

## Beneficios

### Para Agricultores
- Transparencia en la cadena de suministro
- Mejora de la calidad de productos
- Acceso a mercados premium
- Reducción de desperdicios

### Para Supermercados
- Verificación de autenticidad
- Mejora de la confianza del consumidor
- Optimización de la cadena de suministro
- Reducción de riesgos

### Para Consumidores
- Información completa del producto
- Verificación de origen y calidad
- Confianza en la autenticidad
- Toma de decisiones informadas

### Para el Sistema
- Trazabilidad completa y transparente
- Detección temprana de problemas
- Mejora continua de la calidad
- Cumplimiento de normativas

## Implementación

### Requisitos
- Base de datos PostgreSQL
- Servicio de blockchain (Ethereum, Polygon, etc.)
- Sensores IoT conectados
- Aplicación móvil para códigos QR

### Configuración
1. Configurar conexión a blockchain
2. Configurar sensores IoT
3. Ejecutar migraciones de base de datos
4. Configurar endpoints de API
5. Implementar aplicación móvil

### Monitoreo
- Alertas por violaciones de temperatura
- Notificaciones de anomalías
- Reportes de calidad
- Auditoría de eventos

## Futuras Mejoras

### Funcionalidades Adicionales
- Integración con más tipos de sensores
- Análisis predictivo de calidad
- Optimización automática de rutas
- Integración con sistemas de pago

### Tecnologías Emergentes
- Inteligencia artificial para detección de anomalías
- Machine learning para predicción de calidad
- IoT edge computing para procesamiento local
- Blockchain híbrido para mejor escalabilidad

## Conclusión

El sistema de trazabilidad blockchain proporciona una solución completa y transparente para la cadena de suministro de productos alimentarios. Combina tecnología blockchain, sensores IoT y códigos QR para crear un ecosistema de confianza que beneficia a todos los participantes en la cadena de suministro.

La implementación modular permite una integración gradual y la adaptación a diferentes tipos de productos y procesos. El sistema está diseñado para ser escalable, seguro y fácil de usar, proporcionando valor real a agricultores, supermercados y consumidores.
