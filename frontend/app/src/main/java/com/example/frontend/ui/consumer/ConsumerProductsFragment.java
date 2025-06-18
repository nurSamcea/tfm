package com.example.frontend.ui.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.ui.adapters.ProductAdapter;
import com.example.frontend.ui.fragments.ProductsFragmentDirections;

import java.util.ArrayList;
import java.util.List;

public class ConsumerProductsFragment extends Fragment {
    private static final String TAG = "ConsumerProductsFragment";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> products;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando el fragmento");
        View view = inflater.inflate(R.layout.fragment_consumer_products, container, false);
        
        // Inicializar RecyclerView
        recyclerView = view.findViewById(R.id.products_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Inicializar lista de productos
        products = new ArrayList<>();
        adapter = new ProductAdapter(product -> {
            Navigation.findNavController(requireView())
                    .navigate(ProductsFragmentDirections.actionProductsFragmentToAddProductFragment());
        }, true);        recyclerView.setAdapter(adapter);
        
        // Cargar productos de ejemplo
        loadSampleProducts();
        
        return view;
    }

    private void loadSampleProducts() {
        // Datos de ejemplo
        products.add(new Product("1", "Tomates Orgánicos", "Tomates frescos de cultivo ecológico", 2.99, 50, 6));
        products.add(new Product("2", "Lechuga", "Lechuga fresca de temporada", 1.99, 30, 20));
        products.add(new Product("3", "Zanahorias", "Zanahorias orgánicas", 1.50, 40, 10));
        products.add(new Product("4", "Manzanas", "Manzanas de producción local", 2.50, 60, 40));
        
        adapter.submitList(products);
    }
} 