# Sistema IoT Híbrido

Este directorio contiene el sistema IoT híbrido que maneja tanto sensores reales (ESP32) como simulados (Python).

## Archivos

- `iot_manager.py` - Script principal para simular sensores IoT
- `register_sensor.py` - Script para registrar sensores en la base de datos
- `esp32_sensors/` - Código para sensores ESP32 reales
- `README.md` - Este archivo

## Uso

### 1. Instalar dependencias

**IMPORTANTE**: Primero instala las dependencias necesarias:

```bash
cd iot
pip install -r requirements.txt
```

### 2. Configuración inicial

Asegúrate de que el backend esté ejecutándose y la base de datos esté disponible.

### 3. Registrar sensores en la base de datos

**IMPORTANTE**: Antes de ejecutar `iot_manager.py`, debes registrar los sensores en la base de datos:

```bash
python register_sensor.py
```

Este script:
- Registra el sensor simulado en la base de datos
- Configura umbrales de temperatura (15-35°C)
- Habilita alertas automáticas

### 4. Ejecutar simulación de sensores

```bash
python iot_manager.py
```

Este script:
- Conecta al backend
- Verifica que el sensor existe en la base de datos
- Simula lecturas de temperatura y humedad cada 30 segundos
- Envía datos al endpoint `/iot/ingest`

## Configuración

### Variables de entorno

Crea un archivo `.env` en la raíz del proyecto con:

```env
# Backend
BACKEND_IP=10.100.194.237
BACKEND_PORT=8000
BACKEND_PROTOCOL=http

# IoT
IOT_INGEST_TOKEN=dev_iot_token_2024

# Base de datos
DATABASE_URL=postgresql://usuario:password@localhost:5432/database
```

### Configuración del sensor simulado

El sensor simulado está configurado con:
- **Device ID**: `pedro-sanchez-sensor-01`
- **Tipo**: `humidity`
- **Temperatura base**: 22°C (±3°C)
- **Humedad base**: 70% (±8%)
- **Intervalo**: 30 segundos

## Solución de problemas

### Error: "ModuleNotFoundError: No module named 'aiohttp'"

**Solución**: Instala las dependencias necesarias

```bash
pip install -r requirements.txt
```

### Error: "Sensor no encontrado en la base de datos"

**Solución**: Ejecuta primero `register_sensor.py`

```bash
python register_sensor.py
```

### Error: "Error conectando al backend"

**Verificaciones**:
1. El backend está ejecutándose
2. La IP y puerto son correctos en `.env`
3. No hay firewall bloqueando la conexión

### Error: "Token de autenticación inválido"

**Verificaciones**:
1. El token en `.env` coincide con el del backend
2. El header `X-Ingest-Token` se está enviando correctamente

## Logs

El sistema genera logs detallados que incluyen:
- ✅ Operaciones exitosas
- ❌ Errores y fallos
- 📊 Estadísticas de envío
- 🔍 Información de depuración

## Estructura de datos

### Formato de datos enviados al backend

```json
{
  "device_id": "pedro-sanchez-sensor-01",
  "temperature": 24.3,
  "humidity": 61.5,
  "timestamp": 1726200000,
  "extra_data": {
    "sensor_name": "Sensor Simulado Pedro Sánchez",
    "simulated": true,
    "reading_number": 42
  }
}
```

### Endpoint de ingesta

- **URL**: `POST /iot/ingest`
- **Headers**: `X-Ingest-Token: dev_iot_token_2024`
- **Content-Type**: `application/json`

## Desarrollo

### Añadir nuevos sensores simulados

1. Modifica `create_simulated_sensors()` en `iot_manager.py`
2. Registra el sensor con `register_sensor.py`
3. Ajusta la configuración según necesidades

### Modificar parámetros de simulación

Edita la clase `SimulatedSensor` en `iot_manager.py`:
- `base_temperature`: Temperatura base
- `base_humidity`: Humedad base
- `temperature_variance`: Variación de temperatura
- `humidity_variance`: Variación de humedad