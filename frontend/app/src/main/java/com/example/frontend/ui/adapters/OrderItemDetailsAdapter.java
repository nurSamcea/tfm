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

public class OrderItemDetailsAdapter extends RecyclerView.Adapter<OrderItemDetailsAdapter.OrderItemViewHolder> {
    
    private List<OrderItem> orderItems;
    
    public OrderItemDetailsAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail, parent, false);
        return new OrderItemViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.bind(item);
    }
    
    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }
    
    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private TextView productNameTextView;
        private TextView quantityTextView;
        private TextView unitPriceTextView;
        private TextView totalPriceTextView;
        
        OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.text_product_name);
            quantityTextView = itemView.findViewById(R.id.text_quantity);
            unitPriceTextView = itemView.findViewById(R.id.text_unit_price);
            totalPriceTextView = itemView.findViewById(R.id.text_total_price);
        }
        
        void bind(OrderItem item) {
            productNameTextView.setText(item.product_name);
            quantityTextView.setText(String.format("%.1f", item.quantity));
            unitPriceTextView.setText(String.format("%.2f €", item.unit_price));
            totalPriceTextView.setText(String.format("%.2f €", item.total_price));
        }
    }
}
