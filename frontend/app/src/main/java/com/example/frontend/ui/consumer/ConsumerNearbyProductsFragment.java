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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.services.LocationService;
import com.example.frontend.ui.adapters.ProductWithDistanceAdapter;
import com.example.frontend.ui.location.LocationPermissionFragment;
import com.example.frontend.ui.location.LocationPreferencesFragment;
import com.example.frontend.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConsumerNearbyProductsFragment extends Fragment {
    private static final String TAG = "ConsumerNearbyProducts";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private LocationService locationService;
    private ProductWithDistanceAdapter productAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView statusText;
    private Button refreshButton;
    private Button settingsButton;
    
    private Location userLocation;
    private double maxDistanceKm = 10.0;
    private List<Product> nearbyProducts = new ArrayList<>();
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationService = new LocationService(requireContext());
        
        // Cargar preferencias de distancia
        maxDistanceKm = LocationPreferencesFragment.getMaxDistance(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumer_nearby_products, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();
        
        // Verificar permisos y cargar datos
        checkLocationPermissionsAndLoadData();
        
        return view;
    }
    
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_products);
        progressBar = view.findViewById(R.id.progress_bar);
        statusText = view.findViewById(R.id.status_text);
        refreshButton = view.findViewById(R.id.btn_refresh);
        settingsButton = view.findViewById(R.id.btn_settings);
    }
    
    private void setupRecyclerView() {
        productAdapter = new ProductWithDistanceAdapter(new ProductWithDistanceAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Manejar click en producto
                Log.d(TAG, "Producto clickeado: " + product.getName());
            }
            
            @Override
            public void onViewDetails(Product product) {
                // Mostrar detalles del producto
                Log.d(TAG, "Ver detalles: " + product.getName());
            }
            
            @Override
            public void onAddToCart(Product product) {
                // Añadir al carrito
                Log.d(TAG, "Añadir al carrito: " + product.getName());
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(productAdapter);
    }
    
    private void setupClickListeners() {
        refreshButton.setOnClickListener(v -> {
            checkLocationPermissionsAndLoadData();
        });
        
        settingsButton.setOnClickListener(v -> {
            showLocationPreferences();
        });
    }
    
    private void checkLocationPermissionsAndLoadData() {
        if (!locationService.hasLocationPermissions()) {
            showLocationPermissionDialog();
        } else {
            loadNearbyProducts();
        }
    }
    
    private void showLocationPermissionDialog() {
        LocationPermissionFragment permissionFragment = new LocationPermissionFragment();
        permissionFragment.setOnLocationPermissionResultListener(new LocationPermissionFragment.OnLocationPermissionResultListener() {
            @Override
            public void onPermissionGranted() {
                loadNearbyProducts();
            }
            
            @Override
            public void onPermissionDenied() {
                showStatusMessage("Permisos de ubicación denegados. No se pueden mostrar productos cercanos.", false);
            }
            
            @Override
            public void onPermissionSkipped() {
                showStatusMessage("Puedes activar la ubicación más tarde en configuración.", false);
            }
        });
        
        // Mostrar el fragmento de permisos
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, permissionFragment)
                .addToBackStack(null)
                .commit();
    }
    
    private void showLocationPreferences() {
        LocationPreferencesFragment preferencesFragment = new LocationPreferencesFragment();
        preferencesFragment.setOnPreferencesSavedListener(new LocationPreferencesFragment.OnPreferencesSavedListener() {
            @Override
            public void onPreferencesSaved(double maxDistanceKm, boolean showDistance, boolean sortByDistance) {
                ConsumerNearbyProductsFragment.this.maxDistanceKm = maxDistanceKm;
                loadNearbyProducts();
                Toast.makeText(requireContext(), "Preferencias guardadas", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onPreferencesCancelled() {
                // No hacer nada
            }
        });
        
        // Mostrar el fragmento de preferencias
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, preferencesFragment)
                .addToBackStack(null)
                .commit();
    }
    
    private void loadNearbyProducts() {
        showLoading(true);
        
        // Obtener ubicación actual
        CompletableFuture<Location> locationFuture = locationService.getCurrentLocation();
        
        locationFuture.thenAccept(location -> {
            if (location != null) {
                userLocation = location;
                productAdapter.setUserLocation(location);
                productAdapter.setMaxDistance(maxDistanceKm);
                
                // Simular carga de productos (aquí harías la llamada al API)
                loadProductsFromAPI(location);
            } else {
                showStatusMessage("No se pudo obtener la ubicación actual", false);
            }
        }).exceptionally(throwable -> {
            Log.e(TAG, "Error obteniendo ubicación", throwable);
            showStatusMessage("Error obteniendo ubicación: " + throwable.getMessage(), false);
            return null;
        });
    }
    
    private void loadProductsFromAPI(Location location) {
        // Aquí harías la llamada real al API usando los endpoints que creamos
        // Por ahora simulamos datos de ejemplo
        
        // Simular delay de red
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                
                // Simular productos de ejemplo
                List<Product> mockProducts = createMockProducts(location);
                
                requireActivity().runOnUiThread(() -> {
                    nearbyProducts = mockProducts;
                    productAdapter.submitList(nearbyProducts);
                    showLoading(false);
                    
                    if (nearbyProducts.isEmpty()) {
                        showStatusMessage("No se encontraron productos cercanos en el rango de " + 
                                LocationUtils.formatDistance(maxDistanceKm), false);
                    } else {
                        showStatusMessage("Encontrados " + nearbyProducts.size() + " productos cercanos", true);
                    }
                });
                
            } catch (InterruptedException e) {
                requireActivity().runOnUiThread(() -> {
                    showStatusMessage("Error cargando productos", false);
                });
            }
        }).start();
    }
    
    private List<Product> createMockProducts(Location userLocation) {
        List<Product> products = new ArrayList<>();
        
        // Crear productos de ejemplo con ubicaciones cercanas
        double baseLat = userLocation.getLatitude();
        double baseLon = userLocation.getLongitude();
        
        // Producto 1 - Muy cerca
        Product product1 = new Product();
        product1.setId("1");
        product1.setName("Tomates Cherry Orgánicos");
        product1.setPrice(3.50);
        product1.setStock(25);
        product1.setProviderName("Granja Verde");
        product1.setProviderLat(baseLat + 0.001); // ~100m
        product1.setProviderLon(baseLon + 0.001);
        products.add(product1);
        
        // Producto 2 - Cerca
        Product product2 = new Product();
        product2.setId("2");
        product2.setName("Lechuga Romana");
        product2.setPrice(2.80);
        product2.setStock(15);
        product2.setProviderName("Huerto Familiar");
        product2.setProviderLat(baseLat + 0.01); // ~1km
        product2.setProviderLon(baseLon + 0.01);
        products.add(product2);
        
        // Producto 3 - Moderado
        Product product3 = new Product();
        product3.setId("3");
        product3.setName("Pepinos Ecológicos");
        product3.setPrice(4.20);
        product3.setStock(30);
        product3.setProviderName("Cultivos Sostenibles");
        product3.setProviderLat(baseLat + 0.05); // ~5km
        product3.setProviderLon(baseLon + 0.05);
        products.add(product3);
        
        return products;
    }
    
    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        refreshButton.setEnabled(!loading);
    }
    
    private void showStatusMessage(String message, boolean isSuccess) {
        statusText.setText(message);
        statusText.setTextColor(getResources().getColor(
                isSuccess ? R.color.success : R.color.error
        ));
        statusText.setVisibility(View.VISIBLE);
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
                loadNearbyProducts();
            } else {
                showStatusMessage("Permisos de ubicación denegados", false);
            }
        }
    }
}








