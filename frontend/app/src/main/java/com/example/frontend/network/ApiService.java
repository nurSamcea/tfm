package com.example.frontend.network;

import com.example.frontend.models.CartItem;
import com.example.frontend.models.ProductTraceability;
import com.example.frontend.models.Transaction;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // ===== TRANSACCIONES =====
    @GET("transactions/")
    Call<List<Transaction>> getTransactions();
    
    @POST("transactions/")
    Call<Transaction> createTransaction(@Body Transaction transaction);
    
    // ===== TRAZABILIDAD =====
    @GET("traceability/product/{qr_hash}")
    Call<ProductTraceability> getProductTraceability(@Path("qr_hash") String qrHash);
    
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
    }
    
    // Product classes
    public static class Product {
        public Integer id;
        public String name;
        public String description;
        public Double price;
        public String category;
        public Boolean is_eco;
        public Integer stock_available;
        public String image_url;
    }
    
    public static class ProductFilterRequest {
        public String search;
        public String provider_role;
        public Double user_lat;
        public Double user_lon;
        public Double price_weight;
        public Double distance_weight;
        public Double sustainability_weight;
    }
    
    public static class ProductOptimizedResponse {
        public Product product;
        public Double score;
        public Double distance;
        public Double sustainability_score;
    }
    
    // Shopping List classes
    public static class ShoppingList {
        public Integer id;
        public String name;
        public String description;
        public Integer user_id;
        public String status;
    }
    
    public static class ShoppingListItem {
        public Integer id;
        public Integer shopping_list_id;
        public Integer product_id;
        public Integer quantity;
        public String unit;
    }
    
    public static class OptimizeBasketRequest {
        public Integer shopping_list_id;
        public Double user_lat;
        public Double user_lon;
        public Double price_weight;
        public Double distance_weight;
        public Double sustainability_weight;
    }
    
    public static class OptimizedBasket {
        public List<CartItem> items;
        public Double total_price;
        public Double total_distance;
        public Double total_sustainability_score;
    }
    
    // Sensor Reading
    public static class SensorReading {
        public Integer id;
        public Integer product_id;
        public Double temperature;
        public Double humidity;
        public String sensor_type;
        public String source_device;
        public String created_at;
    }
    
    // QR
    public static class QR {
        public Integer id;
        public String qr_hash;
        public Integer product_id;
        public String qr_metadata;
        public String created_at;
    }
    
    // Blockchain
    public static class BlockchainResponse {
        public String hash;
        public String status;
        public String message;
    }
    
    public static class BlockchainVerification {
        public Boolean verified;
        public String hash;
        public String timestamp;
        public String message;
    }
    
    // Impact Metric
    public static class ImpactMetric {
        public Integer id;
        public Integer transaction_id;
        public Double co2_saved;
        public Double local_support_score;
        public Double waste_prevention_score;
        public Double total_impact_score;
        public String created_at;
    }
    
    // User
    public static class User {
        public Integer id;
        public String name;
        public String email;
        public String role;
        public String entity_name;
        public Double location_lat;
        public Double location_lon;
    }
    
    // Product History
    public static class ProductHistory {
        public Integer product_id;
        public String product_name;
        public List<Transaction> transactions;
        public List<SensorReading> sensor_readings;
        public List<BlockchainResponse> blockchain_logs;
    }
}
