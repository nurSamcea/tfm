package com.example.frontend.ui.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextWatcher;
import android.text.Editable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.model.CartItem;
import com.example.frontend.model.OrderItem;
import com.example.frontend.model.OrderRequest;
import com.example.frontend.model.Transaction;
import com.example.frontend.ui.adapters.SupermarketProductAdapter;
import com.example.frontend.ui.adapters.CartAdapter;
import com.example.frontend.ui.dialogs.ProductTraceabilityDialog;
import com.example.frontend.api.ApiService;
import com.example.frontend.api.ApiClient;
import com.example.frontend.utils.SessionManager;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.button.MaterialButton;
import android.widget.TextView;
import android.widget.LinearLayout;
import com.example.frontend.services.LocationService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsumerSearchProductsFragment extends Fragment implements SupermarketProductAdapter.OnProductActionListener, CartAdapter.OnCartItemActionListener {

    private static final String TAG = "ConsumerSearchProducts";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private RecyclerView recyclerProducts;
    private SupermarketProductAdapter productAdapter;
    private EditText searchProducts;
    private ImageButton filterButton;
    private ImageButton sortButton;
    private ChipGroup chipGroupFilters;
    private View filtersScroll;

    // Carrito de compras
    private LinearLayout cartContainer;
    private RecyclerView recyclerCart;
    private CartAdapter cartAdapter;
    private TextView cartItemCount;
    private TextView cartTotal;
    private MaterialButton btnClearCart;
    private MaterialButton btnCheckout;

    private final List<Product> allProducts = new ArrayList<>();
    private final List<Product> filteredProducts = new ArrayList<>();
    private final List<CartItem> cartItems = new ArrayList<>();
    private SessionManager sessionManager;
    private LocationService locationService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            Log.d(TAG, "Iniciando onCreateView");

            View view = inflater.inflate(R.layout.fragment_consumer_search_products, container, false);

            // Inicializar SessionManager
            sessionManager = new SessionManager(requireContext());
            // Inicializar servicio de ubicación y solicitar permisos/logging
            locationService = new LocationService(requireContext());
            Log.d("Curr LocationService", "Entrando en ConsumerSearchProductsFragment: inicializando ubicación");
            ensureLocationSetup();
            // Verificar ajustes de ubicación (GPS/Alta precisión) y mostrar diálogo si procede
            try { locationService.checkAndPromptEnableLocation(requireActivity(), 5000L, 5.0f); } catch (Exception ignored) {}
            // Forzar un intento con fallback y log
            locationService.getCurrentLocationWithFallback().thenAccept(loc -> {
                if (loc != null) {
                    Log.d("Curr LocationService", String.format("fallback ok: lat=%.6f, lon=%.6f", loc.getLatitude(), loc.getLongitude()));
                    try {
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), String.format("Ubicación: %.5f, %.5f", loc.getLatitude(), loc.getLongitude()), Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception ignored) {}
                }
            }).exceptionally(ex -> {
                Log.e("Curr LocationService", "fallback error: "+ex.getMessage());
                return null;
            });

            // Referencias UI
            recyclerProducts = view.findViewById(R.id.recycler_products);
            searchProducts = view.findViewById(R.id.search_products);
            filterButton = view.findViewById(R.id.filter_button);
            sortButton = view.findViewById(R.id.sort_button);
            chipGroupFilters = view.findViewById(R.id.chip_group_filters);
            filtersScroll = view.findViewById(R.id.filters_scroll);

            // Referencias del carrito
            cartContainer = view.findViewById(R.id.cart_container);
            recyclerCart = view.findViewById(R.id.recycler_cart);
            cartItemCount = view.findViewById(R.id.cart_item_count);
            cartTotal = view.findViewById(R.id.cart_total);
            btnClearCart = view.findViewById(R.id.btn_clear_cart);
            btnCheckout = view.findViewById(R.id.btn_checkout);

            // Layouts
            recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));

            // Adapters
            productAdapter = new SupermarketProductAdapter(filteredProducts);
            productAdapter.setOnProductActionListener(this);
            recyclerProducts.setAdapter(productAdapter);

            cartAdapter = new CartAdapter(cartItems);
            cartAdapter.setOnCartItemActionListener(this);
            recyclerCart.setAdapter(cartAdapter);

            // Configurar listeners
            setupListeners();

            // Cargar productos de agricultores y supermercados
            loadAllProducts();

            Log.d(TAG, "Vista de búsqueda de productos cargada correctamente");
            return view;

        } catch (Exception e) {
            Log.e(TAG, "Error en ConsumerSearchProductsFragment: " + e.getMessage(), e);
            return null;
        }
    }

    private void ensureLocationSetup() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Curr LocationService", "Solicitando permisos desde ConsumerSearchProductsFragment");
            requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        // Permisos concedidos. Verificar si la ubicación del sistema está activada
        try {
            LocationManager lm = (LocationManager) requireContext().getSystemService(android.content.Context.LOCATION_SERVICE);
            boolean gpsOn = lm != null && lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean netOn = lm != null && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!gpsOn && !netOn) {
                Log.w("Curr LocationService", "Ubicación del sistema desactivada. Abriendo ajustes");
                Toast.makeText(requireContext(), "Activa la ubicación del dispositivo", Toast.LENGTH_LONG).show();
                try {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        try {
            Log.d("Curr LocationService", "Permisos ya concedidos en ConsumerSearchProducts. Iniciando logging y envío");
            locationService.startLocationLogging(5000L, 5.0f);
            // Obtener ubicación puntual y registrar resultado
            locationService.getCurrentLocation().thenAccept(loc -> {
                if (loc != null) {
                    Log.d("Curr LocationService", String.format("getCurrentLocation ok: lat=%.6f, lon=%.6f", loc.getLatitude(), loc.getLongitude()));
                } else {
                    Log.w("Curr LocationService", "getCurrentLocation devolvió null");
                }
            }).exceptionally(ex -> {
                Log.e("Curr LocationService", "Error en getCurrentLocation: " + ex.getMessage());
                return null;
            });
            locationService.sendCurrentLocationToBackend();
        } catch (Exception ignored) {}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Curr LocationService", "Permisos concedidos en ConsumerSearchProducts");
                try {
                    locationService.startLocationLogging(5000L, 5.0f);
                    locationService.sendCurrentLocationToBackend();
                } catch (Exception ignored) {}
            } else {
                // Si el usuario denegó y marcó "no volver a preguntar", guiar a ajustes de la app
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                if (!showRationale) {
                    Toast.makeText(requireContext(), "Concede el permiso de ubicación en Ajustes de la app", Toast.LENGTH_LONG).show();
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(android.net.Uri.parse("package:" + requireContext().getPackageName()));
                        startActivity(intent);
                    } catch (Exception ignored) {}
                } else {
                    Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setupListeners() {
        // Búsqueda
        searchProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Botón de filtros
        filterButton.setOnClickListener(v -> {
            if (filtersScroll.getVisibility() == View.GONE) {
                filtersScroll.setVisibility(View.VISIBLE);
            } else {
                filtersScroll.setVisibility(View.GONE);
            }
        });

        // Botón de ordenar
        sortButton.setOnClickListener(v -> showSortDialog());

        // Filtros dinámicos
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            filterProducts();
        });

        // Botones del carrito
        btnClearCart.setOnClickListener(v -> clearCart());
        btnCheckout.setOnClickListener(v -> processCheckout());
    }

    private void loadAllProducts() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        
        // Obtener productos de agricultores
        Call<List<Product>> farmerCall = api.getProductsFiltered("", "farmer");
        farmerCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Marcar productos como de agricultores
                    for (Product product : response.body()) {
                        product.setSellerType("farmer");
                    }
                    allProducts.addAll(response.body());
                    Log.d(TAG, "Productos de agricultores cargados: " + response.body().size());
                }
                // Cargar productos de supermercados después
                loadSupermarketProducts();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Error al cargar productos de agricultores: " + t.getMessage());
                // Intentar cargar productos de supermercados de todas formas
                loadSupermarketProducts();
            }
        });
    }

    private void loadSupermarketProducts() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        
        // Obtener productos de supermercados
        Call<List<Product>> supermarketCall = api.getProductsFiltered("", "supermarket");
        supermarketCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Marcar productos como de supermercados
                    for (Product product : response.body()) {
                        product.setSellerType("supermarket");
                    }
                    allProducts.addAll(response.body());
                    Log.d(TAG, "Productos de supermercados cargados: " + response.body().size());
                }
                filterProducts();
                Log.d(TAG, "Total productos cargados: " + allProducts.size());
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Error al cargar productos de supermercados: " + t.getMessage());
                filterProducts();
            }
        });
    }

    private void filterProducts() {
        String query = searchProducts.getText().toString().toLowerCase();
        filteredProducts.clear();

        for (Product product : allProducts) {
            // Solo filtro de texto
            boolean matchesText = query.isEmpty() || 
                product.getName().toLowerCase().contains(query) ||
                product.getCategory().toLowerCase().contains(query);

            if (matchesText) {
                filteredProducts.add(product);
            }
        }

        // Aplicar algoritmo de optimización (el backend ya usa la ubicación guardada del usuario)
        applyOptimizationAlgorithm();
        
        productAdapter.updateProducts(filteredProducts);
    }

    private void applyOptimizationAlgorithm() {
        // Determinar criterios de priorización basados en filtros activos
        String sortCriteria = determineSortCriteria();
        
        // Aplicar algoritmo de optimización según criterios
        Collections.sort(filteredProducts, (p1, p2) -> {
            double score1 = calculateProductScore(p1, sortCriteria);
            double score2 = calculateProductScore(p2, sortCriteria);
            return Double.compare(score2, score1); // Orden descendente (mejor primero)
        });
    }

    private String determineSortCriteria() {
        // Usar algoritmo óptimo por defecto
        // El usuario puede cambiar el criterio usando el botón de ordenar
        return "optimal";
    }

    private double calculateProductScore(Product product, String sortCriteria) {
        double score = 0.0;
        
        switch (sortCriteria) {
            case "price":
                // Priorizar precio bajo (menor precio = mayor score)
                score = 100.0 - (product.getPrice() * 20); // Normalizar precio
                break;
                
            case "price_desc":
                // Priorizar precio alto (mayor precio = mayor score)
                score = product.getPrice() * 20; // Normalizar precio
                break;
                
            case "distance":
                // Priorizar proximidad
                if (product.getDistance_km() != null) {
                    score = Math.max(0, 100 - (product.getDistance_km() * 2));
                } else {
                    score = 50; // Score neutral si no hay distancia
                }
                break;
                
            case "sustainability":
                // Priorizar sostenibilidad
                if (product.getScore() != null) {
                    score = product.getScore();
                } else {
                    score = 0;
                }
                break;
                
            case "eco":
                // Priorizar productos ecológicos
                if (product.getIsEco() != null && product.getIsEco()) {
                    score = 100;
                } else {
                    score = 0;
                }
                break;
                
            case "stock":
                // Priorizar stock disponible
                double stock = product.getStockAvailable() != null ? 
                    product.getStockAvailable() : product.getStock();
                score = Math.min(stock * 10, 100);
                break;
                
            case "optimal":
            default:
                // Algoritmo óptimo que considera múltiples factores
                score = calculateOptimalScore(product);
                break;
        }
        
        return score;
    }

    private double calculateOptimalScore(Product product) {
        double score = 0.0;
        
        // 1. Puntuación por precio (30% peso)
        double priceScore = Math.max(0, 100 - (product.getPrice() * 10));
        score += 0.3 * priceScore;
        
        // 2. Puntuación por distancia (25% peso)
        if (product.getDistance_km() != null) {
            double distanceScore = Math.max(0, 100 - (product.getDistance_km() * 2));
            score += 0.25 * distanceScore;
        } else {
            score += 0.25 * 50; // Score neutral si no hay distancia
        }
        
        // 3. Puntuación por sostenibilidad (20% peso)
        if (product.getScore() != null) {
            score += 0.2 * product.getScore();
        }
        
        // 4. Bonus por ser ecológico (15% peso)
        if (product.getIsEco() != null && product.getIsEco()) {
            score += 0.15 * 100;
        }
        
        // 5. Puntuación por stock disponible (10% peso)
        double stock = product.getStockAvailable() != null ? 
            product.getStockAvailable() : product.getStock();
        double stockScore = Math.min(stock * 10, 100);
        score += 0.1 * stockScore;
        
        return score;
    }

    private void showSortDialog() {
        String[] sortOptions = {
            "Precio (menor a mayor)",
            "Precio (mayor a menor)",
            "Distancia (más cerca)",
            "Sostenibilidad (mayor)",
            "Stock disponible",
            "Algoritmo optimizado"
        };

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Ordenar productos")
                .setItems(sortOptions, (dialog, which) -> {
                    sortProducts(which);
                });
        builder.show();
    }

    private void sortProducts(int sortType) {
        String sortCriteria;

        switch (sortType) {
            case 0: // Precio menor a mayor
                sortCriteria = "price";
                break;
            case 1: // Precio mayor a menor
                sortCriteria = "price_desc";
                break;
            case 2: // Distancia más cerca
                // Para distancia, pedir al backend la lista optimizada con distancia calculada
                Log.d("Curr LocationService", "Ordenar por distancia: solicitando al backend /products/optimized/ con sort_criteria=distance");
                fetchOptimizedProductsSortedByDistance();
                return;
            case 3: // Sostenibilidad mayor
                sortCriteria = "sustainability";
                break;
            case 4: // Stock disponible
                sortCriteria = "stock";
                break;
            case 5: // Algoritmo optimizado
                sortCriteria = "optimal";
                break;
            default:
                sortCriteria = "optimal";
                break;
        }

        // Aplicar ordenación local para criterios distintos de distancia
        Collections.sort(filteredProducts, (p1, p2) -> {
            double score1 = calculateProductScore(p1, sortCriteria);
            double score2 = calculateProductScore(p2, sortCriteria);
            return Double.compare(score2, score1);
        });

        productAdapter.updateProducts(filteredProducts);
    }

    private void fetchOptimizedProductsSortedByDistance() {
        try {
            ApiService.ProductFilterRequest req = new ApiService.ProductFilterRequest();
            req.search = searchProducts.getText() != null ? searchProducts.getText().toString() : null;
            req.sort_criteria = "distance";
            req.filters = new java.util.HashMap<>();
            req.weights = new java.util.HashMap<>();

            // Si NO hay sesión, enviar user_lat/user_lon desde ubicación actual para evitar depender de token
            boolean loggedIn = (sessionManager != null && sessionManager.isLoggedIn());
            if (!loggedIn) {
                Log.d("Curr LocationService", "No logueado: enviando lat/lon en la petición para ordenar por distancia");
                LocationService ls = new LocationService(requireContext());
                ls.getCurrentLocationWithFallback().thenAccept(loc -> {
                    if (loc != null) {
                        req.user_lat = loc.getLatitude();
                        req.user_lon = loc.getLongitude();
                        Log.d("Curr LocationService", String.format("Adjuntando coords en request: %.6f, %.6f", req.user_lat, req.user_lon));
                    } else {
                        Log.w("Curr LocationService", "No se obtuvo ubicación para adjuntar en request (se enviará sin coords)");
                    }
                    // Ejecutar llamada una vez resuelta la ubicación
                    executeDistanceRequest(req);
                }).exceptionally(ex -> {
                    Log.e("Curr LocationService", "Error obteniendo ubicación para request distance: " + ex.getMessage());
                    executeDistanceRequest(req);
                    return null;
                });
                return;
            }

            // Si hay sesión, el backend usará la ubicación guardada -> ejecutamos directamente
            executeDistanceRequest(req);
        } catch (Exception ignored) {}
    }

    private void executeDistanceRequest(ApiService.ProductFilterRequest req) {
        // Usar RetrofitClient (con token si existe)
        ApiService api = com.example.frontend.api.RetrofitClient.getInstance(requireContext()).getRetrofit().create(ApiService.class);
        retrofit2.Call<java.util.List<Product>> call = api.getProductsOptimized(req);
        Log.d("Curr LocationService", "Enviando petición de productos optimizados (distance). search=" + req.search +
                (req.user_lat != null ? (", lat/lon adjuntos") : ", sin lat/lon"));
        call.enqueue(new retrofit2.Callback<java.util.List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<Product>> call, retrofit2.Response<java.util.List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Curr LocationService", "Respuesta OK (distance). Productos recibidos=" + response.body().size());
                    filteredProducts.clear();
                    filteredProducts.addAll(response.body());
                    productAdapter.updateProducts(filteredProducts);
                } else {
                    Log.w("Curr LocationService", "Respuesta no exitosa al ordenar por distance. code=" + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<Product>> call, Throwable t) {
                Log.e("Curr LocationService", "Fallo al solicitar orden por distance: " + t.getMessage());
            }
        });
    }

    // Implementación de OnProductActionListener
    @Override
    public void onAddToCart(Product product) {
        addProductToCart(product);
    }

    @Override
    public void onViewDetails(Product product) {
        // Mostrar diálogo de trazabilidad
        ProductTraceabilityDialog dialog = ProductTraceabilityDialog.newInstance(product);
        dialog.show(getParentFragmentManager(), "traceability_dialog");
    }

    // Métodos del carrito de compras
    private void addProductToCart(Product product) {
        // Verificar si el producto ya está en el carrito
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getId().equals(product.getId())) {
                // Si ya existe, incrementar cantidad
                cartItem.incrementQuantity();
                updateCartUI();
                // Cantidad actualizada silenciosamente
                return;
            }
        }

        // Si no existe, agregar nuevo item al carrito
        CartItem newCartItem = new CartItem(product, 1);
        cartItems.add(newCartItem);
        updateCartUI();
        // Producto agregado al carrito silenciosamente
    }

    private void updateCartUI() {
        // Actualizar contador de productos
        int totalItems = 0;
        for (CartItem item : cartItems) {
            totalItems += item.getQuantity();
        }
        cartItemCount.setText(totalItems + " productos");

        // Actualizar total
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        cartTotal.setText(String.format("%.2f €", total));

        // Actualizar adapter del carrito
        cartAdapter.updateCartItems(cartItems);
    }

    private void clearCart() {
        if (cartItems.isEmpty()) {
            // El carrito ya está vacío
            return;
        }

        // Mostrar diálogo de confirmación
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Vaciar carrito")
                .setMessage("¿Está seguro de que desea vaciar el carrito?")
                .setPositiveButton("Sí, vaciar", (dialog, which) -> {
                    cartItems.clear();
                    updateCartUI();
                    // Carrito vaciado silenciosamente
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void processCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        // Agrupar productos por vendedor (agricultor o supermercado)
        Map<String, List<CartItem>> ordersBySeller = new HashMap<>();
        for (CartItem item : cartItems) {
            // Determinar el tipo de vendedor basado en el producto
            String sellerType = determineSellerType(item.getProduct());
            String sellerKey = item.getProduct().getProviderId() + "_" + sellerType;
            
            if (!ordersBySeller.containsKey(sellerKey)) {
                ordersBySeller.put(sellerKey, new ArrayList<>());
            }
            ordersBySeller.get(sellerKey).add(item);
        }

        // Mostrar diálogo de confirmación
        StringBuilder message = new StringBuilder("¿Desea realizar " + ordersBySeller.size() + " pedido(s) a diferentes vendedores?\n\n");
        for (Map.Entry<String, List<CartItem>> entry : ordersBySeller.entrySet()) {
            double total = 0;
            for (CartItem item : entry.getValue()) {
                total += item.getTotalPrice();
            }
            String[] sellerInfo = entry.getKey().split("_");
            String sellerType = sellerInfo[1].equals("farmer") ? "Agricultor" : "Supermercado";
            message.append("• ").append(sellerType).append(" ").append(sellerInfo[0]).append(": ").append(entry.getValue().size()).append(" productos - ").append(String.format("%.2f €", total)).append("\n");
        }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Confirmar pedidos")
                .setMessage(message.toString())
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    createOrders(ordersBySeller);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private String determineSellerType(Product product) {
        // Usar el campo sellerType que establecimos al cargar los productos
        if (product.getSellerType() != null && !product.getSellerType().isEmpty()) {
            return product.getSellerType();
        }
        
        // Fallback: determinar basándose en el provider_id si no hay sellerType
        if (product.getProviderId() != null) {
            int providerId = product.getProviderId();
            // Si el provider_id es mayor a 10, asumimos que es un supermercado
            if (providerId > 10) {
                return "supermarket";
            }
        }
        
        return "farmer"; // Por defecto, asumimos agricultor
    }

    private void createOrders(Map<String, List<CartItem>> ordersBySeller) {
        // Obtener ID del consumidor desde la sesión
        Integer consumerId = sessionManager.getUserId();
        if (consumerId == null) {
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del consumidor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear pedidos para cada vendedor
        for (Map.Entry<String, List<CartItem>> entry : ordersBySeller.entrySet()) {
            String[] sellerInfo = entry.getKey().split("_");
            int sellerId = Integer.parseInt(sellerInfo[0]);
            String sellerType = sellerInfo[1];
            List<CartItem> sellerItems = entry.getValue();

            // Crear lista de OrderItems
            List<OrderItem> orderItems = new ArrayList<>();
            double totalPrice = 0;

            for (CartItem cartItem : sellerItems) {
                OrderItem orderItem = new OrderItem(
                    Integer.parseInt(cartItem.getProduct().getId()),
                    cartItem.getProduct().getName(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice()
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
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Pedido creado exitosamente para " + sellerType + " " + sellerId);
                    } else {
                        Log.e(TAG, "Error al crear pedido: " + response.code());
                        Toast.makeText(getContext(), "Error al crear pedido para " + sellerType + " " + sellerId, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Transaction> call, Throwable t) {
                    Log.e(TAG, "Error de conexión al crear pedido: " + t.getMessage());
                    Toast.makeText(getContext(), "Error de conexión al crear pedido", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Limpiar carrito y mostrar mensaje de éxito
        cartItems.clear();
        updateCartUI();
        Toast.makeText(getContext(), "Pedidos realizados correctamente", Toast.LENGTH_SHORT).show();
        
        // Notificar que se han creado nuevos pedidos para que se actualice la lista de pedidos
        // Esto se puede hacer mediante un callback o evento
        Log.d(TAG, "Pedidos creados exitosamente, notificando actualización de lista de pedidos");
    }

    // Implementación de OnCartItemActionListener
    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        item.setQuantity(newQuantity);
        updateCartUI();
    }

    @Override
    public void onRemoveItem(CartItem item) {
        cartItems.remove(item);
        updateCartUI();
        // Producto eliminado del carrito silenciosamente
    }
}

