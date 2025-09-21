package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;
import com.example.frontend.api.ApiClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FarmerStockAdapter extends RecyclerView.Adapter<FarmerStockAdapter.ViewHolder> {

    private List<Product> products;
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onEdit(Product product);
        void onToggleHidden(Product product);
        void onDelete(Product product);
    }

    public FarmerStockAdapter(List<Product> products) {
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
                .inflate(R.layout.item_farmer_stock, parent, false);
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
        private TextView productName, productStock, productPrice, productExpiration;
        private MaterialButton btnEdit, btnToggleHidden, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productStock = itemView.findViewById(R.id.product_stock);
            productPrice = itemView.findViewById(R.id.product_price);
            productExpiration = itemView.findViewById(R.id.product_expiration);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnToggleHidden = itemView.findViewById(R.id.btn_toggle_hidden);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            // Configurar listeners
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(products.get(getAdapterPosition()));
                }
            });

            btnToggleHidden.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onToggleHidden(products.get(getAdapterPosition()));
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(products.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            
            // Usar stock_available si está disponible, sino usar stock
            double stockValue = 0.0;
            if (product.getStockAvailable() != null) {
                stockValue = product.getStockAvailable();
            } else {
                stockValue = product.getStock();
            }
            productStock.setText("Stock: " + String.format("%.0f", stockValue));
            
            productPrice.setText(String.format("Precio: %.2f €", product.getPrice()));
            
            // Usar expiration_date si está disponible, sino usar harvestDate
            if (product.getExpirationDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                productExpiration.setText("Caducidad: " + sdf.format(product.getExpirationDate()));
            } else if (product.getHarvestDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                productExpiration.setText("Caducidad: " + sdf.format(product.getHarvestDate()));
            } else {
                productExpiration.setText("Caducidad: --");
            }

            // Mostrar imagen del producto si existe, sino mostrar placeholder
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                // Construir URL completa si es relativa
                String imageUrl = product.getImageUrl();
                if (imageUrl.startsWith("/")) {
                    // Si es una ruta relativa, construir URL completa usando la URL base del servidor
                    String baseUrl = ApiClient.getBaseUrl();
                    // Remover la barra final de la URL base si existe
                    if (baseUrl.endsWith("/")) {
                        baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                    }
                    imageUrl = baseUrl + imageUrl;
                }
                
                // Agregar timestamp para evitar cache
                imageUrl += "?t=" + System.currentTimeMillis();
                
                // Cargar imagen usando Glide
                Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // No usar cache para evitar problemas de actualización
                    .skipMemoryCache(true) // No usar cache en memoria
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.ic_product_placeholder);
            }

            // Mostrar nombre del producto sin emojis
            productName.setText(product.getName());
            
            // Cambiar color del texto según visibilidad
            if (product.isHidden()) {
                productName.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
            } else {
                productName.setTextColor(itemView.getContext().getColor(R.color.text_primary));
            }

            // Configurar botón de visibilidad de forma más intuitiva
            if (product.isHidden()) {
                // Producto OCULTO - No visible para usuarios
                btnToggleHidden.setIcon(itemView.getContext().getDrawable(R.drawable.ic_visibility_off));
                btnToggleHidden.setBackgroundColor(itemView.getContext().getColor(R.color.error));
                btnToggleHidden.setContentDescription("Producto oculto - Click para hacer visible");
            } else {
                // Producto VISIBLE - Visible para usuarios
                btnToggleHidden.setIcon(itemView.getContext().getDrawable(R.drawable.ic_visibility));
                btnToggleHidden.setBackgroundColor(itemView.getContext().getColor(R.color.success));
                btnToggleHidden.setContentDescription("Producto visible - Click para ocultar");
            }
        }
    }
}
