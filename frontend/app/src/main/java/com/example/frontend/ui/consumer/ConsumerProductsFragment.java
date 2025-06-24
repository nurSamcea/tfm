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
import com.example.frontend.api.ApiService;
import com.example.frontend.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.frontend.api.ProductFilterRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

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
                loadSampleProducts();
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        filterDistance.setOnCheckedChangeListener((b, v) -> {
            cartPrefs.saveFilterDistance(v);
            loadSampleProducts();
        });
        filterPrice.setOnCheckedChangeListener((b, v) -> {
            cartPrefs.saveFilterPrice(v);
            loadSampleProducts();
        });
        filterGlutenFree.setOnCheckedChangeListener((b, v) -> {
            cartPrefs.saveFilterGlutenFree(v);
            loadSampleProducts();
        });
        filterEco.setOnCheckedChangeListener((b, v) -> {
            cartPrefs.saveFilterEco(v);
            loadSampleProducts();
        });
        filterCategory.setOnCheckedChangeListener((b, v) -> {
            cartPrefs.saveFilterCategory(v);
            loadSampleProducts();
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

    private ProductFilterRequest construirRequest() {
        ProductFilterRequest req = new ProductFilterRequest();
        req.search = searchBar.getText().toString();
        req.filters = new HashMap<>();
        req.filters.put("eco", filterEco.isChecked());
        req.filters.put("gluten_free", filterGlutenFree.isChecked());
        req.filters.put("price", filterPrice.isChecked()); // 👈 importante
        req.filters.put("distance", filterDistance.isChecked()); // 👈 importante

        boolean usarScore = filterDistance.isChecked(); // solo usamos score si hay distancia

        if (usarScore) {
            req.weights = new HashMap<>();
            if (filterPrice.isChecked()) {
                req.weights.put("price", 1.0f);
            }
            if (filterDistance.isChecked()) {
                req.weights.put("distance", 1.0f);
            }
        } else {
            req.weights = null; // 🔥 NO usamos pesos si solo es precio
        }

        return req;
    }

    private void loadSampleProducts() {
        ProductFilterRequest req = construirRequest();
        ApiService apiService = RetrofitClient.getInstance().getRetrofit().create(ApiService.class);
        Call<List<Product>> call = apiService.getProductsOptimized(req);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    products.clear();
                    products.addAll(response.body());
                    adapter.submitList(new ArrayList<>(products));
                } else {
                    Toast.makeText(getContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
