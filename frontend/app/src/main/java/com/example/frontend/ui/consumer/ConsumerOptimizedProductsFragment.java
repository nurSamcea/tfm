package com.example.frontend.ui.consumer;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.services.LocationService;
import com.example.frontend.services.ProductOptimizationService;
import com.example.frontend.ui.adapters.ProductWithDistanceAdapter;
import com.example.frontend.ui.location.LocationPreferencesFragment;
import com.example.frontend.utils.LocationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ConsumerOptimizedProductsFragment extends Fragment {
    private static final String TAG = "ConsumerOptimizedProducts";
    
    private LocationService locationService;
    private ProductOptimizationService optimizationService;
    private ProductWithDistanceAdapter productAdapter;
    
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView statusText;
    private TextView locationInfoText;
    private Button refreshButton;
    private Button settingsButton;
    
    private Location userLocation;
    private double maxDistanceKm = 10.0;
    private List<Product> optimizedProducts = new ArrayList<>();
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationService = new LocationService(requireContext());
        optimizationService = new ProductOptimizationService(requireContext());
        
        // Cargar preferencias de distancia
        maxDistanceKm = LocationPreferencesFragment.getMaxDistance(requireContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumer_optimized_products, container, false);
        
        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();
        
        // Iniciar el flujo de optimización
        startOptimizationFlow();
        
        return view;
    }
    
    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_products);
        progressBar = view.findViewById(R.id.progress_bar);
        statusText = view.findViewById(R.id.status_text);
        locationInfoText = view.findViewById(R.id.location_info_text);
        refreshButton = view.findViewById(R.id.btn_refresh);
        settingsButton = view.findViewById(R.id.btn_settings);
    }
    
    private void setupRecyclerView() {
        productAdapter = new ProductWithDistanceAdapter(new ProductWithDistanceAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                Log.d(TAG, "Producto clickeado: " + product.getName());
                Toast.makeText(requireContext(), "Producto: " + product.getName(), Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onViewDetails(Product product) {
                Log.d(TAG, "Ver detalles: " + product.getName());
                Toast.makeText(requireContext(), "Detalles de: " + product.getName(), Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onAddToCart(Product product) {
                Log.d(TAG, "Añadir al carrito: " + product.getName());
                Toast.makeText(requireContext(), "Añadido al carrito: " + product.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(productAdapter);
    }
    
    private void setupClickListeners() {
        refreshButton.setOnClickListener(v -> startOptimizationFlow());
        settingsButton.setOnClickListener(v -> showLocationPreferences());
    }
    
    private void startOptimizationFlow() {
        showLoading(true);
        statusText.setText("Iniciando optimización de productos...");
        
        // Paso 1: Verificar permisos de ubicación
        if (!locationService.hasLocationPermissions()) {
            showLocationPermissionRequest();
            return;
        }
        
        // Paso 2: Obtener ubicación del usuario
        getCurrentLocation();
    }
    
    private void showLocationPermissionRequest() {
        ConsumerLocationRequestFragment locationFragment = new ConsumerLocationRequestFragment();
        locationFragment.setOnLocationObtainedListener(new ConsumerLocationRequestFragment.OnLocationObtainedListener() {
            @Override
            public void onLocationObtained(double latitude, double longitude) {
                // Crear objeto Location con las coordenadas obtenidas
                userLocation = new Location("manual");
                userLocation.setLatitude(latitude);
                userLocation.setLongitude(longitude);
                
                // Continuar con la optimización
                loadOptimizedProducts();
            }
            
            @Override
            public void onLocationSkipped() {
                // Continuar sin ubicación
                userLocation = null;
                loadProductsWithoutLocation();
            }
        });
        
        // Mostrar el fragmento de solicitud de ubicación
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, locationFragment)
                .addToBackStack(null)
                .commit();
    }
    
    private void getCurrentLocation() {
        statusText.setText("Obteniendo ubicación...");
        
        CompletableFuture<Location> locationFuture = locationService.getCurrentLocation();
        
        locationFuture.thenAccept(location -> {
            requireActivity().runOnUiThread(() -> {
                if (location != null) {
                    userLocation = location;
                    updateLocationInfo();
                    loadOptimizedProducts();
                } else {
                    showLoading(false);
                    statusText.setText("No se pudo obtener la ubicación. Continuando sin optimización de distancia.");
                    loadProductsWithoutLocation();
                }
            });
        }).exceptionally(throwable -> {
            requireActivity().runOnUiThread(() -> {
                showLoading(false);
                statusText.setText("Error obteniendo ubicación: " + throwable.getMessage());
                loadProductsWithoutLocation();
            });
            return null;
        });
    }
    
    private void updateLocationInfo() {
        if (userLocation != null) {
            locationInfoText.setText(String.format(
                "Ubicación: %.4f, %.4f\nRango de búsqueda: %s",
                userLocation.getLatitude(),
                userLocation.getLongitude(),
                LocationUtils.formatDistance(maxDistanceKm)
            ));
            locationInfoText.setVisibility(View.VISIBLE);
        } else {
            locationInfoText.setVisibility(View.GONE);
        }
    }
    
    private void loadOptimizedProducts() {
        if (userLocation == null) {
            loadProductsWithoutLocation();
            return;
        }
        
        statusText.setText("Optimizando productos según tu ubicación...");
        
        // Crear filtros con distancia máxima
        Map<String, Object> filters = ProductOptimizationService.createFiltersWithMaxDistance(maxDistanceKm);
        
        // Crear pesos para la optimización (priorizar distancia y precio)
        Map<String, Float> weights = new HashMap<>();
        weights.put("distance", 0.4f);  // Priorizar distancia
        weights.put("price", 0.3f);     // Priorizar precio
        weights.put("sustainability", 0.2f);
        weights.put("eco", 0.1f);
        
        // Llamar al servicio de optimización
        CompletableFuture<List<Product>> future = optimizationService.getOptimizedProducts(
                userLocation.getLatitude(),
                userLocation.getLongitude(),
                filters,
                weights,
                "optimal"
        );
        
        future.thenAccept(products -> {
            requireActivity().runOnUiThread(() -> {
                optimizedProducts = products;
                productAdapter.setUserLocation(userLocation);
                productAdapter.setMaxDistance(maxDistanceKm);
                productAdapter.submitList(optimizedProducts);
                
                showLoading(false);
                updateStatusMessage();
            });
        }).exceptionally(throwable -> {
            requireActivity().runOnUiThread(() -> {
                Log.e(TAG, "Error cargando productos optimizados", throwable);
                showLoading(false);
                statusText.setText("Error cargando productos optimizados: " + throwable.getMessage());
                statusText.setTextColor(getResources().getColor(R.color.error));
                
                // Intentar cargar productos sin optimización como fallback
                loadProductsWithoutLocation();
            });
            return null;
        });
    }
    
    private void loadProductsWithoutLocation() {
        statusText.setText("Cargando productos disponibles...");
        
        // Cargar productos sin optimización de distancia
        Map<String, Object> filters = ProductOptimizationService.createDefaultFilters();
        filters.remove("distance"); // Remover filtro de distancia
        
        Map<String, Float> weights = new HashMap<>();
        weights.put("price", 0.5f);     // Priorizar precio
        weights.put("sustainability", 0.3f);
        weights.put("eco", 0.2f);
        
        // Usar coordenadas por defecto (Madrid) para el algoritmo
        CompletableFuture<List<Product>> future = optimizationService.getOptimizedProducts(
                40.4168, // Madrid lat
                -3.7038, // Madrid lon
                filters,
                weights,
                "price"
        );
        
        future.thenAccept(products -> {
            requireActivity().runOnUiThread(() -> {
                optimizedProducts = products;
                productAdapter.setUserLocation(null); // Sin ubicación del usuario
                productAdapter.setMaxDistance(Double.MAX_VALUE); // Sin límite de distancia
                productAdapter.submitList(optimizedProducts);
                
                showLoading(false);
                statusText.setText("Productos cargados sin optimización de distancia");
                statusText.setTextColor(getResources().getColor(R.color.warning));
            });
        }).exceptionally(throwable -> {
            requireActivity().runOnUiThread(() -> {
                Log.e(TAG, "Error cargando productos", throwable);
                showLoading(false);
                statusText.setText("Error cargando productos: " + throwable.getMessage());
                statusText.setTextColor(getResources().getColor(R.color.error));
            });
            return null;
        });
    }
    
    private void updateStatusMessage() {
        if (optimizedProducts.isEmpty()) {
            statusText.setText("No se encontraron productos en el rango de " + 
                    LocationUtils.formatDistance(maxDistanceKm));
            statusText.setTextColor(getResources().getColor(R.color.warning));
        } else {
            statusText.setText(String.format(
                "Encontrados %d productos optimizados para tu ubicación",
                optimizedProducts.size()
            ));
            statusText.setTextColor(getResources().getColor(R.color.success));
        }
    }
    
    private void showLocationPreferences() {
        LocationPreferencesFragment preferencesFragment = new LocationPreferencesFragment();
        preferencesFragment.setOnPreferencesSavedListener(new LocationPreferencesFragment.OnPreferencesSavedListener() {
            @Override
            public void onPreferencesSaved(double maxDistanceKm, boolean showDistance, boolean sortByDistance) {
                ConsumerOptimizedProductsFragment.this.maxDistanceKm = maxDistanceKm;
                updateLocationInfo();
                loadOptimizedProducts();
                Toast.makeText(requireContext(), "Preferencias actualizadas", Toast.LENGTH_SHORT).show();
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
    
    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        refreshButton.setEnabled(!loading);
    }
}
