package com.example.frontend.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SupermarketInventoryAdapter extends RecyclerView.Adapter<SupermarketInventoryAdapter.ViewHolder> {
    private List<Product> products;
    private OnInventoryActionListener listener;

    public interface OnInventoryActionListener {
        void onUpdateStock(Product product);
        void onUpdatePrice(Product product);
        void onViewMetrics(Product product);
        void onOrderMore(Product product);
    }

    public SupermarketInventoryAdapter(List<Product> products, OnInventoryActionListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supermarket_inventory, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        holder.productName.setText(product.getName());
        holder.productDescription.setText(product.getDescription());
        holder.productStock.setText(String.format("Stock: %d unidades", product.getStock()));
        holder.productPrice.setText(String.format("%.2f €", product.getPrice()));
        holder.farmerName.setText(String.format("Agricultor ID: %d", product.getFarmerId()));
        holder.harvestDate.setText(String.format("Fecha de cosecha: %s", 
            dateFormat.format(product.getHarvestDate())));

        holder.updateStockButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdateStock(product);
            }
        });

        holder.updatePriceButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdatePrice(product);
            }
        });

        holder.metricsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewMetrics(product);
            }
        });

        holder.orderMoreButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderMore(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productDescription;
        TextView productStock;
        TextView productPrice;
        TextView farmerName;
        TextView harvestDate;
        Button updateStockButton;
        Button updatePriceButton;
        Button metricsButton;
        Button orderMoreButton;

        ViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.productName);
            productDescription = view.findViewById(R.id.productDescription);
            productStock = view.findViewById(R.id.productStock);
            productPrice = view.findViewById(R.id.productPrice);
            farmerName = view.findViewById(R.id.farmerName);
            harvestDate = view.findViewById(R.id.harvestDate);
            updateStockButton = view.findViewById(R.id.updateStockButton);
            updatePriceButton = view.findViewById(R.id.updatePriceButton);
            metricsButton = view.findViewById(R.id.metricsButton);
            orderMoreButton = view.findViewById(R.id.orderMoreButton);
        }
    }
} 