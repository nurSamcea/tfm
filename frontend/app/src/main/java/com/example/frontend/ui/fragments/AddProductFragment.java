package com.example.frontend.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.frontend.databinding.FragmentAddProductBinding;
import com.example.frontend.model.Product;
import com.example.frontend.model.SustainabilityMetrics;
import com.example.frontend.api.ApiService;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import javax.inject.Inject;

@AndroidEntryPoint
public class AddProductFragment extends Fragment {
    private FragmentAddProductBinding binding;
    private static final String TAG = "Curr.ERROR FragmentAddProductBinding";

    @Inject
    ApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando la aplicación");
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSave.setOnClickListener(v -> saveProduct());
        binding.buttonCancel.setOnClickListener(v -> navigateBack());
    }

    private void saveProduct() {
        String name = binding.editTextName.getText().toString();
        String category = binding.editTextCategory.getText().toString();
        String priceStr = binding.editTextPrice.getText().toString();
        String quantityStr = binding.editTextQuantity.getText().toString();

        if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setQuantity(quantity);
        
        // Inicializar métricas de sostenibilidad
        SustainabilityMetrics metrics = new SustainabilityMetrics();
        metrics.setCarbonFootprint(0.0f); // Se calculará en el backend
        product.setSustainabilityMetrics(metrics);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonSave.setEnabled(false);

        apiService.createProduct(product).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(@NonNull Call<Product> call, @NonNull Response<Product> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonSave.setEnabled(true);

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Producto creado exitosamente", Toast.LENGTH_SHORT).show();
                    navigateBack();
                } else {
                    Toast.makeText(requireContext(), "Error al crear el producto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Product> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonSave.setEnabled(true);
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateBack() {
        NavHostFragment.findNavController(this).navigateUp();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 