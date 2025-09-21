package com.example.frontend.utils;

import android.location.Location;

/**
 * Utilidades para manejo de ubicaciones y cálculos geográficos
 */
public class LocationUtils {
    
    /**
     * Radio de la Tierra en kilómetros
     */
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine
     * @param lat1 Latitud del primer punto
     * @param lon1 Longitud del primer punto
     * @param lat2 Latitud del segundo punto
     * @param lon2 Longitud del segundo punto
     * @return Distancia en kilómetros
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Calcula la distancia entre dos objetos Location
     * @param location1 Primera ubicación
     * @param location2 Segunda ubicación
     * @return Distancia en kilómetros
     */
    public static double calculateDistance(Location location1, Location location2) {
        if (location1 == null || location2 == null) {
            return Double.MAX_VALUE;
        }
        return calculateDistance(
                location1.getLatitude(), location1.getLongitude(),
                location2.getLatitude(), location2.getLongitude()
        );
    }
    
    /**
     * Formatea la distancia para mostrar al usuario de forma legible
     * @param distanceKm Distancia en kilómetros
     * @return String formateado (ej: "250 m", "1.2 km", "15 km")
     */
    public static String formatDistance(double distanceKm) {
        if (distanceKm < 0.1) {
            return String.format("%.0f m", distanceKm * 1000);
        } else if (distanceKm < 1) {
            return String.format("%.0f m", distanceKm * 1000);
        } else if (distanceKm < 10) {
            return String.format("%.1f km", distanceKm);
        } else {
            return String.format("%.0f km", distanceKm);
        }
    }
    
    /**
     * Formatea la distancia con un símbolo de dirección
     * @param distanceKm Distancia en kilómetros
     * @return String formateado con símbolo (ej: "250 m ↗", "1.2 km ↗")
     */
    public static String formatDistanceWithDirection(double distanceKm) {
        return formatDistance(distanceKm) + " ↗";
    }
    
    /**
     * Verifica si una distancia está dentro del rango especificado
     * @param distanceKm Distancia en kilómetros
     * @param maxDistanceKm Distancia máxima en kilómetros
     * @return true si está dentro del rango
     */
    public static boolean isWithinRange(double distanceKm, double maxDistanceKm) {
        return distanceKm <= maxDistanceKm;
    }
    
    /**
     * Convierte metros a kilómetros
     * @param meters Distancia en metros
     * @return Distancia en kilómetros
     */
    public static double metersToKilometers(double meters) {
        return meters / 1000.0;
    }
    
    /**
     * Convierte kilómetros a metros
     * @param kilometers Distancia en kilómetros
     * @return Distancia en metros
     */
    public static double kilometersToMeters(double kilometers) {
        return kilometers * 1000.0;
    }
    
    /**
     * Valida si las coordenadas son válidas
     * @param latitude Latitud
     * @param longitude Longitud
     * @return true si las coordenadas son válidas
     */
    public static boolean isValidCoordinate(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && 
               longitude >= -180 && longitude <= 180;
    }
    
    /**
     * Obtiene un emoji basado en la distancia para mostrar en la UI
     * @param distanceKm Distancia en kilómetros
     * @return Emoji representativo de la distancia
     */
    public static String getDistanceEmoji(double distanceKm) {
        if (distanceKm < 0.5) {
            return "🟢"; // Muy cerca
        } else if (distanceKm < 2) {
            return "🟡"; // Cerca
        } else if (distanceKm < 10) {
            return "🟠"; // Moderado
        } else {
            return "🔴"; // Lejos
        }
    }
    
    /**
     * Obtiene una descripción textual de la distancia
     * @param distanceKm Distancia en kilómetros
     * @return Descripción de la distancia
     */
    public static String getDistanceDescription(double distanceKm) {
        if (distanceKm < 0.5) {
            return "Muy cerca";
        } else if (distanceKm < 2) {
            return "Cerca";
        } else if (distanceKm < 10) {
            return "Moderado";
        } else {
            return "Lejos";
        }
    }
}

