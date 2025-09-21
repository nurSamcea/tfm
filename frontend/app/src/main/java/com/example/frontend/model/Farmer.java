package com.example.frontend.model;

import java.util.List;

public class Farmer extends User {
    private List<Product> products;

    public Farmer(int id, String name, String email, String role, String entity_name, Location location, float sustainabilityScore,
                 List<Product> products) {
        super(id, name, email, role, entity_name, location, sustainabilityScore);
        this.products = products;
    }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
