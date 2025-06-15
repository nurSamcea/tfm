package com.example.frontend.api;

import com.example.frontend.model.*;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

import com.example.frontend.model.RegisterRequest;
import com.example.frontend.model.LoginRequest;

public interface ApiService {
    @POST("auth/login")
    Call<User> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<User> register(@Body RegisterRequest request);

    // Endpoints para Agricultores
    @GET("farmers/{id}/products")
    Call<List<Product>> getFarmerProducts(@Path("id") String farmerId);

    @POST("farmers/{id}/products")
    Call<Product> addProduct(@Path("id") String farmerId, @Body Product product);

    @GET("products")
    Call<List<Product>> getProducts();

    @GET("products/{id}")
    Call<Product> getProduct(@Path("id") String id);

    @POST("products")
    Call<Product> createProduct(@Body Product product);

    @GET("farmers/{id}/certifications")
    Call<List<Certification>> getFarmerCertifications(@Path("id") String farmerId);

    @POST("farmers/{id}/certifications")
    Call<Certification> addCertification(@Path("id") String farmerId, @Body Certification certification);

    // Endpoints para Supermercados
    @GET("supermarkets/{id}/inventory")
    Call<List<InventoryItem>> getInventory(@Path("id") String supermarketId);

    @POST("supermarkets/{id}/inventory")
    Call<InventoryItem> addInventoryItem(@Path("id") String supermarketId, @Body InventoryItem item);

    @GET("supermarkets/{id}/suppliers")
    Call<List<Farmer>> getSuppliers(@Path("id") String supermarketId);

    @POST("supermarkets/{id}/suppliers")
    Call<Farmer> addSupplier(@Path("id") String supermarketId, @Body String farmerId);

    // Endpoints para Consumidores
    @GET("consumers/{id}/purchases")
    Call<List<Purchase>> getPurchaseHistory(@Path("id") String consumerId);

    @GET("consumers/{id}/preferences")
    Call<Preferences> getPreferences(@Path("id") String consumerId);

    @PUT("consumers/{id}/preferences")
    Call<Preferences> updatePreferences(@Path("id") String consumerId, @Body Preferences preferences);

    // Endpoints para Productos
    @GET("products")
    Call<List<Product>> getProducts(@Query("category") String category,
                                   @Query("minSustainabilityScore") Float minScore,
                                   @Query("maxDistance") Integer maxDistance);

    @GET("products/{id}")
    Call<Product> getProductDetails(@Path("id") String productId);

    // Endpoints para Blockchain
    @GET("blockchain/products/{id}")
    Call<BlockchainProductInfo> getProductBlockchainInfo(@Path("id") String productId);

    @GET("blockchain/transactions/{id}")
    Call<BlockchainTransactionInfo> getTransactionBlockchainInfo(@Path("id") String transactionId);
}



