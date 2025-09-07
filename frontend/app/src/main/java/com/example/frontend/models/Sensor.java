package com.example.frontend.models;

import com.google.gson.annotations.SerializedName;

public class Sensor {
    @SerializedName("id")
    private int id;
    
    @SerializedName("device_id")
    private String deviceId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("sensor_type")
    private String sensorType;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("zone_id")
    private Integer zoneId;
    
    @SerializedName("location_lat")
    private Double locationLat;
    
    @SerializedName("location_lon")
    private Double locationLon;
    
    @SerializedName("location_description")
    private String locationDescription;
    
    @SerializedName("min_threshold")
    private Double minThreshold;
    
    @SerializedName("max_threshold")
    private Double maxThreshold;
    
    @SerializedName("alert_enabled")
    private boolean alertEnabled;
    
    @SerializedName("reading_interval")
    private int readingInterval;
    
    @SerializedName("firmware_version")
    private String firmwareVersion;
    
    @SerializedName("last_seen")
    private String lastSeen;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Constructores
    public Sensor() {}
    
    public Sensor(int id, String deviceId, String name, String sensorType, String status) {
        this.id = id;
        this.deviceId = deviceId;
        this.name = name;
        this.sensorType = sensorType;
        this.status = status;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSensorType() { return sensorType; }
    public void setSensorType(String sensorType) { this.sensorType = sensorType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getZoneId() { return zoneId; }
    public void setZoneId(Integer zoneId) { this.zoneId = zoneId; }
    
    public Double getLocationLat() { return locationLat; }
    public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
    
    public Double getLocationLon() { return locationLon; }
    public void setLocationLon(Double locationLon) { this.locationLon = locationLon; }
    
    public String getLocationDescription() { return locationDescription; }
    public void setLocationDescription(String locationDescription) { this.locationDescription = locationDescription; }
    
    public Double getMinThreshold() { return minThreshold; }
    public void setMinThreshold(Double minThreshold) { this.minThreshold = minThreshold; }
    
    public Double getMaxThreshold() { return maxThreshold; }
    public void setMaxThreshold(Double maxThreshold) { this.maxThreshold = maxThreshold; }
    
    public boolean isAlertEnabled() { return alertEnabled; }
    public void setAlertEnabled(boolean alertEnabled) { this.alertEnabled = alertEnabled; }
    
    public int getReadingInterval() { return readingInterval; }
    public void setReadingInterval(int readingInterval) { this.readingInterval = readingInterval; }
    
    public String getFirmwareVersion() { return firmwareVersion; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
    
    public String getLastSeen() { return lastSeen; }
    public void setLastSeen(String lastSeen) { this.lastSeen = lastSeen; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    // Métodos de utilidad
    public boolean isOnline() {
        if (lastSeen == null) return false;
        // Considerar online si ha enviado datos en los últimos 5 minutos
        try {
            long lastSeenTime = java.time.Instant.parse(lastSeen).toEpochMilli();
            long currentTime = System.currentTimeMillis();
            return (currentTime - lastSeenTime) < 300000; // 5 minutos
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getStatusDisplay() {
        if (isOnline()) {
            return "Online";
        } else {
            return "Offline";
        }
    }
    
    public int getStatusColor() {
        if (isOnline()) {
            return android.graphics.Color.GREEN;
        } else {
            return android.graphics.Color.RED;
        }
    }
}
