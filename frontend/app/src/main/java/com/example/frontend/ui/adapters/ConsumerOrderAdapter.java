package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.ConsumerOrder;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ConsumerOrderAdapter extends RecyclerView.Adapter<ConsumerOrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onClick(ConsumerOrder order);
    }

    private final List<ConsumerOrder> orderList;
    private final OnOrderClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ConsumerOrderAdapter(List<ConsumerOrder> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consumer_order, parent, false);
        return new OrderViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        ConsumerOrder order = orderList.get(position);
        
        holder.orderId.setText("Pedido #" + order.getId());
        holder.sellerId.setText("Vendedor: " + order.getSellerId());
        holder.date.setText("Fecha: " + (order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "N/A"));
        holder.total.setText("Total: " + String.format("%.2f €", order.getTotalAmount()));
        holder.status.setText(getStatusDisplayName(order.getStatus()));
        holder.status.setTextColor(android.graphics.Color.WHITE);
        holder.status.setBackgroundResource(getStatusBackground(order.getStatus()));
        
        // Mostrar productos
        StringBuilder productsText = new StringBuilder("Productos: ");
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (int i = 0; i < order.getItems().size(); i++) {
                ConsumerOrder.OrderItem item = order.getItems().get(i);
                if (i > 0) productsText.append(", ");
                productsText.append(item.getProductName()).append(" (").append(item.getQuantity()).append("kg)");
            }
        } else {
            productsText.append("Sin productos");
        }
        holder.products.setText(productsText.toString());

        holder.detailsBtn.setOnClickListener(v -> listener.onClick(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String getStatusDisplayName(ConsumerOrder.OrderStatus status) {
        if (status == null) return "Desconocido";
        
        switch (status) {
            case PENDING:
                return "Pendiente";
            case CONFIRMED:
                return "En Curso";
            case IN_TRANSIT:
                return "En Curso";
            case DELIVERED:
                return "Entregado";
            case CANCELLED:
                return "Cancelado";
            default:
                return status.name();
        }
    }
    
    private int getStatusBackground(ConsumerOrder.OrderStatus status) {
        if (status == null) return R.drawable.status_pending_background;
        
        switch (status) {
            case PENDING:
                return R.drawable.status_pending_background;
            case CONFIRMED:
            case IN_TRANSIT:
                return R.drawable.status_in_progress_background;
            case DELIVERED:
                return R.drawable.status_delivered_background;
            case CANCELLED:
                return R.drawable.status_cancelled_background;
            default:
                return R.drawable.status_pending_background;
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, sellerId, products, date, total, status;
        Button detailsBtn;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            sellerId = itemView.findViewById(R.id.seller_id);
            products = itemView.findViewById(R.id.order_products);
            date = itemView.findViewById(R.id.order_date);
            total = itemView.findViewById(R.id.order_total);
            status = itemView.findViewById(R.id.order_status);
            detailsBtn = itemView.findViewById(R.id.order_details_button);
        }
    }
}
