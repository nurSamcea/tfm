package com.example.frontend.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.CompletableFuture;

public class LocationService {
    private static final String TAG = "LocationService";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    
    public LocationService(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }
    
    /**
     * Verifica si se tienen los permisos de ubicación
     */
    public boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Solicita permisos de ubicación al usuario
     */
    public void requestLocationPermissions(Activity activity) {
        if (!hasLocationPermissions()) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    
    /**
     * Obtiene la ubicación actual del usuario de forma asíncrona
     */
    public CompletableFuture<Location> getCurrentLocation() {
        CompletableFuture<Location> future = new CompletableFuture<>();
        
        if (!hasLocationPermissions()) {
            Log.w(TAG, "No se tienen permisos de ubicación");
            future.completeExceptionally(new SecurityException("Permisos de ubicación no concedidos"));
            return future;
        }
        
        try {
            Task<Location> locationTask = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
            );
            
            locationTask.addOnSuccessListener(location -> {
                if (location != null) {
                    Log.d(TAG, "Ubicación obtenida: " + location.getLatitude() + ", " + location.getLongitude());
                    future.complete(location);
                } else {
                    Log.w(TAG, "No se pudo obtener la ubicación");
                    future.completeExceptionally(new RuntimeException("No se pudo obtener la ubicación"));
                }
            });
            
            locationTask.addOnFailureListener(exception -> {
                Log.e(TAG, "Error al obtener ubicación", exception);
                future.completeExceptionally(exception);
            });
            
        } catch (SecurityException e) {
            Log.e(TAG, "Error de seguridad al obtener ubicación", e);
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Obtiene la última ubicación conocida (más rápida pero menos precisa)
     */
    public CompletableFuture<Location> getLastKnownLocation() {
        CompletableFuture<Location> future = new CompletableFuture<>();
        
        if (!hasLocationPermissions()) {
            Log.w(TAG, "No se tienen permisos de ubicación");
            future.completeExceptionally(new SecurityException("Permisos de ubicación no concedidos"));
            return future;
        }
        
        try {
            Task<Location> locationTask = fusedLocationClient.getLastLocation();
            
            locationTask.addOnSuccessListener(location -> {
                if (location != null) {
                    Log.d(TAG, "Última ubicación conocida: " + location.getLatitude() + ", " + location.getLongitude());
                    future.complete(location);
                } else {
                    Log.w(TAG, "No hay última ubicación conocida");
                    future.completeExceptionally(new RuntimeException("No hay última ubicación conocida"));
                }
            });
            
            locationTask.addOnFailureListener(exception -> {
                Log.e(TAG, "Error al obtener última ubicación", exception);
                future.completeExceptionally(exception);
            });
            
        } catch (SecurityException e) {
            Log.e(TAG, "Error de seguridad al obtener última ubicación", e);
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Calcula la distancia entre dos puntos en kilómetros
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en kilómetros
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        
        return distance;
    }
    
    /**
     * Formatea la distancia para mostrar al usuario
     */
    public static String formatDistance(double distanceKm) {
        if (distanceKm < 1) {
            return String.format("%.0f m", distanceKm * 1000);
        } else if (distanceKm < 10) {
            return String.format("%.1f km", distanceKm);
        } else {
            return String.format("%.0f km", distanceKm);
        }
    }
}

