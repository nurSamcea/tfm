package com.example.frontend.model;

public class FarmerProduct {

    private String name;
    private boolean available;
    private String stock;
    private String price;
    private String harvestDate;

    public FarmerProduct(String name, boolean available, String stock, String price, String harvestDate) {
        this.name = name;
        this.available = available;
        this.stock = stock;
        this.price = price;
        this.harvestDate = harvestDate;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getStock() {
        return stock;
    }

    public String getPrice() {
        return price;
    }

    public String getHarvestDate() {
        return harvestDate;
    }

    // Si necesitas setters también:
    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }
}
