package com.example.frontend.ui.adapters;

import android.location.Location;
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
import com.example.frontend.utils.LocationUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductWithDistanceAdapter extends RecyclerView.Adapter<ProductWithDistanceAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onViewDetails(Product product);
        void onAddToCart(Product product);
    }

    private List<Product> productList = new ArrayList<>();
    private final OnProductClickListener listener;
    private Location userLocation;
    private double maxDistanceKm = 50.0; // Distancia máxima por defecto

    public ProductWithDistanceAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
        notifyDataSetChanged();
    }

    public void setMaxDistance(double maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
        notifyDataSetChanged();
    }

    public void submitList(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_with_distance, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, userLocation, maxDistanceKm, listener);
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
        TextView providerNameTextView;
        TextView distanceTextView;
        TextView distanceEmojiTextView;
        Button addToCartButton;
        Button viewDetailsButton;
        ImageView productImageView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.productName);
            priceTextView = itemView.findViewById(R.id.productPrice);
            stockTextView = itemView.findViewById(R.id.productStock);
            discountTextView = itemView.findViewById(R.id.productDiscount);
            providerNameTextView = itemView.findViewById(R.id.providerName);
            distanceTextView = itemView.findViewById(R.id.distanceText);
            distanceEmojiTextView = itemView.findViewById(R.id.distanceEmoji);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            viewDetailsButton = itemView.findViewById(R.id.viewDetailsButton);
            productImageView = itemView.findViewById(R.id.imageViewProduct);
        }

        public void bind(Product product, Location userLocation, double maxDistanceKm, OnProductClickListener listener) {
            // Información básica del producto
            nameTextView.setText(product.getName());
            priceTextView.setText(String.format("€%.2f", product.getPrice()));
            stockTextView.setText(String.format("Stock: %d", product.getStock()));
            
            // Información del proveedor
            if (product.getProviderName() != null) {
                providerNameTextView.setText(product.getProviderName());
            } else {
                providerNameTextView.setText("Proveedor");
            }
            
            // Configurar descuento (ejemplo: 20% OFF)
            discountTextView.setText("20% OFF");
            
            // Imagen de ejemplo
            productImageView.setImageResource(R.drawable.ic_products);
            
            // Calcular y mostrar distancia
            if (userLocation != null && product.getProviderLat() != null && product.getProviderLon() != null) {
                double distance = LocationUtils.calculateDistance(
                        userLocation.getLatitude(), userLocation.getLongitude(),
                        product.getProviderLat(), product.getProviderLon()
                );
                
                // Mostrar distancia formateada
                distanceTextView.setText(LocationUtils.formatDistance(distance));
                distanceTextView.setVisibility(View.VISIBLE);
                
                // Mostrar emoji basado en distancia
                distanceEmojiTextView.setText(LocationUtils.getDistanceEmoji(distance));
                distanceEmojiTextView.setVisibility(View.VISIBLE);
                
                // Cambiar color del texto de distancia según la distancia
                int colorRes;
                if (distance < 2) {
                    colorRes = R.color.success; // Verde para cerca
                } else if (distance < 10) {
                    colorRes = R.color.warning; // Naranja para moderado
                } else {
                    colorRes = R.color.error; // Rojo para lejos
                }
                distanceTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), colorRes));
                
                // Ocultar producto si está fuera del rango máximo
                if (distance > maxDistanceKm) {
                    itemView.setAlpha(0.5f);
                    itemView.setEnabled(false);
                } else {
                    itemView.setAlpha(1.0f);
                    itemView.setEnabled(true);
                }
            } else {
                // Sin ubicación del usuario o del proveedor
                distanceTextView.setVisibility(View.GONE);
                distanceEmojiTextView.setVisibility(View.GONE);
            }
            
            // Configurar click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
            
            addToCartButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCart(product);
                }
            });
            
            viewDetailsButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetails(product);
                }
            });
        }
    }
}

