package com.example.frontend.ui.consumer;

import android.os.Bundle;
import android.util.Log;
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
import com.example.frontend.ui.adapters.CartAdapter;
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
        Log.d("ConsumerCheckout", "=== CONSUMER CHECKOUT FRAGMENT ===");
        Log.d("ConsumerCheckout", "onCreateView iniciado");
        View root = inflater.inflate(R.layout.fragment_consumer_checkout, container, false);
        Log.d("ConsumerCheckout", "Layout inflado correctamente");

        recyclerView = root.findViewById(R.id.recycler_cart_items);
        totalPriceText = root.findViewById(R.id.text_total_price);
        confirmPurchaseButton = root.findViewById(R.id.button_confirm_purchase);
        Log.d("ConsumerCheckout", "Componentes UI inicializados - recyclerView: " + (recyclerView != null ? "OK" : "NULL"));

        setupRecyclerView();
        Log.d("ConsumerCheckout", "RecyclerView configurado");
        loadCartItems();
        Log.d("ConsumerCheckout", "Items del carrito cargados");
        setupConfirmButton();
        Log.d("ConsumerCheckout", "Botón de confirmación configurado");

        Log.d("ConsumerCheckout", "=== FINALIZANDO INICIALIZACIÓN ===");
        Log.d("ConsumerCheckout", "Total de items en carrito: " + cartItems.size());
        Log.d("ConsumerCheckout", "Precio total: " + totalPrice + " €");
        Log.d("ConsumerCheckout", "Fragmento listo para usar");

        return root;
    }

    private void setupRecyclerView() {
        Log.d("ConsumerCheckout", "=== CONFIGURANDO RECYCLERVIEW ===");
        cartItems = new ArrayList<>();
        Log.d("ConsumerCheckout", "Lista de items del carrito inicializada");
        
        adapter = new CartAdapter(cartItems, new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                Log.d("ConsumerCheckout", "Cantidad cambiada en posición " + position + " a " + newQuantity);
                if (position >= 0 && position < cartItems.size()) {
                    cartItems.get(position).setQuantity(newQuantity);
                    adapter.notifyDataSetChanged();
                    updateTotalPrice();
                }
            }

            @Override
            public void onItemRemoved(int position) {
                Log.d("ConsumerCheckout", "Item removido en posición " + position);
                cartItems.remove(position);
                adapter.notifyDataSetChanged();
                updateTotalPrice();
            }
        });
        Log.d("ConsumerCheckout", "CartAdapter creado con listener");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        Log.d("ConsumerCheckout", "RecyclerView configurado con LayoutManager y Adapter");
    }

    private void loadCartItems() {
        Log.d("ConsumerCheckout", "=== CARGANDO ITEMS DEL CARRITO ===");
        // Aquí cargarías los items del carrito desde la lista de compra
        // Por ahora usamos datos de ejemplo
        Log.d("ConsumerCheckout", "Añadiendo items de ejemplo...");
        cartItems.add(new CartItem(1, "Tomates Ecológicos", 2.5, 2, "kg"));
        cartItems.add(new CartItem(2, "Lechuga Local", 1.8, 1, "unidad"));
        cartItems.add(new CartItem(3, "Zanahorias Orgánicas", 3.2, 1, "kg"));
        Log.d("ConsumerCheckout", "Items añadidos: " + cartItems.size());
        
        adapter.notifyDataSetChanged();
        Log.d("ConsumerCheckout", "Adapter notificado del cambio");
        updateTotalPrice();
        Log.d("ConsumerCheckout", "Precio total actualizado");
    }

    private void updateTotalPrice() {
        Log.d("ConsumerCheckout", "=== ACTUALIZANDO PRECIO TOTAL ===");
        totalPrice = 0.0;
        Log.d("ConsumerCheckout", "Precio total reiniciado a 0.0");
        
        for (CartItem item : cartItems) {
            double itemTotal = item.getPrice() * item.getQuantity();
            totalPrice += itemTotal;
            Log.d("ConsumerCheckout", "Item: " + item.getName() + " - Precio: " + item.getPrice() + " x " + item.getQuantity() + " = " + itemTotal);
        }
        
        Log.d("ConsumerCheckout", "Precio total calculado: " + totalPrice);
        totalPriceText.setText(String.format("Total: %.2f €", totalPrice));
        Log.d("ConsumerCheckout", "Texto del precio total actualizado: " + totalPriceText.getText());
    }

    private void setupConfirmButton() {
        Log.d("ConsumerCheckout", "=== CONFIGURANDO BOTÓN DE CONFIRMACIÓN ===");
        confirmPurchaseButton.setOnClickListener(v -> {
            Log.d("ConsumerCheckout", "Botón de confirmación clickeado");
            Log.d("ConsumerCheckout", "Items en carrito: " + cartItems.size());
            
            if (cartItems.isEmpty()) {
                Log.w("ConsumerCheckout", "Carrito vacío, no se puede confirmar");
                Toast.makeText(getContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Log.d("ConsumerCheckout", "Procediendo con la confirmación de compra...");
            confirmPurchase();
        });
        Log.d("ConsumerCheckout", "Listener del botón de confirmación configurado");
    }

    private void confirmPurchase() {
        Log.d("ConsumerCheckout", "=== CONFIRMANDO COMPRA ===");
        Log.d("ConsumerCheckout", "Precio total: " + totalPrice + " €");
        Log.d("ConsumerCheckout", "Items en carrito: " + cartItems.size());
        
        confirmPurchaseButton.setEnabled(false);
        confirmPurchaseButton.setText("Procesando...");
        Log.d("ConsumerCheckout", "Botón deshabilitado y texto cambiado a 'Procesando...'");

        // Crear la transacción
        Transaction transaction = new Transaction();
        transaction.setUserId(1); // ID del usuario actual
        transaction.setShoppingListId(1); // ID de la lista de compra
        transaction.setTotalPrice(totalPrice);
        transaction.setCurrency("EUR");
        transaction.setStatus("pending");
        Log.d("ConsumerCheckout", "Transacción creada - UserID: 1, ShoppingListID: 1, Total: " + totalPrice + " EUR, Status: pending");

        Log.d("ConsumerCheckout", "Creando llamada a la API...");
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Transaction> call = apiService.createTransaction(transaction);
        Log.d("ConsumerCheckout", "Llamada a la API creada, enviando...");

        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                Log.d("ConsumerCheckout", "=== RESPUESTA DE LA API ===");
                Log.d("ConsumerCheckout", "Código de respuesta: " + response.code());
                Log.d("ConsumerCheckout", "Respuesta exitosa: " + response.isSuccessful());
                Log.d("ConsumerCheckout", "Cuerpo de respuesta: " + (response.body() != null ? "OK" : "NULL"));
                
                confirmPurchaseButton.setEnabled(true);
                confirmPurchaseButton.setText("Confirmar Compra");
                Log.d("ConsumerCheckout", "Botón re-habilitado y texto restaurado");

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ConsumerCheckout", "Compra exitosa, transacción ID: " + response.body().getId());
                    Toast.makeText(getContext(), "¡Compra realizada con éxito!", Toast.LENGTH_LONG).show();
                    
                    // Limpiar carrito
                    Log.d("ConsumerCheckout", "Limpiando carrito...");
                    cartItems.clear();
                    adapter.notifyDataSetChanged();
                    updateTotalPrice();
                    Log.d("ConsumerCheckout", "Carrito limpiado y UI actualizada");
                    
                    // Navegar a la pantalla de confirmación o historial
                    navigateToConfirmation(response.body());
                } else {
                    Log.e("ConsumerCheckout", "Error en la respuesta de la API");
                    String errorMessage = "Error al procesar la compra";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                            Log.e("ConsumerCheckout", "Mensaje de error: " + errorMessage);
                        } catch (Exception e) {
                            Log.e("ConsumerCheckout", "Error al leer mensaje de error: " + e.getMessage());
                            // Usar mensaje por defecto
                        }
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Log.e("ConsumerCheckout", "=== FALLO EN LA LLAMADA A LA API ===");
                Log.e("ConsumerCheckout", "Error: " + t.getMessage(), t);
                
                confirmPurchaseButton.setEnabled(true);
                confirmPurchaseButton.setText("Confirmar Compra");
                Log.d("ConsumerCheckout", "Botón re-habilitado después del fallo");
                
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("ConsumerCheckout", "Toast de error mostrado en UI thread");
                    });
                } else {
                    Log.w("ConsumerCheckout", "Activity es null, no se puede mostrar Toast");
                }
            }
        });
    }

    private void navigateToConfirmation(Transaction transaction) {
        Log.d("ConsumerCheckout", "=== NAVEGANDO A CONFIRMACIÓN ===");
        Log.d("ConsumerCheckout", "Transacción ID: " + transaction.getId());
        Log.d("ConsumerCheckout", "Precio total: " + transaction.getTotalPrice());
        
        // Aquí navegarías a una pantalla de confirmación
        // Por ahora solo mostramos un mensaje
        Toast.makeText(getContext(), 
            "Transacción #" + transaction.getId() + " confirmada", 
            Toast.LENGTH_LONG).show();
        Log.d("ConsumerCheckout", "Toast de confirmación mostrado");
        Log.d("ConsumerCheckout", "Navegación completada");
    }
}
