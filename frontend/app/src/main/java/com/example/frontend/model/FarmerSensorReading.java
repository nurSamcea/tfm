package com.example.frontend.model;

public class FarmerSensorReading {
    private String name;
    private String value;
    private String extraInfo;

    public FarmerSensorReading(String name, String value, String extraInfo) {
        this.name = name;
        this.value = value;
        this.extraInfo = extraInfo;
    }

    public String getName() { return name; }
    public String getValue() { return value; }
    public String getExtraInfo() { return extraInfo; }
}
