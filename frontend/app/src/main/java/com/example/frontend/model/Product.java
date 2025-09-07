package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {
    private String id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private int stock;
    private SustainabilityMetrics sustainabilityMetrics;
    private String blockchainId;
    private String description;
    private Integer farmerId;
    private Date harvestDate;
    private boolean sustainable;
    private Double distance_km;
    private Double score;
    @SerializedName("is_hidden")
    private boolean isHidden;
    
    // Campos adicionales para compatibilidad con el backend
    @SerializedName("stock_available")
    private Double stockAvailable;
    @SerializedName("is_eco")
    private Boolean isEco;
    @SerializedName("expiration_date")
    private Date expirationDate;
    @SerializedName("provider_id")
    private Integer providerId;
    @SerializedName("image_url")
    private String imageUrl;
    
    // Campo para almacenar el tipo de vendedor (farmer/supermarket)
    private String sellerType;


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
        this.isHidden = false;
    }

    public Product(String id, String name, String category, double price, int quantity, int stock) {
        this(id, name, category, price, quantity, stock, null, null, "", 0, new Date());
    }

    public Product() {
        // necesario para Retrofit, Gson o inicialización manual
        this.isHidden = false;
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

    public void setHarvestDate(Date harvestDate) {
        this.harvestDate = harvestDate;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(Integer farmerId) {
        this.farmerId = farmerId;
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

    public Double getDistance_km() {
        return distance_km;
    }

    public void setDistance_km(Double distance_km) {
        this.distance_km = distance_km;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    // Getters y setters para compatibilidad con backend
    public Double getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(Double stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public Boolean getIsEco() {
        return isEco;
    }

    public void setIsEco(Boolean isEco) {
        this.isEco = isEco;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSellerType() {
        return sellerType;
    }

    public void setSellerType(String sellerType) {
        this.sellerType = sellerType;
    }
}
