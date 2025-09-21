package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SensorZone {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("location_lat")
    private Double locationLat;
    
    @SerializedName("location_lon")
    private Double locationLon;
    
    @SerializedName("location_description")
    private String locationDescription;
    
    @SerializedName("farmer_id")
    private int farmerId;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    @SerializedName("sensors")
    private List<Sensor> sensors;
    
    // Constructores
    public SensorZone() {}
    
    public SensorZone(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getLocationLat() { return locationLat; }
    public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
    
    public Double getLocationLon() { return locationLon; }
    public void setLocationLon(Double locationLon) { this.locationLon = locationLon; }
    
    public String getLocationDescription() { return locationDescription; }
    public void setLocationDescription(String locationDescription) { this.locationDescription = locationDescription; }
    
    public int getFarmerId() { return farmerId; }
    public void setFarmerId(int farmerId) { this.farmerId = farmerId; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public List<Sensor> getSensors() { return sensors; }
    public void setSensors(List<Sensor> sensors) { this.sensors = sensors; }
    
    // Métodos de utilidad
    public int getSensorCount() {
        return sensors != null ? sensors.size() : 0;
    }
    
    public int getOnlineSensorCount() {
        if (sensors == null) return 0;
        int count = 0;
        for (Sensor sensor : sensors) {
            if (sensor.isOnline()) {
                count++;
            }
        }
        return count;
    }
    
    public int getOfflineSensorCount() {
        return getSensorCount() - getOnlineSensorCount();
    }
    
    public String getLocationDisplay() {
        if (locationDescription != null && !locationDescription.isEmpty()) {
            return locationDescription;
        } else if (locationLat != null && locationLon != null) {
            return String.format("%.4f, %.4f", locationLat, locationLon);
        } else {
            return "Ubicación no especificada";
        }
    }
    
    public boolean hasLocation() {
        return locationLat != null && locationLon != null;
    }
}
