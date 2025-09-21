package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class SensorAlert {
    @SerializedName("id")
    private int id;
    
    @SerializedName("sensor_id")
    private int sensorId;
    
    @SerializedName("alert_type")
    private String alertType;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("severity")
    private String severity;
    
    @SerializedName("threshold_value")
    private Double thresholdValue;
    
    @SerializedName("actual_value")
    private Double actualValue;
    
    @SerializedName("unit")
    private String unit;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("acknowledged_at")
    private String acknowledgedAt;
    
    @SerializedName("resolved_at")
    private String resolvedAt;
    
    @SerializedName("acknowledged_by")
    private Integer acknowledgedBy;
    
    @SerializedName("extra_data")
    private Map<String, Object> extraData;
    
    // Constructores
    public SensorAlert() {}
    
    public SensorAlert(int sensorId, String alertType, String title, String message, String severity) {
        this.sensorId = sensorId;
        this.alertType = alertType;
        this.title = title;
        this.message = message;
        this.severity = severity;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getSensorId() { return sensorId; }
    public void setSensorId(int sensorId) { this.sensorId = sensorId; }
    
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public Double getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(Double thresholdValue) { this.thresholdValue = thresholdValue; }
    
    public Double getActualValue() { return actualValue; }
    public void setActualValue(Double actualValue) { this.actualValue = actualValue; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(String acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
    
    public String getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(String resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public Integer getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(Integer acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }
    
    public Map<String, Object> getExtraData() { return extraData; }
    public void setExtraData(Map<String, Object> extraData) { this.extraData = extraData; }
    
    // Métodos de utilidad
    public boolean isActive() {
        return "active".equals(status);
    }
    
    public boolean isAcknowledged() {
        return "acknowledged".equals(status);
    }
    
    public boolean isResolved() {
        return "resolved".equals(status);
    }
    
    public boolean isDismissed() {
        return "dismissed".equals(status);
    }
    
    public int getSeverityColor() {
        switch (severity.toLowerCase()) {
            case "critical":
                return android.graphics.Color.RED;
            case "high":
                return android.graphics.Color.parseColor("#FF6B35");
            case "medium":
                return android.graphics.Color.parseColor("#FFA500");
            case "low":
                return android.graphics.Color.parseColor("#FFD700");
            default:
                return android.graphics.Color.GRAY;
        }
    }
    
    public int getStatusColor() {
        if (isActive()) {
            return android.graphics.Color.RED;
        } else if (isAcknowledged()) {
            return android.graphics.Color.parseColor("#FFA500");
        } else if (isResolved()) {
            return android.graphics.Color.GREEN;
        } else {
            return android.graphics.Color.GRAY;
        }
    }
    
    public String getStatusDisplay() {
        switch (status.toLowerCase()) {
            case "active":
                return "Activa";
            case "acknowledged":
                return "Reconocida";
            case "resolved":
                return "Resuelta";
            case "dismissed":
                return "Descartada";
            default:
                return status;
        }
    }
    
    public String getSeverityDisplay() {
        switch (severity.toLowerCase()) {
            case "critical":
                return "Crítica";
            case "high":
                return "Alta";
            case "medium":
                return "Media";
            case "low":
                return "Baja";
            default:
                return severity;
        }
    }
    
    public String getAlertTypeDisplay() {
        switch (alertType.toLowerCase()) {
            case "temperature_high":
                return "Temperatura Alta";
            case "temperature_low":
                return "Temperatura Baja";
            case "humidity_high":
                return "Humedad Alta";
            case "humidity_low":
                return "Humedad Baja";
            case "sensor_offline":
                return "Sensor Desconectado";
            case "sensor_error":
                return "Error del Sensor";
            case "threshold_exceeded":
                return "Umbral Excedido";
            default:
                return alertType;
        }
    }
    
    public String getTimestampDisplay() {
        if (createdAt == null) return "N/A";
        
        try {
            java.time.Instant instant = java.time.Instant.parse(createdAt);
            java.time.LocalDateTime localDateTime = java.time.LocalDateTime.ofInstant(
                instant, java.time.ZoneId.systemDefault()
            );
            return localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        } catch (Exception e) {
            return "N/A";
        }
    }
    
    public String getValueDisplay() {
        if (actualValue != null && unit != null) {
            return String.format("%.1f %s", actualValue, unit);
        }
        return "N/A";
    }
}
