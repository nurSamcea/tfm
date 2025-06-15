package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.databinding.ItemProductBinding;
import com.example.frontend.model.Product;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {
    private final OnProductClickListener listener;

    public ProductAdapter(OnProductClickListener listener) {
        super(new ProductDiffCallback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductBinding binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        ProductViewHolder(ItemProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Product product) {
            binding.productName.setText(product.getName());
            binding.productPrice.setText(String.format("$%.2f", product.getPrice()));
            binding.productStock.setText(String.format("Stock: %d", product.getStock()));
            
            if (product.isSustainable()) {
                binding.imageViewSustainable.setVisibility(ViewGroup.VISIBLE);
            } else {
                binding.imageViewSustainable.setVisibility(ViewGroup.GONE);
            }

            itemView.setOnClickListener(v -> listener.onProductClick(product));
        }
    }

    private static class ProductDiffCallback extends DiffUtil.ItemCallback<Product> {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.equals(newItem);
        }
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
} 