package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
    
    // Tabs
    private Button tabSuppliers, tabClients;
    private boolean isSuppliersTabSelected = true;
    
    // Botones de filtro de estado
    private Button filterAll, filterPending, filterInProgress, filterDelivered, filterCancelled;
    private String currentStatusFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando SupermarketOrdersFragment");
        View view = inflater.inflate(R.layout.fragment_supermarket_orders, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupTabs();
        setupFilters();
        loadOrders();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_orders);
        
        // Tabs
        tabSuppliers = view.findViewById(R.id.tab_suppliers);
        tabClients = view.findViewById(R.id.tab_clients);
        
        // Filtros de estado
        filterAll = view.findViewById(R.id.filter_all);
        filterPending = view.findViewById(R.id.filter_pending);
        filterInProgress = view.findViewById(R.id.filter_in_progress);
        filterDelivered = view.findViewById(R.id.filter_delivered);
        filterCancelled = view.findViewById(R.id.filter_cancelled);
        
        orderList = new ArrayList<>();
        allOrders = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new SupermarketOrderAdapter(orderList, this::showOrderDetails);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupTabs() {
        // Configurar tab de proveedores (seleccionado por defecto)
        tabSuppliers.setOnClickListener(v -> switchToSuppliersTab());
        tabClients.setOnClickListener(v -> switchToClientsTab());
        
        // Aplicar estilo inicial
        updateTabStyles();
    }

    private void setupFilters() {
        // Filtros de estado
        filterAll.setOnClickListener(v -> filterByStatus("all"));
        filterPending.setOnClickListener(v -> filterByStatus("pending"));
        filterInProgress.setOnClickListener(v -> filterByStatus("in_progress"));
        filterDelivered.setOnClickListener(v -> filterByStatus("delivered"));
        filterCancelled.setOnClickListener(v -> filterByStatus("cancelled"));
    }

    private void filterByStatus(String status) {
        Log.d(TAG, "filterByStatus: " + status);
        
        orderList.clear();
        
        if ("all".equals(status)) {
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
        resetFilterButtonStyle(filterAll);
        resetFilterButtonStyle(filterPending);
        resetFilterButtonStyle(filterInProgress);
        resetFilterButtonStyle(filterDelivered);
        resetFilterButtonStyle(filterCancelled);
        
        // Highlight selected button
        switch (selectedStatus) {
            case "all":
                setSelectedFilterButtonStyle(filterAll);
                break;
            case "pending":
                setSelectedFilterButtonStyle(filterPending);
                break;
            case "in_progress":
                setSelectedFilterButtonStyle(filterInProgress);
                break;
            case "delivered":
                setSelectedFilterButtonStyle(filterDelivered);
                break;
            case "cancelled":
                setSelectedFilterButtonStyle(filterCancelled);
                break;
        }
    }

    private void resetFilterButtonStyle(Button button) {
        button.setBackgroundResource(R.drawable.filter_background);
        button.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void setSelectedFilterButtonStyle(Button button) {
        button.setBackgroundResource(R.drawable.filter_selected_background);
        button.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void switchToSuppliersTab() {
        isSuppliersTabSelected = true;
        updateTabStyles();
        loadOrders();
    }

    private void switchToClientsTab() {
        isSuppliersTabSelected = false;
        updateTabStyles();
        loadOrders();
    }

    private void updateTabStyles() {
        if (isSuppliersTabSelected) {
            tabSuppliers.setBackgroundResource(R.drawable.tab_selected_background);
            tabSuppliers.setTextColor(getResources().getColor(android.R.color.white));
            tabClients.setBackgroundResource(android.R.color.transparent);
            tabClients.setTextColor(getResources().getColor(R.color.text_secondary));
        } else {
            tabClients.setBackgroundResource(R.drawable.tab_selected_background);
            tabClients.setTextColor(getResources().getColor(android.R.color.white));
            tabSuppliers.setBackgroundResource(android.R.color.transparent);
            tabSuppliers.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    private void loadOrders() {
        Log.d(TAG, "loadOrders: Cargando pedidos del supermercado - Tab: " + (isSuppliersTabSelected ? "Proveedores" : "Clientes"));
        
        allOrders.clear();
        
        if (isSuppliersTabSelected) {
            loadSupplierOrders();
        } else {
            loadClientOrders();
        }
        
        // Aplicar filtro actual
        filterByStatus(currentStatusFilter);
        
        Log.d(TAG, "loadOrders: " + allOrders.size() + " pedidos cargados");
    }

    private void loadSupplierOrders() {
        // Pedidos realizados a proveedores (agricultores) desde la pestaña de búsqueda
        allOrders.add(new SupermarketOrder(
            "Agricultor Juan Pérez",
            Arrays.asList("Tomates Ecológicos (50kg)", "Lechuga Romana (30uds)"),
            "2024-01-15",
            "125,50 €",
            "pending",
            "TO_SUPPLIER"
        ));
        
        allOrders.add(new SupermarketOrder(
            "Granja Ecológica María",
            Arrays.asList("Manzanas Rojas (25kg)", "Peras Verdes (20kg)"),
            "2024-01-16",
            "98,80 €",
            "in_progress",
            "TO_SUPPLIER"
        ));
        
        allOrders.add(new SupermarketOrder(
            "Huerto Familiar Los Pinos",
            Arrays.asList("Espinacas (15kg)", "Acelgas (10kg)", "Rábanos (8kg)"),
            "2024-01-12",
            "67,30 €",
            "delivered",
            "TO_SUPPLIER"
        ));
        
        allOrders.add(new SupermarketOrder(
            "Cultivos Sostenibles S.L.",
            Arrays.asList("Brócoli (20kg)", "Coliflor (15kg)"),
            "2024-01-18",
            "85,40 €",
            "cancelled",
            "TO_SUPPLIER"
        ));
    }

    private void loadClientOrders() {
        // Pedidos realizados por clientes (consumidores) de los productos del stock
        allOrders.add(new SupermarketOrder(
            "Cliente Ana García",
            Arrays.asList("Tomates Ecológicos (2kg)", "Lechuga Romana (1ud)"),
            "2024-01-14",
            "8,50 €",
            "delivered",
            "FROM_CLIENT"
        ));
        
        allOrders.add(new SupermarketOrder(
            "Cliente Carlos López",
            Arrays.asList("Zanahorias Orgánicas (1kg)", "Manzanas Rojas (3kg)"),
            "2024-01-17",
            "6,20 €",
            "pending",
            "FROM_CLIENT"
        ));
        
        allOrders.add(new SupermarketOrder(
            "Cliente María Rodríguez",
            Arrays.asList("Espinacas (500g)", "Acelgas (500g)"),
            "2024-01-13",
            "4,80 €",
            "in_progress",
            "FROM_CLIENT"
        ));
        
        allOrders.add(new SupermarketOrder(
            "Cliente Pedro Martín",
            Arrays.asList("Brócoli (1kg)", "Coliflor (1kg)"),
            "2024-01-19",
            "7,60 €",
            "cancelled",
            "FROM_CLIENT"
        ));
    }

    private void showOrderDetails(SupermarketOrder order) {
        Log.d(TAG, "showOrderDetails: " + order.getClientOrSupplier());
        
        String orderTypeText = "TO_SUPPLIER".equals(order.getOrderType()) ? "Pedido a Proveedor" : "Pedido de Cliente";
        
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