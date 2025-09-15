package com.example.frontend.services;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.frontend.api.ApiService;
import com.example.frontend.api.RetrofitClient;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductOptimizationService {
    private static final String TAG = "ProductOptimizationService";
    
    private final ApiService apiService;
    
    public ProductOptimizationService(Context context) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(context);
        this.apiService = retrofitClient.getApiService();
    }
    
    /**
     * Obtiene productos optimizados usando la ubicación del usuario
     */
    public CompletableFuture<List<Product>> getOptimizedProducts(
            double userLat, 
            double userLon, 
            Map<String, Object> filters,
            Map<String, Float> weights,
            String sortCriteria
    ) {
        CompletableFuture<List<Product>> future = new CompletableFuture<>();
        
        // Crear el request para el endpoint de productos optimizados
        Map<String, Object> request = new HashMap<>();
        request.put("user_lat", userLat);
        request.put("user_lon", userLon);
        request.put("filters", filters != null ? filters : new HashMap<>());
        request.put("weights", weights != null ? weights : getDefaultWeights());
        request.put("sort_criteria", sortCriteria != null ? sortCriteria : "optimal");
        
        Log.d(TAG, "Solicitando productos optimizados para ubicación: " + userLat + ", " + userLon);
        
        // Llamar al endpoint de productos optimizados
        Call<List<Map<String, Object>>> call = apiService.getOptimizedProducts(request);
        
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = convertToProducts(response.body());
                    Log.d(TAG, "Productos optimizados obtenidos: " + products.size());
                    future.complete(products);
                } else {
                    Log.e(TAG, "Error en respuesta: " + response.code() + " - " + response.message());
                    future.completeExceptionally(new RuntimeException("Error del servidor: " + response.message()));
                }
            }
            
            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e(TAG, "Error en llamada API", t);
                future.completeExceptionally(t);
            }
        });
        
        return future;
    }
    
    /**
     * Obtiene productos cercanos usando el nuevo endpoint
     */
    public CompletableFuture<List<Product>> getNearbyProducts(
            double userLat, 
            double userLon, 
            double maxDistanceKm,
            int limit
    ) {
        CompletableFuture<List<Product>> future = new CompletableFuture<>();
        
        Log.d(TAG, "Solicitando productos cercanos para ubicación: " + userLat + ", " + userLon);
        
        // Llamar al endpoint de productos cercanos
        Call<List<Map<String, Object>>> call = apiService.getNearbyProducts(
                userLat, userLon, maxDistanceKm, limit
        );
        
        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = convertToProducts(response.body());
                    Log.d(TAG, "Productos cercanos obtenidos: " + products.size());
                    future.complete(products);
                } else {
                    Log.e(TAG, "Error en respuesta: " + response.code() + " - " + response.message());
                    future.completeExceptionally(new RuntimeException("Error del servidor: " + response.message()));
                }
            }
            
            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e(TAG, "Error en llamada API", t);
                future.completeExceptionally(t);
            }
        });
        
        return future;
    }
    
    /**
     * Optimiza una lista de compra usando la ubicación del usuario
     */
    public CompletableFuture<Map<String, Object>> optimizeShoppingList(
            double userLat,
            double userLon,
            List<Map<String, Object>> shoppingList,
            Map<String, Float> criteria
    ) {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        
        Map<String, Object> request = new HashMap<>();
        request.put("user_lat", userLat);
        request.put("user_lon", userLon);
        request.put("items", shoppingList);
        request.put("criteria", criteria != null ? criteria : getDefaultOptimizationCriteria());
        
        Log.d(TAG, "Optimizando lista de compra para ubicación: " + userLat + ", " + userLon);
        
        Call<Map<String, Object>> call = apiService.optimizeShoppingList(request);
        
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Lista de compra optimizada correctamente");
                    future.complete(response.body());
                } else {
                    Log.e(TAG, "Error optimizando lista: " + response.code() + " - " + response.message());
                    future.completeExceptionally(new RuntimeException("Error del servidor: " + response.message()));
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Error en llamada API", t);
                future.completeExceptionally(t);
            }
        });
        
        return future;
    }
    
    /**
     * Convierte la respuesta del API a objetos Product
     */
    private List<Product> convertToProducts(List<Map<String, Object>> apiResponse) {
        List<Product> products = new ArrayList<>();
        
        for (Map<String, Object> item : apiResponse) {
            try {
                Product product = new Product();
                
                // Campos básicos
                if (item.get("id") != null) {
                    product.setId(String.valueOf(item.get("id")));
                }
                if (item.get("name") != null) {
                    product.setName(String.valueOf(item.get("name")));
                }
                if (item.get("description") != null) {
                    product.setDescription(String.valueOf(item.get("description")));
                }
                if (item.get("price") != null) {
                    product.setPrice(Double.parseDouble(String.valueOf(item.get("price"))));
                }
                if (item.get("stock_available") != null) {
                    product.setStockAvailable(Double.parseDouble(String.valueOf(item.get("stock_available"))));
                }
                if (item.get("category") != null) {
                    product.setCategory(String.valueOf(item.get("category")));
                }
                if (item.get("is_eco") != null) {
                    product.setIsEco(Boolean.parseBoolean(String.valueOf(item.get("is_eco"))));
                }
                if (item.get("image_url") != null) {
                    product.setImageUrl(String.valueOf(item.get("image_url")));
                }
                
                // Campos de ubicación del proveedor
                if (item.get("provider_id") != null) {
                    product.setProviderId(Integer.parseInt(String.valueOf(item.get("provider_id"))));
                }
                if (item.get("provider_name") != null) {
                    product.setProviderName(String.valueOf(item.get("provider_name")));
                }
                if (item.get("provider_lat") != null) {
                    product.setProviderLat(Double.parseDouble(String.valueOf(item.get("provider_lat"))));
                }
                if (item.get("provider_lon") != null) {
                    product.setProviderLon(Double.parseDouble(String.valueOf(item.get("provider_lon"))));
                }
                
                // Distancia calculada
                if (item.get("distance_km") != null) {
                    product.setDistance_km(Double.parseDouble(String.valueOf(item.get("distance_km"))));
                }
                
                // Score de optimización
                if (item.get("optimization_score") != null) {
                    product.setScore(Double.parseDouble(String.valueOf(item.get("optimization_score"))));
                }
                
                products.add(product);
                
            } catch (Exception e) {
                Log.w(TAG, "Error convirtiendo producto: " + e.getMessage());
            }
        }
        
        return products;
    }
    
    /**
     * Obtiene pesos por defecto para la optimización
     */
    private Map<String, Float> getDefaultWeights() {
        Map<String, Float> weights = new HashMap<>();
        weights.put("price", 0.3f);
        weights.put("distance", 0.25f);
        weights.put("sustainability", 0.2f);
        weights.put("eco", 0.15f);
        weights.put("stock", 0.1f);
        return weights;
    }
    
    /**
     * Obtiene criterios por defecto para optimización de lista de compra
     */
    private Map<String, Float> getDefaultOptimizationCriteria() {
        Map<String, Float> criteria = new HashMap<>();
        criteria.put("price_weight", 0.4f);
        criteria.put("distance_weight", 0.3f);
        criteria.put("provider_weight", 0.2f);
        criteria.put("route_efficiency_weight", 0.1f);
        return criteria;
    }
    
    /**
     * Crea filtros por defecto
     */
    public static Map<String, Object> createDefaultFilters() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("eco", false);
        filters.put("gluten_free", false);
        filters.put("distance", true); // Priorizar distancia por defecto
        filters.put("max_distance_km", 50.0);
        return filters;
    }
    
    /**
     * Crea filtros con distancia máxima personalizada
     */
    public static Map<String, Object> createFiltersWithMaxDistance(double maxDistanceKm) {
        Map<String, Object> filters = createDefaultFilters();
        filters.put("max_distance_km", maxDistanceKm);
        return filters;
    }
}
