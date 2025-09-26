/*
 * ESP32 IoT Sensors - Sistema Simplificado
 * Water Sensor + NTC KY-013 + Conexión Backend
 * 
 * Características:
 * - Water Sensor para humedad del suelo (GPIO34)
 * - NTC KY-013 para temperatura (GPIO35)
 * - Autenticación por token
 * - Envío de datos cada 30 segundos
 * - Manejo de errores robusto
 */

#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <math.h>

// ========================================
// CONFIGURACIÓN
// ========================================

// WiFi
const char* WIFI_SSID = "Galaxy S21 5G95d5";
const char* WIFI_PASSWORD = "nur980512";

// Backend
const char* BACKEND_URL = "http://10.100.194.237:8000/iot/ingest";
const char* IOT_INGEST_TOKEN = "dev_iot_token_2024";

// Dispositivo
const char* DEVICE_ID = "maria-garcia-sensor-001";

// Pines
const int WATER_PIN = 34;   // Water Sensor
const int THERM_PIN = 35;   // NTC KY-013

// Configuración ADC
const int ADC_BITS = 12;
const float VREF = 3.3;

// Parámetros NTC (calibrados para 25°C)
const float SERIES_RESISTOR_OHM = 10000.0;
const float NOMINAL_RESISTANCE = 10000.0;         // 10kΩ @ 25°C (típico KY-013)
const float NOMINAL_TEMPERATURE = 25.0;           // °C a 25°C
const float NOMINAL_TEMPERATURE_K = NOMINAL_TEMPERATURE + 273.15;  // Kelvin
const float B_COEFFICIENT = 3950.0;               // B típico 3950

// Orientación del divisor de tensión
// true  -> NTC a VREF y resistencia serie a GND (Vout en nodo intermedio)
// false -> NTC a GND  y resistencia serie a VREF
const bool DIVIDER_NTC_TO_VREF = false;           // NTC a GND, Rserie a VREF

// Offset de calibración en °C (ajústalo hasta que marque ~25°C a temperatura ambiente)
float CALIBRATION_OFFSET_C = 11.0f;  // Ajuste inicial para acercar a ~25°C

// Habilitar trazas de depuración en serie
const bool DEBUG_NTC = true;

// Intervalos
const unsigned long SEND_INTERVAL = 30000;  // 30 segundos
const unsigned long WIFI_RETRY_INTERVAL = 30000;  // 30 segundos

// LEDs
const int LED_PIN = 2;        // LED integrado
const int ERROR_LED_PIN = 4;  // LED de error

// Variables globales
unsigned long lastSendTime = 0;
unsigned long lastWiFiRetry = 0;
bool wifiConnected = false;

// ========================================
// FUNCIONES DE SENSORES
// ========================================

float readTemperature() {
  // Media simple de varias lecturas para reducir ruido
  const int samples = 10;
  unsigned long acc = 0;
  for (int i = 0; i < samples; i++) {
    acc += analogRead(THERM_PIN);
    delay(2);
  }
  int adcValue = acc / samples;

  const float adcMax = float((1 << ADC_BITS) - 1);
  float voltage = (adcValue * VREF) / adcMax;

  // Evitar valores extremos que llevan a división por cero o negativos
  if (voltage <= 0.001 || voltage >= (VREF - 0.001)) {
    return NAN;
  }

  // Resistencia del NTC según orientación del divisor
  // Si NTC está a VREF:   Rntc = Rseries * (Vref/Vout - 1)
  // Si NTC está a GND:    Rntc = Rseries * (Vout/(Vref - Vout))
  float resistance = DIVIDER_NTC_TO_VREF
                     ? SERIES_RESISTOR_OHM * (VREF / voltage - 1.0f)
                     : SERIES_RESISTOR_OHM * (voltage / (VREF - voltage));
  if (resistance <= 0) {
    return NAN;
  }

  // Beta equation: 1/T = 1/T0 + (1/B) * ln(R/R0)  -> T en Kelvin
  float tempK = 1.0f / ( (1.0f / NOMINAL_TEMPERATURE_K) + (1.0f / B_COEFFICIENT) * log(resistance / NOMINAL_RESISTANCE) );
  float tempC = tempK - 273.15f + CALIBRATION_OFFSET_C;

  // Filtro de plausibilidad para NTC comunes
  if (isnan(tempC) || tempC < -40.0f || tempC > 85.0f) {
    return NAN;
  }

  if (DEBUG_NTC) {
    Serial.print("[NTC] ADC="); Serial.print(adcValue);
    Serial.print(" V="); Serial.print(voltage, 4);
    Serial.print(" R="); Serial.print(resistance, 1);
    Serial.print(" Tcruda="); Serial.print(tempC - CALIBRATION_OFFSET_C, 2);
    Serial.print(" Toffset="); Serial.println(tempC, 2);
  }
  return tempC;
}

