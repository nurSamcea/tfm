package com.example.frontend.ui.consumer;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearSnapHelper;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.model.ConsumerOrder;
import com.example.frontend.utils.CartPreferences;
import com.example.frontend.ui.adapters.ProductAdapter;
import com.example.frontend.ui.adapters.OrderItemCartAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.frontend.api.ApiService;
import com.example.frontend.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.frontend.api.ProductFilterRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class ConsumerProductsFragment extends Fragment {
    private static final String TAG = "ConsumerProductsFrag";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> products;
    private RecyclerView cartRecyclerView;
    private OrderItemCartAdapter cartAdapter;
    private List<ConsumerOrder.OrderItem> cartItems = new ArrayList<>();
    private EditText searchBar;
    private Chip filterDistance, filterPrice, filterGlutenFree, filterEco, filterCategory;
    private double totalCarrito = 0.0;
    private TextView cartTotalText;
    private CartPreferences cartPrefs;
    private FusedLocationProviderClient fusedLocationClient;
    private Double userLat = null;
    private Double userLon = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private Call<List<Product>> pendingApiCall = null; // Para cancelar llamadas pendientes

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Fragmento creado");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: Fragmento adjunto al contexto");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Vista del fragmento creada");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Fragmento iniciado");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Fragmento resumido");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Log.d(TAG, "onCreateView: Iniciando el fragm");
            View view = inflater.inflate(R.layout.fragment_consumer_products, container, false);
            Log.d(TAG, "onCreateView: Layout inflado correctamente");
            
            // Inicializar UI
            searchBar = view.findViewById(R.id.search_bar);
            filterDistance = view.findViewById(R.id.filter_distance);
            filterPrice = view.findViewById(R.id.filter_price);
            filterGlutenFree = view.findViewById(R.id.filter_gluten_free);
            filterEco = view.findViewById(R.id.filter_eco);
            filterCategory = view.findViewById(R.id.filter_category);
            Log.d(TAG, "onCreateView: UI básica inicializada");

        // Configurar el toggle de filtros desde el icono de la barra de búsqueda
        HorizontalScrollView filtersScroll = view.findViewById(R.id.filters_scroll);
        ImageButton filterButton = view.findViewById(R.id.filter_button);
        if (filterButton != null) {
            filterButton.setOnClickListener(v -> {
                if (filtersScroll.getVisibility() == View.GONE) {
                    filtersScroll.setVisibility(View.VISIBLE);
                } else {
                    filtersScroll.setVisibility(View.GONE);
                }
            });
        }

        // Inicializar CartPreferences en segundo plano para evitar StrictMode
        initializeCartPreferencesAsync();

        // Carrito
        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new OrderItemCartAdapter(cartItems);
        cartRecyclerView.setAdapter(cartAdapter);
        cartAdapter.setOnCartItemClickListener(new OrderItemCartAdapter.OnCartItemClickListener() {
            public void onQuantityChanged(ConsumerOrder.OrderItem item, int newQuantity) {
                item.setQuantity(newQuantity);
                actualizarTotalCarrito();
                if (cartPrefs != null) {
                    cartPrefs.saveCartItems(cartItems);
                }
                cartAdapter.notifyDataSetChanged();
            }
            public void onRemoveItem(ConsumerOrder.OrderItem item) {
                cartItems.remove(item);
                actualizarTotalCarrito();
                if (cartPrefs != null) {
                    cartPrefs.saveCartItems(cartItems);
                }
                cartAdapter.notifyDataSetChanged();
            }
        });

        // Productos
        recyclerView = view.findViewById(R.id.products_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        products = new ArrayList<>();
        adapter = new ProductAdapter(product -> {
            ConsumerOrder.OrderItem item = buscarEnCarrito(product.getId());
            if (item == null) {
                item = new ConsumerOrder.OrderItem(product.getId(), 1, product.getPrice());
                item.setProductName(product.getName());
                cartItems.add(item);
                showSafeToast("Añadido al carrito");
            } else {
                item.setQuantity(item.getQuantity() + 1);
            }
            actualizarTotalCarrito();
            if (cartPrefs != null) {
                cartPrefs.saveCartItems(cartItems);
            }
            cartAdapter.notifyDataSetChanged();
        }, true);
        recyclerView.setAdapter(adapter);

        // Cargar productos y estado
        loadSampleProducts();
        cartTotalText = view.findViewById(R.id.cart_total_text);
        actualizarTotalCarrito();

        Button btnFinalizeCart = view.findViewById(R.id.btn_finalize_cart);
        btnFinalizeCart.setOnClickListener(v -> {
            // Guardar carrito actual
            if (cartPrefs != null) {
                cartPrefs.saveCartItems(cartItems);
            }
            // Resetear carrito local de la pantalla 1
            cartItems.clear();
            cartAdapter.notifyDataSetChanged();
            actualizarTotalCarrito();
            // Mostrar mensaje de éxito
            showSafeToast("Carrito guardado correctamente");
            // Intentar navegar de forma segura
            try {
                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.navigation_consumer_purchases);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al navegar: " + e.getMessage());
                showSafeToast("Error al navegar al carrito");
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        obtenerUbicacionUsuario();

        Log.d(TAG, "onCreateView: Fragmento inicializado completamente");
        return view;
        
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreateView: " + e.getMessage(), e);
            // Retornar una vista simple en caso de error
            View errorView = new View(requireContext());
            errorView.setBackgroundColor(0xFF000000); // Fondo negro
            return errorView;
        }
    }

    private void obtenerUbicacionUsuario() {
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "obtenerUbicacionUsuario: Fragmento no adjunto o contexto nulo, cancelando obtención de ubicación");
            return;
        }
        
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(location -> {
                // Verificar que el fragmento siga adjunto antes de procesar la ubicación
                if (!isAdded() || getContext() == null) {
                    Log.w(TAG, "obtenerUbicacionUsuario: Fragmento no adjunto en callback de ubicación, cancelando procesamiento");
                    return;
                }
                
                if (location != null) {
                    userLat = location.getLatitude();
                    userLon = location.getLongitude();
                    // Si el filtro de distancia está marcado, recarga productos
                    if (filterDistance != null && filterDistance.isChecked()) {
                        loadSampleProducts();
                    }
                }
            });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionUsuario();
            }
        }
    }

    private ProductFilterRequest construirRequest() {
        ProductFilterRequest req = new ProductFilterRequest();
        req.search = searchBar.getText().toString();
        req.filters = new HashMap<>();
        req.filters.put("eco", filterEco.isChecked());
        req.filters.put("gluten_free", filterGlutenFree.isChecked());
        req.filters.put("price", filterPrice.isChecked());
        req.filters.put("distance", filterDistance.isChecked());

        boolean usarScore = filterDistance.isChecked();
        if (usarScore) {
            req.weights = new HashMap<>();
            if (filterPrice.isChecked()) {
                req.weights.put("price", 1.0f);
            }
            if (filterDistance.isChecked()) {
                req.weights.put("distance", 1.0f);
            }
            // Añadir ubicación si está disponible
            if (userLat != null && userLon != null) {
                req.user_lat = userLat;
                req.user_lon = userLon;
            }
        } else {
            req.weights = null;
            req.user_lat = null;
            req.user_lon = null;
        }
        return req;
    }

    private void loadSampleProducts() {
        // Verificar que el fragmento esté adjunto antes de continuar
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "loadSampleProducts: Fragmento no adjunto o contexto nulo, cancelando carga");
            return;
        }
        
        Log.d(TAG, "loadSampleProducts: Iniciando carga de productos");
        try {
            ProductFilterRequest req = construirRequest();
            Log.d(TAG, "loadSampleProducts: Request construido, creando ApiService");
            ApiService apiService = RetrofitClient.getInstance(requireContext()).getRetrofit().create(ApiService.class);
            Log.d(TAG, "loadSampleProducts: ApiService creado, haciendo llamada");
            Call<List<Product>> call = apiService.getProductsOptimized(req);
            pendingApiCall = call; // Almacenar la llamada pendiente
            call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                // Verificar que el fragmento siga adjunto antes de actualizar la UI
                if (!isAdded() || getContext() == null) {
                    Log.w(TAG, "loadSampleProducts: Fragmento no adjunto en onResponse, cancelando actualización");
                    return;
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    products.clear();
                    products.addAll(response.body());
                    adapter.submitList(new ArrayList<>(products));
                } else {
                    showSafeToast("Error al cargar productos");
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Verificar que el fragmento siga adjunto antes de mostrar el error
                if (!isAdded() || getContext() == null) {
                    Log.w(TAG, "loadSampleProducts: Fragmento no adjunto en onFailure, cancelando actualización");
                    return;
                }
                
                Log.e(TAG, "loadSampleProducts: Error de red: " + t.getMessage(), t);
                showSafeToast("Error de red: " + t.getMessage());
            }
        });
        } catch (Exception e) {
            // Verificar que el fragmento siga adjunto antes de mostrar el error
            if (!isAdded() || getContext() == null) {
                Log.w(TAG, "loadSampleProducts: Fragmento no adjunto en catch, cancelando actualización");
                return;
            }
            
            Log.e(TAG, "loadSampleProducts: Error general: " + e.getMessage(), e);
            showSafeToast("Error al cargar productos: " + e.getMessage());
        }
    }

    private void actualizarTotalCarrito() {
        totalCarrito = 0.0;
        for (ConsumerOrder.OrderItem item : cartItems) {
            totalCarrito += item.getUnitPrice() * item.getQuantity();
        }
        if (cartTotalText != null) {
            cartTotalText.setText(String.format(Locale.getDefault(), "Total: %.2f €", totalCarrito));
        }
    }

    private ConsumerOrder.OrderItem buscarEnCarrito(String productId) {
        for (ConsumerOrder.OrderItem item : cartItems) {
            if (item.getProductId().equals(productId)) return item;
        }
        return null;
    }

    private void initializeCartPreferencesAsync() {
        Log.d(TAG, "initializeCartPreferencesAsync: Iniciando inicialización asíncrona");
        new Thread(() -> {
            try {
                Log.d(TAG, "initializeCartPreferencesAsync: Creando CartPreferences");
                cartPrefs = new CartPreferences(requireContext());
                Log.d(TAG, "initializeCartPreferencesAsync: CartPreferences creado exitosamente");
                
                // Cargar estado guardado
                Log.d(TAG, "initializeCartPreferencesAsync: Cargando estado guardado");
                List<ConsumerOrder.OrderItem> savedCart = cartPrefs.getCartItems();
                String savedSearchQuery = cartPrefs.getSearchQuery();
                boolean savedFilterDistance = cartPrefs.getFilterDistance();
                boolean savedFilterPrice = cartPrefs.getFilterPrice();
                boolean savedFilterGlutenFree = cartPrefs.getFilterGlutenFree();
                boolean savedFilterEco = cartPrefs.getFilterEco();
                boolean savedFilterCategory = cartPrefs.getFilterCategory();
                Log.d(TAG, "initializeCartPreferencesAsync: Estado cargado, actualizando UI");
                
                // Actualizar UI en el hilo principal
                requireActivity().runOnUiThread(() -> {
                    try {
                        Log.d(TAG, "initializeCartPreferencesAsync: Actualizando UI en hilo principal");
                        if (savedCart != null) {
                            cartItems.clear();
                            cartItems.addAll(savedCart);
                            cartAdapter.notifyDataSetChanged();
                        }
                        if (savedSearchQuery != null) {
                            searchBar.setText(savedSearchQuery);
                        }
                        filterDistance.setChecked(savedFilterDistance);
                        filterPrice.setChecked(savedFilterPrice);
                        filterGlutenFree.setChecked(savedFilterGlutenFree);
                        filterEco.setChecked(savedFilterEco);
                        filterCategory.setChecked(savedFilterCategory);
                        
                        actualizarTotalCarrito();
                        
                        // Configurar listeners después de cargar el estado
                        Log.d(TAG, "initializeCartPreferencesAsync: Configurando listeners");
                        setupListeners();
                        Log.d(TAG, "initializeCartPreferencesAsync: Inicialización completada exitosamente");
                    } catch (Exception e) {
                        Log.e(TAG, "Error al actualizar UI: " + e.getMessage(), e);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error al inicializar CartPreferences: " + e.getMessage(), e);
            }
        }).start();
    }

    private void setupListeners() {
        Log.d(TAG, "setupListeners: Iniciando configuración de listeners");
        if (cartPrefs == null) {
            Log.w(TAG, "setupListeners: cartPrefs es null, saliendo");
            return;
        }
        
        Log.d(TAG, "setupListeners: Configurando listener de búsqueda");
        // Listener de búsqueda
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cartPrefs != null) {
                    cartPrefs.saveSearchQuery(s.toString());
                    loadSampleProducts();
                }
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        // Listeners de filtros para chips
        filterDistance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cartPrefs != null) {
                cartPrefs.saveFilterDistance(isChecked);
                loadSampleProducts();
            }
        });
        
        filterPrice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cartPrefs != null) {
                cartPrefs.saveFilterPrice(isChecked);
                loadSampleProducts();
            }
        });
        
        filterGlutenFree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cartPrefs != null) {
                cartPrefs.saveFilterGlutenFree(isChecked);
                loadSampleProducts();
            }
        });
        
        filterEco.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cartPrefs != null) {
                cartPrefs.saveFilterEco(isChecked);
                loadSampleProducts();
            }
        });
        
        filterCategory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (cartPrefs != null) {
                cartPrefs.saveFilterCategory(isChecked);
                loadSampleProducts();
            }
        });
        
        Log.d(TAG, "setupListeners: Todos los listeners configurados exitosamente");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Fragmento pausado");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Fragmento detenido");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: Vista del fragmento destruida");
        
        // Cancelar llamada de API pendiente si existe
        if (pendingApiCall != null && !pendingApiCall.isCanceled()) {
            pendingApiCall.cancel();
            pendingApiCall = null;
            Log.d(TAG, "onDestroyView: Llamada de API pendiente cancelada");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Fragmento destruido");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: Fragmento desvinculado del contexto");
    }

    /**
     * Muestra un mensaje de manera segura y elegante, evitando violaciones de StrictMode
     */
    private void showSafeToast(String message) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        
        // Usar Snackbar personalizado para una experiencia más elegante
        try {
            View rootView = getView();
            if (rootView != null) {
                Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
                
                // Personalizar el Snackbar para que sea más compacto y elegante
                snackbar.setAction("OK", v -> snackbar.dismiss());

                // Aplicar estilo personalizado
                View snackbarView = snackbar.getView();
                if (snackbarView != null) {
                    // Hacer el Snackbar más compacto y elegante
                    snackbarView.setMinimumHeight(0);
                    snackbarView.setPadding(16, 12, 16, 12);

                    // Aplicar esquinas redondeadas
                    snackbarView.setBackgroundResource(R.drawable.snackbar_background);

                    // Centrar el texto y hacerlo más pequeño
                    TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                    if (textView != null) {
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textView.setTextSize(12); // Texto más pequeño y elegante
                        textView.setTextColor(getResources().getColor(R.color.white));
                    }
                }
                
                snackbar.show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al mostrar Snackbar: " + e.getMessage());
        }
    }
}
