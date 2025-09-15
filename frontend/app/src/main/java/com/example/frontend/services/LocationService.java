package com.example.frontend.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.Criteria;
import android.util.Log;
import com.example.frontend.api.RetrofitClient;
import com.example.frontend.api.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.CompletableFuture;
import android.os.Handler;
import android.os.Looper;
import android.os.Bundle;

public class LocationService {
    private static final String TAG = "Curr LocationService";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location lastLoggedLocation;
    
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
     * Envía la ubicación actual al backend (PUT /users/me/location)
     */
    public CompletableFuture<Boolean> sendCurrentLocationToBackend() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        // Verificar sesión/token antes de llamar al backend
        try {
            com.example.frontend.utils.SessionManager sm = new com.example.frontend.utils.SessionManager(context);
            String fullToken = sm.getFullToken();
            if (fullToken == null || !sm.isLoggedIn()) {
                Log.w(TAG, "No hay token JWT o sesión no iniciada. Omitiendo envío de ubicación (evitar 401)");
                future.complete(false);
                return future;
            }
        } catch (Exception ignored) {}

        getCurrentLocation().thenAccept(location -> {
            ApiService api = RetrofitClient.getInstance(context).getRetrofit().create(ApiService.class);
            ApiService.UserLocationUpdate payload = new ApiService.UserLocationUpdate(
                    location.getLatitude(), location.getLongitude()
            );

            api.updateMyLocation(payload).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "updateMyLocation OK (HTTP " + response.code() + ")");
                        future.complete(true);
                    } else {
                        Log.w(TAG, "updateMyLocation fallo HTTP: " + response.code());
                        if (response.code() == 401) {
                            Log.w(TAG, "401 Unauthorized al actualizar ubicación. ¿Token JWT ausente o expirado?");
                        }
                        future.complete(false);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "updateMyLocation error de red: " + t.getMessage());
                    future.completeExceptionally(t);
                }
            });
        }).exceptionally(ex -> {
            Log.e(TAG, "sendCurrentLocationToBackend: no se pudo obtener ubicación: " + ex.getMessage());
            future.completeExceptionally(ex);
            return null;
        });

        return future;
    }

    /**
     * Verifica que los ajustes de ubicación cumplan los requisitos y, si no, solicita al usuario activarlos.
     * Muestra un diálogo del sistema si es resoluble.
     */
    public void checkAndPromptEnableLocation(Activity activity, long intervalMs, float minDistanceMeters) {
        try {
            LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
                    .setMinUpdateIntervalMillis(Math.max(1000, intervalMs / 2))
                    .setMinUpdateDistanceMeters(Math.max(0.0f, minDistanceMeters))
                    .build();

            LocationSettingsRequest settingsRequest = new LocationSettingsRequest.Builder()
                    .addLocationRequest(request)
                    .setAlwaysShow(true)
                    .build();

            SettingsClient client = LocationServices.getSettingsClient(activity);
            client.checkLocationSettings(settingsRequest)
                    .addOnSuccessListener((LocationSettingsResponse response) -> {
                        Log.d(TAG, "Ajustes de ubicación OK (GPS/Alta precisión activo)");
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Ajustes de ubicación NO satisfechos: " + e.getMessage());
                        if (e instanceof ResolvableApiException) {
                            try {
                                ((ResolvableApiException) e).startResolutionForResult(activity, 2001);
                                Log.d(TAG, "Mostrando diálogo para activar ubicación");
                            } catch (Exception ex) {
                                Log.e(TAG, "No se pudo mostrar el diálogo de activación de ubicación", ex);
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error verificando ajustes de ubicación", e);
        }
    }

    /**
     * Inicia el logging continuo de la ubicación para verificar los cambios al moverse.
     * Escribe en Logcat con tag "LocationService" cada actualización y la distancia desde la anterior.
     * 
     * @param intervalMs Intervalo deseado entre actualizaciones (p.ej., 5000 ms)
     * @param minDistanceMeters Distancia mínima para considerar un cambio significativo (p.ej., 5 m)
     */
    public void startLocationLogging(long intervalMs, float minDistanceMeters) {
        if (!hasLocationPermissions()) {
            Log.w(TAG, "No se puede iniciar logging: faltan permisos de ubicación");
            return;
        }

        if (locationCallback != null) {
            Log.d(TAG, "Logging ya estaba iniciado");
            return;
        }

        LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
                .setMinUpdateIntervalMillis(Math.max(1000, intervalMs / 2))
                .setMinUpdateDistanceMeters(Math.max(0.0f, minDistanceMeters))
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLastLocation() == null) {
                    Log.w(TAG, "onLocationResult sin datos de ubicación");
                    return;
                }
                Location loc = locationResult.getLastLocation();

                double lat = loc.getLatitude();
                double lon = loc.getLongitude();

                if (lastLoggedLocation == null) {
                    Log.i(TAG, String.format("Inicio logging: lat=%.6f, lon=%.6f", lat, lon));
                } else {
                    float delta = lastLoggedLocation.distanceTo(loc);
                    Log.i(TAG, String.format("Update: lat=%.6f, lon=%.6f, Δ=%.1f m", lat, lon, delta));
                }

                lastLoggedLocation = loc;
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
            Log.d(TAG, "Logging de ubicación iniciado (intervalMs=" + intervalMs + ", minDistance=" + minDistanceMeters + "m)");
        } catch (SecurityException e) {
            Log.e(TAG, "Error iniciando logging de ubicación", e);
            locationCallback = null;
        }
    }

    /**
     * Detiene el logging continuo de la ubicación.
     */
    public void stopLocationLogging() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
            Log.d(TAG, "Logging de ubicación detenido");
        }
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
     * Obtiene ubicación con fallback: Fused -> lastKnown -> LocationManager single update
     */
    public CompletableFuture<Location> getCurrentLocationWithFallback() {
        CompletableFuture<Location> future = new CompletableFuture<>();

        getCurrentLocation().thenAccept(location -> {
            if (location != null) {
                future.complete(location);
            } else {
                Log.w(TAG, "FusedLocationProvider devolvió null. Probando lastKnown...");
                getLastKnownLocation().thenAccept(last -> {
                    if (last != null) {
                        future.complete(last);
                    } else {
                        Log.w(TAG, "lastKnown también es null. Solicitando actualización única por LocationManager...");
                        requestSingleUpdateWithLocationManager().thenAccept(future::complete)
                                .exceptionally(ex -> { future.completeExceptionally(ex); return null; });
                    }
                }).exceptionally(ex -> {
                    Log.e(TAG, "Error obteniendo lastKnown: "+ex.getMessage());
                    requestSingleUpdateWithLocationManager().thenAccept(future::complete)
                            .exceptionally(ex2 -> { future.completeExceptionally(ex2); return null; });
                    return null;
                });
            }
        }).exceptionally(ex -> {
            Log.e(TAG, "Error en getCurrentLocation (Fused): "+ex.getMessage());
            getLastKnownLocation().thenAccept(last -> {
                if (last != null) {
                    future.complete(last);
                } else {
                    requestSingleUpdateWithLocationManager().thenAccept(future::complete)
                            .exceptionally(ex2 -> { future.completeExceptionally(ex2); return null; });
                }
            });
            return null;
        });

        return future;
    }

    private CompletableFuture<Location> requestSingleUpdateWithLocationManager() {
        CompletableFuture<Location> future = new CompletableFuture<>();
        try {
            if (!hasLocationPermissions()) {
                future.completeExceptionally(new SecurityException("Permisos de ubicación no concedidos"));
                return future;
            }

            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (lm == null) {
                future.completeExceptionally(new RuntimeException("LocationManager no disponible"));
                return future;
            }

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = lm.getBestProvider(criteria, true);

            // Intentar last known de ambos proveedores como parte del fallback inmediato
            Location lastGps = null, lastNet = null;
            try { lastGps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER); } catch (Exception ignored) {}
            try { lastNet = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); } catch (Exception ignored) {}
            Location best = pickBetter(lastGps, lastNet);
            if (best != null) {
                Log.d(TAG, "Fallback LocationManager lastKnown: lat="+best.getLatitude()+", lon="+best.getLongitude());
                future.complete(best);
                return future;
            }

            // Solicitar una única actualización y resolver
            final android.location.LocationListener listener = new android.location.LocationListener() {
                @Override public void onLocationChanged(Location location) {
                    Log.d(TAG, String.format("Single update (LM): lat=%.6f, lon=%.6f", location.getLatitude(), location.getLongitude()));
                    future.complete(location);
                    try { lm.removeUpdates(this); } catch (Exception ignored) {}
                }
                @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override public void onProviderEnabled(String provider) {}
                @Override public void onProviderDisabled(String provider) {}
            };

            // Usar NETWORK primero si disponible para rapidez
            boolean requested = false;
            try {
                if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, listener, Looper.getMainLooper());
                    requested = true;
                }
            } catch (Exception ignored) {}
            try {
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, listener, Looper.getMainLooper());
                    requested = true;
                }
            } catch (Exception ignored) {}

            if (!requested) {
                future.completeExceptionally(new RuntimeException("No hay proveedores de ubicación habilitados"));
            }

            // Seguridad: timeout a 8s para no colgar
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!future.isDone()) {
                    try { lm.removeUpdates(listener); } catch (Exception ignored) {}
                    future.completeExceptionally(new RuntimeException("Timeout esperando ubicación (LM)"));
                }
            }, 8000);

        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    private Location pickBetter(Location a, Location b) {
        if (a == null) return b;
        if (b == null) return a;
        long ta = a.getTime();
        long tb = b.getTime();
        return ta >= tb ? a : b;
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

