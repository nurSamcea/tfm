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
import com.example.frontend.model.FarmerOrder;

import java.util.List;

public class FarmerOrderAdapter extends RecyclerView.Adapter<FarmerOrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onClick(FarmerOrder order);
    }

    public interface OnOrderActionListener {
        void onAcceptOrder(FarmerOrder order);
        void onDeliverOrder(FarmerOrder order);
        void onCancelOrder(FarmerOrder order);
    }

    private final List<FarmerOrder> orderList;
    private final OnOrderClickListener listener;
    private final OnOrderActionListener actionListener;

    public FarmerOrderAdapter(List<FarmerOrder> orderList, OnOrderClickListener listener, OnOrderActionListener actionListener) {
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
        FarmerOrder order = orderList.get(position);
        holder.title.setText(order.getClientOrMarket());
        holder.products.setText("Productos: " + String.join(", ", order.getProducts()));
        holder.date.setText("Entrega: " + order.getDeliveryDate());
        holder.total.setText("Total: " + order.getTotal());
        holder.status.setText("Estado: " + order.getStatus());

        holder.detailsBtn.setOnClickListener(v -> listener.onClick(order));

        // Configurar botones de acción según el estado
        setupActionButtons(holder, order);
    }

    private void setupActionButtons(OrderViewHolder holder, FarmerOrder order) {
        String status = order.getStatus().toLowerCase();
        
        // Ocultar todos los botones por defecto
        holder.actionButtonsLayout.setVisibility(View.GONE);
        holder.acceptBtn.setVisibility(View.GONE);
        holder.deliverBtn.setVisibility(View.GONE);
        holder.cancelBtn.setVisibility(View.GONE);

        // Mostrar botones según el estado
        if (status.contains("pending")) {
            holder.actionButtonsLayout.setVisibility(View.VISIBLE);
            holder.acceptBtn.setVisibility(View.VISIBLE);
            holder.cancelBtn.setVisibility(View.VISIBLE);
            
            holder.acceptBtn.setOnClickListener(v -> actionListener.onAcceptOrder(order));
            holder.cancelBtn.setOnClickListener(v -> actionListener.onCancelOrder(order));
        } else if (status.contains("in_progress")) {
            holder.actionButtonsLayout.setVisibility(View.VISIBLE);
            holder.deliverBtn.setVisibility(View.VISIBLE);
            holder.cancelBtn.setVisibility(View.VISIBLE);
            
            holder.deliverBtn.setOnClickListener(v -> actionListener.onDeliverOrder(order));
            holder.cancelBtn.setOnClickListener(v -> actionListener.onCancelOrder(order));
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView title, products, date, total, status;
        Button detailsBtn;
        LinearLayout actionButtonsLayout;
        Button acceptBtn, deliverBtn, cancelBtn;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.order_title);
            products = itemView.findViewById(R.id.order_products);
            date = itemView.findViewById(R.id.order_date);
            total = itemView.findViewById(R.id.order_total);
            status = itemView.findViewById(R.id.order_status);
            detailsBtn = itemView.findViewById(R.id.order_details_button);
            
            actionButtonsLayout = itemView.findViewById(R.id.action_buttons_layout);
            acceptBtn = itemView.findViewById(R.id.btn_accept_order);
            deliverBtn = itemView.findViewById(R.id.btn_deliver_order);
            cancelBtn = itemView.findViewById(R.id.btn_cancel_order);
        }
    }
}
