package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onViewDetails(Product product);
    }

    private List<Product> productList = new ArrayList<>();
    private final boolean isConsumerMode;
    private final OnProductClickListener listener;
    private int selectedPosition = -1;

    // Constructor adaptado para lambda
    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
        this.isConsumerMode = false; // por defecto falso; puedes sobrecargar si necesitas modo consumidor
    }

    // Constructor opcional con isConsumerMode
    public ProductAdapter(OnProductClickListener listener, boolean isConsumerMode) {
        this.listener = listener;
        this.isConsumerMode = isConsumerMode;
    }

    public void submitList(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.format("€%.2f", product.getPrice()));
        holder.stockTextView.setText(String.format("Stock: %d", product.getStock()));
        
        // Configurar descuento (ejemplo: 20% OFF)
        holder.discountTextView.setText("20% OFF");
        
        // Imagen de ejemplo
        holder.productImageView.setImageResource(R.drawable.ic_products);
        // Selección visual
        holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), position == selectedPosition ? R.color.primary_light : android.R.color.white));
        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
        // Botón añadir al carrito
        if (isConsumerMode) {
            holder.addToCartButton.setVisibility(View.VISIBLE);
            holder.addToCartButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        } else {
            holder.addToCartButton.setVisibility(View.GONE);
        }
        
        // Botón ver detalles
        if (isConsumerMode) {
            holder.viewDetailsButton.setVisibility(View.VISIBLE);
            holder.viewDetailsButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetails(product);
                }
            });
        } else {
            holder.viewDetailsButton.setVisibility(View.GONE);
        }
        // Si quieres mostrar el score o la distancia en el layout, añade aquí la lógica para mostrar esos campos en el ViewHolder.
        // Por ejemplo:
        // holder.scoreTextView.setText(product.getScore() != null ? String.format("Score: %.2f", product.getScore()) : "");
        // holder.distanceTextView.setText(product.getDistance_km() != null ? String.format("Distancia: %.1f km", product.getDistance_km()) : "");
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        TextView stockTextView;
        TextView discountTextView;
        Button addToCartButton;
        Button viewDetailsButton;
        ImageView productImageView;

        ProductViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            stockTextView = itemView.findViewById(R.id.productStock);
            discountTextView = itemView.findViewById(R.id.productDiscount);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            productImageView = itemView.findViewById(R.id.imageViewProduct);
        }
    }
}
