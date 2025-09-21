package com.example.frontend.model;

import java.util.List;

public class OrderRequest {
    public int seller_id;
    public String seller_type;
    public List<OrderItem> order_details;
    public double total_price;
    public String currency = "EUR";
    
    // Constructor sin parámetros para JSON
    public OrderRequest() {}
    
    public OrderRequest(int sellerId, String sellerType, List<OrderItem> orderDetails, double totalPrice) {
        this.seller_id = sellerId;
        this.seller_type = sellerType;
        this.order_details = orderDetails;
        this.total_price = totalPrice;
    }
    
    // Getters y setters
    public int getSeller_id() {
        return seller_id;
    }
    
    public void setSeller_id(int seller_id) {
        this.seller_id = seller_id;
    }
    
    public String getSeller_type() {
        return seller_type;
    }
    
    public void setSeller_type(String seller_type) {
        this.seller_type = seller_type;
    }
    
    public List<OrderItem> getOrder_details() {
        return order_details;
    }
    
    public void setOrder_details(List<OrderItem> order_details) {
        this.order_details = order_details;
    }
    
    public double getTotal_price() {
        return total_price;
    }
    
    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
