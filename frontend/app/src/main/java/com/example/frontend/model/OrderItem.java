package com.example.frontend.model;

public class OrderItem {
    public int product_id;
    public String product_name;
    public double quantity;
    public double unit_price;
    public double total_price;
    
    // Constructor sin parámetros para JSON
    public OrderItem() {}
    
    public OrderItem(int productId, String productName, double quantity, double unitPrice) {
        this.product_id = productId;
        this.product_name = productName;
        this.quantity = quantity;
        this.unit_price = unitPrice;
        this.total_price = quantity * unitPrice;
    }
    
    // Getters y setters
    public int getProduct_id() {
        return product_id;
    }
    
    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
    
    public String getProduct_name() {
        return product_name;
    }
    
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
    
    public double getQuantity() {
        return quantity;
    }
    
    public void setQuantity(double quantity) {
        this.quantity = quantity;
        this.total_price = quantity * unit_price;
    }
    
    public double getUnit_price() {
        return unit_price;
    }
    
    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
        this.total_price = quantity * unit_price;
    }
    
    public double getTotal_price() {
        return total_price;
    }
    
    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }
}
