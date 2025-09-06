package com.example.frontend.utils;

import android.util.Log;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    
    public static boolean isBackendReachable() {
        try {
            String baseUrl = Constants.getBaseUrl();
            URL url = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5 segundos
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Backend reachable check - URL: " + baseUrl + " - Response: " + responseCode);
            
            connection.disconnect();
            return responseCode == 200;
            
        } catch (IOException e) {
            Log.e(TAG, "Backend not reachable: " + e.getMessage());
            return false;
        }
    }
    
    public static void testBackendConnection() {
        Log.d(TAG, "=== TESTING BACKEND CONNECTION ===");
        Log.d(TAG, "Base URL: " + Constants.getBaseUrl());
        Log.d(TAG, "Backend reachable: " + isBackendReachable());
        Log.d(TAG, "=== END CONNECTION TEST ===");
    }
}
