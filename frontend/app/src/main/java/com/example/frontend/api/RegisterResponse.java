package com.example.frontend.api;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    public RegisterResponse(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
