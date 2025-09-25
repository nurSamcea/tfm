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
import com.example.frontend.api.ApiClient;

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
    }

    public SupermarketProductAdapter(List<Product> products) {
        this.products = products;
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        
        // Log para debuggear el problema de imágenes
        android.util.Log.d("SupermarketProductAdapter", "Actualizando productos. Total: " + newProducts.size());
        for (Product product : newProducts) {
            android.util.Log.d("SupermarketProductAdapter", "Producto: " + product.getName() + 
                ", ImageUrl: " + (product.getImageUrl() != null ? product.getImageUrl() : "null"));
        }
        
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
        private TextView productName, productPrice, productStock;
        private Chip chipSustainable;
        private MaterialButton btnAddToCart, btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productStock = itemView.findViewById(R.id.product_stock);
            chipSustainable = itemView.findViewById(R.id.chip_sustainable);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);

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

            // Mostrar imagen del producto si existe
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty() && !product.getImageUrl().equals("null")) {
                String imageUrl = product.getImageUrl();
                if (imageUrl.startsWith("/")) {
                    String baseUrl = ApiClient.getBaseUrl();
                    if (baseUrl.endsWith("/")) {
                        baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                    }
                    imageUrl = baseUrl + imageUrl;
                }
                
                imageUrl += "?t=" + System.currentTimeMillis();
                
                // Log para debuggear el problema de imágenes
                android.util.Log.d("SupermarketProductAdapter", "Cargando imagen para producto: " + product.getName() + 
                    ", URL: " + imageUrl);
                
                // Limpiar la imagen anterior antes de cargar la nueva
                Glide.with(itemView.getContext()).clear(productImage);
                
                Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .into(productImage);
            } else {
                // Log cuando no hay URL de imagen
                android.util.Log.d("SupermarketProductAdapter", "No hay URL de imagen para producto: " + product.getName() + 
                    ", ImageUrl: " + product.getImageUrl());
                // Limpiar la imagen anterior y mostrar placeholder
                Glide.with(itemView.getContext()).clear(productImage);
                productImage.setImageResource(R.drawable.ic_product_placeholder);
            }

            // Configurar chip de sostenibilidad
            if (product.isSustainable() || (product.getScore() != null && product.getScore() > 70)) {
                chipSustainable.setVisibility(View.VISIBLE);
                if (product.getScore() != null) {
                    chipSustainable.setText("Sostenible (" + String.format("%.0f", product.getScore()) + "%)");
                } else {
                    chipSustainable.setText("Sostenible");
                }
            } else {
                chipSustainable.setVisibility(View.GONE);
            }
        }
    }
}
