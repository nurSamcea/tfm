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

public class FarmerProductAdapter extends RecyclerView.Adapter<FarmerProductAdapter.FarmerProductViewHolder> {
    private List<Product> productsList;
    private OnFarmerProductClickListener listener;

    public interface OnFarmerProductClickListener {
        void onEditProduct(Product product);
        void onUpdateStock(Product product, int newStock);
        void onViewMetrics(Product product);
    }

    public FarmerProductAdapter(List<Product> productsList) {
        this.productsList = productsList;
    }

    public void setOnFarmerProductClickListener(OnFarmerProductClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FarmerProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_farmer_product, parent, false);
        return new FarmerProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmerProductViewHolder holder, int position) {
        Product product = productsList.get(position);
        
        holder.nameTextView.setText(product.getName());
        holder.descriptionTextView.setText(product.getDescription());
        holder.priceTextView.setText(String.format("€%.2f", product.getPrice()));
        holder.stockTextView.setText(String.format("Stock: %d", product.getStock()));
        
        if (product.getHarvestDate() != null) {
            holder.harvestDateTextView.setText(String.format("Cosecha: %s", 
                product.getHarvestDate().toString()));
        }
        
        if (product.isOrganic()) {
            holder.organicBadge.setVisibility(View.VISIBLE);
        } else {
            holder.organicBadge.setVisibility(View.GONE);
        }

        // Configurar botones
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditProduct(product);
            }
        });

        holder.metricsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewMetrics(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    static class FarmerProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        TextView stockTextView;
        TextView harvestDateTextView;
        View organicBadge;
        Button editButton;
        Button metricsButton;

        FarmerProductViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewProductName);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            priceTextView = itemView.findViewById(R.id.textViewPrice);
            stockTextView = itemView.findViewById(R.id.textViewStock);
            harvestDateTextView = itemView.findViewById(R.id.harvestDate);
            organicBadge = itemView.findViewById(R.id.organicBadge);
            editButton = itemView.findViewById(R.id.buttonEdit);
            metricsButton = itemView.findViewById(R.id.metricsButton);
        }
    }
} 