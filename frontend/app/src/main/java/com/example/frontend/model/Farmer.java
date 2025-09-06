package com.example.frontend.model;

import java.util.List;

public class Farmer extends User {
    private List<Product> products;
    private List<Certification> certifications;

    public Farmer(int id, String name, String email, String role, String entity_name, Location location, float sustainabilityScore,
                 List<Product> products, List<Certification> certifications) {
        super(id, name, email, role, entity_name, location, sustainabilityScore);
        this.products = products;
        this.certifications = certifications;
    }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
    public List<Certification> getCertifications() { return certifications; }
    public void setCertifications(List<Certification> certifications) { this.certifications = certifications; }
}
