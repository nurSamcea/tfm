package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class SensorReading {
    @SerializedName("id")
    private int id;
    
    @SerializedName("sensor_id")
    private int sensorId;
    
    @SerializedName("product_id")
    private Integer productId;
    
    @SerializedName("temperature")
    private Double temperature;
    
    @SerializedName("humidity")
    private Double humidity;
    
    @SerializedName("gas_level")
    private Double gasLevel;
    
    @SerializedName("light_level")
    private Double lightLevel;
    
    @SerializedName("shock_detected")
    private Boolean shockDetected;
    
    @SerializedName("soil_moisture")
    private Double soilMoisture;
    
    @SerializedName("ph_level")
    private Double phLevel;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("source_device")
    private String sourceDevice;
    
    @SerializedName("reading_quality")
    private Double readingQuality;
    
    @SerializedName("is_processed")
    private Boolean isProcessed;
    
    @SerializedName("extra_data")
    private Map<String, Object> extraData;
    
    // Constructores
    public SensorReading() {}
    
    public SensorReading(int sensorId, Double temperature, Double humidity) {
        this.sensorId = sensorId;
        this.temperature = temperature;
        this.humidity = humidity;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getSensorId() { return sensorId; }
    public void setSensorId(int sensorId) { this.sensorId = sensorId; }
    
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }
    
    public Double getGasLevel() { return gasLevel; }
    public void setGasLevel(Double gasLevel) { this.gasLevel = gasLevel; }
    
    public Double getLightLevel() { return lightLevel; }
    public void setLightLevel(Double lightLevel) { this.lightLevel = lightLevel; }
    
    public Boolean getShockDetected() { return shockDetected; }
    public void setShockDetected(Boolean shockDetected) { this.shockDetected = shockDetected; }
    
    public Double getSoilMoisture() { return soilMoisture; }
    public void setSoilMoisture(Double soilMoisture) { this.soilMoisture = soilMoisture; }
    
    public Double getPhLevel() { return phLevel; }
    public void setPhLevel(Double phLevel) { this.phLevel = phLevel; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getSourceDevice() { return sourceDevice; }
    public void setSourceDevice(String sourceDevice) { this.sourceDevice = sourceDevice; }
    
    public Double getReadingQuality() { return readingQuality; }
    public void setReadingQuality(Double readingQuality) { this.readingQuality = readingQuality; }
    
    public Boolean getIsProcessed() { return isProcessed; }
    public void setIsProcessed(Boolean isProcessed) { this.isProcessed = isProcessed; }
    
    public Map<String, Object> getExtraData() { return extraData; }
    public void setExtraData(Map<String, Object> extraData) { this.extraData = extraData; }
    
    // Métodos de utilidad
    public String getTemperatureDisplay() {
        if (temperature != null) {
            return String.format("%.1f°C", temperature);
        }
        return "N/A";
    }
    
    public String getHumidityDisplay() {
        if (humidity != null) {
            return String.format("%.1f%%", humidity);
        }
        return "N/A";
    }
    
    public String getQualityDisplay() {
        if (readingQuality != null) {
            return String.format("%.0f%%", readingQuality * 100);
        }
        return "N/A";
    }
    
    public int getQualityColor() {
        if (readingQuality == null) return android.graphics.Color.GRAY;
        
        if (readingQuality >= 0.9) {
            return android.graphics.Color.GREEN;
        } else if (readingQuality >= 0.7) {
            return android.graphics.Color.YELLOW;
        } else {
            return android.graphics.Color.RED;
        }
    }
    
    public boolean hasAnomaly() {
        // Verificar si hay datos que indiquen anomalías
        if (extraData != null) {
            Object anomalies = extraData.get("anomalies_detected");
            if (anomalies instanceof Number) {
                return ((Number) anomalies).intValue() > 0;
            }
        }
        return false;
    }
    
    public String getTimestampDisplay() {
        if (createdAt == null) return "N/A";
        
        try {
            java.time.Instant instant = java.time.Instant.parse(createdAt);
            java.time.LocalDateTime localDateTime = java.time.LocalDateTime.ofInstant(
                instant, java.time.ZoneId.systemDefault()
            );
            return localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception e) {
            return "N/A";
        }
    }
}
