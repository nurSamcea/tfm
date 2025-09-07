package com.example.frontend.utils;

/**
 * Configuración para la actualización automática de datos.
 */
public class RefreshConfig {
    
    // Intervalos de actualización disponibles (en milisegundos)
    public static final int INTERVAL_15_SECONDS = 15000;
    public static final int INTERVAL_30_SECONDS = 30000;
    public static final int INTERVAL_1_MINUTE = 60000;
    public static final int INTERVAL_2_MINUTES = 120000;
    public static final int INTERVAL_5_MINUTES = 300000;
    
    // Intervalo por defecto
    public static final int DEFAULT_INTERVAL = INTERVAL_30_SECONDS;
    
    // Configuración de actualización automática
    private static boolean autoRefreshEnabled = true;
    private static int refreshInterval = DEFAULT_INTERVAL;
    
    /**
     * Obtiene si la actualización automática está habilitada.
     */
    public static boolean isAutoRefreshEnabled() {
        return autoRefreshEnabled;
    }
    
    /**
     * Establece si la actualización automática está habilitada.
     */
    public static void setAutoRefreshEnabled(boolean enabled) {
        autoRefreshEnabled = enabled;
    }
    
    /**
     * Obtiene el intervalo de actualización en milisegundos.
     */
    public static int getRefreshInterval() {
        return refreshInterval;
    }
    
    /**
     * Establece el intervalo de actualización en milisegundos.
     */
    public static void setRefreshInterval(int interval) {
        refreshInterval = interval;
    }
    
    /**
     * Obtiene el intervalo de actualización en segundos.
     */
    public static int getRefreshIntervalSeconds() {
        return refreshInterval / 1000;
    }
    
    /**
     * Establece el intervalo de actualización en segundos.
     */
    public static void setRefreshIntervalSeconds(int seconds) {
        refreshInterval = seconds * 1000;
    }
    
    /**
     * Obtiene una descripción legible del intervalo.
     */
    public static String getIntervalDescription() {
        int seconds = getRefreshIntervalSeconds();
        if (seconds < 60) {
            return seconds + " segundos";
        } else {
            int minutes = seconds / 60;
            return minutes + " minuto" + (minutes > 1 ? "s" : "");
        }
    }
}
