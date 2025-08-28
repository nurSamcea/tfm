package com.example.frontend.utils;

import android.content.Context;
import io.github.cdimascio.dotenv.Dotenv;

public class Constants {
    private static Dotenv dotenv;
    
    // Inicializar dotenv
    public static void init(Context context) {
        if (dotenv == null) {
            try {
                dotenv = Dotenv.configure()
                    .directory(".")
                    .filename(".env")
                    .ignoreIfMissing()
                    .load();
            } catch (Exception e) {
                // Si no se puede cargar .env, usar valores por defecto
                e.printStackTrace();
            }
        }
    }
    
    // Métodos para obtener valores del .env con fallbacks
    private static String getEnvVar(String key, String defaultValue) {
        if (dotenv != null) {
            String value = dotenv.get(key);
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        }
        return defaultValue;
    }
    
    // Configuración del backend
    public static String getBackendIP() {
        return getEnvVar("BACKEND_IP", "192.168.201.237");
    }
    
    public static String getBackendPort() {
        return getEnvVar("BACKEND_PORT", "8000");
    }
    
    public static String getBackendProtocol() {
        return getEnvVar("BACKEND_PROTOCOL", "http");
    }
    
    public static String getBaseUrl() {
        return getEnvVar("BACKEND_BASE_URL", 
            getBackendProtocol() + "://" + getBackendIP() + ":" + getBackendPort() + "/");
    }
    
    public static String getAuthBaseUrl() {
        return getEnvVar("BACKEND_AUTH_URL", getBaseUrl() + "auth/");
    }
    
    public static String getApiBaseUrl() {
        return getEnvVar("BACKEND_API_URL", getBaseUrl() + "api/");
    }
    
    // Timeouts
    public static int getConnectTimeout() {
        try {
            return Integer.parseInt(getEnvVar("CONNECT_TIMEOUT", "30"));
        } catch (NumberFormatException e) {
            return 30;
        }
    }
    
    public static int getReadTimeout() {
        try {
            return Integer.parseInt(getEnvVar("READ_TIMEOUT", "30"));
        } catch (NumberFormatException e) {
            return 30;
        }
    }
    
    public static int getWriteTimeout() {
        try {
            return Integer.parseInt(getEnvVar("WRITE_TIMEOUT", "30"));
        } catch (NumberFormatException e) {
            return 30;
        }
    }
}
