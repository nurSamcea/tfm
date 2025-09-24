package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextWatcher;
import android.text.Editable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.model.CartItem;
import com.example.frontend.model.OrderItem;
import com.example.frontend.model.OrderRequest;
import com.example.frontend.model.Transaction;
import com.example.frontend.ui.adapters.SupermarketProductAdapter;
import com.example.frontend.ui.adapters.CartAdapter;
import com.example.frontend.api.ApiService;
import com.example.frontend.api.ApiClient;
import com.example.frontend.utils.SessionManager;
import com.example.frontend.services.LocationService;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.button.MaterialButton;
import android.widget.TextView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupermarketSearchProductsFragment extends Fragment implements SupermarketProductAdapter.OnProductActionListener, CartAdapter.OnCartItemActionListener {

    private static final String TAG = "SupermarketSearchProducts";

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            Log.d(TAG, "Iniciando onCreateView");

            View view = inflater.inflate(R.layout.fragment_supermarket_search_products, container, false);

            // Inicializar SessionManager
            sessionManager = new SessionManager(requireContext());

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

            // Cargar productos de agricultores
            loadFarmerProducts();

            Log.d(TAG, "Vista de búsqueda de productos cargada correctamente");
            return view;

        } catch (Exception e) {
            Log.e(TAG, "Error en SupermarketSearchProductsFragment: " + e.getMessage(), e);
            return null;
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

    private void loadFarmerProducts() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        
        // Obtener todos los productos de agricultores
        Call<List<Product>> call = api.getProductsFiltered("", "farmer");
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allProducts.clear();
                    allProducts.addAll(response.body());
                    filterProducts();
                    Log.d(TAG, "Productos cargados: " + allProducts.size());
                } else {
                    Log.e(TAG, "Error al cargar productos: " + response.code());
                    Toast.makeText(getContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

        // Aplicar algoritmo de optimización
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
                // Priorizar sostenibilidad/huella de carbono
                score = calculateSustainabilityScore(product);
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
        
        // 1. Puntuación por precio (50% peso)
        double priceScore = Math.max(0, 100 - (product.getPrice() * 10));
        score += 0.5 * priceScore;
        
        // 2. Puntuación por distancia (30% peso)
        if (product.getDistance_km() != null) {
            double distanceScore = Math.max(0, 100 - (product.getDistance_km() * 2));
            score += 0.3 * distanceScore;
        } else {
            score += 0.3 * 50; // Score neutral si no hay distancia
        }
        
        // 3. Puntuación por sostenibilidad/huella de carbono (20% peso)
        double sustainabilityScore = calculateSustainabilityScore(product);
        score += 0.2 * sustainabilityScore;
        
        return score;
    }
    
    private double calculateSustainabilityScore(Product product) {
        // Score base del producto
        double baseScore = product.getScore() != null ? product.getScore() : 0;
        
        // Factores de huella de carbono por categoría (kg CO2 por kg)
        double co2Factor = getCo2FactorByCategory(product.getCategory());
        
        // Penalización por distancia de transporte (0.1 kg CO2 por km)
        double transportPenalty = 0;
        if (product.getDistance_km() != null) {
            double transportCo2 = product.getDistance_km() * 0.1;
            // Penalizar productos que vienen de lejos (máximo 10 kg CO2 de transporte = -20 puntos)
            transportPenalty = Math.min(20, transportCo2 * 2);
        }
        
        // Calcular puntuación final de sostenibilidad
        double sustainabilityScore = baseScore - transportPenalty;
        
        // Asegurar que esté en el rango 0-100
        return Math.max(0, Math.min(100, sustainabilityScore));
    }
    
    private double getCo2FactorByCategory(String category) {
        if (category == null) return 0.8; // Factor por defecto
        
        switch (category.toLowerCase()) {
            case "frutas": return 0.5;
            case "verduras": return 0.4;
            case "carnes": return 2.5;
            case "pescados": return 1.8;
            case "lacteos": return 1.2;
            case "cereales": return 0.3;
            case "legumbres": return 0.2;
            default: return 0.8; // "otros"
        }
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
                sortCriteria = "distance";
                break;
            case 3: // Sostenibilidad mayor
                Log.d("Curr LocationService", "Ordenar por sostenibilidad: solicitando al backend /products/optimized/ con sort_criteria=sustainability");
                fetchOptimizedProductsSortedBySustainability();
                return;
            case 4: // Stock disponible
                Log.d("Curr LocationService", "Ordenar por stock: solicitando al backend /products/optimized/ con sort_criteria=stock");
                fetchOptimizedProductsSortedByStock();
                return;
            case 5: // Algoritmo optimizado
                sortCriteria = "optimal";
                break;
            default:
                sortCriteria = "optimal";
                break;
        }
        
        // Aplicar ordenación según el criterio seleccionado
        Collections.sort(filteredProducts, (p1, p2) -> {
            double score1 = calculateProductScore(p1, sortCriteria);
            double score2 = calculateProductScore(p2, sortCriteria);
            return Double.compare(score2, score1); // Orden descendente (mejor primero)
        });
        
        productAdapter.updateProducts(filteredProducts);
    }

    // Implementación de OnProductActionListener
    @Override
    public void onAddToCart(Product product) {
        addProductToCart(product);
    }

    @Override
    public void onViewDetails(Product product) {
        // Ver detalles del producto
        // TODO: Implementar vista de detalles del producto
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
        cartTotal.setText(String.format("Total: %.2f €", total));

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

        // Agrupar productos por agricultor
        Map<Integer, List<CartItem>> ordersByFarmer = new HashMap<>();
        for (CartItem item : cartItems) {
            Integer farmerId = item.getProduct().getProviderId();
            if (farmerId == null) {
                Toast.makeText(getContext(), "Error: Producto sin agricultor asignado", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!ordersByFarmer.containsKey(farmerId)) {
                ordersByFarmer.put(farmerId, new ArrayList<>());
            }
            ordersByFarmer.get(farmerId).add(item);
        }

        // Mostrar diálogo de confirmación
        StringBuilder message = new StringBuilder("¿Desea realizar " + ordersByFarmer.size() + " pedido(s) a diferentes agricultores?\n\n");
        for (Map.Entry<Integer, List<CartItem>> entry : ordersByFarmer.entrySet()) {
            double total = 0;
            for (CartItem item : entry.getValue()) {
                total += item.getTotalPrice();
            }
            message.append("• Agricultor ").append(entry.getKey()).append(": ").append(entry.getValue().size()).append(" productos - ").append(String.format("%.2f €", total)).append("\n");
        }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Confirmar pedidos")
                .setMessage(message.toString())
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    createOrders(ordersByFarmer);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void createOrders(Map<Integer, List<CartItem>> ordersByFarmer) {
        // Obtener ID del supermercado desde la sesión
        Integer supermarketId = sessionManager.getUserId();
        if (supermarketId == null) {
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del supermercado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear pedidos para cada agricultor
        for (Map.Entry<Integer, List<CartItem>> entry : ordersByFarmer.entrySet()) {
            Integer farmerId = entry.getKey();
            List<CartItem> farmerItems = entry.getValue();

            // Crear lista de OrderItems
            List<OrderItem> orderItems = new ArrayList<>();
            double totalPrice = 0;

            for (CartItem cartItem : farmerItems) {
                OrderItem orderItem = new OrderItem(
                    Integer.parseInt(cartItem.getProduct().getId()),
                    cartItem.getProduct().getName(),
                    cartItem.getQuantity(),
                    cartItem.getProduct().getPrice()
                );
                orderItems.add(orderItem);
                totalPrice += orderItem.total_price;
            }

            // Crear OrderRequest con seller_type
            OrderRequest orderRequest = new OrderRequest(farmerId, "farmer", orderItems, totalPrice);

            // Enviar pedido al backend
            ApiService api = ApiClient.getClient().create(ApiService.class);
            Call<Transaction> call = api.createOrderFromCart(supermarketId, "supermarket", orderRequest);
            
            call.enqueue(new Callback<Transaction>() {
                @Override
                public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Pedido creado exitosamente para agricultor " + farmerId);
                    } else {
                        Log.e(TAG, "Error al crear pedido: " + response.code());
                        Toast.makeText(getContext(), "Error al crear pedido para agricultor " + farmerId, Toast.LENGTH_SHORT).show();
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
    }

    // Implementación de OnCartItemActionListener
    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        item.setQuantity(newQuantity);
        updateCartUI();
    }

    private void fetchOptimizedProductsSortedBySustainability() {
        try {
            ApiService.ProductFilterRequest req = new ApiService.ProductFilterRequest();
            req.search = searchProducts.getText() != null ? searchProducts.getText().toString() : null;
            req.sort_criteria = "sustainability";
            req.filters = new java.util.HashMap<>();
            req.weights = new java.util.HashMap<>();

            // Si NO hay sesión, enviar user_lat/user_lon desde ubicación actual para evitar depender de token
            boolean loggedIn = (sessionManager != null && sessionManager.isLoggedIn());
            if (!loggedIn) {
                Log.d("Curr LocationService", "No logueado: enviando lat/lon en la petición para ordenar por sostenibilidad");
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
                    executeSustainabilityRequest(req);
                }).exceptionally(ex -> {
                    Log.e("Curr LocationService", "Error obteniendo ubicación para request sustainability: " + ex.getMessage());
                    executeSustainabilityRequest(req);
                    return null;
                });
                return;
            }

            // Si hay sesión, el backend usará la ubicación guardada -> ejecutamos directamente
            executeSustainabilityRequest(req);
        } catch (Exception ignored) {}
    }

    private void fetchOptimizedProductsSortedByStock() {
        try {
            ApiService.ProductFilterRequest req = new ApiService.ProductFilterRequest();
            req.search = searchProducts.getText() != null ? searchProducts.getText().toString() : null;
            req.sort_criteria = "stock";
            req.filters = new java.util.HashMap<>();
            req.weights = new java.util.HashMap<>();

            // Si NO hay sesión, enviar user_lat/user_lon desde ubicación actual para evitar depender de token
            boolean loggedIn = (sessionManager != null && sessionManager.isLoggedIn());
            if (!loggedIn) {
                Log.d("Curr LocationService", "No logueado: enviando lat/lon en la petición para ordenar por stock");
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
                    executeStockRequest(req);
                }).exceptionally(ex -> {
                    Log.e("Curr LocationService", "Error obteniendo ubicación para request stock: " + ex.getMessage());
                    executeStockRequest(req);
                    return null;
                });
                return;
            }

            // Si hay sesión, el backend usará la ubicación guardada -> ejecutamos directamente
            executeStockRequest(req);
        } catch (Exception ignored) {}
    }

    private void executeSustainabilityRequest(ApiService.ProductFilterRequest req) {
        // Usar ApiClient (con token si existe)
        ApiService api = ApiClient.getApiService(requireContext());
        retrofit2.Call<java.util.List<Product>> call = api.getProductsOptimized(req);
        Log.d("Curr LocationService", "Enviando petición de productos optimizados (sustainability). search=" + req.search +
                (req.user_lat != null ? (", lat/lon adjuntos") : ", sin lat/lon"));
        call.enqueue(new retrofit2.Callback<java.util.List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<Product>> call, retrofit2.Response<java.util.List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Curr LocationService", "Respuesta OK (sustainability). Productos recibidos=" + response.body().size());
                    filteredProducts.clear();
                    filteredProducts.addAll(response.body());
                    productAdapter.updateProducts(filteredProducts);
                } else {
                    Log.w("Curr LocationService", "Respuesta no exitosa al ordenar por sustainability. code=" + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<Product>> call, Throwable t) {
                Log.e("Curr LocationService", "Fallo al solicitar orden por sustainability: " + t.getMessage());
            }
        });
    }

    private void executeStockRequest(ApiService.ProductFilterRequest req) {
        // Usar ApiClient (con token si existe)
        ApiService api = ApiClient.getApiService(requireContext());
        retrofit2.Call<java.util.List<Product>> call = api.getProductsOptimized(req);
        Log.d("Curr LocationService", "Enviando petición de productos optimizados (stock). search=" + req.search +
                (req.user_lat != null ? (", lat/lon adjuntos") : ", sin lat/lon"));
        call.enqueue(new retrofit2.Callback<java.util.List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<Product>> call, retrofit2.Response<java.util.List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Curr LocationService", "Respuesta OK (stock). Productos recibidos=" + response.body().size());
                    filteredProducts.clear();
                    filteredProducts.addAll(response.body());
                    productAdapter.updateProducts(filteredProducts);
                } else {
                    Log.w("Curr LocationService", "Respuesta no exitosa al ordenar por stock. code=" + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<Product>> call, Throwable t) {
                Log.e("Curr LocationService", "Fallo al solicitar orden por stock: " + t.getMessage());
            }
        });
    }

    @Override
    public void onRemoveItem(CartItem item) {
        cartItems.remove(item);
        updateCartUI();
        // Producto eliminado del carrito silenciosamente
    }
}
