package com.example.frontend.api;

public class RegisterRequest {
    public String name;
    public String email;
    public String password;
    public String role;
    public String entity_name;
    public Double location_lat;
    public Double location_lon;
    
    public RegisterRequest(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    
    public RegisterRequest(String name, String email, String password, String role, 
                         String entity_name, Double location_lat, Double location_lon) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.entity_name = entity_name;
        this.location_lat = location_lat;
        this.location_lon = location_lon;
    }
}

