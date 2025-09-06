package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
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
    private List<InventoryItem> inventoryList;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando SupermarketStockFragment");
        View view = inflater.inflate(R.layout.fragment_farmer_stock, container, false);

        initializeViews(view);
        setupRecyclerView();
        loadProducts();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_stock);
        sessionManager = new SessionManager(requireContext());
        inventoryList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        if (recyclerView == null) {
            Log.e(TAG, "setupRecyclerView: RecyclerView es null");
            return;
        }
        
        // TODO: Crear adaptador específico para InventoryItem o convertir a Product
        // Por ahora usamos un adaptador temporal
        adapter = null;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        Log.d(TAG, "loadProducts: Cargando inventario del supermercado");
        
        // Obtener ID del supermercado desde la sesión
        Integer supermarketId = sessionManager.getUserId();
        if (supermarketId == null) {
            Log.e(TAG, "No se pudo obtener el ID del supermercado");
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del supermercado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cargar inventario del supermercado desde la API
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<InventoryItem>> call = api.getInventory(String.valueOf(supermarketId));
        
        call.enqueue(new Callback<List<InventoryItem>>() {
            @Override
            public void onResponse(Call<List<InventoryItem>> call, Response<List<InventoryItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    inventoryList.clear();
                    inventoryList.addAll(response.body());
                    // TODO: Notificar al adaptador cuando esté implementado
                    Log.d(TAG, "Inventario del supermercado cargado: " + inventoryList.size() + " items");
                } else {
                    Log.e(TAG, "Error al cargar inventario: " + response.code());
                    Toast.makeText(getContext(), "Error al cargar inventario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<InventoryItem>> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onProductAction(InventoryItem item, String action) {
        // Corregido: usar item.getProduct().getName() en lugar de item.getName()
        String itemName = item.getProduct() != null ? item.getProduct().getName() : "Item sin nombre";
        Log.d(TAG, "onProductAction: " + action + " para item: " + itemName);
        
        switch (action) {
            case "edit":
                // TODO: Implementar edición de item
                Toast.makeText(getContext(), "Editar item: " + itemName, Toast.LENGTH_SHORT).show();
                break;
            case "delete":
                // TODO: Implementar eliminación de item
                Toast.makeText(getContext(), "Eliminar item: " + itemName, Toast.LENGTH_SHORT).show();
                break;
            case "view":
                // TODO: Implementar vista de detalles
                Toast.makeText(getContext(), "Ver detalles: " + itemName, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
