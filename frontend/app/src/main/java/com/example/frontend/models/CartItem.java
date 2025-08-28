package com.example.frontend.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String unit;
    private String imageUrl;
    private boolean isEco;
    private boolean isLocal;

    public CartItem() {
    }

    public CartItem(int id, String name, double price, int quantity, String unit) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
    }

    public CartItem(int id, String name, String description, double price, int quantity, String unit, String imageUrl, boolean isEco, boolean isLocal) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.unit = unit;
        this.imageUrl = imageUrl;
        this.isEco = isEco;
        this.isLocal = isLocal;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isEco() {
        return isEco;
    }

    public boolean isLocal() {
        return isLocal;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setEco(boolean eco) {
        isEco = eco;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    // Métodos de utilidad
    public double getTotalPrice() {
        return price * quantity;
    }

    public String getFormattedPrice() {
        return String.format("%.2f €", price);
    }

    public String getFormattedTotalPrice() {
        return String.format("%.2f €", getTotalPrice());
    }

    public String getFormattedQuantity() {
        return quantity + " " + unit;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return id == cartItem.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                '}';
    }
}
