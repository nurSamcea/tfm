package com.example.iotapp.models;

public class User {
    public enum UserType {
        CONSUMER,
        FARMER,
        SUPERMARKET
    }

    private String id;
    private String name;
    private String email;
    private UserType userType;
    private String address;
    private String phone;

    public User(String id, String name, String email, UserType userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserType getUserType() { return userType; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setUserType(UserType userType) { this.userType = userType; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
} 