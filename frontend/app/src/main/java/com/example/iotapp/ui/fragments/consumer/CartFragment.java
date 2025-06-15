package com.example.iotapp.ui.fragments.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.iotapp.adapters.CartAdapter;
import com.example.iotapp.models.Order;
import com.example.iotapp.models.Product;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Order.OrderItem> cartItems;
    private TextView totalTextView;
    private Button checkoutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        // Inicializar RecyclerView
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Inicializar lista de items del carrito
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems);
        cartRecyclerView.setAdapter(cartAdapter);
        
        // Inicializar vistas
        totalTextView = view.findViewById(R.id.totalTextView);
        checkoutButton = view.findViewById(R.id.checkoutButton);
        
        // Configurar botón de checkout
        checkoutButton.setOnClickListener(v -> proceedToCheckout());
        
        // Cargar items del carrito
        loadCartItems();
        
        return view;
    }
    
    private void loadCartItems() {
        // TODO: Implementar carga de items del carrito desde SharedPreferences o base de datos local
        // Por ahora, añadimos datos de ejemplo
        Product product1 = new Product("1", "Tomates Orgánicos", "Tomates frescos", 2.99, 50);
        Product product2 = new Product("2", "Lechuga", "Lechuga fresca", 1.99, 30);
        
        cartItems.add(new Order.OrderItem(product1.getId(), 2, product1.getPrice()));
        cartItems.add(new Order.OrderItem(product2.getId(), 1, product2.getPrice()));
        
        cartAdapter.notifyDataSetChanged();
        updateTotal();
    }
    
    private void updateTotal() {
        double total = cartItems.stream()
                .mapToDouble(Order.OrderItem::getTotalPrice)
                .sum();
        totalTextView.setText(String.format("Total: €%.2f", total));
    }
    
    private void proceedToCheckout() {
        // TODO: Implementar proceso de checkout
        // 1. Validar stock
        // 2. Crear pedido
        // 3. Procesar pago
        // 4. Confirmar pedido
    }
} 