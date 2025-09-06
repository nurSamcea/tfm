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
import com.example.frontend.model.CartItem;
import com.example.frontend.model.Product;
import com.example.frontend.model.OrderItem;
import com.example.frontend.model.OrderRequest;
import com.example.frontend.models.Transaction;
import com.example.frontend.network.ApiClient;
import com.example.frontend.api.ApiService;
import com.example.frontend.utils.SessionManager;
import com.example.frontend.utils.NetworkUtils;

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
        
        adapter = new CartAdapter(cartItems);
        adapter.setOnCartItemActionListener(new CartAdapter.OnCartItemActionListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
                Log.d("ConsumerCheckout", "Cantidad cambiada a " + newQuantity);
                item.setQuantity(newQuantity);
                adapter.notifyDataSetChanged();
                updateTotalPrice();
            }

            @Override
            public void onRemoveItem(CartItem item) {
                Log.d("ConsumerCheckout", "Item removido");
                cartItems.remove(item);
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
        
        // Crear productos de ejemplo
        Product tomates = new Product();
        tomates.setId("1");
        tomates.setName("Tomates Ecológicos");
        tomates.setPrice(2.5);
        tomates.setCategory("Verduras");
        
        Product lechuga = new Product();
        lechuga.setId("2");
        lechuga.setName("Lechuga Local");
        lechuga.setPrice(1.8);
        lechuga.setCategory("Verduras");
        
        Product zanahorias = new Product();
        zanahorias.setId("3");
        zanahorias.setName("Zanahorias Orgánicas");
        zanahorias.setPrice(3.2);
        zanahorias.setCategory("Verduras");
        
        // Crear CartItems con los productos
        cartItems.add(new CartItem(tomates, 2));
        cartItems.add(new CartItem(lechuga, 1));
        cartItems.add(new CartItem(zanahorias, 1));
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
            double itemTotal = item.getTotalPrice();
            totalPrice += itemTotal;
            Log.d("ConsumerCheckout", "Item: " + item.getProductName() + " - Precio: " + item.getUnitPrice() + " x " + item.getQuantity() + " = " + itemTotal);
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

        // Verificar conectividad del backend
        NetworkUtils.testBackendConnection();
        
        // Crear la transacción usando el nuevo método
        Log.d("ConsumerCheckout", "Creando llamada a la API...");
        Log.d("ConsumerCheckout", "URL Base: " + ApiClient.getBaseUrl());
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Log.d("ConsumerCheckout", "ApiService creado correctamente");
        
        // Obtener ID del consumidor desde la sesión
        SessionManager sessionManager = new SessionManager(requireContext());
        Integer consumerId = sessionManager.getUserId();
        if (consumerId == null) {
            Log.e("ConsumerCheckout", "No se pudo obtener el ID del consumidor");
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del consumidor", Toast.LENGTH_SHORT).show();
            confirmPurchaseButton.setEnabled(true);
            confirmPurchaseButton.setText("Confirmar Compra");
            return;
        }
        
        // Crear lista de OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(
                Integer.parseInt(cartItem.getProduct().getId()),
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getProduct().getPrice()
            );
            orderItems.add(orderItem);
        }
        
        // Crear OrderRequest
        OrderRequest orderRequest = new OrderRequest(
            1, // TODO: Obtener seller_id del carrito
            "farmer", // TODO: Determinar seller_type según el carrito
            orderItems,
            totalPrice
        );
        
        Log.d("ConsumerCheckout", "OrderRequest creado:");
        Log.d("ConsumerCheckout", "- seller_id: " + orderRequest.seller_id);
        Log.d("ConsumerCheckout", "- seller_type: " + orderRequest.seller_type);
        Log.d("ConsumerCheckout", "- total_price: " + orderRequest.total_price);
        Log.d("ConsumerCheckout", "- order_items count: " + orderRequest.order_details.size());
        
        Call<Transaction> call = apiService.createOrderFromCart(consumerId.intValue(), "consumer", orderRequest);
        Log.d("ConsumerCheckout", "Llamada a la API creada, enviando...");
        Log.d("ConsumerCheckout", "URL completa: " + ApiClient.getBaseUrl() + "transactions/create-order");

        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                Log.d("ConsumerCheckout", "=== RESPUESTA DE LA API ===");
                Log.d("ConsumerCheckout", "Código de respuesta: " + response.code());
                Log.d("ConsumerCheckout", "Respuesta exitosa: " + response.isSuccessful());
                Log.d("ConsumerCheckout", "Cuerpo de respuesta: " + (response.body() != null ? "OK" : "NULL"));
                
                // Log detallado del response body
                if (response.body() != null) {
                    Log.d("ConsumerCheckout", "Transaction ID: " + response.body().getId());
                    Log.d("ConsumerCheckout", "Transaction Status: " + response.body().getStatus());
                    Log.d("ConsumerCheckout", "Transaction Total: " + response.body().getTotalPrice());
                } else {
                    Log.e("ConsumerCheckout", "Response body es NULL - problema de deserialización");
                }
                
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
                    Log.e("ConsumerCheckout", "isSuccessful: " + response.isSuccessful());
                    Log.e("ConsumerCheckout", "body is null: " + (response.body() == null));
                    
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
                Log.e("ConsumerCheckout", "Tipo de error: " + t.getClass().getSimpleName());
                Log.e("ConsumerCheckout", "URL de la llamada: " + call.request().url());
                Log.e("ConsumerCheckout", "Método: " + call.request().method());
                
                if (t instanceof java.net.UnknownHostException) {
                    Log.e("ConsumerCheckout", "ERROR: No se puede resolver el host. Verificar IP del backend.");
                } else if (t instanceof java.net.ConnectException) {
                    Log.e("ConsumerCheckout", "ERROR: No se puede conectar al servidor. Verificar que el backend esté ejecutándose.");
                } else if (t instanceof java.net.SocketTimeoutException) {
                    Log.e("ConsumerCheckout", "ERROR: Timeout de conexión. El servidor no responde.");
                } else if (t instanceof java.io.IOException) {
                    Log.e("ConsumerCheckout", "ERROR: Error de I/O. Problema de red o servidor.");
                }
                
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
