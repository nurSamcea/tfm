package com.example.frontend.ui.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import com.example.frontend.services.LocationService;

public class LocationPermissionFragment extends Fragment {
    private static final String TAG = "LocationPermissionFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private LocationService locationService;
    private Button btnRequestPermission;
    private Button btnSkip;
    private TextView tvPermissionDescription;
    private TextView tvPermissionBenefits;
    
    private OnLocationPermissionResultListener permissionListener;
    
    public interface OnLocationPermissionResultListener {
        void onPermissionGranted();
        void onPermissionDenied();
        void onPermissionSkipped();
    }
    
    public void setOnLocationPermissionResultListener(OnLocationPermissionResultListener listener) {
        this.permissionListener = listener;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationService = new LocationService(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_permission, container, false);
        
        initializeViews(view);
        setupClickListeners();
        updateUI();
        
        return view;
    }
    
    private void initializeViews(View view) {
        btnRequestPermission = view.findViewById(R.id.btn_request_location_permission);
        btnSkip = view.findViewById(R.id.btn_skip_location);
        tvPermissionDescription = view.findViewById(R.id.tv_permission_description);
        tvPermissionBenefits = view.findViewById(R.id.tv_permission_benefits);
    }
    
    private void setupClickListeners() {
        btnRequestPermission.setOnClickListener(v -> requestLocationPermission());
        btnSkip.setOnClickListener(v -> {
            if (permissionListener != null) {
                permissionListener.onPermissionSkipped();
            }
        });
    }
    
    private void updateUI() {
        if (locationService.hasLocationPermissions()) {
            // Si ya tiene permisos, mostrar mensaje de confirmación
            tvPermissionDescription.setText("¡Perfecto! Ya tienes permisos de ubicación activados.");
            btnRequestPermission.setText("Continuar");
            btnRequestPermission.setOnClickListener(v -> {
                if (permissionListener != null) {
                    permissionListener.onPermissionGranted();
                }
            });
        } else {
            // Mostrar información sobre por qué necesitamos la ubicación
            tvPermissionDescription.setText("Para mostrarte los productores y supermercados más cercanos, necesitamos acceder a tu ubicación.");
            tvPermissionBenefits.setText("• Encuentra productores locales\n• Calcula distancias automáticamente\n• Optimiza tus rutas de compra");
            btnRequestPermission.setText("Activar ubicación");
        }
    }
    
    private void requestLocationPermission() {
        if (locationService.hasLocationPermissions()) {
            if (permissionListener != null) {
                permissionListener.onPermissionGranted();
            }
            return;
        }
        
        // Solicitar permisos
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
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
                Toast.makeText(requireContext(), "Permisos de ubicación concedidos", Toast.LENGTH_SHORT).show();
                if (permissionListener != null) {
                    permissionListener.onPermissionGranted();
                }
            } else {
                Toast.makeText(requireContext(), "Permisos de ubicación denegados. Puedes activarlos más tarde en configuración.", Toast.LENGTH_LONG).show();
                if (permissionListener != null) {
                    permissionListener.onPermissionDenied();
                }
            }
        }
    }
    
    /**
     * Método estático para verificar si se tienen permisos de ubicación
     */
    public static boolean hasLocationPermissions(android.content.Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
    }
}

