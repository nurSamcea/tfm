package com.example.frontend.ui.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.ConsumerOrder;
import com.example.frontend.utils.CartPreferences;
import com.example.frontend.ui.adapters.CartAdapter;

import java.util.ArrayList;
import java.util.List;

public class ConsumerPurchasesFragment extends Fragment {
    private static final String TAG = "ConsumerPurchasesFragment";
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private CartPreferences cartPrefs;
    private List<ConsumerOrder.OrderItem> cartItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumer_purchases, container, false);

        TextView title = view.findViewById(R.id.title);
        title.setText("Carrito");

        cartPrefs = new CartPreferences(requireContext());

        // Cargar carrito
        List<ConsumerOrder.OrderItem> savedCart = cartPrefs.getCartItems();
        if (savedCart != null) cartItems.addAll(savedCart);

        // Configurar RecyclerView
        recyclerView = view.findViewById(R.id.purchases_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(cartAdapter);

        cartAdapter.setOnCartItemClickListener(new CartAdapter.OnCartItemClickListener() {
            @Override
            public void onQuantityChanged(ConsumerOrder.OrderItem item, int newQuantity) {
                item.setQuantity(newQuantity);
                cartPrefs.saveCartItems(cartItems);
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRemoveItem(ConsumerOrder.OrderItem item) {
                cartItems.remove(item);
                cartPrefs.saveCartItems(cartItems);
                cartAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}