float readSoilHumidity() {
  // Media simple de varias lecturas para reducir ruido
  const int samples = 10;
  unsigned long acc = 0;
  for (int i = 0; i < samples; i++) {
    acc += analogRead(WATER_PIN);
    delay(2);
  }
  int adcValue = acc / samples;
  
  const float adcMax = float((1 << ADC_BITS) - 1);
  float voltage = (adcValue * VREF) / adcMax;
  
  // DIAGNÓSTICO: Verificar si el sensor está conectado correctamente
  Serial.print("[HUMIDITY] ADC="); 
  Serial.print(adcValue);
  Serial.print(" V="); 
  Serial.print(voltage, 4);
  
  // Verificar si el sensor está respondiendo
  if (adcValue < 50) {
    Serial.println(" - SENSOR NO CONECTADO O EN CORTO (voltaje muy bajo)");
    return 0.0; // Retornar 0% si no hay sensor conectado
  } else if (adcValue > 4000) {
    Serial.println(" - SENSOR EN ABIERTO O DESCONECTADO (voltaje muy alto)");
    return 0.0; // Retornar 0% si sensor abierto
  }
  
  // Convertir voltaje a porcentaje de humedad
  // Para sensores de agua/humedad: más agua = más conductividad = menor voltaje
  // Calibración: 0V = 100% humedad, 3.3V = 0% humedad
  float humidity = ((VREF - voltage) / VREF) * 100.0;
  humidity = constrain(humidity, 0, 100);
  
  Serial.print(" H="); 
  Serial.println(humidity, 2);
  
  return humidity;
}

// ========================================
// FUNCIONES DE CONEXIÓN
// ========================================

bool connectWiFi() {
  if (WiFi.status() == WL_CONNECTED) {
    return true;
  }
  
  Serial.println("Conectando a WiFi...");
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 20) {
    delay(500);
    Serial.print(".");
    attempts++;
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println();
    Serial.println("WiFi conectado!");
    Serial.print("IP: ");
    Serial.println(WiFi.localIP());
    digitalWrite(LED_PIN, HIGH);
    return true;
  } else {
    Serial.println();
    Serial.println("Error conectando a WiFi");
    digitalWrite(ERROR_LED_PIN, HIGH);
    return false;
  }
}

bool sendSensorData(float temperature, float humidity) {
  if (!wifiConnected) {
    Serial.println("WiFi no conectado, no se pueden enviar datos");
    return false;
  }
  
  // Verificar conectividad antes de enviar
  Serial.print("Verificando conectividad a ");
  Serial.print(BACKEND_URL);
  Serial.print(" (IP: ");
  Serial.print(WiFi.localIP());
  Serial.println(")");
  
  HTTPClient http;
  http.begin(BACKEND_URL);
  http.setTimeout(10000); // Aumentar timeout
  http.addHeader("Content-Type", "application/json");
  http.addHeader("X-Ingest-Token", IOT_INGEST_TOKEN);
  
  // Crear JSON
  DynamicJsonDocument doc(1024);
  doc["device_id"] = DEVICE_ID;
  doc["sensor_type"] = "temperature";
  doc["temperature"] = temperature;
  doc["humidity"] = humidity;
  // Si la temperatura es inválida, degradar calidad
  doc["reading_quality"] = isnan(temperature) ? 0.0 : 1.0;
  
  JsonObject extraData = doc.createNestedObject("extra_data");
  extraData["sensor_name"] = "Sensor Real María García";
  extraData["sensor_id"] = 1;
  extraData["zone_id"] = 1;
  extraData["farmer_id"] = 1;
  extraData["farmer_name"] = "María García";
  extraData["entity_name"] = "Huerta Ecológica María";
  extraData["firmware_version"] = "2.0.0";
  extraData["battery_level"] = 100;
  extraData["signal_strength"] = WiFi.RSSI();
  extraData["uptime"] = millis() / 1000;
  
  String jsonString;
  serializeJson(doc, jsonString);
  
  Serial.println("Enviando datos:");
  Serial.println(jsonString);
  
  int httpResponseCode = http.POST(jsonString);
  
  // Diagnóstico detallado de errores HTTP
  if (httpResponseCode == -1) {
    Serial.println("Error HTTP -1: Problema de conexión (servidor inalcanzable)");
    Serial.println("Verifica:");
    Serial.println("1. Que el servidor esté ejecutándose");
    Serial.println("2. Que la IP sea correcta: " + String(BACKEND_URL));
    Serial.println("3. Que no haya firewall bloqueando");
  } else if (httpResponseCode == -2) {
    Serial.println("Error HTTP -2: Timeout de conexión");
  } else if (httpResponseCode == -3) {
    Serial.println("Error HTTP -3: Error de DNS (no se puede resolver la IP)");
  } else if (httpResponseCode == -4) {
    Serial.println("Error HTTP -4: Error de conexión");
  } else if (httpResponseCode == -5) {
    Serial.println("Error HTTP -5: Error de envío");
  } else if (httpResponseCode == -6) {
    Serial.println("Error HTTP -6: Error de recepción");
  } else if (httpResponseCode == -7) {
    Serial.println("Error HTTP -7: Error de stream");
  } else if (httpResponseCode == -8) {
    Serial.println("Error HTTP -8: Error de timeout");
  } else if (httpResponseCode == -9) {
    Serial.println("Error HTTP -9: Error de tamaño de respuesta");
  } else if (httpResponseCode == -10) {
    Serial.println("Error HTTP -10: Error de stream de respuesta");
  } else if (httpResponseCode == -11) {
    Serial.println("Error HTTP -11: Error de timeout de lectura");
    // Reintento único tras pequeña espera
    delay(500);
    http.end();
    http.begin(BACKEND_URL);
    http.setTimeout(15000);
    http.addHeader("Content-Type", "application/json");
    http.addHeader("X-Ingest-Token", IOT_INGEST_TOKEN);
    httpResponseCode = http.POST(jsonString);
    Serial.print("Reintento - Código de respuesta: ");
    Serial.println(httpResponseCode);
  }
  
  if (httpResponseCode > 0) {
    String response = http.getString();
    Serial.println("Respuesta del servidor:");
    Serial.println(response);
    
    if (httpResponseCode == 200) {
      Serial.println("✓ Datos enviados exitosamente");
      http.end();
      return true;
    } else {
      Serial.print("Error del servidor - Código: ");
      Serial.println(httpResponseCode);
    }
  } else {
    Serial.print("Error HTTP: ");
    Serial.println(httpResponseCode);
  }
  
  http.end();
  return false;
}

