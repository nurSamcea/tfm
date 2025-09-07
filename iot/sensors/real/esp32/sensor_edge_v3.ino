/*
 * ESP32 IoT Sensor con Edge Computing
 * Versión 3.0 - Procesamiento local de datos
 * 
 * Características:
 * - Procesamiento edge de datos de sensores
 * - Detección de anomalías local
 * - Filtrado y agregación de datos
 * - Almacenamiento local (SPIFFS)
 * - Comunicación optimizada con backend
 * - Configuración OTA
 */

#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <DHT.h>
#include <SPIFFS.h>
#include <Update.h>
#include <WebServer.h>
#include <Preferences.h>

// ========================================
// CONFIGURACIÓN CENTRALIZADA
// ========================================
const char* BACKEND_IP = "192.168.68.116"; // IP del backend
const char* BACKEND_PORT = "8000";
const char* BACKEND_PROTOCOL = "http";

// Configuración WiFi
const char* ssid = "TU_WIFI_SSID";
const char* password = "TU_WIFI_PASSWORD";

// Configuración del sensor DHT22
#define DHTPIN 4
#define DHTTYPE DHT22
DHT dht(DHTPIN, DHTTYPE);

// Configuración del dispositivo
const char* DEVICE_ID = "ESP32_FARM_001";
const int SENSOR_ID = 1; // ID del sensor en la base de datos
const int ZONE_ID = 1;   // ID de la zona en la base de datos

// Configuración de intervalos
const unsigned long SENSOR_READ_INTERVAL = 5000;   // Leer sensor cada 5 segundos
const unsigned long SEND_INTERVAL = 30000;         // Enviar datos cada 30 segundos
const unsigned long ANOMALY_CHECK_INTERVAL = 10000; // Verificar anomalías cada 10 segundos

// Configuración de umbrales (se pueden configurar via OTA)
float TEMP_MIN_THRESHOLD = 15.0;
float TEMP_MAX_THRESHOLD = 35.0;
float HUMIDITY_MIN_THRESHOLD = 30.0;
float HUMIDITY_MAX_THRESHOLD = 80.0;

// Variables globales
float lastTemperature = 0;
float lastHumidity = 0;
unsigned long lastSendTime = 0;
unsigned long lastAnomalyCheck = 0;
unsigned long lastSensorRead = 0;

// Buffer de datos para procesamiento edge
struct SensorData {
  float temperature;
  float humidity;
  unsigned long timestamp;
  bool isAnomaly;
};

SensorData dataBuffer[10]; // Buffer circular de 10 lecturas
int bufferIndex = 0;
int bufferCount = 0;

// Estadísticas de procesamiento
struct ProcessingStats {
  int totalReadings;
  int anomaliesDetected;
  int dataSent;
  int errors;
  float avgTemperature;
  float avgHumidity;
};

ProcessingStats stats = {0, 0, 0, 0, 0.0, 0.0};

// LED para indicar estado
const int LED_PIN = 2;
const int ERROR_LED_PIN = 5;

// Servidor web para configuración OTA
WebServer server(80);
Preferences preferences;

// URLs del backend
String backendUrl = String(BACKEND_PROTOCOL) + "://" + String(BACKEND_IP) + ":" + String(BACKEND_PORT);
String sensorEndpoint = "/sensor_readings/";
String configEndpoint = "/sensors/" + String(SENSOR_ID);

void setup() {
  Serial.begin(115200);
  pinMode(LED_PIN, OUTPUT);
  pinMode(ERROR_LED_PIN, OUTPUT);
  
  // Inicializar SPIFFS
  if (!SPIFFS.begin(true)) {
    Serial.println("Error al montar SPIFFS");
    digitalWrite(ERROR_LED_PIN, HIGH);
  }
  
  // Cargar configuración guardada
  loadConfiguration();
  
  // Inicializar sensor DHT
  dht.begin();
  
  // Conectar WiFi
  connectToWiFi();
  
  // Configurar servidor web para OTA
  setupWebServer();
  
  Serial.println("Sistema IoT con Edge Computing iniciado");
  Serial.println("Device ID: " + String(DEVICE_ID));
  Serial.println("Sensor ID: " + String(SENSOR_ID));
}

