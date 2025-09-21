# Sistema IoT Coherente

## Descripción

Sistema IoT simplificado y coherente que sigue principios SOLID y mantiene total coherencia con la base de datos, backend y frontend del proyecto TFM.

## Arquitectura SOLID

### Single Responsibility Principle
- **IoTConnection**: Solo gestiona conexiones HTTP
- **SensorDataProcessor**: Solo procesa datos de sensores
- **SensorValidator**: Solo valida configuraciones
- **BackendConnector**: Solo conecta con el backend

### Open/Closed Principle
- **Sensor**: Clase base extensible para diferentes tipos de sensores
- **RealSensor**: Implementación específica para sensores reales (ESP32)
- **SimulatedSensor**: Implementación específica para sensores simulados

### Dependency Inversion
- **IoTManager**: Coordina sensores simulados sin depender de implementaciones concretas
- **Sensor**: Clase base extensible para diferentes tipos de sensores
- **RealSensor**: Implementación para sensores reales (ESP32)
- **SimulatedSensor**: Implementación para sensores simulados (Python)

## Estructura del Sistema

```
iot/
├── README.md                    # Esta documentación
├── iot_manager.py              # Gestor híbrido (sensores simulados)
└── esp32_sensors/
    └── esp32_sensors.ino       # Firmware ESP32 (sensores reales)
```

## Componentes

### 1. **IoT Manager** (`iot_manager.py`)
Sistema híbrido que gestiona sensores simulados siguiendo principios SOLID.

**Arquitectura SOLID:**
- **Single Responsibility**: Cada clase tiene una responsabilidad específica
- **Open/Closed**: Extensible para nuevos tipos de sensores
- **Liskov Substitution**: Sensores intercambiables
- **Interface Segregation**: Interfaces específicas por tipo
- **Dependency Inversion**: Depende de abstracciones, no implementaciones

**Características:**
- Gestión de sensores simulados
- Conexión HTTP asíncrona al backend
- Generación de datos realistas
- Estadísticas en tiempo real
- Manejo de errores robusto

### 2. **ESP32 Firmware** (`esp32_sensors/esp32_sensors.ino`)
Firmware para sensores reales de María García.

**Características:**
- Water Sensor + NTC KY-013
- Conexión WiFi robusta
- Envío directo al endpoint `/iot/ingest`
- Autenticación por token
- Manejo de errores
- Código limpio y simple

## Uso del Sistema

### 1. **Configurar Variables de Entorno**
El sistema usa automáticamente las variables del archivo `.env` en la raíz del proyecto:

```bash
# Configurar variables en .env
BACKEND_IP=10.35.89.237
BACKEND_PORT=8000
IOT_INGEST_TOKEN=dev_iot_token_2024
ESP32_WIFI_SSID=TU_WIFI_SSID
ESP32_WIFI_PASSWORD=TU_WIFI_PASSWORD
```

### 2. **Configurar Base de Datos**
```bash
cd database
python db_manager.py create
```
*Esto crea automáticamente todos los sensores en la base de datos*

### 3. **Ejecutar Sensores Simulados**
```bash
cd iot
python iot_manager.py
```
*Gestiona sensores simulados de Pedro Sánchez y Ana López*

### 4. **Cargar Firmware ESP32 (Sensor Real)**
- Abrir `esp32_sensors/esp32_sensors.ino` en Arduino IDE
- Configurar WiFi y backend URL directamente en el código
- Cargar en el ESP32 (María García)

## Configuración

### **Variables de Entorno Principales**
```bash
# Backend
BACKEND_IP=10.35.89.237
BACKEND_PORT=8000
BACKEND_PROTOCOL=http
IOT_INGEST_TOKEN=dev_iot_token_2024

# ESP32
ESP32_WIFI_SSID=TU_WIFI_SSID
ESP32_WIFI_PASSWORD=TU_WIFI_PASSWORD
ESP32_DEVICE_ID=maria-garcia-sensor-001
ESP32_SEND_INTERVAL=30000

# Base de Datos
DATABASE_URL=postgresql://postgres:password@localhost:5432/iot_system
```

### **Configuración Automática**
- ✅ **Database Manager**: Lee automáticamente `DATABASE_URL` del `.env`
- ✅ **IoT Manager**: Lee automáticamente `BACKEND_IP`, `BACKEND_PORT`, `IOT_INGEST_TOKEN`
- ✅ **ESP32**: Configuración directa en el código
- ✅ **Backend**: Endpoint `/iot/ingest` para recibir datos

