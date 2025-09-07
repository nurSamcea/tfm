package com.example.frontend.ui.consumer;

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

public class ConsumerSearchProductsFragment extends Fragment implements SupermarketProductAdapter.OnProductActionListener, CartAdapter.OnCartItemActionListener {

    private static final String TAG = "ConsumerSearchProducts";

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

            View view = inflater.inflate(R.layout.fragment_consumer_search_products, container, false);

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

            // Cargar productos de agricultores y supermercados
            loadAllProducts();

            Log.d(TAG, "Vista de búsqueda de productos cargada correctamente");
            return view;

        } catch (Exception e) {
            Log.e(TAG, "Error en ConsumerSearchProductsFragment: " + e.getMessage(), e);
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
            // Filtro de texto
            boolean matchesText = query.isEmpty() || 
                product.getName().toLowerCase().contains(query) ||
                product.getCategory().toLowerCase().contains(query);

            if (!matchesText) continue;

            // Filtros de chips
            boolean matchesFilters = true;
            List<Integer> checkedIds = chipGroupFilters.getCheckedChipIds();
            
            for (int id : checkedIds) {
                Chip chip = chipGroupFilters.findViewById(id);
                if (chip == null) continue;
                
                String chipText = chip.getText().toString();
                
                switch (chipText) {
                    case "Precio bajo":
                        if (product.getPrice() > 2.0) matchesFilters = false;
                        break;
                    case "Cerca":
                        if (product.getDistance_km() != null && product.getDistance_km() > 50) {
                            matchesFilters = false;
                        }
                        break;
                    case "Disponible":
                        double stock = product.getStockAvailable() != null ? 
                            product.getStockAvailable() : product.getStock();
                        if (stock <= 0) matchesFilters = false;
                        break;
                    case "Sostenible":
                        if (product.getScore() == null || product.getScore() < 70) {
                            matchesFilters = false;
                        }
                        break;
                    case "Ecológico":
                        if (product.getIsEco() == null || !product.getIsEco()) {
                            matchesFilters = false;
                        }
                        break;
                }
            }

            if (matchesFilters) {
                filteredProducts.add(product);
            }
        }

        // Aplicar algoritmo de optimización
        applyOptimizationAlgorithm();
        
        productAdapter.updateProducts(filteredProducts);
    }

    private void applyOptimizationAlgorithm() {
        // Simular algoritmo de optimización basado en los criterios
        Collections.sort(filteredProducts, (p1, p2) -> {
            double score1 = calculateProductScore(p1);
            double score2 = calculateProductScore(p2);
            return Double.compare(score2, score1); // Orden descendente (mejor primero)
        });
    }

    private double calculateProductScore(Product product) {
        double score = 0.0;
        
        // Puntuación por precio (más bajo = mejor)
        score += (5.0 - product.getPrice()) * 10;
        
        // Puntuación por distancia (más cerca = mejor)
        if (product.getDistance_km() != null) {
            score += Math.max(0, 50 - product.getDistance_km()) * 2;
        }
        
        // Puntuación por sostenibilidad
        if (product.getScore() != null) {
            score += product.getScore() * 0.5;
        }
        
        // Bonus por ser ecológico
        if (product.getIsEco() != null && product.getIsEco()) {
            score += 20;
        }
        
        // Bonus por stock disponible
        double stock = product.getStockAvailable() != null ? 
            product.getStockAvailable() : product.getStock();
        score += Math.min(stock * 2, 20);
        
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
        switch (sortType) {
            case 0: // Precio menor a mayor
                Collections.sort(filteredProducts, (p1, p2) -> 
                    Double.compare(p1.getPrice(), p2.getPrice()));
                break;
            case 1: // Precio mayor a menor
                Collections.sort(filteredProducts, (p1, p2) -> 
                    Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case 2: // Distancia más cerca
                Collections.sort(filteredProducts, (p1, p2) -> {
                    Double d1 = p1.getDistance_km() != null ? p1.getDistance_km() : Double.MAX_VALUE;
                    Double d2 = p2.getDistance_km() != null ? p2.getDistance_km() : Double.MAX_VALUE;
                    return Double.compare(d1, d2);
                });
                break;
            case 3: // Sostenibilidad mayor
                Collections.sort(filteredProducts, (p1, p2) -> {
                    Double s1 = p1.getScore() != null ? p1.getScore() : 0.0;
                    Double s2 = p2.getScore() != null ? p2.getScore() : 0.0;
                    return Double.compare(s2, s1);
                });
                break;
            case 4: // Stock disponible
                Collections.sort(filteredProducts, (p1, p2) -> {
                    double stock1 = p1.getStockAvailable() != null ? p1.getStockAvailable() : p1.getStock();
                    double stock2 = p2.getStockAvailable() != null ? p2.getStockAvailable() : p2.getStock();
                    return Double.compare(stock2, stock1);
                });
                break;
            case 5: // Algoritmo optimizado
                applyOptimizationAlgorithm();
                break;
        }
        
        productAdapter.updateProducts(filteredProducts);
    }

    // Implementación de OnProductActionListener
    @Override
    public void onAddToCart(Product product) {
        addProductToCart(product);
    }

    @Override
    public void onViewDetails(Product product) {
        Toast.makeText(getContext(), "Ver detalles de " + product.getName(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Cantidad actualizada: " + cartItem.getQuantity(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Si no existe, agregar nuevo item al carrito
        CartItem newCartItem = new CartItem(product, 1);
        cartItems.add(newCartItem);
        updateCartUI();
        Toast.makeText(getContext(), product.getName() + " agregado al carrito", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "El carrito ya está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar diálogo de confirmación
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Vaciar carrito")
                .setMessage("¿Está seguro de que desea vaciar el carrito?")
                .setPositiveButton("Sí, vaciar", (dialog, which) -> {
                    cartItems.clear();
                    updateCartUI();
                    Toast.makeText(getContext(), "Carrito vaciado", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getContext(), item.getProductName() + " eliminado del carrito", Toast.LENGTH_SHORT).show();
    }
}

