package com.example.frontend.ui.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.adapters.CartAdapter;
import com.example.frontend.models.CartItem;
import com.example.frontend.models.Transaction;
import com.example.frontend.network.ApiClient;
import com.example.frontend.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsumerCheckoutFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalPriceText;
    private Button confirmPurchaseButton;
    private List<CartItem> cartItems;
    private double totalPrice = 0.0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_consumer_checkout, container, false);

        recyclerView = root.findViewById(R.id.recycler_cart_items);
        totalPriceText = root.findViewById(R.id.text_total_price);
        confirmPurchaseButton = root.findViewById(R.id.button_confirm_purchase);

        setupRecyclerView();
        loadCartItems();
        setupConfirmButton();

        return root;
    }

    private void setupRecyclerView() {
        cartItems = new ArrayList<>();
        adapter = new CartAdapter(cartItems, new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                updateTotalPrice();
            }

            @Override
            public void onItemRemoved(int position) {
                cartItems.remove(position);
                adapter.notifyDataSetChanged();
                updateTotalPrice();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadCartItems() {
        // Aquí cargarías los items del carrito desde la lista de compra
        // Por ahora usamos datos de ejemplo
        cartItems.add(new CartItem(1, "Tomates Ecológicos", 2.5, 2, "kg"));
        cartItems.add(new CartItem(2, "Lechuga Local", 1.8, 1, "unidad"));
        cartItems.add(new CartItem(3, "Zanahorias Orgánicas", 3.2, 1, "kg"));
        
        adapter.notifyDataSetChanged();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        totalPrice = 0.0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        totalPriceText.setText(String.format("Total: %.2f €", totalPrice));
    }

    private void setupConfirmButton() {
        confirmPurchaseButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(getContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmPurchase();
        });
    }

    private void confirmPurchase() {
        confirmPurchaseButton.setEnabled(false);
        confirmPurchaseButton.setText("Procesando...");

        // Crear la transacción
        Transaction transaction = new Transaction();
        transaction.setUserId(1); // ID del usuario actual
        transaction.setShoppingListId(1); // ID de la lista de compra
        transaction.setTotalPrice(totalPrice);
        transaction.setCurrency("EUR");
        transaction.setStatus("pending");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Transaction> call = apiService.createTransaction(transaction);

        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                confirmPurchaseButton.setEnabled(true);
                confirmPurchaseButton.setText("Confirmar Compra");

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "¡Compra realizada con éxito!", Toast.LENGTH_LONG).show();
                    
                    // Limpiar carrito
                    cartItems.clear();
                    adapter.notifyDataSetChanged();
                    updateTotalPrice();
                    
                    // Navegar a la pantalla de confirmación o historial
                    navigateToConfirmation(response.body());
                } else {
                    String errorMessage = "Error al procesar la compra";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            // Usar mensaje por defecto
                        }
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                confirmPurchaseButton.setEnabled(true);
                confirmPurchaseButton.setText("Confirmar Compra");
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToConfirmation(Transaction transaction) {
        // Aquí navegarías a una pantalla de confirmación
        // Por ahora solo mostramos un mensaje
        Toast.makeText(getContext(), 
            "Transacción #" + transaction.getId() + " confirmada", 
            Toast.LENGTH_LONG).show();
    }
}