### **Variables de Entorno**
```bash
export BACKEND_URL="http://10.35.89.237:8000"
export IOT_INGEST_TOKEN="dev_iot_token_2024"
```

### **ESP32 (Sensor Real)**
```cpp
const char* WIFI_SSID = "TU_WIFI_SSID";
const char* WIFI_PASSWORD = "TU_WIFI_PASSWORD";
const char* BACKEND_URL = "http://10.35.89.237:8000/iot/ingest";
const char* IOT_INGEST_TOKEN = "dev_iot_token_2024";
const char* DEVICE_ID = "maria-garcia-sensor-001";
```

## Formato de Datos

### **Payload del Sensor**
```json
{
    "device_id": "maria-garcia-sensor-001",
    "temperature": 25.5,
    "humidity": 65.0,
    "timestamp": 1726200000,
    "extra_data": {
        "sensor_name": "Sensor Real María García",
        "sensor_type": "temperature",
        "firmware_version": "2.0.0",
        "signal_strength": -63,
        "uptime": 6571
    }
}
```

### **Respuesta del Backend**
```json
{
    "status": "success",
    "message": "Datos ingeridos exitosamente",
    "reading_id": 123,
    "device_id": "maria-garcia-sensor-001",
    "timestamp": "2024-09-14T15:53:37.827Z"
}
```

## Coherencia con el Sistema

### **Base de Datos**
- Usa el esquema simplificado (`db_schema.sql`)
- Compatible con modelos SQLAlchemy del backend
- Campos coherentes: `device_id`, `sensor_type`, `location_lat/lon`, etc.

### **Backend**
- Endpoint `/iot/ingest` para ingesta de datos
- Autenticación por token `X-Ingest-Token`
- Formato de datos coherente con schemas Pydantic
- Endpoint `/iot/devices/{device_id}/telemetry` para consultas

### **Frontend**
- Modelos Java coherentes con la estructura de datos
- APIs alineadas con el backend
- Datos consistentes en toda la aplicación

## Sensores Configurados

### **Sensor Real (María García)**
- **Device ID**: `maria-garcia-sensor-001`
- **Tipo**: `temperature`
- **Zona**: Invernadero Principal
- **Farmer ID**: 1
- **Intervalo**: 30 segundos

### **Sensores Simulados**
- **Pedro Sánchez**: `pedro-sanchez-sensor-01` (humidity)
- **Intervalo**: 60 segundos

## Monitoreo

### **Ver Datos del Sensor Real**
```bash
curl "http://10.35.89.237:8000/iot/devices/maria-garcia-sensor-001/telemetry"
```

### **Ver Datos del Simulador**
```bash
curl "http://10.35.89.237:8000/iot/devices/pedro-sanchez-sensor-01/telemetry"
```

### **API Documentation**
```
http://10.35.89.237:8000/docs
```

## Troubleshooting


### **Error de conexión al backend**
```bash
# Verificar que el backend esté ejecutándose
cd backend
python -m uvicorn app.main:app --reload

# Verificar IP en .env
BACKEND_IP=10.35.89.237  # Cambiar por tu IP local
BACKEND_PORT=8000
```

### **Error: column "product_id" of relation "sensor_readings" does not exist**
```bash
# Solución: Recrear base de datos con esquema corregido
cd database
python db_manager.py drop create
```

### **Error 404: Sensor no encontrado**
- Ejecutar `python database/db_manager.py create`
- Verificar que el device_id coincida

### **Error 401: Token inválido**
- Verificar `X-Ingest-Token: dev_iot_token_2024`
- Comprobar variable de entorno en backend

### **Error de conexión WiFi (ESP32)**
- Verificar credenciales WiFi
- Comprobar IP del backend

## Ventajas del Sistema Coherente

### **Simplicidad**
- Código limpio y organizado
- Principios SOLID aplicados
- Sin redundancias

### **Coherencia**
- Alineado con base de datos, backend y frontend
- Formato de datos consistente
- APIs coherentes

### **Mantenibilidad**
- Responsabilidades bien definidas
- Fácil de extender
- Código reutilizable

### **Robustez**
- Manejo de errores completo
- Conexiones asíncronas
- Validación de datos

El sistema está diseñado para ser **fácil de usar, mantener y extender** siguiendo las mejores prácticas de desarrollo y manteniendo **100% de coherencia** con el resto del proyecto.