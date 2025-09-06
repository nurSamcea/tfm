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
        
        // Configurar el título según el tipo de orden
        String titleText;
        if ("TO_SUPPLIER".equals(order.getOrderType())) {
            titleText = "🏪 " + order.getClientOrSupplier();
        } else {
            titleText = "👤 " + order.getClientOrSupplier();
        }
        holder.title.setText(titleText);
        
        holder.products.setText("Productos: " + String.join(", ", order.getProducts()));
        holder.date.setText("Fecha: " + order.getDeliveryDate());
        holder.total.setText("Total: " + order.getTotal());
        holder.status.setText("Estado: " + order.getStatus());

        holder.detailsBtn.setOnClickListener(v -> listener.onClick(order));

        // Configurar botones de acción según el estado
        setupActionButtons(holder, order);
    }

    private void setupActionButtons(OrderViewHolder holder, SupermarketOrder order) {
        String status = order.getStatus().toLowerCase();
        
        // Ocultar todos los botones por defecto
        holder.actionButtonsLayout.setVisibility(View.GONE);
        holder.cancelBtn.setVisibility(View.GONE);

        // Solo mostrar botón de cancelar para pedidos pendientes o en curso
        if (status.contains("pending") || status.contains("in_progress")) {
            holder.actionButtonsLayout.setVisibility(View.VISIBLE);
            holder.cancelBtn.setVisibility(View.VISIBLE);
            
            holder.cancelBtn.setOnClickListener(v -> actionListener.onCancelOrder(order));
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

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView title, products, date, total, status;
        Button detailsBtn;
        LinearLayout actionButtonsLayout;
        Button cancelBtn;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.order_title);
            products = itemView.findViewById(R.id.order_products);
            date = itemView.findViewById(R.id.order_date);
            total = itemView.findViewById(R.id.order_total);
            status = itemView.findViewById(R.id.order_status);
            detailsBtn = itemView.findViewById(R.id.order_details_button);
            
            actionButtonsLayout = itemView.findViewById(R.id.action_buttons_layout);
            cancelBtn = itemView.findViewById(R.id.btn_cancel_order);
        }
    }
}