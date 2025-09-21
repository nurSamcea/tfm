package com.example.frontend.services;

import com.example.frontend.model.FarmerDashboard;
import com.example.frontend.model.ZoneStatistics;
import com.example.frontend.model.SensorHistory;
import com.example.frontend.model.FarmerAlert;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FarmerMetricsApiService {
    
    // Dashboard principal del farmer
    @GET("farmer-metrics/dashboard/{farmer_id}")
    Call<FarmerDashboard> getFarmerDashboard(@Path("farmer_id") int farmerId);
    
    // Estadísticas de una zona específica
    @GET("farmer-metrics/zones/{zone_id}/statistics")
    Call<ZoneStatistics> getZoneStatistics(
        @Path("zone_id") int zoneId,
        @Query("hours") Integer hours
    );
    
    // Historial de un sensor específico
    @GET("farmer-metrics/sensors/{sensor_id}/history")
    Call<SensorHistory> getSensorHistory(
        @Path("sensor_id") int sensorId,
        @Query("hours") Integer hours,
        @Query("limit") Integer limit
    );
    
    // Alertas del farmer
    @GET("farmer-metrics/alerts")
    Call<FarmerAlertsResponse> getFarmerAlerts(
        @Query("status") String status,
        @Query("severity") String severity,
        @Query("hours") Integer hours
    );
    
    // Clases de respuesta
    class FarmerAlertsResponse {
        public List<FarmerAlert> alerts;
        public int total;
        public AlertFilters filters;
        
        public static class AlertFilters {
            public String status;
            public String severity;
            public int hours;
        }
    }
}
