package com.example.frontend.api;

import java.util.Map;

public class ProductFilterRequest {
    public String search;
    public Map<String, Boolean> filters;
    public Map<String, Float> weights;
    public Double user_lat;
    public Double user_lon;
    public String sort_criteria; // "optimal", "price", "distance", "sustainability", "eco", "stock"
} 