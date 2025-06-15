package com.example.frontend.model;

import java.util.List;

public class Purchase {
    private String id;
    private String date;
    private List<PurchaseItem> items;
    private double totalAmount;
    private SustainabilityMetrics sustainabilityImpact;

    public Purchase(String id, String date, List<PurchaseItem> items, double totalAmount,
                   SustainabilityMetrics sustainabilityImpact) {
        this.id = id;
        this.date = date;
        this.items = items;
        this.totalAmount = totalAmount;
        this.sustainabilityImpact = sustainabilityImpact;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public List<PurchaseItem> getItems() { return items; }
    public void setItems(List<PurchaseItem> items) { this.items = items; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public SustainabilityMetrics getSustainabilityImpact() { return sustainabilityImpact; }
    public void setSustainabilityImpact(SustainabilityMetrics sustainabilityImpact) { this.sustainabilityImpact = sustainabilityImpact; }
}
