package com.example.frontend.model;

public class Certification {
    private String id;
    private String type;
    private String issuer;
    private String validUntil;

    public Certification(String id, String type, String issuer, String validUntil) {
        this.id = id;
        this.type = type;
        this.issuer = issuer;
        this.validUntil = validUntil;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    public String getValidUntil() { return validUntil; }
    public void setValidUntil(String validUntil) { this.validUntil = validUntil; }
}
