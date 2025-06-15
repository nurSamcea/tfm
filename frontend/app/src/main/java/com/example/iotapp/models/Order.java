package com.example.iotapp.models;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Order {
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        IN_TRANSIT,
        DELIVERED,
        CANCELLED
    }

    private String id;
    private String consumerId;
    private String sellerId; // Puede ser farmerId o supermarketId
    private Date orderDate;
    private OrderStatus status;
    private double totalAmount;
    private List<OrderItem> items;
    private String deliveryAddress;
    private String paymentMethod;
    private String trackingNumber;

    public Order(String id, String consumerId, String sellerId) {
        this.id = id;
        this.consumerId = consumerId;
        this.sellerId = sellerId;
        this.orderDate = new Date();
        this.status = OrderStatus.PENDING;
        this.items = new ArrayList<>();
    }

    public static class OrderItem {
        private String productId;
        private int quantity;
        private double unitPrice;

        public OrderItem(String productId, int quantity, double unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        // Getters
        public String getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public double getTotalPrice() { return quantity * unitPrice; }

        // Setters
        public void setProductId(String productId) { this.productId = productId; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    }

    // Getters
    public String getId() { return id; }
    public String getConsumerId() { return consumerId; }
    public String getSellerId() { return sellerId; }
    public Date getOrderDate() { return orderDate; }
    public OrderStatus getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public List<OrderItem> getItems() { return items; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getTrackingNumber() { return trackingNumber; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    // Métodos de utilidad
    public void addItem(OrderItem item) {
        items.add(item);
        calculateTotal();
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        calculateTotal();
    }

    private void calculateTotal() {
        totalAmount = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }
} 