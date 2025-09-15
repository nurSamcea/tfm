# Sistema IoT Coherente

## Descripción

Sistema IoT híbrido que integra sensores reales (ESP32) y simulados (Python) siguiendo principios SOLID. Mantiene total coherencia con la base de datos, backend y frontend del proyecto TFM, proporcionando una solución completa para la gestión de sensores IoT.

## Arquitectura SOLID

### Single Responsibility Principle
- **IoTConnection**: Solo gestiona conexiones HTTP asíncronas
- **Sensor**: Clase base para diferentes tipos de sensores
- **RealSensor**: Solo maneja sensores reales (ESP32)
- **SimulatedSensor**: Solo maneja sensores simulados (Python)
- **IoTManager**: Solo coordina la gestión general del sistema

### Open/Closed Principle
- **Sensor**: Clase base extensible para nuevos tipos de sensores
- **RealSensor**: Implementación específica para sensores reales
- **SimulatedSensor**: Implementación específica para sensores simulados
- **IoTConfig**: Configuración extensible

### Dependency Inversion
- **IoTManager**: Depende de abstracciones (Sensor) no de implementaciones concretas
- Uso de interfaces para desacoplar componentes

## Estructura del Sistema

```
iot/
├── README.md                    # Esta documentación
├── iot_manager.py              # Gestor híbrido (278 líneas)
└── esp32_sensors/
    └── esp32_sensors.ino       # Firmware ESP32 (322 líneas)
```

## Componentes

### 1. **IoT Manager** (`iot_manager.py`)
Sistema híbrido que gestiona sensores simulados siguiendo principios SOLID.

**Características:**
- Gestión de sensores simulados con datos realistas
- Conexión HTTP asíncrona al backend
- Generación de datos con variaciones naturales
- Estadísticas en tiempo real
- Manejo de errores robusto
- Configuración automática desde `.env`

**Arquitectura SOLID:**
- **Single Responsibility**: Cada clase tiene una responsabilidad específica
- **Open/Closed**: Extensible para nuevos tipos de sensores
- **Liskov Substitution**: Sensores intercambiables
- **Interface Segregation**: Interfaces específicas por tipo
- **Dependency Inversion**: Depende de abstracciones, no implementaciones

### 2. **ESP32 Firmware** (`esp32_sensors/esp32_sensors.ino`)
Firmware para sensores reales de María García.

**Características:**
- **Water Sensor** (GPIO34) para humedad del suelo
- **NTC KY-013** (GPIO35) para temperatura
- Conexión WiFi robusta con reconexión automática
- Envío directo al endpoint `/iot/ingest`
- Autenticación por token `X-Ingest-Token`
- Manejo de errores y reintentos
- Calibración de sensores incluida
- LEDs de estado (conexión/error)

**Sensores Implementados:**
- Temperatura con calibración NTC
- Humedad del suelo
- Señal WiFi (RSSI)
- Uptime del dispositivo

## Uso del Sistema

### 1. **Configurar Variables de Entorno**
El sistema usa automáticamente las variables del archivo `.env` en la raíz del proyecto:

```bash
# Configurar variables en .env
BACKEND_IP=192.168.68.116
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
*Gestiona sensores simulados de Pedro Sánchez*

### 4. **Cargar Firmware ESP32 (Sensor Real)**
- Abrir `esp32_sensors/esp32_sensors.ino` en Arduino IDE
- Instalar librerías: `WiFi`, `HTTPClient`, `ArduinoJson`
- Configurar WiFi y backend URL en el código
- Cargar en el ESP32 (María García)

## Configuración

### **Variables de Entorno Principales**
```bash
# Backend
BACKEND_IP=192.168.68.116
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

### **ESP32 (Sensor Real)**
```cpp
const char* WIFI_SSID = "TU_WIFI_SSID";
const char* WIFI_PASSWORD = "TU_WIFI_PASSWORD";
const char* BACKEND_URL = "http://192.168.68.116:8000/iot/ingest";
const char* IOT_INGEST_TOKEN = "dev_iot_token_2024";
const char* DEVICE_ID = "maria-garcia-sensor-001";
```

## Formato de Datos

