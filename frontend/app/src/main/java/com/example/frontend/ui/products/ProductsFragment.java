package com.example.frontend.ui.products;

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
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.adapters.ProductAdapter;
import com.example.frontend.model.Product;
import com.example.frontend.api.ApiService;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ProductsFragment extends Fragment implements ProductAdapter.OnProductClickListener {
    
    @Inject
    ApiService apiService;
    
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> products = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        
        productAdapter = new ProductAdapter(products, this);
        recyclerViewProducts.setAdapter(productAdapter);
        
        loadProducts();
    }

    private void loadProducts() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    products.clear();
                    products.addAll(response.body());
                    productAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        // Navegar al detalle del producto
        Bundle args = new Bundle();
        args.putString("productId", product.getId());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_products_to_product_detail, args);
    }

    @Override
    public void onAddToCartClick(Product product) {
        // Implementar lógica para añadir al carrito
        Toast.makeText(getContext(), "Producto añadido al carrito", Toast.LENGTH_SHORT).show();
    }
} 