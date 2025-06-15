package com.example.iotapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.iotapp.models.Product;
import java.util.List;

public class SupermarketInventoryAdapter extends RecyclerView.Adapter<SupermarketInventoryAdapter.InventoryViewHolder> {
    private List<Product> inventoryList;
    private OnInventoryItemClickListener listener;

    public interface OnInventoryItemClickListener {
        void onUpdateStock(Product product, int newStock);
        void onUpdatePrice(Product product, double newPrice);
        void onViewMetrics(Product product);
        void onOrderMore(Product product);
    }

    public SupermarketInventoryAdapter(List<Product> inventoryList) {
        this.inventoryList = inventoryList;
    }

    public void setOnInventoryItemClickListener(OnInventoryItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supermarket_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Product product = inventoryList.get(position);
        
        holder.nameTextView.setText(product.getName());
        holder.descriptionTextView.setText(product.getDescription());
        holder.stockTextView.setText(String.format("Stock: %d", product.getStock()));
        holder.priceTextView.setText(String.format("Precio: €%.2f", product.getPrice()));
        
        if (product.getFarmerId() != null) {
            holder.farmerTextView.setText("Proveedor: " + product.getFarmerId());
        }
        
        if (product.getHarvestDate() != null) {
            holder.harvestDateTextView.setText(String.format("Fecha cosecha: %s", 
                product.getHarvestDate().toString()));
        }

        // Configurar botones
        holder.updateStockButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdateStock(product, product.getStock());
            }
        });

        holder.updatePriceButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdatePrice(product, product.getPrice());
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
        return inventoryList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        TextView stockTextView;
        TextView priceTextView;
        TextView farmerTextView;
        TextView harvestDateTextView;
        Button updateStockButton;
        Button updatePriceButton;
        Button metricsButton;
        Button orderMoreButton;

        InventoryViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewProductName);
            descriptionTextView = itemView.findViewById(R.id.productDescription);
            stockTextView = itemView.findViewById(R.id.textViewStock);
            priceTextView = itemView.findViewById(R.id.textViewPrice);
            farmerTextView = itemView.findViewById(R.id.farmerName);
            harvestDateTextView = itemView.findViewById(R.id.harvestDate);
            updateStockButton = itemView.findViewById(R.id.updateStockButton);
            updatePriceButton = itemView.findViewById(R.id.updatePriceButton);
            metricsButton = itemView.findViewById(R.id.metricsButton);
            orderMoreButton = itemView.findViewById(R.id.orderMoreButton);
        }
    }
} 