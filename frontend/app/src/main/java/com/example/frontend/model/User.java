package com.example.frontend.model;

import java.util.List;

public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected Location location;
    protected float sustainabilityScore;

    public User(String id, String name, String email, Location location, float sustainabilityScore) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.location = location;
        this.sustainabilityScore = sustainabilityScore;
    }

    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public float getSustainabilityScore() { return sustainabilityScore; }
    public void setSustainabilityScore(float sustainabilityScore) { this.sustainabilityScore = sustainabilityScore; }
}
