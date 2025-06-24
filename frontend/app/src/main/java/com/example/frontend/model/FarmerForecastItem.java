package com.example.frontend.model;

public class FarmerForecastItem {
    private String name;
    private String quantity;
    private String date;

    public FarmerForecastItem(String name, String quantity, String date) {
        this.name = name;
        this.quantity = quantity;
        this.date = date;
    }

    public String getName() { return name; }
    public String getQuantity() { return quantity; }
    public String getDate() { return date; }
}
