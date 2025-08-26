# Sensores usados e integración IoT

## Dispositivos
- ESP32: firmwares `iot/esp32/inventory_sensor.ino`, `iot/esp32/temperature_monitor.ino`
- Raspberry Pi: script `iot/raspberry/dht22_logger.py`

## Tipos de lectura
- Temperatura/Humedad (DHT22/DHT11)
- Inventario (ejemplo de conteo, expansible)
- GPS (desde móvil o módulo; coordenadas del proveedor/usuario)

## Flujo de datos
1. Sensor capta medición (p. ej., temperatura)
2. Publica/Envía a backend (HTTP/MQTT → HTTP)
3. Backend persiste como `sensor_reading` asociado a `product_id`

## Esquema de `sensor_reading`
- `type`: temperatura, humedad, gps, etc.
- `value`/`payload` y `recorded_at`
- `product_id` (FK)

## Datos de ejemplo
- `data/sensors/locations.csv`
- `data/sensors/readings.csv`
