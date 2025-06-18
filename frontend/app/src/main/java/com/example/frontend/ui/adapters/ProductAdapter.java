package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private List<Product> productList = new ArrayList<>();
    private final boolean isConsumerMode;
    private final OnProductClickListener listener;

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
        holder.descriptionTextView.setText(product.getDescription());
        holder.priceTextView.setText(String.format("€%.2f", product.getPrice()));
        holder.stockTextView.setText(String.format("Stock: %d", product.getStock()));

        // Clic en toda la tarjeta
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        // Botón solo visible para consumidores
        if (isConsumerMode) {
            holder.addToCartButton.setVisibility(View.VISIBLE);
            holder.addToCartButton.setOnClickListener(v -> {
                // Aquí puedes extender para avisar también al listener si quieres
                // o manejar lógica desde el adapter
            });
        } else {
            holder.addToCartButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        TextView stockTextView;
        Button addToCartButton;

        ProductViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            descriptionTextView = itemView.findViewById(R.id.productDescription);
            priceTextView = itemView.findViewById(R.id.productPrice);
            stockTextView = itemView.findViewById(R.id.productStock);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}
