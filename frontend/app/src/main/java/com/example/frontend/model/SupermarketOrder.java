package com.example.frontend.model;

import java.util.Date;
import java.util.List;

public class SupermarketOrder {

    private String id;
    private String clientName;
    private Date orderDate;
    private Date estimatedDeliveryDate;
    private double total;
    private String status;
    private List<OrderItem> items;
    private String address;

    public SupermarketOrder(String id, String clientName, Date estimatedDeliveryDate, double total, String status, List<OrderItem> items, String address) {
        this.id = id;
        this.clientName = clientName;
        this.orderDate = new Date(); // Se genera automáticamente al crear el pedido
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.total = total;
        this.status = status;
        this.items = items;
        this.address = address;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public String getAddress() {
        return address;
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setEstimatedDeliveryDate(Date date) {
        this.estimatedDeliveryDate = date;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    // Clase interna para representar cada producto del pedido
    public static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private double unitPrice;

        public OrderItem(String productId, String productName, int quantity, double unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        // Getters
        public String getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }
    }
}
