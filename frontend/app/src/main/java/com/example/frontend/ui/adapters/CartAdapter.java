package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.example.frontend.network.ApiClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemActionListener listener;

    public interface OnCartItemActionListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public void setOnCartItemActionListener(OnCartItemActionListener listener) {
        this.listener = listener;
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName, productPrice, productQuantity, productTotal, productFarmer;
        private MaterialButton btnDecrease, btnIncrease, btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            productTotal = itemView.findViewById(R.id.product_total);
            productFarmer = itemView.findViewById(R.id.product_farmer);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnRemove = itemView.findViewById(R.id.btn_remove);

            // Configurar listeners
            btnDecrease.setOnClickListener(v -> {
                if (listener != null) {
                    CartItem item = cartItems.get(getAdapterPosition());
                    if (item.getQuantity() > 1) {
                        listener.onQuantityChanged(item, item.getQuantity() - 1);
                    }
                }
            });

            btnIncrease.setOnClickListener(v -> {
                if (listener != null) {
                    CartItem item = cartItems.get(getAdapterPosition());
                    listener.onQuantityChanged(item, item.getQuantity() + 1);
                }
            });

            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveItem(cartItems.get(getAdapterPosition()));
                }
            });
        }

        public void bind(CartItem cartItem) {
            productName.setText(cartItem.getProductName());
            productPrice.setText(String.format("Precio: %.2f €", cartItem.getUnitPrice()));
            productQuantity.setText(String.valueOf(cartItem.getQuantity()));
            productTotal.setText(String.format("Total: %.2f €", cartItem.getTotalPrice()));
            productFarmer.setText(cartItem.getFarmerInfo());

            // Mostrar imagen del producto si existe
            if (cartItem.getProduct().getImageUrl() != null && !cartItem.getProduct().getImageUrl().isEmpty()) {
                String imageUrl = cartItem.getProduct().getImageUrl();
                if (imageUrl.startsWith("/")) {
                    String baseUrl = ApiClient.getBaseUrl();
                    if (baseUrl.endsWith("/")) {
                        baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                    }
                    imageUrl = baseUrl + imageUrl;
                }
                
                imageUrl += "?t=" + System.currentTimeMillis();
                
                Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.ic_product_placeholder);
            }

            // Deshabilitar botón de disminuir si la cantidad es 1
            btnDecrease.setEnabled(cartItem.getQuantity() > 1);
        }
    }
}