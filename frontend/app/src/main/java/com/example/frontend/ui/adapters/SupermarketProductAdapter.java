package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.example.frontend.network.ApiClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SupermarketProductAdapter extends RecyclerView.Adapter<SupermarketProductAdapter.ViewHolder> {

    private List<Product> products;
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onAddToCart(Product product);
        void onViewDetails(Product product);
        void onContactFarmer(Product product);
    }

    public SupermarketProductAdapter(List<Product> products) {
        this.products = products;
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supermarket_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName, productPrice, productStock, productDistance, productFarmer;
        private Chip chipEco, chipSustainable;
        private MaterialButton btnAddToCart, btnViewDetails, btnContactFarmer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productStock = itemView.findViewById(R.id.product_stock);
            productDistance = itemView.findViewById(R.id.product_distance);
            productFarmer = itemView.findViewById(R.id.product_farmer);
            chipEco = itemView.findViewById(R.id.chip_eco);
            chipSustainable = itemView.findViewById(R.id.chip_sustainable);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
            btnContactFarmer = itemView.findViewById(R.id.btn_contact_farmer);

            // Configurar listeners
            btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCart(products.get(getAdapterPosition()));
                }
            });

            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetails(products.get(getAdapterPosition()));
                }
            });

            btnContactFarmer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContactFarmer(products.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            
            // Precio
            productPrice.setText(String.format("Precio: %.2f €", product.getPrice()));
            
            // Stock disponible
            double stockValue = 0.0;
            if (product.getStockAvailable() != null) {
                stockValue = product.getStockAvailable();
            } else {
                stockValue = product.getStock();
            }
            productStock.setText("Stock: " + String.format("%.0f", stockValue));
            
            // Distancia
            if (product.getDistance_km() != null) {
                productDistance.setText(String.format("Distancia: %.1f km", product.getDistance_km()));
            } else {
                productDistance.setText("Distancia: -- km");
            }
            
            // Información del agricultor
            if (product.getProviderId() != null) {
                productFarmer.setText("Agricultor ID: " + product.getProviderId());
            } else if (product.getFarmerId() != null) {
                productFarmer.setText("Agricultor ID: " + product.getFarmerId());
            } else {
                productFarmer.setText("Agricultor: --");
            }

            // Mostrar imagen del producto si existe
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                String imageUrl = product.getImageUrl();
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

            // Configurar chips de sostenibilidad
            if (product.getIsEco() != null && product.getIsEco()) {
                chipEco.setVisibility(View.VISIBLE);
                chipEco.setText("Ecológico");
            } else {
                chipEco.setVisibility(View.GONE);
            }

            if (product.isSustainable() || (product.getScore() != null && product.getScore() > 70)) {
                chipSustainable.setVisibility(View.VISIBLE);
                chipSustainable.setText("Sostenible");
            } else {
                chipSustainable.setVisibility(View.GONE);
            }

            // Mostrar score de sostenibilidad si está disponible
            if (product.getScore() != null) {
                chipSustainable.setText("Sostenible (" + String.format("%.0f", product.getScore()) + "%)");
            }
        }
    }
}
