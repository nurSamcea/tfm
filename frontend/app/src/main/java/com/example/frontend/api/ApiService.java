package com.example.frontend.api;

import com.example.frontend.model.*;

import retrofit2.Call;
import retrofit2.http.*;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.List;
import java.util.Map;

import com.example.frontend.model.OrderRequest;
import com.example.frontend.model.Transaction;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    // Endpoints para Agricultores
    @GET("farmers/{id}/products")
    Call<List<com.example.frontend.model.Product>> getFarmerProducts(@Path("id") String farmerId);

    @POST("farmers/{id}/products")
    Call<com.example.frontend.model.Product> addProduct(@Path("id") String farmerId, @Body com.example.frontend.model.Product product);

    @GET("products")
    Call<List<com.example.frontend.model.Product>> getProducts();

    @GET("products/{id}")
    Call<com.example.frontend.model.Product> getProduct(@Path("id") String id);

    @POST("products")
    Call<com.example.frontend.model.Product> createProduct(@Body com.example.frontend.model.Product product);


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
    Call<List<com.example.frontend.model.Product>> getProducts(@Query("category") String category,
                                   @Query("minSustainabilityScore") Float minScore,
                                   @Query("maxDistance") Integer maxDistance);

    @GET("products/{id}")
    Call<com.example.frontend.model.Product> getProductDetails(@Path("id") String productId);

    // Endpoints para Blockchain
    @GET("blockchain/products/{id}")
    Call<BlockchainProductInfo> getProductBlockchainInfo(@Path("id") String productId);

    @GET("blockchain/transactions/{id}")
    Call<BlockchainTransactionInfo> getTransactionBlockchainInfo(@Path("id") String transactionId);

    // Endpoint mejorado para buscar productos con filtros de texto y tipo de proveedor
    @GET("products")
    Call<List<com.example.frontend.model.Product>> getProductsFiltered(@Query("search") String search, @Query("provider_role") String providerRole);

    @POST("/products/optimized/")
    Call<List<Map<String, Object>>> getOptimizedProducts(@Body Map<String, Object> request);
    
    @POST("/products/optimized/")
    Call<List<com.example.frontend.model.Product>> getProductsOptimized(@Body ProductFilterRequest request);
    
    // Nuevos endpoints para geolocalización
    @GET("products/nearby/")
    Call<List<Map<String, Object>>> getNearbyProducts(
            @Query("user_lat") double userLat,
            @Query("user_lon") double userLon,
            @Query("max_distance_km") double maxDistanceKm,
            @Query("limit") int limit
    );
    
    @GET("products/providers/nearby/")
    Call<List<Map<String, Object>>> getNearbyProviders(
            @Query("user_lat") double userLat,
            @Query("user_lon") double userLon,
            @Query("max_distance_km") double maxDistanceKm,
            @Query("provider_role") String providerRole,
            @Query("limit") int limit
    );
    
    @POST("shopping-lists/optimize/")
    Call<Map<String, Object>> optimizeShoppingList(@Body Map<String, Object> request);

    // Crear producto con imagen (multipart)
    @Multipart
    @POST("products/upload")
    Call<com.example.frontend.model.Product> createProductWithImage(
            @Part("name") RequestBody name,
            @Part("provider_id") RequestBody providerId,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price,
            @Part("currency") RequestBody currency,
            @Part("unit") RequestBody unit,
            @Part("category") RequestBody category,
            @Part("stock_available") RequestBody stockAvailable,
            @Part("expiration_date") RequestBody expirationDate,
            @Part("is_eco") RequestBody isEco,
            @Part MultipartBody.Part image
    );

    // Obtener productos de un farmer específico
    @GET("products/farmer/{farmerId}")
    Call<List<com.example.frontend.model.Product>> getFarmerProducts(@Path("farmerId") int farmerId);

    // Eliminar producto
    @DELETE("products/{productId}")
    Call<Void> deleteProduct(@Path("productId") int productId);

    // Toggle visibilidad del producto
    @PATCH("products/{productId}/toggle-hidden")
    Call<Map<String, Object>> toggleProductHidden(@Path("productId") int productId);

    // Actualizar producto
    @PUT("products/{productId}")
    Call<com.example.frontend.model.Product> updateProduct(@Path("productId") int productId, @Body com.example.frontend.model.ProductUpdate productUpdate);

    // Actualizar imagen de producto
    @Multipart
    @PATCH("products/{productId}/image")
    Call<com.example.frontend.model.Product> updateProductImage(
            @Path("productId") int productId,
            @Part MultipartBody.Part image
    );


    // ===== USUARIOS =====
    @GET("users/by-role/{role}")
    Call<List<User>> getUsersByRole(@Path("role") String role);
    
    // ===== TRANSACCIONES =====
    @GET("transactions/")
    Call<List<Transaction>> getTransactions();
    
    @GET("transactions/{transactionId}")
    Call<Transaction> getTransactionById(@Path("transactionId") int transactionId);
    
    @POST("transactions/")
    Call<Transaction> createTransaction(@Body Transaction transaction);
    
    // Nuevos endpoints para el flujo de pedidos
    @POST("transactions/create-order")
    Call<Transaction> createOrderFromCart(@Query("buyer_id") int buyerId, @Query("buyer_type") String buyerType, @Body OrderRequest orderRequest);
    
    @GET("transactions/buyer/{buyerId}/{buyerType}")
    Call<List<Transaction>> getBuyerOrders(@Path("buyerId") int buyerId, @Path("buyerType") String buyerType);
    
    @GET("transactions/seller/{sellerId}/{sellerType}")
    Call<List<Transaction>> getSellerOrders(@Path("sellerId") int sellerId, @Path("sellerType") String sellerType);
    
    // Endpoints específicos para mantener compatibilidad
    @GET("transactions/supermarket/{supermarketId}")
    Call<List<Transaction>> getSupermarketOrders(@Path("supermarketId") int supermarketId);
    
    @GET("transactions/farmer/{farmerId}")
    Call<List<Transaction>> getFarmerOrders(@Path("farmerId") int farmerId);
    
    @GET("transactions/consumer/{consumerId}")
    Call<List<Transaction>> getConsumerOrders(@Path("consumerId") int consumerId);
    
    @PATCH("transactions/{transactionId}/status")
    Call<Transaction> updateTransactionStatus(@Path("transactionId") int transactionId, @Body StatusUpdateRequest statusUpdate);
    
    @PATCH("transactions/{transactionId}/cancel")
    Call<Transaction> cancelTransaction(
            @Path("transactionId") int transactionId,
            @Query("user_id") int userId,
            @Query("user_type") String userType
    );
    
    @PATCH("transactions/{transactionId}/deliver")
    Call<Transaction> deliverTransaction(
            @Path("transactionId") int transactionId,
            @Query("user_id") int userId,
            @Query("user_type") String userType
    );
    
    // ===== TRAZABILIDAD =====
    
    // ===== CLASES DE REQUEST/RESPONSE =====
    
    // Login Request/Response
    public static class LoginRequest {
        public String email;
        public String password;
        
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
    
    public static class LoginResponse {
        public String access_token;
        public String token_type;
        public int user_id;
        public String user_email;
        public String user_role;
        public String user_name;
    }
    
    // Register Request/Response
    public static class RegisterRequest {
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
    }
    
    public static class RegisterResponse {
        public Integer id;
        public String name;
        public String email;
        public String role;
        public String entity_name;
        public String message;
    }
    // ===== GEOLOCALIZACIÓN USUARIO =====
    public static class UserLocationUpdate {
        public double location_lat;
        public double location_lon;

        public UserLocationUpdate(double lat, double lon) {
            this.location_lat = lat;
            this.location_lon = lon;
        }
    }

    @PUT("users/me/location")
    Call<Void> updateMyLocation(@Body UserLocationUpdate payload);
    
    // Product classes
    
    public static class ProductFilterRequest {
        public String search;
        public String provider_role;
        public Double user_lat;
        public Double user_lon;
        public Double price_weight;
        public Double distance_weight;
        public Double sustainability_weight;
        public java.util.Map<String, Boolean> filters;
        public java.util.Map<String, Float> weights;
        public String sort_criteria; // "optimal", "price", "distance", "sustainability", "eco", "stock"
    }
    
    public static class ProductOptimizedResponse {
        public com.example.frontend.model.Product product;
        public Double score;
        public Double distance;
        public Double sustainability_score;
    }
    
    // Shopping List classes
    
    // Order Request classes - ahora están en archivos separados
    
    public static class StatusUpdateRequest {
        public String status;
        
        public StatusUpdateRequest(String status) {
            this.status = status;
        }
    }
}



