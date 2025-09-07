package com.example.frontend.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.models.Transaction;
import com.example.frontend.ui.adapters.OrderItemDetailsAdapter;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderDetailsDialog extends DialogFragment {
    
    private static final String ARG_TRANSACTION = "transaction";
    private static final String ARG_USER_TYPE = "user_type";
    private static final String ARG_USER_ID = "user_id";
    
    private Transaction transaction;
    private String userType;
    private int userId;
    private OnOrderActionListener listener;
    
    public interface OnOrderActionListener {
        void onDeliverOrder(Transaction transaction);
        void onCancelOrder(Transaction transaction);
    }
    
    public static OrderDetailsDialog newInstance(Transaction transaction, String userType, int userId) {
        OrderDetailsDialog dialog = new OrderDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRANSACTION, transaction);
        args.putString(ARG_USER_TYPE, userType);
        args.putInt(ARG_USER_ID, userId);
        dialog.setArguments(args);
        return dialog;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transaction = (Transaction) getArguments().getSerializable(ARG_TRANSACTION);
            userType = getArguments().getString(ARG_USER_TYPE);
            userId = getArguments().getInt(ARG_USER_ID);
        }
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_order_details, container, false);
        
        setupViews(view);
        setupListeners(view);
        
        return view;
    }
    
    private void setupViews(View view) {
        // Configurar información básica
        TextView orderIdText = view.findViewById(R.id.text_order_id);
        TextView statusText = view.findViewById(R.id.text_order_status);
        TextView buyerNameText = view.findViewById(R.id.text_buyer_name);
        TextView sellerNameText = view.findViewById(R.id.text_seller_name);
        TextView totalPriceText = view.findViewById(R.id.text_total_price);
        TextView createdAtText = view.findViewById(R.id.text_created_at);
        
        orderIdText.setText("#" + transaction.getId());
        statusText.setText(getStatusDisplayName(transaction.getStatus()));
        statusText.setTextColor(getStatusColor(transaction.getStatus()));
        buyerNameText.setText(transaction.getBuyerName());
        sellerNameText.setText(transaction.getSellerName());
        totalPriceText.setText(String.format("%.2f %s", transaction.getTotalPrice(), transaction.getCurrency()));
        
        // Formatear fecha
        if (transaction.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            createdAtText.setText(sdf.format(transaction.getCreatedAt()));
        }
        
        // Configurar RecyclerView de productos
        RecyclerView recyclerView = view.findViewById(R.id.recycler_order_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OrderItemDetailsAdapter adapter = new OrderItemDetailsAdapter(transaction.getOrderDetails());
        recyclerView.setAdapter(adapter);
        
        // Configurar botones según el estado y tipo de usuario
        setupActionButtons(view);
    }
    
    private void setupActionButtons(View view) {
        MaterialButton deliverButton = view.findViewById(R.id.btn_deliver_order);
        MaterialButton cancelButton = view.findViewById(R.id.btn_cancel_order);
        
        // Determinar qué botones mostrar según el estado y tipo de usuario
        boolean isSeller = isCurrentUserSeller();
        boolean isBuyer = isCurrentUserBuyer();
        String status = transaction.getStatus();
        
        switch (status) {
            case "in_progress":
                // En curso - vendedor puede entregar, comprador puede cancelar
                if (isSeller) {
                    deliverButton.setText("Marcar como Entregado");
                    deliverButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.GONE);
                } else if (isBuyer) {
                    deliverButton.setVisibility(View.GONE);
                    cancelButton.setText("Cancelar Pedido");
                    cancelButton.setVisibility(View.VISIBLE);
                } else {
                    deliverButton.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);
                }
                break;
                
            case "delivered":
            case "cancelled":
            case "completed":
                // Estados finales - no se pueden cambiar
                deliverButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                break;
        }
    }
    
    private void setupListeners(View view) {
        // Botón cerrar
        view.findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());
        
        // Botón entregar
        MaterialButton deliverButton = view.findViewById(R.id.btn_deliver_order);
        deliverButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeliverOrder(transaction);
            }
            dismiss();
        });
        
        // Botón cancelar
        MaterialButton cancelButton = view.findViewById(R.id.btn_cancel_order);
        cancelButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelOrder(transaction);
            }
            dismiss();
        });
    }
    
    private boolean isCurrentUserSeller() {
        return userType.equals(transaction.getSellerType()) && userId == transaction.getSellerId();
    }
    
    private boolean isCurrentUserBuyer() {
        return userType.equals(transaction.getBuyerType()) && userId == transaction.getBuyerId();
    }
    
    private String getStatusDisplayName(String status) {
        switch (status) {
            case "in_progress": return "En Curso";
            case "delivered": return "Entregado";
            case "cancelled": return "Cancelado";
            case "completed": return "Completado";
            default: return status;
        }
    }
    
    private int getStatusColor(String status) {
        switch (status) {
            case "in_progress": return getResources().getColor(R.color.status_in_progress);
            case "delivered": return getResources().getColor(R.color.status_delivered);
            case "cancelled": return getResources().getColor(R.color.status_cancelled);
            case "completed": return getResources().getColor(R.color.status_completed);
            default: return getResources().getColor(R.color.gray_600);
        }
    }
    
    public void setOnOrderActionListener(OnOrderActionListener listener) {
        this.listener = listener;
    }
}
