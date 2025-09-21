package com.example.frontend.model;

import java.util.List;

public class SensorHistory {
    public int sensor_id;
    public String sensor_name;
    public String device_id;
    public String type;
    public String zone_name;
    public SensorThresholds thresholds;
    public List<SensorReading> readings;
    public HistoryStats statistics;
    
    public static class SensorThresholds {
        public Double min;
        public Double max;
    }
    
    public static class SensorReading {
        public int id;
        public Double value;
        public String unit;
        public double quality;
        public String created_at;
        public Object extra_data;
    }
    
    public static class HistoryStats {
        public int total_readings;
        public int period_hours;
        public Double latest_value;
    }
}
