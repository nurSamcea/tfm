package com.example.frontend.model;

import java.util.List;

public class SupermarketOrder {
    private int transactionId;
    private String clientOrSupplier;
    private List<String> products;
    private String deliveryDate;
    private String total;
    private String status;
    private String orderType; // "TO_FARMER" o "FROM_CONSUMER"

    public SupermarketOrder(int transactionId, String clientOrSupplier, List<String> products, String deliveryDate, String total, String status, String orderType) {
        this.transactionId = transactionId;
        this.clientOrSupplier = clientOrSupplier;
        this.products = products;
        this.deliveryDate = deliveryDate;
        this.total = total;
        this.status = status;
        this.orderType = orderType;
    }

    public int getTransactionId() { return transactionId; }
    public String getClientOrSupplier() { return clientOrSupplier; }
    public List<String> getProducts() { return products; }
    public String getDeliveryDate() { return deliveryDate; }
    public String getTotal() { return total; }
    public String getStatus() { return status; }
    public String getOrderType() { return orderType; }
}