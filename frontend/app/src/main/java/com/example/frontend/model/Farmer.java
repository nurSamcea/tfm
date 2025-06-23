package com.example.frontend.model;

import java.util.List;

public class Farmer extends User {
    private List<Product> products;
    private List<Certification> certifications;

    public Farmer(String id, String name, String email, Location location, float sustainabilityScore,
                 List<Product> products, List<Certification> certifications) {
        super(id, name, email, location, sustainabilityScore);
        this.products = products;
        this.certifications = certifications;
    }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
    public List<Certification> getCertifications() { return certifications; }
    public void setCertifications(List<Certification> certifications) { this.certifications = certifications; }
}
