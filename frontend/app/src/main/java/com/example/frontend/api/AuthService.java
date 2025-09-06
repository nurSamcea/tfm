package com.example.frontend.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auth/login")
    Call<ApiService.LoginResponse> login(@Body ApiService.LoginRequest loginRequest);

    @POST("auth/register")
    Call<ApiService.RegisterResponse> register(@Body ApiService.RegisterRequest registerRequest);
}
