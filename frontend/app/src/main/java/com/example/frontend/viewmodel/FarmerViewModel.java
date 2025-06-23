package com.example.frontend.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.frontend.model.Product;
import com.example.frontend.api.ApiService;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FarmerViewModel extends ViewModel {
    private final ApiService apiService;
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public FarmerViewModel(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadProducts() {
        isLoading.setValue(true);
        apiService.getProducts().enqueue(new retrofit2.Callback<List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    products.setValue(response.body());
                } else {
                    error.setValue("Error al cargar productos");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }
} 