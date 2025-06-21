package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.ui.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class SupermarketOrdersInventoryFragment extends Fragment {
    private static final String TAG = "Curr.ERROR SupermarketInventoryFragment";

    private RecyclerView inventoryRecyclerView;
    private ProductAdapter adapter;
    private List<Product> inventoryList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando la aplicación");
        View view = inflater.inflate(R.layout.fragment_supermarket_orders_inventory, container, false);

        inventoryRecyclerView = view.findViewById(R.id.inventory_recycler_view);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ProductAdapter(product -> {
            // Acción si se desea editar/ver más detalles
        });

        inventoryRecyclerView.setAdapter(adapter);

        loadInventorySample();
        return view;
    }

    private void loadInventorySample() {
        inventoryList = new ArrayList<>();
        inventoryList.add(new Product("1", "Tomates", "Tomates frescos", 2.50, 100, 10));
        inventoryList.add(new Product("2", "Lechugas", "Lechugas verdes", 1.20, 50, 5));
        inventoryList.add(new Product("3", "Zanahorias", "Zanahorias bio", 1.00, 80, 8));
        adapter.submitList(inventoryList);
    }
}
