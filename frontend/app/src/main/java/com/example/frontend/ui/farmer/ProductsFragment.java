package com.example.frontend.ui.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.frontend.R;
import com.example.frontend.databinding.FragmentFarmerProductsBinding;
import com.example.frontend.ui.adapters.ProductAdapter;
import com.example.frontend.ui.fragments.ProductsFragmentDirections;
import com.example.frontend.viewmodel.FarmerViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProductsFragment extends Fragment {
    private FragmentFarmerProductsBinding binding;
    private FarmerViewModel viewModel;
    private ProductAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        binding = FragmentFarmerProductsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(FarmerViewModel.class);
        setupRecyclerView();
        setupObservers();
        setupListeners();
        
        viewModel.loadProducts();
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(product -> {
            // Navegar al detalle del producto
            Navigation.findNavController(requireView())
                    .navigate(ProductsFragmentDirections.actionProductsFragmentToAddProductFragment());
        });
        
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.submitList(products);
            binding.emptyView.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        binding.fabAddProduct.setOnClickListener(v -> {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_productsFragment_to_addProductFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 