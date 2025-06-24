package com.example.frontend.model;

import java.util.List;

public class FarmerOrder {
    private String clientOrMarket;
    private List<String> products;
    private String deliveryDate;
    private String total;
    private String status;

    public FarmerOrder(String clientOrMarket, List<String> products, String deliveryDate, String total, String status) {
        this.clientOrMarket = clientOrMarket;
        this.products = products;
        this.deliveryDate = deliveryDate;
        this.total = total;
        this.status = status;
    }

    public String getClientOrMarket() { return clientOrMarket; }
    public List<String> getProducts() { return products; }
    public String getDeliveryDate() { return deliveryDate; }
    public String getTotal() { return total; }
    public String getStatus() { return status; }
}
