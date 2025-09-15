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
import com.example.frontend.model.OrderItem;
import com.example.frontend.model.OrderRequest;
import com.example.frontend.models.Transaction;
import com.example.frontend.utils.CartPreferences;
import com.example.frontend.utils.SessionManager;
import com.example.frontend.ui.adapters.ProductAdapter;
import com.example.frontend.ui.adapters.OrderItemCartAdapter;
import com.example.frontend.ui.dialogs.ProductTraceabilityDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.frontend.api.ApiService;
import com.example.frontend.api.RetrofitClient;
import com.example.frontend.network.ApiClient;
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
import com.example.frontend.services.LocationService;

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
    private SessionManager sessionManager;
    private FusedLocationProviderClient fusedLocationClient;
    private Double userLat = null;
    private Double userLon = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private Call<List<Product>> pendingApiCall = null; // Para cancelar llamadas pendientes
    private LocationService locationService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_consumer_products, container, false);
            
            // Inicializar UI
            searchBar = view.findViewById(R.id.search_bar);
            filterDistance = view.findViewById(R.id.filter_distance);
            filterPrice = view.findViewById(R.id.filter_price);
            filterGlutenFree = view.findViewById(R.id.filter_gluten_free);
            filterEco = view.findViewById(R.id.filter_eco);
            filterCategory = view.findViewById(R.id.filter_category);

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

        // Inicializar SessionManager
        sessionManager = new SessionManager(requireContext());
        
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
        adapter = new ProductAdapter(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
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
            }
            
            @Override
            public void onViewDetails(Product product) {
                // Mostrar diálogo de trazabilidad
                ProductTraceabilityDialog dialog = ProductTraceabilityDialog.newInstance(product);
                dialog.show(getParentFragmentManager(), "traceability_dialog");
            }
        }, true);
        recyclerView.setAdapter(adapter);

        // Cargar productos y estado
        loadSampleProducts();
        cartTotalText = view.findViewById(R.id.cart_total_text);
        actualizarTotalCarrito();

        Button btnFinalizeCart = view.findViewById(R.id.btn_finalize_cart);
        btnFinalizeCart.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                showSafeToast("El carrito está vacío");
                return;
            }
            
            // Crear transacciones reales
            createConsumerOrders();
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationService = new LocationService(requireContext());
        Log.d("Curr LocationService", "Entrando en ConsumerProductsFragment: inicializando ubicación");
        showSafeToast("Inicializando ubicación...");
        obtenerUbicacionUsuario();

        // Iniciar logging continuo y enviar ubicación si hay permisos
        try {
            if (locationService.hasLocationPermissions()) {
                locationService.startLocationLogging(5000L, 5.0f);
                locationService.sendCurrentLocationToBackend();
            }
        } catch (Exception ignored) {}

        return view;
        
        } catch (Exception e) {
            // Retornar una vista simple en caso de error
            View errorView = new View(requireContext());
            errorView.setBackgroundColor(0xFF000000); // Fondo negro
            return errorView;
        }
    }

    private void obtenerUbicacionUsuario() {
        if (!isAdded() || getContext() == null) {
            return;
        }
        
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Usar el método del Fragment para que el callback llegue a este Fragment
            Log.d("Curr LocationService", "Solicitando permisos de ubicación desde Fragment");
            showSafeToast("Solicitando permisos de ubicación...");
            requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        Log.d("Curr LocationService", "Permisos ya concedidos. Solicitando última ubicación conocida...");
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(location -> {
                // Verificar que el fragmento siga adjunto antes de procesar la ubicación
                if (!isAdded() || getContext() == null) {
                    return;
                }
                
                if (location != null) {
                    userLat = location.getLatitude();
                    userLon = location.getLongitude();
                    Log.d("Curr LocationService", "Última ubicación conocida: lat=" + userLat + ", lon=" + userLon);
                    // Intentar enviar al backend también aquí
                    try { if (locationService != null) locationService.sendCurrentLocationToBackend(); } catch (Exception ignored) {}
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
                Log.d("Curr LocationService", "Permisos concedidos por el usuario");
                showSafeToast("Permisos de ubicación concedidos");
                obtenerUbicacionUsuario();
                // Tras conceder permisos, iniciar logging y enviar ubicación
                try {
                    if (locationService != null) {
                        Log.d("Curr LocationService", "Iniciando logging tras conceder permisos");
                        showSafeToast("Iniciando logging de ubicación...");
                        locationService.startLocationLogging(5000L, 5.0f);
                        locationService.sendCurrentLocationToBackend();
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    private ApiService.ProductFilterRequest construirRequest() {
        ApiService.ProductFilterRequest req = new ApiService.ProductFilterRequest();
        req.search = searchBar.getText().toString();
        req.filters = new HashMap<>();
        req.filters.put("eco", filterEco.isChecked());
        req.filters.put("gluten_free", filterGlutenFree.isChecked());
        req.filters.put("price", filterPrice.isChecked());
        req.filters.put("distance", filterDistance.isChecked());

        // Determinar criterio de ordenación basado en filtros activos
        req.sort_criteria = determinarCriterioOrdenacion();

        // Configurar pesos y ubicación
        boolean usarScore = filterDistance.isChecked() || filterPrice.isChecked();
        if (usarScore) {
            req.weights = new HashMap<>();
            if (filterPrice.isChecked()) {
                req.weights.put("price", 0.4f);
            }
            if (filterDistance.isChecked()) {
                req.weights.put("distance", 0.4f);
            }
            req.weights.put("sustainability", 0.2f); // Siempre incluir sostenibilidad
            
            // Añadir ubicación si está disponible
            if (userLat != null && userLon != null) {
                req.user_lat = userLat;
                req.user_lon = userLon;
            }
        } else {
            // Usar pesos por defecto para algoritmo óptimo
            req.weights = new HashMap<>();
            req.weights.put("price", 0.3f);
            req.weights.put("distance", 0.25f);
            req.weights.put("sustainability", 0.2f);
            req.weights.put("eco", 0.15f);
            req.weights.put("stock", 0.1f);
            
            if (userLat != null && userLon != null) {
                req.user_lat = userLat;
                req.user_lon = userLon;
            }
        }
        return req;
    }

    private String determinarCriterioOrdenacion() {
        // Si no hay filtros activos, usar algoritmo óptimo
        if (!filterEco.isChecked() && !filterGlutenFree.isChecked() && 
            !filterPrice.isChecked() && !filterDistance.isChecked()) {
            return "optimal";
        }
        
        // Determinar criterio basado en filtros activos
        if (filterPrice.isChecked()) {
            return "price";
        } else if (filterDistance.isChecked()) {
            return "distance";
        } else if (filterEco.isChecked()) {
            return "eco";
        } else {
            return "optimal";
        }
    }

    private void loadSampleProducts() {
        // Verificar que el fragmento esté adjunto antes de continuar
        if (!isAdded() || getContext() == null) {
            return;
        }
        
        try {
            ApiService.ProductFilterRequest req = construirRequest();
            ApiService apiService = RetrofitClient.getInstance(requireContext()).getRetrofit().create(ApiService.class);
            Call<List<Product>> call = apiService.getProductsOptimized(req);
            pendingApiCall = call; // Almacenar la llamada pendiente
            call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                // Verificar que el fragmento siga adjunto antes de actualizar la UI
                if (!isAdded() || getContext() == null) {
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
                    return;
                }
                
                showSafeToast("Error de red");
            }
        });
        } catch (Exception e) {
            // Verificar que el fragmento siga adjunto antes de mostrar el error
            if (!isAdded() || getContext() == null) {
                return;
            }
            
            showSafeToast("Error al cargar productos");
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
        new Thread(() -> {
            try {
                cartPrefs = new CartPreferences(requireContext());
                
                // Cargar estado guardado
                List<ConsumerOrder.OrderItem> savedCart = cartPrefs.getCartItems();
                String savedSearchQuery = cartPrefs.getSearchQuery();
                boolean savedFilterDistance = cartPrefs.getFilterDistance();
                boolean savedFilterPrice = cartPrefs.getFilterPrice();
                boolean savedFilterGlutenFree = cartPrefs.getFilterGlutenFree();
                boolean savedFilterEco = cartPrefs.getFilterEco();
                boolean savedFilterCategory = cartPrefs.getFilterCategory();
                
                // Actualizar UI en el hilo principal
                requireActivity().runOnUiThread(() -> {
                    try {
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
                        setupListeners();
                    } catch (Exception e) {
                        // Error silencioso
                    }
                });
            } catch (Exception e) {
                // Error silencioso
            }
        }).start();
    }

    private void setupListeners() {
        if (cartPrefs == null) {
            return;
        }
        
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
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Cancelar llamada de API pendiente si existe
        if (pendingApiCall != null && !pendingApiCall.isCanceled()) {
            pendingApiCall.cancel();
            pendingApiCall = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Crea transacciones reales para el consumidor
     */
    private void createConsumerOrders() {
        // Obtener ID del consumidor desde la sesión
        Integer consumerId = sessionManager.getUserId();
        if (consumerId == null) {
            showSafeToast("Error: No se pudo obtener el ID del consumidor");
            return;
        }

        // Agrupar productos por vendedor (agricultor o supermercado)
        Map<String, List<ConsumerOrder.OrderItem>> ordersBySeller = new HashMap<>();
        
        for (ConsumerOrder.OrderItem item : cartItems) {
            // Buscar el producto para obtener información del vendedor
            Product product = findProductById(item.getProductId());
            if (product == null) continue;
            
            String sellerKey = product.getProviderId() + "_" + getSellerType(product);
            if (!ordersBySeller.containsKey(sellerKey)) {
                ordersBySeller.put(sellerKey, new ArrayList<>());
            }
            ordersBySeller.get(sellerKey).add(item);
        }

        // Crear pedidos para cada vendedor
        for (Map.Entry<String, List<ConsumerOrder.OrderItem>> entry : ordersBySeller.entrySet()) {
            String[] sellerInfo = entry.getKey().split("_");
            int sellerId = Integer.parseInt(sellerInfo[0]);
            String sellerType = sellerInfo[1];
            List<ConsumerOrder.OrderItem> sellerItems = entry.getValue();

            // Crear lista de OrderItems
            List<OrderItem> orderItems = new ArrayList<>();
            double totalPrice = 0;

            for (ConsumerOrder.OrderItem cartItem : sellerItems) {
                OrderItem orderItem = new OrderItem(
                    Integer.parseInt(cartItem.getProductId()),
                    cartItem.getProductName(),
                    cartItem.getQuantity(),
                    cartItem.getUnitPrice()
                );
                orderItems.add(orderItem);
                totalPrice += orderItem.total_price;
            }

            // Crear OrderRequest
            OrderRequest orderRequest = new OrderRequest(sellerId, sellerType, orderItems, totalPrice);

            // Enviar pedido al backend
            ApiService api = ApiClient.getClient().create(ApiService.class);
            Call<Transaction> call = api.createOrderFromCart(consumerId, "consumer", orderRequest);
            
            call.enqueue(new Callback<Transaction>() {
                @Override
                public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                    if (!response.isSuccessful()) {
                        showSafeToast("Error al crear pedido");
                    }
                }

                @Override
                public void onFailure(Call<Transaction> call, Throwable t) {
                    showSafeToast("Error de conexión al crear pedido");
                }
            });
        }

        // Limpiar carrito y mostrar mensaje de éxito
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        actualizarTotalCarrito();
        
        // Guardar carrito vacío
        if (cartPrefs != null) {
            cartPrefs.saveCartItems(cartItems);
        }
        
        showSafeToast("Pedidos realizados correctamente");
        
        // Navegar a la pantalla de compras
        try {
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.navigation_consumer_purchases);
            }
        } catch (Exception e) {
            // Error silencioso
        }
    }

    /**
     * Busca un producto por ID en la lista de productos cargados
     */
    private Product findProductById(String productId) {
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Determina el tipo de vendedor basado en el producto
     */
    private String getSellerType(Product product) {
        // Por ahora, asumimos que si el producto tiene provider_id, es de un agricultor
        // En el futuro, podríamos tener un campo específico para el tipo de vendedor
        return "farmer"; // Por defecto, asumimos agricultor
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
            // Error silencioso
        }
    }
}
