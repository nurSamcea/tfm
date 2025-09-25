package com.example.frontend.services;

import com.example.frontend.model.TraceabilityEvent;
import com.example.frontend.model.ProductTraceabilityChain;
import com.example.frontend.model.FullTraceabilityResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TraceabilityApiService {
    
    @GET("consumer/products/{productId}/trace/json")
    Call<FullTraceabilityResponse> getProductTraceability(@Path("productId") Integer productId);
    
    @GET("consumer/products/{productId}/trace/summary")
    Call<ProductTraceabilityChain> getProductTraceabilitySummary(@Path("productId") Integer productId);
    
    @GET("traceability/products/{productId}/events")
    Call<List<TraceabilityEvent>> getProductTraceabilityEvents(@Path("productId") Integer productId);

    @POST("traceability/products/{productId}/create-chain")
    Call<Object> createProductTraceabilityChain(
        @Path("productId") Integer productId,
        @Query("blockchain_private_key") String blockchainPrivateKey
    );
}