void loop() {
  // Manejar peticiones web
  server.handleClient();
  
  // Verificar conexión WiFi
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi desconectado. Reconectando...");
    connectToWiFi();
    return;
  }
  
  // Leer sensor
  if (millis() - lastSensorRead >= SENSOR_READ_INTERVAL) {
    readSensorData();
    lastSensorRead = millis();
  }
  
  // Verificar anomalías
  if (millis() - lastAnomalyCheck >= ANOMALY_CHECK_INTERVAL) {
    checkForAnomalies();
    lastAnomalyCheck = millis();
  }
  
  // Enviar datos procesados
  if (millis() - lastSendTime >= SEND_INTERVAL) {
    sendProcessedData();
    lastSendTime = millis();
  }
  
  delay(100);
}

void readSensorData() {
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();
  
  if (isnan(temperature) || isnan(humidity)) {
    Serial.println("Error al leer sensor DHT");
    stats.errors++;
    digitalWrite(ERROR_LED_PIN, HIGH);
    return;
  }
  
  digitalWrite(ERROR_LED_PIN, LOW);
  
  // Almacenar en buffer
  dataBuffer[bufferIndex].temperature = temperature;
  dataBuffer[bufferIndex].humidity = humidity;
  dataBuffer[bufferIndex].timestamp = millis();
  dataBuffer[bufferIndex].isAnomaly = false;
  
  bufferIndex = (bufferIndex + 1) % 10;
  if (bufferCount < 10) bufferCount++;
  
  stats.totalReadings++;
  
  // Actualizar promedios
  updateAverages();
  
  Serial.printf("Lectura: T=%.2f°C, H=%.2f%%\n", temperature, humidity);
}

void checkForAnomalies() {
  if (bufferCount < 3) return; // Necesitamos al menos 3 lecturas
  
  float currentTemp = dataBuffer[(bufferIndex - 1 + 10) % 10].temperature;
  float currentHumidity = dataBuffer[(bufferIndex - 1 + 10) % 10].humidity;
  
  // Verificar umbrales
  bool tempAnomaly = (currentTemp < TEMP_MIN_THRESHOLD || currentTemp > TEMP_MAX_THRESHOLD);
  bool humidityAnomaly = (currentHumidity < HUMIDITY_MIN_THRESHOLD || currentHumidity > HUMIDITY_MAX_THRESHOLD);
  
  // Verificar cambios bruscos (más de 5°C o 20% en 5 segundos)
  float prevTemp = dataBuffer[(bufferIndex - 2 + 10) % 10].temperature;
  float prevHumidity = dataBuffer[(bufferIndex - 2 + 10) % 10].humidity;
  
  bool tempSpike = abs(currentTemp - prevTemp) > 5.0;
  bool humiditySpike = abs(currentHumidity - prevHumidity) > 20.0;
  
  bool isAnomaly = tempAnomaly || humidityAnomaly || tempSpike || humiditySpike;
  
  if (isAnomaly) {
    dataBuffer[(bufferIndex - 1 + 10) % 10].isAnomaly = true;
    stats.anomaliesDetected++;
    
    Serial.println("¡ANOMALÍA DETECTADA!");
    if (tempAnomaly) Serial.printf("Temperatura fuera de rango: %.2f°C\n", currentTemp);
    if (humidityAnomaly) Serial.printf("Humedad fuera de rango: %.2f%%\n", currentHumidity);
    if (tempSpike) Serial.printf("Cambio brusco de temperatura: %.2f°C\n", currentTemp - prevTemp);
    if (humiditySpike) Serial.printf("Cambio brusco de humedad: %.2f%%\n", currentHumidity - prevHumidity);
  }
}

