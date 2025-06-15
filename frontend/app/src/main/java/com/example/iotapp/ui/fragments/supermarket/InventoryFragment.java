package com.example.iotapp.ui.fragments.supermarket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.iotapp.adapters.SupermarketInventoryAdapter;
import com.example.iotapp.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {
    private RecyclerView inventoryRecyclerView;
    private SupermarketInventoryAdapter inventoryAdapter;
    private List<Product> inventoryList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supermarket_inventory, container, false);
        
        // Inicializar RecyclerView
        inventoryRecyclerView = view.findViewById(R.id.inventoryRecyclerView);
        inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Inicializar lista de inventario
        inventoryList = new ArrayList<>();
        inventoryAdapter = new SupermarketInventoryAdapter(inventoryList);
        inventoryRecyclerView.setAdapter(inventoryAdapter);
        
        // Configurar FAB para añadir nuevo producto
        FloatingActionButton fabAddProduct = view.findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());
        
        // Cargar inventario
        loadInventory();
        
        return view;
    }
    
    private void loadInventory() {
        // TODO: Implementar carga de inventario desde el backend
        // Por ahora, añadimos datos de ejemplo
        inventoryList.add(new Product("1", "Tomates Orgánicos", "Tomates frescos de cultivo ecológico", 2.99, 50));
        inventoryList.add(new Product("2", "Lechuga", "Lechuga fresca de temporada", 1.99, 30));
        inventoryAdapter.notifyDataSetChanged();
    }
    
    private void showAddProductDialog() {
        // TODO: Implementar diálogo para añadir nuevo producto al inventario
        // 1. Mostrar formulario con campos:
        //    - Seleccionar producto de la lista de agricultores
        //    - Cantidad a comprar
        //    - Precio de compra
        //    - Precio de venta
        // 2. Validar datos
        // 3. Crear pedido al agricultor
        // 4. Actualizar inventario
    }
} 