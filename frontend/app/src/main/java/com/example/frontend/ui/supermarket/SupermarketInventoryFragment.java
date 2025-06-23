package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.model.SupermarketOrder;
import com.example.frontend.ui.adapters.SupermarketInventoryAdapter;
import com.example.frontend.ui.adapters.SupermarketOrderAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SupermarketInventoryFragment extends Fragment {
    private static final String TAG = "SupermarketInventory";
    private RecyclerView recyclerRecentOrders;
    private RecyclerView recyclerOrderHistory;
    private SupermarketOrderAdapter recentOrdersAdapter;
    private SupermarketOrderAdapter orderHistoryAdapter;
    private List<SupermarketOrder> recentOrders = new ArrayList<>();
    private List<SupermarketOrder> orderHistory = new ArrayList<>();
    private TextView textKpis, textFilters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Log.d(TAG, "Iniciando onCreateView");

            View view = inflater.inflate(R.layout.fragment_supermarket_inventory, container, false);
            recyclerRecentOrders = view.findViewById(R.id.recycler_recent_orders);
            recyclerOrderHistory = view.findViewById(R.id.recycler_order_history);
            textKpis = view.findViewById(R.id.text_kpis);
            textFilters = view.findViewById(R.id.text_filters);
            recyclerRecentOrders.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));

            mockRecentOrders();
            mockOrderHistory();
            SupermarketOrderAdapter.OnOrderActionListener orderActionListener = new SupermarketOrderAdapter.OnOrderActionListener() {
                @Override
                public void onChangeStatus(SupermarketOrder order) {
                    // Simula cambio de estado
                    if ("Preparando pedido".equals(order.getStatus())) {
                        order.setStatus("En camino");
                    } else if ("En camino".equals(order.getStatus())) {
                        order.setStatus("Entregado");
                    }
                    recentOrdersAdapter.notifyDataSetChanged();
                    orderHistoryAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Estado cambiado a: " + order.getStatus(), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onViewDetail(SupermarketOrder order) {
                    Toast.makeText(getContext(), "Ver detalle de " + order.getId(), Toast.LENGTH_SHORT).show();
                }
            };
            recentOrdersAdapter = new SupermarketOrderAdapter(recentOrders, orderActionListener);
            orderHistoryAdapter = new SupermarketOrderAdapter(orderHistory, orderActionListener);
            recyclerRecentOrders.setAdapter(recentOrdersAdapter);
            recyclerOrderHistory.setAdapter(orderHistoryAdapter);
            textKpis.setText("📊 KPIs:\n- Pedidos entregados esta semana: 15\n- Pendientes de envío: 3\n- Tiempo promedio entrega: 1.2 días");
            textFilters.setText("📝 Filtros: [✓ Estado: En camino] [✓ Últimos 7 días]");

            Log.d(TAG, "Vista de inventario creada correctamente");

            return view;

        } catch (Exception e) {
            Log.e(TAG, "Error en SupermarketInventoryFragment: " + e.getMessage(), e);
            return null;
        }
    }

    private void mockRecentOrders() {
        recentOrders.clear();
        recentOrders.add(new SupermarketOrder("C-001", "Ana López", new java.util.Date(), 42.30, "Preparando pedido", null, "Calle Mayor 12, Madrid"));
        recentOrders.add(new SupermarketOrder("C-002", "Luis Pérez", new java.util.Date(), 67.80, "Entregado", null, "Calle Gran Vía 7, Madrid"));
    }

    private void mockOrderHistory() {
        orderHistory.clear();
        orderHistory.add(new SupermarketOrder("C-003", "Marta Ruiz", new java.util.Date(), 35.10, "En camino", null, "Calle Toledo 5, Madrid"));
        orderHistory.add(new SupermarketOrder("C-004", "Carlos Gómez", new java.util.Date(), 51.20, "Entregado", null, "Calle Alcalá 22, Madrid"));
    }
}
