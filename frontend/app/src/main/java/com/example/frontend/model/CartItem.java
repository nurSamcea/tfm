package com.example.frontend.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Product product;
    private int quantity;
    private double totalPrice;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        updateTotalPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    private void updateTotalPrice() {
        this.totalPrice = product.getPrice() * quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
        updateTotalPrice();
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            updateTotalPrice();
        }
    }

    public String getProductName() {
        return product.getName();
    }

    public double getUnitPrice() {
        return product.getPrice();
    }

    public String getFarmerInfo() {
        if (product.getProviderId() != null) {
            return "Agricultor ID: " + product.getProviderId();
        } else if (product.getFarmerId() != null) {
            return "Agricultor ID: " + product.getFarmerId();
        }
        return "Agricultor: --";
    }
}
