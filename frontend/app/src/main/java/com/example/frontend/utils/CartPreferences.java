package com.example.frontend.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.frontend.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CartPreferences {
    private static final String PREFS_NAME = "CartPrefs";
    private static final String KEY_CART_ITEMS = "cart_items";
    private static final String KEY_SEARCH_QUERY = "search_query";
    private static final String KEY_FILTER_DISTANCE = "filter_distance";
    private static final String KEY_FILTER_PRICE = "filter_price";
    private static final String KEY_FILTER_GLUTEN_FREE = "filter_gluten_free";
    private static final String KEY_FILTER_ECO = "filter_eco";
    private static final String KEY_FILTER_CATEGORY = "filter_category";

    private final SharedPreferences prefs;
    private final Gson gson;

    public CartPreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // -------------------------------
    // Carrito
    // -------------------------------
    public void saveCartItems(List<Order.OrderItem> items) {
        String json = gson.toJson(items);
        prefs.edit().putString(KEY_CART_ITEMS, json).apply();
    }

    public List<Order.OrderItem> getCartItems() {
        String json = prefs.getString(KEY_CART_ITEMS, null);
        if (json == null) return null;
        Type type = new TypeToken<List<Order.OrderItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // -------------------------------
    // Texto de búsqueda
    // -------------------------------
    public void saveSearchQuery(String query) {
        prefs.edit().putString(KEY_SEARCH_QUERY, query).apply();
    }

    public String getSearchQuery() {
        return prefs.getString(KEY_SEARCH_QUERY, "");
    }

    // -------------------------------
    // Filtros
    // -------------------------------
    public void saveFilterDistance(boolean value) {
        prefs.edit().putBoolean(KEY_FILTER_DISTANCE, value).apply();
    }

    public boolean getFilterDistance() {
        return prefs.getBoolean(KEY_FILTER_DISTANCE, false);
    }

    public void saveFilterPrice(boolean value) {
        prefs.edit().putBoolean(KEY_FILTER_PRICE, value).apply();
    }

    public boolean getFilterPrice() {
        return prefs.getBoolean(KEY_FILTER_PRICE, false);
    }

    public void saveFilterGlutenFree(boolean value) {
        prefs.edit().putBoolean(KEY_FILTER_GLUTEN_FREE, value).apply();
    }

    public boolean getFilterGlutenFree() {
        return prefs.getBoolean(KEY_FILTER_GLUTEN_FREE, false);
    }

    public void saveFilterEco(boolean value) {
        prefs.edit().putBoolean(KEY_FILTER_ECO, value).apply();
    }

    public boolean getFilterEco() {
        return prefs.getBoolean(KEY_FILTER_ECO, false);
    }

    public void saveFilterCategory(boolean value) {
        prefs.edit().putBoolean(KEY_FILTER_CATEGORY, value).apply();
    }

    public boolean getFilterCategory() {
        return prefs.getBoolean(KEY_FILTER_CATEGORY, false);
    }

    // -------------------------------
    // Opción para limpiar todo (por ejemplo al cerrar sesión)
    // -------------------------------
    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
