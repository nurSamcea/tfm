package com.example.frontend.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.OrderItem;
import com.example.frontend.models.Transaction;
import com.example.frontend.ui.adapters.ReceiptProductAdapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderReceiptDialog extends Dialog {
    
    private Transaction transaction;
    private Context context;
    
    public OrderReceiptDialog(@NonNull Context context, Transaction transaction) {
        super(context);
        this.context = context;
        this.transaction = transaction;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_order_receipt);
        
        initializeViews();
        populateData();
    }
    
    private void initializeViews() {
        // Configurar RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_order_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setNestedScrollingEnabled(false);
        
        // Configurar botón cerrar
        Button btnClose = findViewById(R.id.btn_close_dialog);
        btnClose.setOnClickListener(v -> dismiss());
    }
    
    private void populateData() {
        // Información del pedido
        TextView txtOrderId = findViewById(R.id.txt_order_id);
        TextView txtSellerName = findViewById(R.id.txt_seller_name);
        TextView txtOrderDate = findViewById(R.id.txt_order_date);
        TextView txtOrderStatus = findViewById(R.id.txt_order_status);
        TextView txtTotalAmount = findViewById(R.id.txt_total_amount);
        TextView txtPaymentMethod = findViewById(R.id.txt_payment_method);
        TextView txtDeliveryAddress = findViewById(R.id.txt_delivery_address);
        
        // Configurar datos básicos
        txtOrderId.setText("Pedido #" + transaction.getId());
        txtSellerName.setText("Vendedor: " + (transaction.getSellerName() != null ? 
            transaction.getSellerName() : "Vendedor " + transaction.getSellerId()));
        
        if (transaction.getCreatedAt() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            txtOrderDate.setText("Fecha: " + dateFormat.format(transaction.getCreatedAt()));
        } else {
            txtOrderDate.setText("Fecha: N/A");
        }
        
        txtOrderStatus.setText("Estado: " + getStatusDisplayName(transaction.getStatus()));
        txtTotalAmount.setText(String.format("%.2f €", transaction.getTotalPrice()));
        
        if (transaction.getPaymentMethod() != null) {
            txtPaymentMethod.setText("Método de pago: " + transaction.getPaymentMethod());
        } else {
            txtPaymentMethod.setText("Método de pago: No especificado");
        }
        
        if (transaction.getDeliveryAddress() != null) {
            txtDeliveryAddress.setText("Dirección: " + transaction.getDeliveryAddress());
        } else {
            txtDeliveryAddress.setText("Dirección: No especificada");
        }
        
        // Configurar productos
        RecyclerView recyclerView = findViewById(R.id.recycler_order_items);
        List<OrderItem> orderItems = transaction.getOrderDetails();
        
        if (orderItems != null && !orderItems.isEmpty()) {
            ReceiptProductAdapter adapter = new ReceiptProductAdapter(orderItems);
            recyclerView.setAdapter(adapter);
        } else {
            // Si no hay orderDetails, crear un item genérico
            OrderItem genericItem = new OrderItem();
            genericItem.product_name = "Productos del pedido";
            genericItem.quantity = 1;
            genericItem.unit_price = transaction.getTotalPrice();
            genericItem.total_price = transaction.getTotalPrice();
            
            ReceiptProductAdapter adapter = new ReceiptProductAdapter(List.of(genericItem));
            recyclerView.setAdapter(adapter);
        }
    }
    
    private String getStatusDisplayName(String status) {
        if (status == null) return "Desconocido";
        
        switch (status.toLowerCase()) {
            case "in_progress":
                return "En curso";
            case "delivered":
                return "Entregado";
            case "cancelled":
                return "Cancelado";
            default:
                return status;
        }
    }
}
