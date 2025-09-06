package com.example.frontend.model;

import java.util.List;

public class Supermarket extends User {
    private List<InventoryItem> inventory;
    private List<Farmer> suppliers;

    public Supermarket(int id, String name, String email, String role, String entity_name, Location location, float sustainabilityScore,
                       List<InventoryItem> inventory, List<Farmer> suppliers) {
        super(id, name, email, role, entity_name, location, sustainabilityScore);
        this.inventory = inventory;
        this.suppliers = suppliers;
    }

    public List<InventoryItem> getInventory() { return inventory; }
    public void setInventory(List<InventoryItem> inventory) { this.inventory = inventory; }
    public List<Farmer> getSuppliers() { return suppliers; }
    public void setSuppliers(List<Farmer> suppliers) { this.suppliers = suppliers; }
}
