package com.example.frontend.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Transaction implements Serializable {
    @SerializedName("id")
    private Integer id;
    
    @SerializedName("user_id")
    private Integer userId;
    
    @SerializedName("shopping_list_id")
    private Integer shoppingListId;
    
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
    
    @SerializedName("items")
    private List<CartItem> items;

    public Transaction() {
    }

    public Transaction(Integer userId, Integer shoppingListId, Double totalPrice, String currency, String status) {
        this.userId = userId;
        this.shoppingListId = shoppingListId;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.status = status;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getShoppingListId() {
        return shoppingListId;
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

    public List<CartItem> getItems() {
        return items;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setShoppingListId(Integer shoppingListId) {
        this.shoppingListId = shoppingListId;
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
            case "pending":
                return "Pendiente";
            case "confirmed":
                return "Confirmado";
            case "shipped":
                return "Enviado";
            case "delivered":
                return "Entregado";
            case "cancelled":
                return "Cancelado";
            default:
                return status;
        }
    }

    public boolean isPending() {
        return "pending".equalsIgnoreCase(status);
    }

    public boolean isConfirmed() {
        return "confirmed".equalsIgnoreCase(status);
    }

    public boolean isShipped() {
        return "shipped".equalsIgnoreCase(status);
    }

    public boolean isDelivered() {
        return "delivered".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "cancelled".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", shoppingListId=" + shoppingListId +
                ", totalPrice=" + totalPrice +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
