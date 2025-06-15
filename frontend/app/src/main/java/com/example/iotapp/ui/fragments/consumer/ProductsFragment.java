package com.example.iotapp.ui.fragments.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.iotapp.adapters.ProductAdapter;
import com.example.iotapp.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {
    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productsList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        
        // Inicializar RecyclerView
        productsRecyclerView = view.findViewById(R.id.recyclerViewProducts);
        productsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        // Inicializar lista de productos
        productsList = new ArrayList<>();
        productAdapter = new ProductAdapter(productsList, true); // true para modo consumidor
        productsRecyclerView.setAdapter(productAdapter);
        
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
} 