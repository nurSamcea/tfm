package com.example.frontend.services;

import com.example.frontend.model.Sensor;
import com.example.frontend.model.SensorReading;
import com.example.frontend.model.SensorAlert;
import com.example.frontend.model.SensorZone;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SensorApiService {
    
    // ===== SENSORES =====
    @GET("sensors/")
    Call<List<Sensor>> getSensors(
        @Query("zone_id") Integer zoneId,
        @Query("status") String status
    );
    
    // Endpoints públicos para modo demo
    @GET("sensors/public")
    Call<List<Sensor>> getSensorsPublic();
    
    @GET("sensors/{sensor_id}")
    Call<Sensor> getSensor(@Path("sensor_id") int sensorId);
    
    @GET("sensors/{sensor_id}/readings")
    Call<List<SensorReading>> getSensorReadings(
        @Path("sensor_id") int sensorId,
        @Query("limit") Integer limit,
        @Query("hours") Integer hours
    );
    
    @GET("sensors/{sensor_id}/status")
    Call<SensorStatusResponse> getSensorStatus(@Path("sensor_id") int sensorId);
    
    // ===== ZONAS =====
    @GET("sensor-zones/")
    Call<List<SensorZone>> getSensorZones();
    
    @GET("sensor-zones/{zone_id}")
    Call<SensorZone> getSensorZone(@Path("zone_id") int zoneId);
    
    // ===== LECTURAS =====
    @GET("sensor_readings/")
    Call<List<SensorReading>> getSensorReadings(
        @Query("sensor_id") Integer sensorId,
        @Query("product_id") Integer productId,
        @Query("date_from") String dateFrom,
        @Query("date_to") String dateTo,
        @Query("limit") Integer limit
    );
    
    // ===== ALERTAS =====
    @GET("sensor-alerts/")
    Call<List<SensorAlert>> getSensorAlerts(
        @Query("status") String status,
        @Query("severity") String severity,
        @Query("hours") Integer hours
    );
    
    @GET("sensor-alerts/active")
    Call<List<SensorAlert>> getActiveAlerts();
    
    @GET("sensor-alerts/public/active")
    Call<List<SensorAlert>> getActiveAlertsPublic();
    
    @PUT("sensor-alerts/{alert_id}/acknowledge")
    Call<SensorAlert> acknowledgeAlert(@Path("alert_id") int alertId);
    
    @PUT("sensor-alerts/{alert_id}/resolve")
    Call<SensorAlert> resolveAlert(@Path("alert_id") int alertId);
    
    @PUT("sensor-alerts/{alert_id}/dismiss")
    Call<SensorAlert> dismissAlert(@Path("alert_id") int alertId);
    
    @GET("sensor-alerts/stats")
    Call<AlertStatsResponse> getAlertStats(@Query("days") Integer days);
    
    // ===== ESTADÍSTICAS =====
    @GET("sensors/stats")
    Call<SensorStatsResponse> getSensorStats();
    
    // Clases de respuesta
    class SensorStatusResponse {
        public int sensor_id;
        public String device_id;
        public boolean is_online;
        public String last_seen;
        public String status;
    }
    
    class AlertStatsResponse {
        public int period_days;
        public int total_alerts;
        public int active_alerts;
        public int acknowledged_alerts;
        public int resolved_alerts;
        public java.util.Map<String, Integer> severity_breakdown;
        public java.util.Map<String, Integer> type_breakdown;
    }
    
    class SensorStatsResponse {
        public int total_sensors;
        public int online_sensors;
        public int offline_sensors;
        public int total_readings;
        public int total_alerts;
        public int active_alerts;
        public double avg_temperature;
        public double avg_humidity;
    }
}
