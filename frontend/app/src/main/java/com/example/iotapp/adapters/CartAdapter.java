package com.example.iotapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Order;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Order.OrderItem> cartItems;
    private OnCartItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnCartItemClickListener {
        void onQuantityChanged(Order.OrderItem item, int newQuantity);
        void onRemoveItem(Order.OrderItem item);
    }

    public CartAdapter(List<Order.OrderItem> cartItems) {
        this.cartItems = cartItems;
    }

    public void setOnCartItemClickListener(OnCartItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Order.OrderItem item = cartItems.get(position);
        holder.productNameTextView.setText(item.getProductName() != null ? item.getProductName() : "Producto");
        holder.quantityTextView.setText("x" + item.getQuantity());
        holder.priceTextView.setText(String.format("€%.2f", item.getUnitPrice() * item.getQuantity()));
        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView quantityTextView;
        TextView priceTextView;
        ImageButton removeButton;
        CartViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.textViewProductName);
            quantityTextView = itemView.findViewById(R.id.textViewQuantity);
            priceTextView = itemView.findViewById(R.id.textViewPrice);
            removeButton = itemView.findViewById(R.id.buttonRemove);
        }
    }
} 