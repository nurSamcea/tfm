package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.SupermarketOrder;

import java.util.List;

public class SupermarketOrderAdapter extends RecyclerView.Adapter<SupermarketOrderAdapter.OrderViewHolder> {

    public interface OnOrderActionListener {
        void onChangeStatus(SupermarketOrder order);
        void onViewDetail(SupermarketOrder order);
    }

    private final List<SupermarketOrder> orders;
    private final OnOrderActionListener listener;

    public SupermarketOrderAdapter(List<SupermarketOrder> orders, OnOrderActionListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supermarket_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        SupermarketOrder order = orders.get(position);
        holder.txtCustomerName.setText(order.getSupplier());
        holder.txtOrderId.setText("(#" + order.getId() + ")");
        String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").format(order.getOrderDate());
        holder.txtDateTotal.setText(fecha + " · Total: " + String.format("%.2f €", order.getTotal()));
        holder.txtAddress.setText(order.getAddress() != null ? order.getAddress() : "-");
        holder.txtStatus.setText(order.getStatus());
        // Color e icono de estado
        int color;
        int icon;
        switch (order.getStatus()) {
            case "Preparando pedido":
                color = holder.itemView.getResources().getColor(R.color.orange);
                icon = R.drawable.ic_status_preparing;
                break;
            case "En camino":
                color = holder.itemView.getResources().getColor(R.color.teal_700);
                icon = R.drawable.ic_status_shipping;
                break;
            case "Entregado":
                color = holder.itemView.getResources().getColor(R.color.green);
                icon = R.drawable.ic_status_delivered;
                break;
            default:
                color = holder.itemView.getResources().getColor(R.color.darker_gray);
                icon = R.drawable.ic_status;
        }
        holder.txtStatus.setTextColor(color);
        holder.imgStatus.setImageResource(icon);
        // Botones
        holder.btnViewDetail.setOnClickListener(v -> listener.onViewDetail(order));
        if ("Entregado".equals(order.getStatus())) {
            holder.btnChangeStatus.setVisibility(View.GONE);
        } else {
            holder.btnChangeStatus.setVisibility(View.VISIBLE);
            holder.btnChangeStatus.setOnClickListener(v -> listener.onChangeStatus(order));
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtCustomerName, txtOrderId, txtDateTotal, txtAddress, txtStatus;
        Button btnChangeStatus, btnViewDetail;
        ImageView imgStatus;
        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCustomerName = itemView.findViewById(R.id.txt_customer_name);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtDateTotal = itemView.findViewById(R.id.txt_date_total);
            txtAddress = itemView.findViewById(R.id.txt_address);
            txtStatus = itemView.findViewById(R.id.txt_status);
            btnChangeStatus = itemView.findViewById(R.id.btn_change_status);
            btnViewDetail = itemView.findViewById(R.id.btn_view_detail);
            imgStatus = itemView.findViewById(R.id.img_status);
        }
    }
}