void sendProcessedData() {
  if (bufferCount == 0) return;
  
  // Calcular promedios y estadísticas
  float avgTemp = calculateAverageTemperature();
  float avgHumidity = calculateAverageHumidity();
  int anomalyCount = countAnomalies();
  
  // Crear JSON para enviar
  DynamicJsonDocument doc(1024);
  doc["sensor_id"] = SENSOR_ID;
  doc["temperature"] = avgTemp;
  doc["humidity"] = avgHumidity;
  doc["reading_quality"] = calculateReadingQuality();
  doc["is_processed"] = true;
  
  // Metadatos de procesamiento edge
  JsonObject metadata = doc.createNestedObject("extra_data");
  metadata["device_id"] = DEVICE_ID;
  metadata["zone_id"] = ZONE_ID;
  metadata["readings_count"] = bufferCount;
  metadata["anomalies_detected"] = anomalyCount;
  metadata["processing_stats"] = stats.totalReadings;
  metadata["firmware_version"] = "3.0";
  metadata["uptime"] = millis();
  
  String jsonString;
  serializeJson(doc, jsonString);
  
  // Enviar al backend
  HTTPClient http;
  http.begin(backendUrl + sensorEndpoint);
  http.addHeader("Content-Type", "application/json");
  
  int httpResponseCode = http.POST(jsonString);
  
  if (httpResponseCode > 0) {
    String response = http.getString();
    Serial.println("Datos enviados correctamente: " + String(httpResponseCode));
    stats.dataSent++;
    digitalWrite(LED_PIN, HIGH);
    delay(100);
    digitalWrite(LED_PIN, LOW);
  } else {
    Serial.println("Error al enviar datos: " + String(httpResponseCode));
    stats.errors++;
    digitalWrite(ERROR_LED_PIN, HIGH);
  }
  
  http.end();
  
  // Guardar estadísticas
  saveStatistics();
}

float calculateAverageTemperature() {
  float sum = 0;
  for (int i = 0; i < bufferCount; i++) {
    sum += dataBuffer[i].temperature;
  }
  return sum / bufferCount;
}

float calculateAverageHumidity() {
  float sum = 0;
  for (int i = 0; i < bufferCount; i++) {
    sum += dataBuffer[i].humidity;
  }
  return sum / bufferCount;
}

int countAnomalies() {
  int count = 0;
  for (int i = 0; i < bufferCount; i++) {
    if (dataBuffer[i].isAnomaly) count++;
  }
  return count;
}

float calculateReadingQuality() {
  // Calcular calidad basada en estabilidad de las lecturas
  if (bufferCount < 2) return 1.0;
  
  float tempVariance = 0;
  float humidityVariance = 0;
  
  float avgTemp = calculateAverageTemperature();
  float avgHumidity = calculateAverageHumidity();
  
  for (int i = 0; i < bufferCount; i++) {
    tempVariance += pow(dataBuffer[i].temperature - avgTemp, 2);
    humidityVariance += pow(dataBuffer[i].humidity - avgHumidity, 2);
  }
  
  tempVariance /= bufferCount;
  humidityVariance /= bufferCount;
  
  // Calidad basada en varianza (menor varianza = mayor calidad)
  float quality = 1.0 - (tempVariance + humidityVariance) / 100.0;
  return max(0.0, min(1.0, quality));
}

void updateAverages() {
  stats.avgTemperature = calculateAverageTemperature();
  stats.avgHumidity = calculateAverageHumidity();
}

void connectToWiFi() {
  Serial.print("Conectando a WiFi: ");
  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 20) {
    delay(500);
    Serial.print(".");
    digitalWrite(LED_PIN, !digitalRead(LED_PIN));
    attempts++;
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println();
    Serial.println("WiFi conectado!");
    Serial.print("IP: ");
    Serial.println(WiFi.localIP());
    digitalWrite(LED_PIN, HIGH);
  } else {
    Serial.println();
    Serial.println("Error al conectar WiFi");
    digitalWrite(LED_PIN, LOW);
  }
}

void setupWebServer() {
  server.on("/", handleRoot);
  server.on("/config", handleConfig);
  server.on("/stats", handleStats);
  server.on("/update", HTTP_POST, handleUpdate);
  server.begin();
  Serial.println("Servidor web iniciado en http://" + WiFi.localIP().toString());
}

