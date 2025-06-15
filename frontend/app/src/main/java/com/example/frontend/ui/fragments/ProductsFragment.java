package com.example.frontend.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.frontend.databinding.FragmentFarmerProductsBinding;
import com.example.frontend.model.Product;
import com.example.frontend.api.ApiService;
import com.example.frontend.ui.adapters.ProductAdapter;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

@AndroidEntryPoint
public class ProductsFragment extends Fragment implements ProductAdapter.OnProductClickListener {
    private FragmentFarmerProductsBinding binding;
    private ProductAdapter adapter;
    
    @Inject
    ApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        binding = FragmentFarmerProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        loadProducts();

        binding.fabAddProduct.setOnClickListener(v -> navigateToAddProduct());
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(this);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);

        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    adapter.submitList(response.body());
                } else {
                    Toast.makeText(requireContext(), "Error al cargar los productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToAddProduct() {
        NavHostFragment.findNavController(this)
                .navigate(ProductsFragmentDirections.actionProductsFragmentToAddProductFragment());
    }

    @Override
    public void onProductClick(Product product) {
        // TODO: Implementar navegación al detalle del producto
        Toast.makeText(requireContext(), "Producto seleccionado: " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 