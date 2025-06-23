package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.ui.adapters.SupermarketCartAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartBottomSheetFragment extends BottomSheetDialogFragment {

    private List<Product> cartItems;
    private SupermarketCartAdapter adapter;
    private double total = 0;

    public CartBottomSheetFragment(List<Product> cartItems) {
        this.cartItems = cartItems;
    }

    public interface OnCartActionListener {
        void onConfirmOrder(List<Product> confirmedItems);
        void onRemoveItem(Product product);
    }

    private OnCartActionListener listener;

    public void setOnCartActionListener(OnCartActionListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart_bottom_sheet, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.cartRecyclerView);
        TextView totalText = view.findViewById(R.id.totalPriceTextView);
        Button confirmBtn = view.findViewById(R.id.confirmOrderButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SupermarketCartAdapter(cartItems, product -> {
            cartItems.remove(product);
            adapter.notifyDataSetChanged();
            updateTotal(totalText);
            if (listener != null) listener.onRemoveItem(product);
        });
        recyclerView.setAdapter(adapter);

        updateTotal(totalText);

        confirmBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmOrder(cartItems);
                dismiss();
            }
        });

        return view;
    }

    private void updateTotal(TextView totalText) {
        total = 0;
        for (Product p : cartItems) {
            total += p.getPrice(); // suponiendo 1 unidad por producto, puedes adaptarlo
        }
        totalText.setText(String.format("Total: %.2f €", total));
    }
}
