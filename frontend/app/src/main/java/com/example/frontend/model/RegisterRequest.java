package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("user_type")
    private String userType;

    @SerializedName("location")
    private Location location;

    public RegisterRequest(String name, String email, String password, String userType, Location location) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.location = location;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
} 