package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        private ImageButton btnToggleHidden, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productStock = itemView.findViewById(R.id.product_stock);
            productPrice = itemView.findViewById(R.id.product_price);
            productExpiration = itemView.findViewById(R.id.product_expiration);
            btnToggleHidden = itemView.findViewById(R.id.btn_toggle_hidden);
            btnDelete = itemView.findViewById(R.id.btn_delete);

            // Configurar listeners
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
            productStock.setText("Stock: " + product.getStock());
            productPrice.setText(String.format("Precio: %.2f €", product.getPrice()));
            
            if (product.getHarvestDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
                productExpiration.setText("Caducidad: " + sdf.format(product.getHarvestDate()));
            } else {
                productExpiration.setText("Caducidad: --");
            }

            // Cambiar icono según si está oculto o visible
            if (product.isHidden()) {
                btnToggleHidden.setImageResource(R.drawable.ic_visibility_off);
            } else {
                btnToggleHidden.setImageResource(R.drawable.ic_visibility);
            }
        }
    }
}
