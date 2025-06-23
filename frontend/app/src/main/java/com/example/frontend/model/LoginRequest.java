package com.example.frontend.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    private String email;
    private String password;
    private String userType;

    public LoginRequest(String email, String password, String userType) {
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
} 