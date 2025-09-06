package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.model.InventoryItem;
import com.example.frontend.ui.adapters.FarmerStockAdapter;
import com.example.frontend.api.ApiService;
import com.example.frontend.network.ApiClient;
import com.example.frontend.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupermarketStockFragment extends Fragment {
    private static final String TAG = "SupermarketStockFragment";

    private RecyclerView recyclerView;
    private FarmerStockAdapter adapter;
    private List<Product> productList;
    private EditText searchEditText;
    private ImageButton filterButton;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando SupermarketStockFragment");
        View view = inflater.inflate(R.layout.fragment_farmer_stock, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupSearchAndFilters();
        loadSupermarketStock();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_stock);
        searchEditText = view.findViewById(R.id.search_stock);
        filterButton = view.findViewById(R.id.filter_button);
        
        sessionManager = new SessionManager(requireContext());
        apiService = ApiClient.getClient().create(ApiService.class);
        
        productList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new FarmerStockAdapter(productList);
        adapter.setOnProductActionListener(new FarmerStockAdapter.OnProductActionListener() {
            @Override
            public void onEdit(Product product) {
                // Para el supermercado, podríamos permitir editar stock o precios
                Toast.makeText(getContext(), "Editar producto: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToggleHidden(Product product) {
                // Para el supermercado, podríamos permitir ocultar/mostrar productos
                Toast.makeText(getContext(), "Alternar visibilidad: " + product.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(Product product) {
                // Para el supermercado, podríamos permitir eliminar productos del stock
                Toast.makeText(getContext(), "Eliminar producto: " + product.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearchAndFilters() {
        searchEditText.setHint("Buscar en stock...");
        
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchEditText.setHint("");
            } else {
                searchEditText.setHint("Buscar en stock...");
            }
        });

        filterButton.setOnClickListener(v -> {
            // Implementar filtros para el stock del supermercado
            Toast.makeText(getContext(), "Filtros de stock (no implementado)", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadSupermarketStock() {
        Log.d(TAG, "loadSupermarketStock: Cargando stock del supermercado");
        
        String supermarketId = String.valueOf(sessionManager.getUserId());
        if (supermarketId == null || supermarketId.isEmpty()) {
            Log.e(TAG, "loadSupermarketStock: No hay usuario logueado");
            Toast.makeText(getContext(), "Error: Usuario no logueado", Toast.LENGTH_SHORT).show();
            loadMockStock(); // Cargar datos de ejemplo como fallback
            return;
        }
        
        Log.d(TAG, "loadSupermarketStock: Cargando inventario para supermercado ID: " + supermarketId);
        
        Call<List<InventoryItem>> call = apiService.getInventory(supermarketId);
        call.enqueue(new Callback<List<InventoryItem>>() {
            @Override
            public void onResponse(Call<List<InventoryItem>> call, Response<List<InventoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "loadSupermarketStock: Inventario cargado exitosamente");
                    processInventoryItems(response.body());
                } else {
                    Log.e(TAG, "loadSupermarketStock: Error en respuesta - " + response.code());
                    Toast.makeText(getContext(), "Error cargando inventario: " + response.code(), Toast.LENGTH_SHORT).show();
                    loadMockStock(); // Cargar datos de ejemplo como fallback
                }
            }
            
            @Override
            public void onFailure(Call<List<InventoryItem>> call, Throwable t) {
                Log.e(TAG, "loadSupermarketStock: Error en llamada API", t);
                Toast.makeText(getContext(), "Error de conexión. Cargando datos de ejemplo...", Toast.LENGTH_SHORT).show();
                loadMockStock(); // Cargar datos de ejemplo como fallback
            }
        });
    }

    private void processInventoryItems(List<InventoryItem> inventoryItems) {
        Log.d(TAG, "processInventoryItems: Procesando " + inventoryItems.size() + " items del inventario");
        
        productList.clear();
        
        for (InventoryItem item : inventoryItems) {
            Product product = item.getProduct();
            if (product != null) {
                // Actualizar el stock del producto con el stock del inventario
                product.setStock(item.getStock());
                productList.add(product);
                Log.d(TAG, "processInventoryItems: Agregado producto - " + product.getName() + " (Stock: " + item.getStock() + ")");
            }
        }
        
        adapter.updateProducts(productList);
        Log.d(TAG, "processInventoryItems: " + productList.size() + " productos procesados");
        
        if (productList.isEmpty()) {
            Toast.makeText(getContext(), "No hay productos en el inventario", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMockStock() {
        Log.d(TAG, "loadMockStock: Cargando datos de ejemplo");
        
        // Datos de ejemplo para el stock del supermercado
        productList.clear();
        
        Product product1 = new Product();
        product1.setId("1");
        product1.setName("Tomates Ecológicos");
        product1.setPrice(2.50);
        product1.setStock(150);
        product1.setCategory("Verduras");
        
        Product product2 = new Product();
        product2.setId("2");
        product2.setName("Lechuga Local");
        product2.setPrice(1.80);
        product2.setStock(80);
        product2.setCategory("Verduras");
        
        Product product3 = new Product();
        product3.setId("3");
        product3.setName("Zanahorias Orgánicas");
        product3.setPrice(3.20);
        product3.setStock(120);
        product3.setCategory("Verduras");
        
        Product product4 = new Product();
        product4.setId("4");
        product4.setName("Manzanas Rojas");
        product4.setPrice(2.80);
        product4.setStock(200);
        product4.setCategory("Frutas");
        
        Product product5 = new Product();
        product5.setId("5");
        product5.setName("Plátanos");
        product5.setPrice(1.50);
        product5.setStock(180);
        product5.setCategory("Frutas");
        
        productList.add(product1);
        productList.add(product2);
        productList.add(product3);
        productList.add(product4);
        productList.add(product5);
        
        adapter.updateProducts(productList);
        
        Log.d(TAG, "loadMockStock: " + productList.size() + " productos de ejemplo cargados");
    }
}