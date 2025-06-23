package com.example.frontend.model;

public class InventoryItem {
    private Product product;
    private int stock;
    private String lastRestock;
    private Farmer supplier;

    public InventoryItem(Product product, int stock, String lastRestock, Farmer supplier) {
        this.product = product;
        this.stock = stock;
        this.lastRestock = lastRestock;
        this.supplier = supplier;
    }

    // Getters y setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getLastRestock() { return lastRestock; }
    public void setLastRestock(String lastRestock) { this.lastRestock = lastRestock; }
    public Farmer getSupplier() { return supplier; }
    public void setSupplier(Farmer supplier) { this.supplier = supplier; }
}
