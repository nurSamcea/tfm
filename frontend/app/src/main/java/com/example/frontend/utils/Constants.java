package com.example.frontend.utils;

import android.content.Context;
import io.github.cdimascio.dotenv.Dotenv;

public class Constants {
    private static Dotenv dotenv;
    
    // ========================================
    // CONFIGURACIÓN CENTRALIZADA
    // ========================================
    // Para cambiar la IP del backend, modifica únicamente el archivo .env en la raíz del proyecto
    private static final String DEFAULT_BACKEND_IP = "10.167.157.98"; // Esta IP se lee desde .env
    private static final String DEFAULT_BACKEND_PORT = "8000";
    private static final String DEFAULT_BACKEND_PROTOCOL = "http";
    
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
        return getEnvVar("BACKEND_IP", DEFAULT_BACKEND_IP);
    }
    
    public static String getBackendPort() {
        return getEnvVar("BACKEND_PORT", DEFAULT_BACKEND_PORT);
    }
    
    public static String getBackendProtocol() {
        return getEnvVar("BACKEND_PROTOCOL", DEFAULT_BACKEND_PROTOCOL);
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

    // Clave privada blockchain (solo para desarrollo)
    public static String getBlockchainPrivateKey() {
        return getEnvVar("BLOCKCHAIN_PRIVATE_KEY", "");
    }
    
    // Timeouts
    public static int getConnectTimeout() {
        try {
            return Integer.parseInt(getEnvVar("CONNECT_TIMEOUT", "60"));
        } catch (NumberFormatException e) {
            return 60;
        }
    }
    
    public static int getReadTimeout() {
        try {
            return Integer.parseInt(getEnvVar("READ_TIMEOUT", "60"));
        } catch (NumberFormatException e) {
            return 60;
        }
    }
    
    public static int getWriteTimeout() {
        try {
            return Integer.parseInt(getEnvVar("WRITE_TIMEOUT", "60"));
        } catch (NumberFormatException e) {
            return 60;
        }
    }
}
