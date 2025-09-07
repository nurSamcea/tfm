package com.example.frontend.services;

import com.example.frontend.network.ApiClient;
import com.example.frontend.model.TraceabilityEvent;
import com.example.frontend.model.ProductTraceabilityChain;
import com.example.frontend.model.FullTraceabilityResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TraceabilityApiService {
    
    @GET("consumer/products/{productId}/trace/json")
    Call<FullTraceabilityResponse> getProductTraceability(@Path("productId") Integer productId);
    
    @GET("consumer/products/{productId}/trace/summary")
    Call<ProductTraceabilityChain> getProductTraceabilitySummary(@Path("productId") Integer productId);
    
    @GET("traceability/products/{productId}/events")
    Call<List<TraceabilityEvent>> getProductTraceabilityEvents(@Path("productId") Integer productId);
}
