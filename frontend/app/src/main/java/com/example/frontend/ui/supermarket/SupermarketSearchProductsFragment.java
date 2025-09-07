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
import com.example.frontend.models.Transaction;
import com.example.frontend.ui.adapters.SupermarketProductAdapter;
import com.example.frontend.ui.adapters.CartAdapter;
import com.example.frontend.api.ApiService;
import com.example.frontend.network.ApiClient;
import com.example.frontend.utils.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.button.MaterialButton;
import android.widget.TextView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                sortCriteria = "distance";
                break;
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

    @Override
    public void onRemoveItem(CartItem item) {
        cartItems.remove(item);
        updateCartUI();
        // Producto eliminado del carrito silenciosamente
    }
}
