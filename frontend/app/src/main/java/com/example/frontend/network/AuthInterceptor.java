package com.example.frontend.network;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    
    private static final String PREF_NAME = "ZeroAppPrefs";
    private static final String KEY_TOKEN = "auth_token";
    
    private Context context;
    
    public AuthInterceptor(Context context) {
        this.context = context;
    }
    
    public AuthInterceptor() {
        // Constructor sin contexto para uso estático
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Obtener el token almacenado
        String token = getStoredToken();
        
        // Si no hay token, continuar con la petición original
        if (token == null || token.isEmpty()) {
            return chain.proceed(originalRequest);
        }
        
        // Añadir el header de autorización
        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        
        return chain.proceed(newRequest);
    }
    
    private String getStoredToken() {
        if (context == null) {
            return null;
        }
        
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }
    
    // Métodos estáticos para gestión del token
    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }
    
    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }
    
    public static void clearToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_TOKEN).apply();
    }
    
    public static boolean isLoggedIn(Context context) {
        return getToken(context) != null;
    }
}
