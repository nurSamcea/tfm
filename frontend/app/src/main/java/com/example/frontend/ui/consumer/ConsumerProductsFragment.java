package com.example.frontend.ui.consumer;

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
import com.example.frontend.ui.adapters.CartAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConsumerProductsFragment extends Fragment {
    private static final String TAG = "ConsumerProductsFragment";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> products;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<ConsumerOrder.OrderItem> cartItems = new ArrayList<>();
    private EditText searchBar;
    private CheckBox filterDistance, filterPrice, filterGlutenFree, filterEco, filterCategory;
    private double totalCarrito = 0.0;
    private TextView cartTotalText;
    private CartPreferences cartPrefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando el fragmento");
        View view = inflater.inflate(R.layout.fragment_consumer_products, container, false);
        cartPrefs = new CartPreferences(requireContext());

        // Inicializar UI
        searchBar = view.findViewById(R.id.search_bar);
        filterDistance = view.findViewById(R.id.filter_distance);
        filterPrice = view.findViewById(R.id.filter_price);
        filterGlutenFree = view.findViewById(R.id.filter_gluten_free);
        filterEco = view.findViewById(R.id.filter_eco);
        filterCategory = view.findViewById(R.id.filter_category);

        Button btnToggleFilters = view.findViewById(R.id.btn_toggle_filters);
        HorizontalScrollView filtersScroll = view.findViewById(R.id.filters_scroll);
        btnToggleFilters.setOnClickListener(v -> {
            if (filtersScroll.getVisibility() == View.GONE) {
                filtersScroll.setVisibility(View.VISIBLE);
                btnToggleFilters.setText("Ocultar filtros");
            } else {
                filtersScroll.setVisibility(View.GONE);
                btnToggleFilters.setText("Filtros");
            }
        });

        // Cargar estado guardado
        List<ConsumerOrder.OrderItem> savedCart = cartPrefs.getCartItems();
        if (savedCart != null) cartItems.addAll(savedCart);
        searchBar.setText(cartPrefs.getSearchQuery());
        filterDistance.setChecked(cartPrefs.getFilterDistance());
        filterPrice.setChecked(cartPrefs.getFilterPrice());
        filterGlutenFree.setChecked(cartPrefs.getFilterGlutenFree());
        filterEco.setChecked(cartPrefs.getFilterEco());
        filterCategory.setChecked(cartPrefs.getFilterCategory());

        // Listeners
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                cartPrefs.saveSearchQuery(s.toString());
                filtrarProductos();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        filterDistance.setOnCheckedChangeListener((b, v) -> {
            cartPrefs.saveFilterDistance(v);
            filtrarProductos();
        });
        filterPrice.setOnCheckedChangeListener((b, v) -> {
            cartPrefs.saveFilterPrice(v);
            filtrarProductos();
        });
        filterGlutenFree.setOnCheckedChangeListener((b, v) -> {
            cartPrefs.saveFilterGlutenFree(v);
            filtrarProductos();
        });
        filterEco.setOnCheckedChangeListener((b, v) -> {
            cartPrefs.saveFilterEco(v);
            filtrarProductos();
        });
        filterCategory.setOnCheckedChangeListener((b, v) -> {
            cartPrefs.saveFilterCategory(v);
            filtrarProductos();
        });

        // Carrito
        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartItems);
        cartRecyclerView.setAdapter(cartAdapter);
        cartAdapter.setOnCartItemClickListener(new CartAdapter.OnCartItemClickListener() {
            public void onQuantityChanged(ConsumerOrder.OrderItem item, int newQuantity) {
                item.setQuantity(newQuantity);
                actualizarTotalCarrito();
                cartPrefs.saveCartItems(cartItems);
                cartAdapter.notifyDataSetChanged();
            }
            public void onRemoveItem(ConsumerOrder.OrderItem item) {
                cartItems.remove(item);
                actualizarTotalCarrito();
                cartPrefs.saveCartItems(cartItems);
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
                Toast.makeText(getContext(), "Añadido al carrito", Toast.LENGTH_SHORT).show();
            } else {
                item.setQuantity(item.getQuantity() + 1);
            }
            actualizarTotalCarrito();
            cartPrefs.saveCartItems(cartItems);
            cartAdapter.notifyDataSetChanged();
        }, true);
        recyclerView.setAdapter(adapter);

        // Cargar productos y estado
        loadSampleProducts();
        cartTotalText = view.findViewById(R.id.cart_total_text);
        actualizarTotalCarrito();
        filtrarProductos();

        Button btnFinalizeCart = view.findViewById(R.id.btn_finalize_cart);
        btnFinalizeCart.setOnClickListener(v -> {
            // Guardar carrito actual
            cartPrefs.saveCartItems(cartItems);
            
            // Mostrar mensaje de éxito
            Toast.makeText(getContext(), "Carrito guardado correctamente", Toast.LENGTH_SHORT).show();
            
            // Intentar navegar de forma segura
            try {
                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.navigation_consumer_purchases);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al navegar: " + e.getMessage());
                Toast.makeText(getContext(), "Error al navegar al carrito", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void loadSampleProducts() {
        products.add(new Product("1", "Tomates Orgánicos", "Tomates frescos de cultivo ecológico", 2.99, 50, 6));
        products.add(new Product("2", "Lechuga", "Lechuga fresca de temporada", 1.99, 30, 20));
        products.add(new Product("3", "Zanahorias", "Zanahorias orgánicas", 1.50, 40, 10));
        products.add(new Product("4", "Manzanas", "Manzanas de producción local", 2.50, 60, 40));
        adapter.submitList(new ArrayList<>(products));
    }

    private void filtrarProductos() {
        String query = searchBar.getText().toString().toLowerCase(Locale.ROOT);
        List<Product> filtrados = new ArrayList<>();
        for (Product p : products) {
            boolean matches = p.getName().toLowerCase(Locale.ROOT).contains(query);
            if (filterGlutenFree.isChecked() && !p.getDescription().toLowerCase().contains("sin gluten")) continue;
            if (filterEco.isChecked() && !p.getDescription().toLowerCase().contains("eco")) continue;
            // Puedes seguir añadiendo lógica según filtros
            if (matches) filtrados.add(p);
        }
        adapter.submitList(filtrados);
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
}
