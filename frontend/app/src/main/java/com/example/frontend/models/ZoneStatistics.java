package com.example.frontend.models;

import java.util.List;

public class ZoneStatistics {
    public int zone_id;
    public String zone_name;
    public String description;
    public Location location;
    public List<SensorStatistic> sensors;
    public ZoneStats statistics;
    
    public static class Location {
        public double lat;
        public double lon;
    }
    
    public static class SensorStatistic {
        public int sensor_id;
        public String device_id;
        public String name;
        public String type;
        public String status;
        public String last_seen;
        public SensorStats statistics;
        public SensorThresholds thresholds;
        
        public static class SensorStats {
            public int count;
            public double min;
            public double max;
            public double avg;
            public Double latest;
        }
        
        public static class SensorThresholds {
            public Double min;
            public Double max;
        }
    }
    
    public static class ZoneStats {
        public int total_sensors;
        public int online_sensors;
        public int offline_sensors;
        public int active_alerts;
        public int period_hours;
        public int total_readings;
    }
}
