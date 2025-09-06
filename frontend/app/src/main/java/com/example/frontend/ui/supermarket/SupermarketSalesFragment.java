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
import com.example.frontend.model.User;
import com.example.frontend.models.Transaction;
import com.example.frontend.ui.adapters.SupermarketProductAdapter;
import com.example.frontend.ui.adapters.CartAdapter;
import com.example.frontend.ui.adapters.ConsumerSelectionAdapter;
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

public class SupermarketSalesFragment extends Fragment implements SupermarketProductAdapter.OnProductActionListener, CartAdapter.OnCartItemActionListener, ConsumerSelectionAdapter.OnConsumerSelectionListener {

    private static final String TAG = "SupermarketSalesFragment";

    // Views principales
    private RecyclerView recyclerViewProducts;
    private RecyclerView recyclerViewCart;
    private RecyclerView recyclerViewConsumers;
    private EditText searchEditText;
    private ChipGroup categoryChipGroup;
    private LinearLayout cartLayout;
    private TextView cartItemCount;
    private TextView cartTotal;
    private MaterialButton checkoutButton;
    private MaterialButton clearCartButton;
    private MaterialButton selectConsumerButton;

    // Adapters
    private SupermarketProductAdapter productAdapter;
    private CartAdapter cartAdapter;
    private ConsumerSelectionAdapter consumerAdapter;

    // Data
    private List<Product> allProducts;
    private List<Product> filteredProducts;
    private List<CartItem> cartItems;
    private List<User> availableConsumers;
    private User selectedConsumer;
    private SessionManager sessionManager;

    // Estados
    private boolean isSelectingConsumer = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando SupermarketSalesFragment");
        View view = inflater.inflate(R.layout.fragment_supermarket_sales, container, false);

        initializeViews(view);
        setupRecyclerViews();
        setupSearchAndFilters();
        setupCart();
        loadData();

