package com.example.frontend.models;

import java.util.List;

public class FarmerDashboard {
    public int farmer_id;
    public String farmer_name;
    public int total_zones;
    public int total_sensors;
    public int online_sensors;
    public int offline_sensors;
    public List<ZoneSummary> zones;
    public DashboardSummary summary;
    
    public static class ZoneSummary {
        public int zone_id;
        public String zone_name;
        public String description;
        public int total_sensors;
        public int online_sensors;
        public int offline_sensors;
        public int active_alerts;
        public double avg_temperature;
        public double avg_humidity;
        public double avg_soil_moisture;
        public Location location;
        
        public static class Location {
            public double lat;
            public double lon;
        }
    }
    
    public static class DashboardSummary {
        public double avg_temperature;
        public double avg_humidity;
        public double avg_soil_moisture;
        public int active_alerts;
    }
}