// ========================================
// SETUP Y LOOP
// ========================================

void setup() {
  Serial.begin(115200);
  delay(1000);
  
  Serial.println("=== ESP32 IoT Sensor ===");
  Serial.println("Iniciando sistema...");
  
  // Configurar pines
  pinMode(LED_PIN, OUTPUT);
  pinMode(ERROR_LED_PIN, OUTPUT);
  pinMode(WATER_PIN, INPUT);
  pinMode(THERM_PIN, INPUT);
  
  // LEDs iniciales
  digitalWrite(LED_PIN, LOW);
  digitalWrite(ERROR_LED_PIN, LOW);
  
  // Configurar ADC
  analogReadResolution(ADC_BITS);
  // Asegurar escala de 0-3.3V en ESP32
  analogSetPinAttenuation(THERM_PIN, ADC_11db);
  analogSetPinAttenuation(WATER_PIN, ADC_11db);
  
  // Conectar WiFi
  wifiConnected = connectWiFi();
  
  Serial.println("Sistema iniciado");
  Serial.println("Device ID: " + String(DEVICE_ID));
  Serial.println("Backend URL: " + String(BACKEND_URL));
}

void loop() {
  unsigned long currentTime = millis();
  
  // Verificar conexión WiFi
  if (currentTime - lastWiFiRetry > WIFI_RETRY_INTERVAL) {
    wifiConnected = connectWiFi();
    lastWiFiRetry = currentTime;
  }
  
  // Enviar datos cada SEND_INTERVAL
  if (currentTime - lastSendTime > SEND_INTERVAL) {
    if (wifiConnected) {
      // Leer sensores
      float temperature = readTemperature();
      float humidity = readSoilHumidity();
      
      Serial.println("=== Lectura de Sensores ===");
      Serial.print("Temperatura: ");
      if (isnan(temperature)) {
        Serial.println("INVALIDA");
      } else {
        Serial.print(temperature);
        Serial.println(" °C");
      }
      Serial.print("Humedad del suelo: ");
      Serial.print(humidity);
      Serial.println(" %");
      
      // No enviar lecturas inválidas para evitar falsos positivos
      if (isnan(temperature)) {
        Serial.println("Lectura de temperatura inválida, se omite envío");
      } else if (sendSensorData(temperature, humidity)) {
        digitalWrite(LED_PIN, HIGH);
        delay(100);
        digitalWrite(LED_PIN, LOW);
      } else {
        digitalWrite(ERROR_LED_PIN, HIGH);
        delay(100);
        digitalWrite(ERROR_LED_PIN, LOW);
      }
    } else {
      Serial.println("WiFi no conectado, saltando envío");
    }
    
    lastSendTime = currentTime;
  }
  
  delay(1000); // Pequeña pausa
}