        return view;
    }

    private void initializeViews(View view) {
        recyclerViewProducts = view.findViewById(R.id.recycler_products);
        recyclerViewCart = view.findViewById(R.id.recycler_cart);
        recyclerViewConsumers = view.findViewById(R.id.recycler_consumers);
        searchEditText = view.findViewById(R.id.search_edit_text);
        categoryChipGroup = view.findViewById(R.id.category_chip_group);
        cartLayout = view.findViewById(R.id.cart_layout);
        cartItemCount = view.findViewById(R.id.cart_item_count);
        cartTotal = view.findViewById(R.id.cart_total);
        checkoutButton = view.findViewById(R.id.checkout_button);
        clearCartButton = view.findViewById(R.id.clear_cart_button);
        selectConsumerButton = view.findViewById(R.id.select_consumer_button);

        sessionManager = new SessionManager(requireContext());
        allProducts = new ArrayList<>();
        filteredProducts = new ArrayList<>();
        cartItems = new ArrayList<>();
        availableConsumers = new ArrayList<>();
    }

    private void setupRecyclerViews() {
        // Adapter para productos
        productAdapter = new SupermarketProductAdapter(filteredProducts);
        productAdapter.setOnProductActionListener(this);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setAdapter(productAdapter);

        // Adapter para carrito
        cartAdapter = new CartAdapter(cartItems);
        cartAdapter.setOnCartItemActionListener(this);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCart.setAdapter(cartAdapter);

        // Adapter para consumidores
        consumerAdapter = new ConsumerSelectionAdapter(availableConsumers);
        consumerAdapter.setOnConsumerSelectionListener(this);
        recyclerViewConsumers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewConsumers.setAdapter(consumerAdapter);

        // Inicialmente ocultar la lista de consumidores
        recyclerViewConsumers.setVisibility(View.GONE);
    }

    private void setupSearchAndFilters() {
        // Búsqueda
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filtros de categoría
        setupCategoryFilters();
    }

    private void setupCategoryFilters() {
        String[] categories = {"Todos", "Frutas", "Verduras", "Lácteos", "Carnes", "Cereales", "Bebidas"};
        
        for (String category : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> filterProducts());
            categoryChipGroup.addView(chip);
        }
        
        // Seleccionar "Todos" por defecto
        if (categoryChipGroup.getChildCount() > 0) {
            ((Chip) categoryChipGroup.getChildAt(0)).setChecked(true);
        }
    }

    private void setupCart() {
        checkoutButton.setOnClickListener(v -> processCheckout());
        clearCartButton.setOnClickListener(v -> clearCart());
        selectConsumerButton.setOnClickListener(v -> toggleConsumerSelection());
        
        updateCartUI();
    }

    private void loadData() {
        loadSupermarketProducts();
        loadAvailableConsumers();
    }

    private void loadSupermarketProducts() {
        Log.d(TAG, "loadSupermarketProducts: Cargando productos del supermercado");
        
        Integer supermarketId = sessionManager.getUserId();
        if (supermarketId == null) {
            Log.e(TAG, "No se pudo obtener el ID del supermercado");
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del supermercado", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<Product>> call = api.getFarmerProducts(supermarketId);
        
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

    private void loadAvailableConsumers() {
        Log.d(TAG, "loadAvailableConsumers: Cargando consumidores disponibles");
        
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<User>> call = api.getUsersByRole("consumer");
        
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableConsumers.clear();
                    availableConsumers.addAll(response.body());
                    consumerAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Consumidores cargados: " + availableConsumers.size());
                } else {
                    Log.e(TAG, "Error al cargar consumidores: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e(TAG, "Error de conexión al cargar consumidores: " + t.getMessage());
            }
        });
    }

    private void filterProducts() {
        String searchText = searchEditText.getText().toString().toLowerCase();
        String selectedCategory = getSelectedCategory();
        
        filteredProducts.clear();
        
        for (Product product : allProducts) {
            // Filtro por texto
            boolean matchesSearch = searchText.isEmpty() || 
                product.getName().toLowerCase().contains(searchText) ||
                (product.getDescription() != null && product.getDescription().toLowerCase().contains(searchText));
            
            // Filtro por categoría
            boolean matchesCategory = selectedCategory.equals("Todos") || 
                (product.getCategory() != null && product.getCategory().equals(selectedCategory));
            
            // Solo productos con stock disponible
            boolean hasStock = product.getStockAvailable() > 0;
            
            if (matchesSearch && matchesCategory && hasStock) {
                filteredProducts.add(product);
            }
        }
        
        // Ordenar por nombre
        Collections.sort(filteredProducts, Comparator.comparing(Product::getName));
        
        productAdapter.notifyDataSetChanged();
    }

    private String getSelectedCategory() {
        for (int i = 0; i < categoryChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) categoryChipGroup.getChildAt(i);
            if (chip.isChecked()) {
                return chip.getText().toString();
            }
        }
        return "Todos";
    }

    // Implementación de OnProductActionListener
    @Override
    public void onAddToCart(Product product) {
        addProductToCart(product);
    }

    @Override
    public void onViewDetails(Product product) {
        Toast.makeText(getContext(), "Ver detalles de " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    // Implementación de OnCartItemActionListener
    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            cartItems.remove(item);
        } else {
            item.setQuantity(newQuantity);
        }
        updateCartUI();
    }

    @Override
    public void onRemoveItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        updateCartUI();
    }

    // Implementación de OnConsumerSelectionListener
    @Override
    public void onConsumerSelected(User consumer) {
        selectedConsumer = consumer;
        selectConsumerButton.setText("Cliente: " + consumer.getName());
        toggleConsumerSelection();
        Toast.makeText(getContext(), "Cliente seleccionado: " + consumer.getName(), Toast.LENGTH_SHORT).show();
    }

    // Métodos del carrito
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
        cartTotal.setText(String.format("Total: %.2f €", total));

        // Actualizar adapter del carrito
        cartAdapter.updateCartItems(cartItems);

        // Mostrar/ocultar carrito según si tiene items
        if (cartItems.isEmpty()) {
            cartLayout.setVisibility(View.GONE);
        } else {
            cartLayout.setVisibility(View.VISIBLE);
        }

        // Habilitar/deshabilitar botón de checkout
        checkoutButton.setEnabled(!cartItems.isEmpty() && selectedConsumer != null);
    }

    private void clearCart() {
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "El carrito ya está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Limpiar carrito")
                .setMessage("¿Estás seguro de que quieres limpiar el carrito?")
                .setPositiveButton("Sí, limpiar", (dialog, which) -> {
                    cartItems.clear();
                    updateCartUI();
                    Toast.makeText(getContext(), "Carrito limpiado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void toggleConsumerSelection() {
        isSelectingConsumer = !isSelectingConsumer;
        
        if (isSelectingConsumer) {
            recyclerViewConsumers.setVisibility(View.VISIBLE);
            selectConsumerButton.setText("Cancelar selección");
        } else {
            recyclerViewConsumers.setVisibility(View.GONE);
            selectConsumerButton.setText(selectedConsumer != null ? 
                "Cliente: " + selectedConsumer.getName() : "Seleccionar cliente");
        }
    }

    private void processCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedConsumer == null) {
            Toast.makeText(getContext(), "Debes seleccionar un cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear lista de OrderItems
        final List<OrderItem> orderItems = new ArrayList<>();
        final double[] totalPrice = {0};

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(
                Integer.parseInt(cartItem.getProduct().getId()),
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getProduct().getPrice()
            );
            orderItems.add(orderItem);
            totalPrice[0] += orderItem.total_price;
        }

        // Mostrar diálogo de confirmación
        String message = String.format(
            "¿Desea realizar la venta a %s?\n\n" +
            "Productos: %d\n" +
            "Total: %.2f €",
            selectedConsumer.getName(),
            cartItems.size(),
            totalPrice[0]
        );

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirmar venta")
                .setMessage(message)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    createSaleOrder(orderItems, totalPrice[0]);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void createSaleOrder(List<OrderItem> orderItems, double totalPrice) {
        Integer supermarketId = sessionManager.getUserId();
        if (supermarketId == null) {
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del supermercado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear OrderRequest
        OrderRequest orderRequest = new OrderRequest(
            selectedConsumer.getId(),
            "consumer",
            orderItems,
            totalPrice
        );

        // Enviar pedido al backend
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<Transaction> call = api.createOrderFromCart(supermarketId, "supermarket", orderRequest);
        
        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Venta realizada exitosamente", Toast.LENGTH_LONG).show();
                    
                    // Limpiar carrito y recargar productos
                    cartItems.clear();
                    selectedConsumer = null;
                    updateCartUI();
                    selectConsumerButton.setText("Seleccionar cliente");
                    loadSupermarketProducts();
                } else {
                    Log.e(TAG, "Error al crear venta: " + response.code());
                    Toast.makeText(getContext(), "Error al realizar la venta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Log.e(TAG, "Error de conexión al crear venta: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
