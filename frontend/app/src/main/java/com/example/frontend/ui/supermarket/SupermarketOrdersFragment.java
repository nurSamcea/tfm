package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.SupermarketOrder;
import com.example.frontend.ui.adapters.SupermarketOrderAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SupermarketOrdersFragment extends Fragment {
    private static final String TAG = "SupermarketOrdersFragment";

    private RecyclerView recyclerView;
    private SupermarketOrderAdapter adapter;
    private List<SupermarketOrder> orderList;
    private List<SupermarketOrder> allOrders;
    
    // Botones de filtro
    private Button filterAll, filterPending, filterOnWay, filterDone;
    private Button filterToFarmers, filterFromConsumers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando SupermarketOrdersFragment");
        View view = inflater.inflate(R.layout.fragment_farmer_orders, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupFilters();
        loadOrders();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_orders);
        filterAll = view.findViewById(R.id.filter_all);
        filterPending = view.findViewById(R.id.filter_pending);
        filterOnWay = view.findViewById(R.id.filter_on_way);
        filterDone = view.findViewById(R.id.filter_done);
        
        orderList = new ArrayList<>();
        allOrders = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new SupermarketOrderAdapter(orderList, this::showOrderDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupFilters() {
        // Filtros de estado
        filterAll.setOnClickListener(v -> filterByStatus("TODOS"));
        filterPending.setOnClickListener(v -> filterByStatus("PENDIENTE"));
        filterOnWay.setOnClickListener(v -> filterByStatus("EN_CAMINO"));
        filterDone.setOnClickListener(v -> filterByStatus("ENTREGADO"));
    }

    private void filterByStatus(String status) {
        Log.d(TAG, "filterByStatus: " + status);
        
        orderList.clear();
        
        if ("TODOS".equals(status)) {
            orderList.addAll(allOrders);
        } else {
            for (SupermarketOrder order : allOrders) {
                if (order.getStatus().toLowerCase().contains(status.toLowerCase())) {
                    orderList.add(order);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
        updateFilterButtons(status);
    }

    private void updateFilterButtons(String selectedStatus) {
        // Reset all buttons
        filterAll.setAlpha(0.6f);
        filterPending.setAlpha(0.6f);
        filterOnWay.setAlpha(0.6f);
        filterDone.setAlpha(0.6f);
        
        // Highlight selected button
        switch (selectedStatus) {
            case "TODOS":
                filterAll.setAlpha(1.0f);
                break;
            case "PENDIENTE":
                filterPending.setAlpha(1.0f);
                break;
            case "EN_CAMINO":
                filterOnWay.setAlpha(1.0f);
                break;
            case "ENTREGADO":
                filterDone.setAlpha(1.0f);
                break;
        }
    }

    private void loadOrders() {
        Log.d(TAG, "loadOrders: Cargando pedidos del supermercado");
        
        allOrders.clear();
        
        // Pedidos a agricultores (que hace el supermercado)
        allOrders.add(new SupermarketOrder(
            "EcoHuerta",
            Arrays.asList("Tomates Ecológicos (50kg)", "Lechuga Local (30uds)"),
            "28/12",
            "125,50 €",
            "🔴 Pendiente",
            "TO_FARMER"
        ));
        
        allOrders.add(new SupermarketOrder(
            "CampoFresco",
            Arrays.asList("Zanahorias Orgánicas (40kg)", "Manzanas Rojas (25kg)"),
            "29/12",
            "98,80 €",
            "🟡 En camino",
            "TO_FARMER"
        ));
        
        allOrders.add(new SupermarketOrder(
            "FrutasSur",
            Arrays.asList("Plátanos (35kg)", "Naranjas (20kg)"),
            "27/12",
            "67,30 €",
            "🟢 Entregado",
            "TO_FARMER"
        ));
        
        // Pedidos de consumidores (que recibe el supermercado)
        allOrders.add(new SupermarketOrder(
            "María García",
            Arrays.asList("Tomates (2kg)", "Lechuga (1ud)", "Zanahorias (1kg)"),
            "28/12",
            "8,50 €",
            "🔴 Pendiente",
            "FROM_CONSUMER"
        ));
        
        allOrders.add(new SupermarketOrder(
            "Juan Pérez",
            Arrays.asList("Manzanas (3kg)", "Plátanos (2kg)"),
            "29/12",
            "6,20 €",
            "🟡 En camino",
            "FROM_CONSUMER"
        ));
        
        allOrders.add(new SupermarketOrder(
            "Ana López",
            Arrays.asList("Lechuga (2uds)", "Tomates (1kg)"),
            "27/12",
            "4,80 €",
            "🟢 Entregado",
            "FROM_CONSUMER"
        ));
        
        // Mostrar todos los pedidos por defecto
        orderList.clear();
        orderList.addAll(allOrders);
        adapter.notifyDataSetChanged();
        
        // Activar filtro "Todos" por defecto
        updateFilterButtons("TODOS");
        
        Log.d(TAG, "loadOrders: " + allOrders.size() + " pedidos cargados");
    }

    private void showOrderDetails(SupermarketOrder order) {
        Log.d(TAG, "showOrderDetails: " + order.getClientOrSupplier());
        
        String orderTypeText = "TO_FARMER".equals(order.getOrderType()) ? "Pedido a Agricultor" : "Pedido de Consumidor";
        
        Toast.makeText(getContext(), 
            orderTypeText + "\n" +
            "Cliente/Proveedor: " + order.getClientOrSupplier() + "\n" +
            "Productos: " + String.join(", ", order.getProducts()) + "\n" +
            "Fecha: " + order.getDeliveryDate() + "\n" +
            "Total: " + order.getTotal() + "\n" +
            "Estado: " + order.getStatus(),
            Toast.LENGTH_LONG).show();
    }
}