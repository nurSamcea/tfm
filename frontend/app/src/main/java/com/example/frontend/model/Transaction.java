package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Transaction implements Serializable {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("buyer_id")
    private Integer buyerId;
    
    @SerializedName("seller_id")
    private Integer sellerId;
    
    @SerializedName("buyer_type")
    private String buyerType;
    
    @SerializedName("seller_type")
    private String sellerType;
    
    @SerializedName("buyer_name")
    private String buyerName;
    
    @SerializedName("seller_name")
    private String sellerName;
    
    @SerializedName("total_price")
    private Double totalPrice;
    
    @SerializedName("currency")
    private String currency;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("payment_method")
    private String paymentMethod;
    
    @SerializedName("delivery_address")
    private String deliveryAddress;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("created_at")
    private Date createdAt;
    
    @SerializedName("confirmed_at")
    private Date confirmedAt;
    
    @SerializedName("delivered_at")
    private Date deliveredAt;
    
    @SerializedName("order_details")
    private List<OrderItem> orderDetails;
    
    @SerializedName("items")
    private List<CartItem> items;

    public Transaction() {
    }

    public Transaction(Integer buyerId, Integer sellerId, String buyerType, String sellerType, Double totalPrice, String currency, String status) {
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.buyerType = buyerType;
        this.sellerType = sellerType;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.status = status;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getBuyerId() {
        return buyerId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public String getBuyerType() {
        return buyerType;
    }

    public String getSellerType() {
        return sellerType;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getPhone() {
        return phone;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getConfirmedAt() {
        return confirmedAt;
    }

    public Date getDeliveredAt() {
        return deliveredAt;
    }

    public List<OrderItem> getOrderDetails() {
        return orderDetails;
    }

    public List<CartItem> getItems() {
        return items;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setBuyerId(Integer buyerId) {
        this.buyerId = buyerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public void setBuyerType(String buyerType) {
        this.buyerType = buyerType;
    }

    public void setSellerType(String sellerType) {
        this.sellerType = sellerType;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setConfirmedAt(Date confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public void setDeliveredAt(Date deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public void setOrderDetails(List<OrderItem> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // Métodos de utilidad
    public String getFormattedTotalPrice() {
        if (totalPrice != null) {
            return String.format("%.2f %s", totalPrice, currency != null ? currency : "EUR");
        }
        return "0.00 EUR";
    }

    public String getStatusDisplayName() {
        if (status == null) return "Desconocido";
        
        switch (status.toLowerCase()) {
            case "in_progress":
                return "En curso";
            case "delivered":
                return "Entregado";
            case "cancelled":
                return "Cancelado";
            default:
                return status;
        }
    }

    public boolean isInProgress() {
        return "in_progress".equalsIgnoreCase(status);
    }

    public boolean isDelivered() {
        return "delivered".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "cancelled".equalsIgnoreCase(status);
    }

    // Métodos de utilidad para tipos de usuario
    public boolean isBuyerConsumer() {
        return "consumer".equalsIgnoreCase(buyerType);
    }

    public boolean isBuyerSupermarket() {
        return "supermarket".equalsIgnoreCase(buyerType);
    }

    public boolean isSellerFarmer() {
        return "farmer".equalsIgnoreCase(sellerType);
    }

    public boolean isSellerSupermarket() {
        return "supermarket".equalsIgnoreCase(sellerType);
    }

    public String getBuyerTypeDisplayName() {
        if (buyerType == null) return "Desconocido";
        
        switch (buyerType.toLowerCase()) {
            case "consumer":
                return "Consumidor";
            case "supermarket":
                return "Supermercado";
            default:
                return buyerType;
        }
    }

    public String getSellerTypeDisplayName() {
        if (sellerType == null) return "Desconocido";
        
        switch (sellerType.toLowerCase()) {
            case "farmer":
                return "Agricultor";
            case "supermarket":
                return "Supermercado";
            default:
                return sellerType;
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", buyerId=" + buyerId +
                ", sellerId=" + sellerId +
                ", buyerType='" + buyerType + '\'' +
                ", sellerType='" + sellerType + '\'' +
                ", totalPrice=" + totalPrice +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
