package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class ProductUpdate {
    private String name;
    private String description;
    private Double price;
    private String currency;
    private String unit;
    private String category;
    @SerializedName("stock_available")
    private Double stockAvailable;
    @SerializedName("expiration_date")
    private Date expirationDate;
    
    // Campo adicional para enviar la fecha como string
    @SerializedName("expiration_date_string")
    private String expirationDateString;
    @SerializedName("is_eco")
    private Boolean isEco;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("is_hidden")
    private Boolean isHidden;

    public ProductUpdate() {
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(Double stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getExpirationDateString() {
        return expirationDateString;
    }

    public void setExpirationDateString(String expirationDateString) {
        this.expirationDateString = expirationDateString;
    }

    public Boolean getIsEco() {
        return isEco;
    }

    public void setIsEco(Boolean isEco) {
        this.isEco = isEco;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }
}
