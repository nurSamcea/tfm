package com.example.iotapp.models;

import java.util.Date;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String farmerId;
    private String supermarketId;
    private Date harvestDate;
    private Date expirationDate;
    private String origin;
    private String category;
    private String imageUrl;
    private boolean isOrganic;
    private String certification;

    public Product(String id, String name, String description, double price, int stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getFarmerId() { return farmerId; }
    public String getSupermarketId() { return supermarketId; }
    public Date getHarvestDate() { return harvestDate; }
    public Date getExpirationDate() { return expirationDate; }
    public String getOrigin() { return origin; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public boolean isOrganic() { return isOrganic; }
    public String getCertification() { return certification; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public void setSupermarketId(String supermarketId) { this.supermarketId = supermarketId; }
    public void setHarvestDate(Date harvestDate) { this.harvestDate = harvestDate; }
    public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }
    public void setOrigin(String origin) { this.origin = origin; }
    public void setCategory(String category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setOrganic(boolean organic) { isOrganic = organic; }
    public void setCertification(String certification) { this.certification = certification; }
} 