package com.example.frontend.model;

import java.util.Date;

public class Product {
    private String id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private int stock;
    private SustainabilityMetrics sustainabilityMetrics;
    private String blockchainId;
    private String description;
    private int farmerId;
    private Date harvestDate;
    private boolean sustainable;


    public Product(String id, String name, String category, double price, int quantity, int stock,
                   SustainabilityMetrics sustainabilityMetrics, String blockchainId,
                   String description, int farmerId, Date harvestDate) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.stock = stock;
        this.blockchainId = blockchainId;
        this.farmerId = farmerId;
        this.harvestDate = harvestDate;
        this.sustainabilityMetrics = sustainabilityMetrics;
    }
    public Product(String id, String name, String category, double price, int quantity, int stock) {
        this(id, name, category, price, quantity, stock, null, null, "", 0, new Date());
    }

    public Product() {
        // necesario para Retrofit, Gson o inicialización manual
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public SustainabilityMetrics getSustainabilityMetrics() {
        return sustainabilityMetrics;
    }

    public void setSustainabilityMetrics(SustainabilityMetrics sustainabilityMetrics) {
        this.sustainabilityMetrics = sustainabilityMetrics;
    }

    public String getBlockchainId() {
        return blockchainId;
    }

    public void setBlockchainId(String blockchainId) {
        this.blockchainId = blockchainId;
    }

    public String getDescription() {
        return description;
    }

    public int getFarmerId() {
        return farmerId;
    }

    public Date getHarvestDate() {
        return harvestDate;
    }

    public boolean isSustainable() {
        return sustainable;
    }

    public void setSustainable(boolean sustainable) {
        this.sustainable = sustainable;
    }
}
