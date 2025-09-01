package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;

import com.example.frontend.R;
import com.example.frontend.model.ConsumerOrder;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<ConsumerOrder.OrderItem> cartItems;
    private OnCartItemClickListener listener;
    private int selectedPosition = 0;

    public interface OnCartItemClickListener {
        void onQuantityChanged(ConsumerOrder.OrderItem item, int newQuantity);
        void onRemoveItem(ConsumerOrder.OrderItem item);
    }

    public CartAdapter(List<ConsumerOrder.OrderItem> cartItems) {
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
        ConsumerOrder.OrderItem item = cartItems.get(position);
        holder.productNameTextView.setText(item.getProductName() != null ? item.getProductName() : "Producto");
        holder.quantityTextView.setText("x" + item.getQuantity());
        holder.priceTextView.setText(String.format("€%.2f", item.getUnitPrice() * item.getQuantity()));
        
        // Botón para aumentar cantidad
        holder.increaseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChanged(item, item.getQuantity() + 1);
            }
        });
        
        // Botón para disminuir cantidad
        holder.decreaseButton.setOnClickListener(v -> {
            if (listener != null && item.getQuantity() > 1) {
                listener.onQuantityChanged(item, item.getQuantity() - 1);
            }
        });
        
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
        MaterialButton increaseButton;
        MaterialButton decreaseButton;
        CartViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.text_product_name);
            quantityTextView = itemView.findViewById(R.id.text_quantity);
            priceTextView = itemView.findViewById(R.id.text_total_price);
            removeButton = itemView.findViewById(R.id.button_remove);
            increaseButton = itemView.findViewById(R.id.button_increase);
            decreaseButton = itemView.findViewById(R.id.button_decrease);
        }
    }
} 