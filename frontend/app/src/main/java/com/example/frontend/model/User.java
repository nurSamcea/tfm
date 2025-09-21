package com.example.frontend.model;

import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    private String role;
    private String entity_name;
    private Location location;
    private float sustainabilityScore;

    // Constructor por defecto para Gson
    public User() {}

    public User(int id, String name, String email, String role, String entity_name, Location location, float sustainabilityScore) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.entity_name = entity_name;
        this.location = location;
        this.sustainabilityScore = sustainabilityScore;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getEntityName() { return entity_name; }
    public void setEntityName(String entity_name) { this.entity_name = entity_name; }
    
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    
    public float getSustainabilityScore() { return sustainabilityScore; }
    public void setSustainabilityScore(float sustainabilityScore) { this.sustainabilityScore = sustainabilityScore; }
    
    @Override
    public String toString() {
        if (entity_name != null && !entity_name.isEmpty()) {
            return entity_name;
        }
        return name;
    }
}
