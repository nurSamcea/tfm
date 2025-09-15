package com.example.frontend.ui.consumer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import com.example.frontend.services.LocationService;
import com.example.frontend.utils.LocationUtils;

import java.util.concurrent.CompletableFuture;

public class ConsumerLocationRequestFragment extends Fragment {
    private static final String TAG = "ConsumerLocationRequest";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private LocationService locationService;
    private ProgressBar progressBar;
    private TextView statusText;
    private TextView locationInfoText;
    private Button requestLocationButton;
    private Button skipLocationButton;
    private Button continueButton;
    
    private Location userLocation;
    private OnLocationObtainedListener locationListener;
    
    public interface OnLocationObtainedListener {
        void onLocationObtained(double latitude, double longitude);
        void onLocationSkipped();
    }
    
    public void setOnLocationObtainedListener(OnLocationObtainedListener listener) {
        this.locationListener = listener;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationService = new LocationService(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumer_location_request, container, false);
        
        initializeViews(view);
        setupClickListeners();
        updateUI();
        
        return view;
    }
    
    private void initializeViews(View view) {
        progressBar = view.findViewById(R.id.progress_bar);
        statusText = view.findViewById(R.id.status_text);
        locationInfoText = view.findViewById(R.id.location_info_text);
        requestLocationButton = view.findViewById(R.id.btn_request_location);
        skipLocationButton = view.findViewById(R.id.btn_skip_location);
        continueButton = view.findViewById(R.id.btn_continue);
    }
    
    private void setupClickListeners() {
        requestLocationButton.setOnClickListener(v -> requestUserLocation());
        skipLocationButton.setOnClickListener(v -> {
            if (locationListener != null) {
                locationListener.onLocationSkipped();
            }
        });
        continueButton.setOnClickListener(v -> {
            if (userLocation != null && locationListener != null) {
                locationListener.onLocationObtained(
                    userLocation.getLatitude(), 
                    userLocation.getLongitude()
                );
            }
        });
    }
    
    private void updateUI() {
        if (userLocation != null) {
            // Ubicación obtenida
            statusText.setText("¡Ubicación obtenida correctamente!");
            statusText.setTextColor(getResources().getColor(R.color.success));
            
            locationInfoText.setText(String.format(
                "Latitud: %.6f\nLongitud: %.6f\n\nCon esta ubicación podremos mostrarte los productores más cercanos y optimizar tus compras.",
                userLocation.getLatitude(),
                userLocation.getLongitude()
            ));
            locationInfoText.setVisibility(View.VISIBLE);
            
            requestLocationButton.setVisibility(View.GONE);
            skipLocationButton.setVisibility(View.GONE);
            continueButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            // Sin ubicación
            statusText.setText("Para mostrarte los mejores productos cercanos, necesitamos tu ubicación");
            statusText.setTextColor(getResources().getColor(R.color.text_secondary));
            
            locationInfoText.setVisibility(View.GONE);
            requestLocationButton.setVisibility(View.VISIBLE);
            skipLocationButton.setVisibility(View.VISIBLE);
            continueButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }
    
    private void requestUserLocation() {
        if (!locationService.hasLocationPermissions()) {
            // Solicitar permisos
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Ya tiene permisos, obtener ubicación
            getCurrentLocation();
        }
    }
    
    private void getCurrentLocation() {
        showLoading(true);
        statusText.setText("Obteniendo tu ubicación...");
        
        CompletableFuture<Location> locationFuture = locationService.getCurrentLocation();
        
        locationFuture.thenAccept(location -> {
            requireActivity().runOnUiThread(() -> {
                if (location != null) {
                    userLocation = location;
                    updateUI();
                    Toast.makeText(requireContext(), "Ubicación obtenida correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    showLoading(false);
                    statusText.setText("No se pudo obtener la ubicación. Intenta de nuevo.");
                    statusText.setTextColor(getResources().getColor(R.color.error));
                    Toast.makeText(requireContext(), "Error obteniendo ubicación", Toast.LENGTH_SHORT).show();
                }
            });
        }).exceptionally(throwable -> {
            requireActivity().runOnUiThread(() -> {
                showLoading(false);
                statusText.setText("Error obteniendo ubicación: " + throwable.getMessage());
                statusText.setTextColor(getResources().getColor(R.color.error));
                Toast.makeText(requireContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            });
            return null;
        });
    }
    
    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        requestLocationButton.setEnabled(!loading);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            
            if (allPermissionsGranted) {
                getCurrentLocation();
            } else {
                showLoading(false);
                statusText.setText("Permisos de ubicación denegados. Puedes continuar sin ubicación.");
                statusText.setTextColor(getResources().getColor(R.color.warning));
                Toast.makeText(requireContext(), "Permisos denegados. Continuando sin ubicación.", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    /**
     * Método para obtener las coordenadas del usuario (si están disponibles)
     */
    public double[] getUserCoordinates() {
        if (userLocation != null) {
            return new double[]{userLocation.getLatitude(), userLocation.getLongitude()};
        }
        return null;
    }
    
    /**
     * Método para verificar si se tiene ubicación del usuario
     */
    public boolean hasUserLocation() {
        return userLocation != null;
    }
}








