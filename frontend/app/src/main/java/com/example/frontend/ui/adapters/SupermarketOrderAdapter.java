package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.SupermarketOrder;

import java.util.List;

public class SupermarketOrderAdapter extends RecyclerView.Adapter<SupermarketOrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onClick(SupermarketOrder order);
    }

    public interface OnOrderActionListener {
        void onCancelOrder(SupermarketOrder order);
    }

    private final List<SupermarketOrder> orderList;
    private final OnOrderClickListener listener;
    private final OnOrderActionListener actionListener;

    public SupermarketOrderAdapter(List<SupermarketOrder> orderList, OnOrderClickListener listener, OnOrderActionListener actionListener) {
        this.orderList = orderList;
        this.listener = listener;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farmer_order, parent, false);
        return new OrderViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        SupermarketOrder order = orderList.get(position);
        
        // Configurar la información del pedido
        holder.orderId.setText("Pedido #" + order.getTransactionId());
        holder.buyerId.setText("Cliente: " + order.getClientOrSupplier());
        holder.orderProducts.setText("Productos: " + formatProductsList(order.getProducts()));
        holder.date.setText("Fecha: " + order.getDeliveryDate());
        holder.total.setText("Total: " + order.getTotal());
        holder.status.setText(getStatusDisplayName(order.getStatus()));
        
        // Aplicar color de fondo según el estado
        holder.status.setBackgroundResource(getStatusBackground(order.getStatus()));
        holder.status.setTextColor(android.graphics.Color.WHITE);

        holder.detailsBtn.setOnClickListener(v -> listener.onClick(order));

        // Configurar botones de acción según el estado
        setupActionButtons(holder, order);
    }

    private void setupActionButtons(OrderViewHolder holder, SupermarketOrder order) {
        String status = order.getStatus().toLowerCase();
        
        // Solo mostrar botón de cancelar para pedidos en curso
        if (status.contains("in_progress")) {
            holder.cancelBtn.setVisibility(View.VISIBLE);
            holder.cancelBtn.setOnClickListener(v -> actionListener.onCancelOrder(order));
        } else {
            holder.cancelBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateOrders(List<SupermarketOrder> newOrders) {
        this.orderList.clear();
        this.orderList.addAll(newOrders);
        notifyDataSetChanged();
    }

    private String getStatusDisplayName(String status) {
        if (status == null) return "Desconocido";
        
        String lowerStatus = status.toLowerCase();
        if (lowerStatus.contains("in_progress")) {
            return "En Curso";
        } else if (lowerStatus.contains("delivered")) {
            return "Entregado";
        } else if (lowerStatus.contains("cancelled")) {
            return "Cancelado";
        } else {
            return status;
        }
    }
    
    private int getStatusBackground(String status) {
        if (status == null) return R.drawable.status_in_progress_background;
        
        String lowerStatus = status.toLowerCase();
        if (lowerStatus.contains("in_progress")) {
            return R.drawable.status_in_progress_background;
        } else if (lowerStatus.contains("delivered")) {
            return R.drawable.status_delivered_background;
        } else if (lowerStatus.contains("cancelled")) {
            return R.drawable.status_cancelled_background;
        } else {
            return R.drawable.status_in_progress_background;
        }
    }
    
    private String formatProductsList(List<String> products) {
        if (products == null || products.isEmpty()) {
            return "Sin productos";
        }
        
        if (products.size() == 1) {
            return products.get(0);
        } else if (products.size() <= 3) {
            return String.join(", ", products);
        } else {
            return products.get(0) + " y " + (products.size() - 1) + " más";
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, buyerId, orderProducts, date, total, status;
        Button detailsBtn;
        Button cancelBtn;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            buyerId = itemView.findViewById(R.id.buyer_id);
            orderProducts = itemView.findViewById(R.id.order_products);
            date = itemView.findViewById(R.id.order_date);
            total = itemView.findViewById(R.id.order_total);
            status = itemView.findViewById(R.id.order_status);
            detailsBtn = itemView.findViewById(R.id.order_details_button);
            cancelBtn = itemView.findViewById(R.id.btn_cancel_order);
        }
    }
}