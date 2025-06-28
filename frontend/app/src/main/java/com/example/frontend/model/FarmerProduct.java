package com.example.frontend.model;

public class FarmerProduct {

    private String name;
    private boolean available;
    private String stock;
    private String price;
    private String harvestDate;
    private int imageResId;

    public FarmerProduct(String name, boolean isOrganic, String stock, String price, String harvestDate, int imageResId) {
        this.name = name;
        this.available = available;
        this.stock = stock;
        this.price = price;
        this.harvestDate = harvestDate;
        this.imageResId = imageResId;
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

    public int getImageResId() {
        return imageResId;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }
}
