package com.example.iotapp.ui.fragments.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.iotapp.adapters.FarmerProductAdapter;
import com.example.iotapp.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class FarmerProductsFragment extends Fragment {
    private RecyclerView productsRecyclerView;
    private FarmerProductAdapter productAdapter;
    private List<Product> productsList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_products, container, false);
        
        // Inicializar RecyclerView
        productsRecyclerView = view.findViewById(R.id.recyclerView);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Inicializar lista de productos
        productsList = new ArrayList<>();
        productAdapter = new FarmerProductAdapter(productsList);
        productsRecyclerView.setAdapter(productAdapter);
        
        // Configurar FAB para añadir nuevo producto
        FloatingActionButton fabAddProduct = view.findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());
        
        // Cargar productos
        loadProducts();
        
        return view;
    }
    
    private void loadProducts() {
        // TODO: Implementar carga de productos desde el backend
        // Por ahora, añadimos datos de ejemplo
        productsList.add(new Product("1", "Tomates Orgánicos", "Tomates frescos de cultivo ecológico", 2.99, 50));
        productsList.add(new Product("2", "Lechuga", "Lechuga fresca de temporada", 1.99, 30));
        productAdapter.notifyDataSetChanged();
    }
    
    private void showAddProductDialog() {
        // TODO: Implementar diálogo para añadir nuevo producto
        // 1. Mostrar formulario con campos:
        //    - Nombre
        //    - Descripción
        //    - Precio
        //    - Stock inicial
        //    - Fecha de cosecha
        //    - Certificaciones
        // 2. Validar datos
        // 3. Guardar en backend
    }
} 