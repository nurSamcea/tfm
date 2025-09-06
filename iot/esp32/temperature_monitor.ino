#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>
#include <DHT.h>

// ========================================
// CONFIGURACIÓN CENTRALIZADA
// ========================================
// Para cambiar la IP del backend, modifica únicamente el archivo .env en la raíz del proyecto
const char* BACKEND_IP = "192.168.68.116"; // Esta IP se lee desde .env
const char* BACKEND_PORT = "8000";
const char* BACKEND_PROTOCOL = "http";

// Configuración WiFi
const char* ssid = "TU_WIFI_SSID";
const char* password = "TU_WIFI_PASSWORD";

// Configuración del sensor DHT22
#define DHTPIN 4
#define DHTTYPE DHT22
DHT dht(DHTPIN, DHTTYPE);

// Configuración del backend (construida automáticamente)
String backendUrl = String(BACKEND_PROTOCOL) + "://" + String(BACKEND_IP) + ":" + String(BACKEND_PORT);
const char* sensorEndpoint = "/sensor_readings/";

// Configuración del producto (cambiar según el producto que estés monitoreando)
const int PRODUCT_ID = 1; // ID del producto en la base de datos
const char* DEVICE_ID = "ESP32_FARM_001";

// Variables globales
float lastTemperature = 0;
float lastHumidity = 0;
unsigned long lastSendTime = 0;
const unsigned long SEND_INTERVAL = 30000; // Enviar cada 30 segundos

// LED para indicar estado
const int LED_PIN = 2;

void setup() {
  Serial.begin(115200);
  pinMode(LED_PIN, OUTPUT);
  
  // Inicializar sensor DHT
  dht.begin();
  
  // Conectar WiFi
  connectToWiFi();
  
  Serial.println("Sistema de monitoreo de temperatura iniciado");
}

void loop() {
  // Verificar conexión WiFi
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi desconectado. Reconectando...");
    connectToWiFi();
    return;
  }
  
  // Leer sensor cada 5 segundos
  static unsigned long lastReadTime = 0;
  if (millis() - lastReadTime >= 5000) {
    readAndSendSensorData();
    lastReadTime = millis();
  }
  
  // Enviar datos al backend cada 30 segundos
  if (millis() - lastSendTime >= SEND_INTERVAL) {
    sendDataToBackend();
    lastSendTime = millis();
  }
  
  delay(1000);
}

void connectToWiFi() {
  Serial.print("Conectando a WiFi: ");
  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  
  int attempts = 0;
  while (WiFi.status() != WL_CONNECTED && attempts < 20) {
    delay(500);
    Serial.print(".");
    digitalWrite(LED_PIN, !digitalRead(LED_PIN)); // Parpadear LED
    attempts++;
  }
  
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println();
    Serial.println("WiFi conectado!");
    Serial.print("IP: ");
    Serial.println(WiFi.localIP());
    digitalWrite(LED_PIN, HIGH); // LED fijo cuando está conectado
  } else {
    Serial.println();
    Serial.println("Error al conectar WiFi");
    digitalWrite(LED_PIN, LOW);
  }
}

void readAndSendSensorData() {
  // Leer temperatura y humedad
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();
  
  // Verificar si las lecturas son válidas
  if (isnan(temperature) || isnan(humidity)) {
    Serial.println("Error al leer el sensor DHT22");
    return;
  }
  
  // Solo enviar si hay cambios significativos o es la primera lectura
  if (abs(temperature - lastTemperature) > 0.5 || 
      abs(humidity - lastHumidity) > 2.0 || 
      lastTemperature == 0) {
    
    Serial.print("Temperatura: ");
    Serial.print(temperature);
    Serial.print("°C, Humedad: ");
    Serial.print(humidity);
    Serial.println("%");
    
    // Enviar inmediatamente si hay cambios significativos
    sendSensorData(temperature, humidity);
    
    lastTemperature = temperature;
    lastHumidity = humidity;
  }
}

void sendDataToBackend() {
  // Enviar datos actuales al backend
  if (lastTemperature != 0 && lastHumidity != 0) {
    sendSensorData(lastTemperature, lastHumidity);
  }
}

void sendSensorData(float temperature, float humidity) {
  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("WiFi no disponible para enviar datos");
    return;
  }
  
  HTTPClient http;
  String url = backendUrl + String(sensorEndpoint);
  
  http.begin(url);
  http.addHeader("Content-Type", "application/json");
  
  // Crear JSON con los datos del sensor
  DynamicJsonDocument doc(512);
  doc["product_id"] = PRODUCT_ID;
  doc["temperature"] = temperature;
  doc["humidity"] = humidity;
  doc["sensor_type"] = "temperature";
  doc["source_device"] = DEVICE_ID;
  
  String jsonString;
  serializeJson(doc, jsonString);
  
  Serial.print("Enviando datos: ");
  Serial.println(jsonString);
  
  int httpResponseCode = http.POST(jsonString);
  
  if (httpResponseCode > 0) {
    String response = http.getString();
    Serial.print("Respuesta del servidor: ");
    Serial.println(httpResponseCode);
    Serial.println(response);
    
    // Parpadear LED para indicar envío exitoso
    digitalWrite(LED_PIN, LOW);
    delay(100);
    digitalWrite(LED_PIN, HIGH);
  } else {
    Serial.print("Error al enviar datos: ");
    Serial.println(httpResponseCode);
    Serial.println(http.errorToString(httpResponseCode));
    
    // Parpadear rápido para indicar error
    for (int i = 0; i < 3; i++) {
      digitalWrite(LED_PIN, LOW);
      delay(200);
      digitalWrite(LED_PIN, HIGH);
      delay(200);
    }
  }
  
  http.end();
}

// Función para enviar alertas si los valores están fuera de rango
void checkAlerts(float temperature, float humidity) {
  // Alerta de temperatura alta (para productos refrigerados)
  if (temperature > 8.0) {
    Serial.println("ALERTA: Temperatura demasiado alta!");
    sendAlert("temperature_high", temperature);
  }
  
  // Alerta de temperatura baja (para productos congelados)
  if (temperature < -2.0) {
    Serial.println("ALERTA: Temperatura demasiado baja!");
    sendAlert("temperature_low", temperature);
  }
  
  // Alerta de humedad alta
  if (humidity > 85.0) {
    Serial.println("ALERTA: Humedad demasiado alta!");
    sendAlert("humidity_high", humidity);
  }
}

void sendAlert(String alertType, float value) {
  // Aquí podrías enviar una alerta específica al backend
  // o activar un buzzer local
  Serial.print("ALERTA: ");
  Serial.print(alertType);
  Serial.print(" - Valor: ");
  Serial.println(value);
}