### **Payload del Sensor (ESP32)**
```json
{
    "device_id": "maria-garcia-sensor-001",
    "sensor_type": "temperature",
    "temperature": 25.5,
    "humidity": 65.0,
    "timestamp": "2024-09-14T15:53:37.827Z",
    "reading_quality": 1.0,
    "extra_data": {
        "sensor_name": "Sensor Real María García",
        "sensor_id": 1,
        "zone_id": 1,
        "farmer_id": 1,
        "farmer_name": "María García",
        "entity_name": "Huerta Ecológica María",
        "firmware_version": "2.0.0",
        "battery_level": 100,
        "signal_strength": -63,
        "uptime": 6571
    }
}
```

### **Payload del Sensor Simulado**
```json
{
    "device_id": "pedro-sanchez-sensor-01",
    "sensor_type": "humidity",
    "temperature": 22.3,
    "humidity": 68.7,
    "timestamp": "2024-09-14T15:53:37.827Z",
    "reading_quality": 0.95,
    "extra_data": {
        "sensor_name": "Sensor Simulado Pedro Sánchez",
        "simulated": true,
        "reading_number": 45
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
- Sensores configurados automáticamente

### **Backend**
- Endpoint `/iot/ingest` para ingesta de datos
- Autenticación por token `X-Ingest-Token`
- Formato de datos coherente con schemas Pydantic
- Endpoint `/iot/devices/{device_id}/telemetry` para consultas
- Validación de datos completa

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
- **Sensores**: Water Sensor + NTC KY-013
- **Firmware**: v2.0.0

### **Sensores Simulados**
- **Pedro Sánchez**: `pedro-sanchez-sensor-01` (humidity)
- **Intervalo**: 30 segundos (con pausa entre sensores)
- **Datos**: Variaciones naturales realistas

## Monitoreo

### **Ver Datos del Sensor Real**
```bash
curl "http://192.168.68.116:8000/iot/devices/maria-garcia-sensor-001/telemetry"
```

### **Ver Datos del Simulador**
```bash
curl "http://192.168.68.116:8000/iot/devices/pedro-sanchez-sensor-01/telemetry"
```

### **API Documentation**
```
http://192.168.68.116:8000/docs
```

### **Logs del Sistema IoT**
```bash
# Los logs se muestran en tiempo real en la consola
# Incluyen estadísticas de envíos exitosos/fallidos
```

## Troubleshooting

### **Error de conexión al backend**
```bash
# Verificar que el backend esté ejecutándose
cd backend
python -m uvicorn app.main:app --reload

# Verificar IP en .env
BACKEND_IP=192.168.68.116  # Cambiar por tu IP local
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
- Comprobar que el sensor esté en la base de datos

### **Error 401: Token inválido**
- Verificar `X-Ingest-Token: dev_iot_token_2024`
- Comprobar variable de entorno en backend
- Verificar configuración en `.env`

### **Error de conexión WiFi (ESP32)**
- Verificar credenciales WiFi en el código
- Comprobar IP del backend
- Verificar que la red WiFi esté disponible
- Comprobar señal WiFi (RSSI)

### **Error de lectura de sensores (ESP32)**
- Verificar conexiones de los sensores
- Comprobar calibración del NTC
- Verificar voltaje de alimentación (3.3V)
- Revisar configuración ADC

## Dependencias

### **Python (IoT Manager)**
```bash
pip install aiohttp python-dotenv
```

### **ESP32 (Arduino IDE)**
```cpp
// Librerías requeridas:
#include <WiFi.h>           // WiFi
#include <HTTPClient.h>     // HTTP
#include <ArduinoJson.h>    // JSON
#include <math.h>           // Matemáticas
```

## Ventajas del Sistema Coherente

### **Simplicidad**
- Código limpio y organizado
- Principios SOLID aplicados
- Sin redundancias
- Configuración centralizada

### **Coherencia**
- Alineado con base de datos, backend y frontend
- Formato de datos consistente
- APIs coherentes
- Device IDs unificados

### **Mantenibilidad**
- Responsabilidades bien definidas
- Fácil de extender
- Código reutilizable
- Documentación completa

### **Robustez**
- Manejo de errores completo
- Conexiones asíncronas
- Validación de datos
- Reconexión automática
- Reintentos inteligentes

El sistema está diseñado para ser **fácil de usar, mantener y extender** siguiendo las mejores prácticas de desarrollo y manteniendo **100% de coherencia** con el resto del proyecto TFM.