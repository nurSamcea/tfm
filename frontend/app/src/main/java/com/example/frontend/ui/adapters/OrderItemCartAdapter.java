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
import java.util.Locale;

public class OrderItemCartAdapter extends RecyclerView.Adapter<OrderItemCartAdapter.CartViewHolder> {
    private List<ConsumerOrder.OrderItem> cartItems;
    private OnCartItemClickListener listener;

    public interface OnCartItemClickListener {
        void onQuantityChanged(ConsumerOrder.OrderItem item, int newQuantity);
        void onRemoveItem(ConsumerOrder.OrderItem item);
    }

    public OrderItemCartAdapter(List<ConsumerOrder.OrderItem> cartItems) {
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
        
        // Mostrar nombre del producto
        String productName = item.getProductName();
        if (productName != null && !productName.trim().isEmpty()) {
            holder.productNameTextView.setText(productName);
        } else {
            holder.productNameTextView.setText("Producto " + (position + 1));
        }
        
        // Mostrar cantidad
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        
        // Mostrar precio unitario
        holder.unitPriceTextView.setText(String.format(Locale.getDefault(), "%.2f €/ud", item.getUnitPrice()));
        
        // Mostrar precio total
        double totalPrice = item.getUnitPrice() * item.getQuantity();
        holder.totalPriceTextView.setText(String.format(Locale.getDefault(), "%.2f €", totalPrice));
        
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
        
        // Botón para eliminar
        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void updateData(List<ConsumerOrder.OrderItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView quantityTextView;
        TextView unitPriceTextView;
        TextView totalPriceTextView;
        MaterialButton removeButton;
        MaterialButton increaseButton;
        MaterialButton decreaseButton;
        
        CartViewHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.product_name);
            quantityTextView = itemView.findViewById(R.id.product_quantity);
            unitPriceTextView = itemView.findViewById(R.id.product_price);
            totalPriceTextView = itemView.findViewById(R.id.product_total);
            removeButton = itemView.findViewById(R.id.btn_remove);
            increaseButton = itemView.findViewById(R.id.btn_increase);
            decreaseButton = itemView.findViewById(R.id.btn_decrease);
        }
    }
}