void handleRoot() {
  String html = "<html><body>";
  html += "<h1>ESP32 IoT Sensor - Edge Computing</h1>";
  html += "<p>Device ID: " + String(DEVICE_ID) + "</p>";
  html += "<p>Sensor ID: " + String(SENSOR_ID) + "</p>";
  html += "<p>Última temperatura: " + String(lastTemperature) + "°C</p>";
  html += "<p>Última humedad: " + String(lastHumidity) + "%</p>";
  html += "<p><a href='/config'>Configuración</a></p>";
  html += "<p><a href='/stats'>Estadísticas</a></p>";
  html += "</body></html>";
  
  server.send(200, "text/html", html);
}

void handleConfig() {
  String html = "<html><body>";
  html += "<h1>Configuración del Sensor</h1>";
  html += "<form method='POST' action='/config'>";
  html += "<p>Temperatura mínima: <input type='number' name='temp_min' value='" + String(TEMP_MIN_THRESHOLD) + "' step='0.1'></p>";
  html += "<p>Temperatura máxima: <input type='number' name='temp_max' value='" + String(TEMP_MAX_THRESHOLD) + "' step='0.1'></p>";
  html += "<p>Humedad mínima: <input type='number' name='hum_min' value='" + String(HUMIDITY_MIN_THRESHOLD) + "' step='0.1'></p>";
  html += "<p>Humedad máxima: <input type='number' name='hum_max' value='" + String(HUMIDITY_MAX_THRESHOLD) + "' step='0.1'></p>";
  html += "<p><input type='submit' value='Guardar'></p>";
  html += "</form>";
  html += "<p><a href='/'>Volver</a></p>";
  html += "</body></html>";
  
  server.send(200, "text/html", html);
}

void handleStats() {
  String html = "<html><body>";
  html += "<h1>Estadísticas del Sensor</h1>";
  html += "<p>Total lecturas: " + String(stats.totalReadings) + "</p>";
  html += "<p>Anomalías detectadas: " + String(stats.anomaliesDetected) + "</p>";
  html += "<p>Datos enviados: " + String(stats.dataSent) + "</p>";
  html += "<p>Errores: " + String(stats.errors) + "</p>";
  html += "<p>Temperatura promedio: " + String(stats.avgTemperature) + "°C</p>";
  html += "<p>Humedad promedio: " + String(stats.avgHumidity) + "%</p>";
  html += "<p>Uptime: " + String(millis() / 1000) + " segundos</p>";
  html += "<p><a href='/'>Volver</a></p>";
  html += "</body></html>";
  
  server.send(200, "text/html", html);
}

void loadConfiguration() {
  preferences.begin("sensor_config", false);
  TEMP_MIN_THRESHOLD = preferences.getFloat("temp_min", 15.0);
  TEMP_MAX_THRESHOLD = preferences.getFloat("temp_max", 35.0);
  HUMIDITY_MIN_THRESHOLD = preferences.getFloat("hum_min", 30.0);
  HUMIDITY_MAX_THRESHOLD = preferences.getFloat("hum_max", 80.0);
  preferences.end();
}

void saveConfiguration() {
  preferences.begin("sensor_config", false);
  preferences.putFloat("temp_min", TEMP_MIN_THRESHOLD);
  preferences.putFloat("temp_max", TEMP_MAX_THRESHOLD);
  preferences.putFloat("hum_min", HUMIDITY_MIN_THRESHOLD);
  preferences.putFloat("hum_max", HUMIDITY_MAX_THRESHOLD);
  preferences.end();
}

void saveStatistics() {
  preferences.begin("sensor_stats", false);
  preferences.putInt("total_readings", stats.totalReadings);
  preferences.putInt("anomalies", stats.anomaliesDetected);
  preferences.putInt("data_sent", stats.dataSent);
  preferences.putInt("errors", stats.errors);
  preferences.end();
}
