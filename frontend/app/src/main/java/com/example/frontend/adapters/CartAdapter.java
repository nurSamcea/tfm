package com.example.frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(int position, int newQuantity);
        void onItemRemoved(int position);
    }

    public CartAdapter(List<CartItem> cartItems, CartItemListener listener) {
        this.cartItems = cartItems;
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
        CartItem item = cartItems.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateItems(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    public void addItem(CartItem item) {
        cartItems.add(item);
        notifyItemInserted(cartItems.size() - 1);
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateItemQuantity(int position, int newQuantity) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.get(position).setQuantity(newQuantity);
            notifyItemChanged(position);
        }
    }

    public double getTotalPrice() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getTotalItems() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageProduct;
        private TextView textProductName;
        private TextView textProductDescription;
        private TextView textUnitPrice;
        private TextView textQuantity;
        private TextView textTotalPrice;
        private ImageButton buttonDecrease;
        private ImageButton buttonIncrease;
        private ImageButton buttonRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageProduct = itemView.findViewById(R.id.image_product);
            textProductName = itemView.findViewById(R.id.text_product_name);
            textProductDescription = itemView.findViewById(R.id.text_product_description);
            textUnitPrice = itemView.findViewById(R.id.text_unit_price);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            textTotalPrice = itemView.findViewById(R.id.text_total_price);
            buttonDecrease = itemView.findViewById(R.id.button_decrease);
            buttonIncrease = itemView.findViewById(R.id.button_increase);
            buttonRemove = itemView.findViewById(R.id.button_remove);
        }

        public void bind(CartItem item, int position) {
            // Configurar imagen del producto
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getImageUrl())
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .into(imageProduct);
            } else {
                imageProduct.setImageResource(R.drawable.ic_product_placeholder);
            }

            // Configurar información del producto
            textProductName.setText(item.getName());
            
            if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                textProductDescription.setText(item.getDescription());
                textProductDescription.setVisibility(View.VISIBLE);
            } else {
                textProductDescription.setVisibility(View.GONE);
            }

            textUnitPrice.setText(item.getFormattedPrice() + "/" + item.getUnit());
            textQuantity.setText(String.valueOf(item.getQuantity()));
            textTotalPrice.setText(item.getFormattedTotalPrice());

            // Configurar botones de cantidad
            buttonDecrease.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    int newQuantity = item.getQuantity() - 1;
                    item.setQuantity(newQuantity);
                    textQuantity.setText(String.valueOf(newQuantity));
                    textTotalPrice.setText(item.getFormattedTotalPrice());
                    
                    if (listener != null) {
                        listener.onQuantityChanged(position, newQuantity);
                    }
                }
            });

            buttonIncrease.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                item.setQuantity(newQuantity);
                textQuantity.setText(String.valueOf(newQuantity));
                textTotalPrice.setText(item.getFormattedTotalPrice());
                
                if (listener != null) {
                    listener.onQuantityChanged(position, newQuantity);
                }
            });

            // Configurar botón de eliminar
            buttonRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemRemoved(position);
                }
            });

            // Mostrar indicadores de certificación si es necesario
            if (item.isEco()) {
                textProductName.setText(textProductName.getText() + " 🌱");
            }

            if (item.isLocal()) {
                // Añadir indicador de producto local si es necesario
                textProductDescription.setText(textProductDescription.getText() + " 🏠 Local");
            }
        }
    }
}
