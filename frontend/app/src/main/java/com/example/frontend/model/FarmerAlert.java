package com.example.frontend.model;

public class FarmerAlert {
    public int id;
    public int sensor_id;
    public String sensor_name;
    public String zone_name;
    public String alert_type;
    public String status;
    public String severity;
    public String title;
    public String message;
    public Double threshold_value;
    public Double actual_value;
    public String unit;
    public String created_at;
    public String acknowledged_at;
    public String resolved_at;
}
