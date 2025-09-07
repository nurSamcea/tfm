package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.OrderItem;

import java.util.List;

public class ReceiptProductAdapter extends RecyclerView.Adapter<ReceiptProductAdapter.ProductViewHolder> {
    
    private List<OrderItem> orderItems;
    
    public ReceiptProductAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receipt_product, parent, false);
        return new ProductViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.bind(item);
    }
    
    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }
    
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName;
        TextView txtProductQuantity;
        TextView txtProductTotal;
        
        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtProductQuantity = itemView.findViewById(R.id.txt_product_quantity);
            txtProductTotal = itemView.findViewById(R.id.txt_product_total);
        }
        
        void bind(OrderItem item) {
            txtProductName.setText(item.getProduct_name());
            txtProductQuantity.setText(String.format("%.1f kg x %.2f €", 
                item.getQuantity(), item.getUnit_price()));
            txtProductTotal.setText(String.format("%.2f €", item.getTotal_price()));
        }
    }
}
