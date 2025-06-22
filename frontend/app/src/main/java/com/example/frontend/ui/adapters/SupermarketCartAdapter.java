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

import java.util.List;

public class SupermarketCartAdapter extends RecyclerView.Adapter<SupermarketCartAdapter.ViewHolder> {

    public interface OnRemoveClickListener {
        void onRemove(Product product);
    }

    private final List<Product> products;
    private final OnRemoveClickListener listener;

    public SupermarketCartAdapter(List<Product> products, OnRemoveClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupermarketCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supermarket_cart_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupermarketCartAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.name.setText(product.getName() + " · " + product.getFarmerId());
        holder.price.setText(String.format("%.2f €", product.getPrice()));
        holder.removeBtn.setOnClickListener(v -> listener.onRemove(product));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        Button removeBtn;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartProductName);
            price = itemView.findViewById(R.id.cartProductPrice);
            removeBtn = itemView.findViewById(R.id.btnRemoveFromCart);
        }
    }
}
