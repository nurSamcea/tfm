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
import com.example.frontend.models.Transaction;
import com.example.frontend.ui.adapters.SupermarketOrderAdapter;
import com.example.frontend.api.ApiService;
import com.example.frontend.network.ApiClient;
import com.example.frontend.utils.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupermarketOrdersFragment extends Fragment {
    private static final String TAG = "SupermarketOrdersFragment";

    private RecyclerView recyclerView;
    private SupermarketOrderAdapter adapter;
    private List<SupermarketOrder> orderList;
    private List<SupermarketOrder> allOrders;
    private SessionManager sessionManager;
    
    // Tabs
    private Button tabSuppliers, tabClients;
    private boolean isSuppliersTabSelected = true;
    
    // Botones de filtro de estado
    private Button filterAll, filterInProgress, filterDelivered, filterCancelled;
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
        
        // Inicializar SessionManager
        sessionManager = new SessionManager(requireContext());
        
        // Tabs
        tabSuppliers = view.findViewById(R.id.tab_suppliers);
        tabClients = view.findViewById(R.id.tab_clients);
        
        // Filtros de estado
        filterAll = view.findViewById(R.id.filter_all);
        filterInProgress = view.findViewById(R.id.filter_in_progress);
        filterDelivered = view.findViewById(R.id.filter_delivered);
        filterCancelled = view.findViewById(R.id.filter_cancelled);
        
        orderList = new ArrayList<>();
        allOrders = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new SupermarketOrderAdapter(orderList, this::showOrderDetails, new SupermarketOrderAdapter.OnOrderActionListener() {
            @Override
            public void onCancelOrder(SupermarketOrder order) {
                cancelOrder(order);
            }
        });
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
        resetFilterButtonStyle(filterInProgress);
        resetFilterButtonStyle(filterDelivered);
        resetFilterButtonStyle(filterCancelled);
        
        // Highlight selected button
        switch (selectedStatus) {
            case "all":
                setSelectedFilterButtonStyle(filterAll);
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
        // Obtener ID del supermercado desde la sesión
        Integer supermarketId = sessionManager.getUserId();
        if (supermarketId == null) {
            Log.e(TAG, "No se pudo obtener el ID del supermercado");
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del supermercado", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "loadSupplierOrders: Cargando transacciones donde el supermercado es comprador");

        // Cargar pedidos reales desde la API
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<Transaction>> call = api.getBuyerOrders(supermarketId, "supermarket");
        
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allOrders.clear();
                    
                    Log.d(TAG, "Respuesta recibida con " + response.body().size() + " transacciones");
                    
                    for (Transaction transaction : response.body()) {
                        // Convertir Transaction a SupermarketOrder
                        List<String> products = new ArrayList<>();
                        
                        // Intentar extraer información de productos del order_details
                        if (transaction.getOrderDetails() != null && !transaction.getOrderDetails().isEmpty()) {
                            try {
                                // orderDetails puede ser una lista de objetos o un string JSON
                                if (transaction.getOrderDetails() instanceof List) {
                                    List<?> details = (List<?>) transaction.getOrderDetails();
                                    for (Object detail : details) {
                                        if (detail instanceof Map) {
                                            Map<?, ?> detailMap = (Map<?, ?>) detail;
                                            String productName = (String) detailMap.get("product_name");
                                            Integer quantity = (Integer) detailMap.get("quantity");
                                            if (productName != null && quantity != null) {
                                                products.add(productName + " (" + quantity + ")");
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "Error al procesar order_details: " + e.getMessage());
                            }
                        }
                        
                        // Si no se pudieron extraer productos, usar texto genérico
                        if (products.isEmpty()) {
                            products.add("Productos del pedido");
                        }
                        
                        SupermarketOrder order = new SupermarketOrder(
                            transaction.getId(),
                            transaction.getSellerName() != null ? transaction.getSellerName() : "Vendedor " + transaction.getSellerId(),
                            products,
                            transaction.getCreatedAt() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(transaction.getCreatedAt()) : "N/A",
                            String.format("%.2f €", transaction.getTotalPrice()),
                            transaction.getStatus(),
                            "TO_SUPPLIER"
                        );
                        allOrders.add(order);
                        
                        Log.d(TAG, "Agregada transacción de proveedor: " + transaction.getSellerName() + " - " + transaction.getTotalPrice() + "€");
                    }
                    
                    // Aplicar filtro actual
                    filterByStatus(currentStatusFilter);
                    Log.d(TAG, "Pedidos de proveedores cargados: " + allOrders.size());
                } else {
                    Log.e(TAG, "Error al cargar pedidos: " + response.code());
                    Toast.makeText(getContext(), "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadClientOrders() {
        // Obtener ID del supermercado desde la sesión
        Integer supermarketId = sessionManager.getUserId();
        if (supermarketId == null) {
            Log.e(TAG, "No se pudo obtener el ID del supermercado");
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del supermercado", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "loadClientOrders: Cargando transacciones donde el supermercado es vendedor");

        // Cargar pedidos de clientes desde la API
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<Transaction>> call = api.getSellerOrders(supermarketId, "supermarket");
        
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allOrders.clear();
                    
                    Log.d(TAG, "Respuesta recibida con " + response.body().size() + " transacciones");
                    
                    for (Transaction transaction : response.body()) {
                        // Solo mostrar pedidos de consumidores
                        if ("consumer".equalsIgnoreCase(transaction.getBuyerType())) {
                            // Convertir Transaction a SupermarketOrder
                            List<String> products = new ArrayList<>();
                            
                            // Intentar extraer información de productos del order_details
                            if (transaction.getOrderDetails() != null && !transaction.getOrderDetails().isEmpty()) {
                                try {
                                    // orderDetails puede ser una lista de objetos o un string JSON
                                    if (transaction.getOrderDetails() instanceof List) {
                                        List<?> details = (List<?>) transaction.getOrderDetails();
                                        for (Object detail : details) {
                                            if (detail instanceof Map) {
                                                Map<?, ?> detailMap = (Map<?, ?>) detail;
                                                String productName = (String) detailMap.get("product_name");
                                                Integer quantity = (Integer) detailMap.get("quantity");
                                                if (productName != null && quantity != null) {
                                                    products.add(productName + " (" + quantity + ")");
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.w(TAG, "Error al procesar order_details: " + e.getMessage());
                                }
                            }
                            
                            // Si no se pudieron extraer productos, usar texto genérico
                            if (products.isEmpty()) {
                                products.add("Productos del pedido");
                            }
                            
                            SupermarketOrder order = new SupermarketOrder(
                                transaction.getId(),
                                transaction.getBuyerName() != null ? transaction.getBuyerName() : "Cliente " + transaction.getBuyerId(),
                                products,
                                transaction.getCreatedAt() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(transaction.getCreatedAt()) : "N/A",
                                String.format("%.2f €", transaction.getTotalPrice()),
                                transaction.getStatus(),
                                "FROM_CLIENT"
                            );
                            allOrders.add(order);
                            
                            Log.d(TAG, "Agregada transacción de cliente: " + transaction.getBuyerName() + " - " + transaction.getTotalPrice() + "€");
                        }
                    }
                    
                    // Aplicar filtro actual
                    filterByStatus(currentStatusFilter);
                    Log.d(TAG, "Pedidos de clientes cargados: " + allOrders.size());
                } else {
                    Log.e(TAG, "Error al cargar pedidos de clientes: " + response.code());
                    Toast.makeText(getContext(), "Error al cargar pedidos de clientes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Log.e(TAG, "Error de conexión: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOrderDetails(SupermarketOrder order) {
        Log.d(TAG, "showOrderDetails: " + order.getClientOrSupplier());
        
        String orderTypeText;
        String participantLabel;
        
        if ("TO_SUPPLIER".equals(order.getOrderType())) {
            orderTypeText = "Pedido a Proveedor";
            participantLabel = "Proveedor";
        } else {
            orderTypeText = "Venta a Cliente";
            participantLabel = "Cliente";
        }
        
        Toast.makeText(getContext(), 
            orderTypeText + "\n" +
            participantLabel + ": " + order.getClientOrSupplier() + "\n" +
            "Productos: " + String.join(", ", order.getProducts()) + "\n" +
            "Fecha: " + order.getDeliveryDate() + "\n" +
            "Total: " + order.getTotal() + "\n" +
            "Estado: " + order.getStatus(),
            Toast.LENGTH_LONG).show();
    }

    private void cancelOrder(SupermarketOrder order) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Cancelar Pedido")
                .setMessage("¿Estás seguro de que quieres cancelar este pedido? El stock se restaurará al vendedor.")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    cancelOrderTransaction(order.getTransactionId());
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelOrderTransaction(int transactionId) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        
        Call<com.example.frontend.models.Transaction> call = api.cancelTransaction(transactionId);
        call.enqueue(new Callback<com.example.frontend.models.Transaction>() {
            @Override
            public void onResponse(Call<com.example.frontend.models.Transaction> call, Response<com.example.frontend.models.Transaction> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Pedido cancelado correctamente. Stock restaurado al vendedor.", Toast.LENGTH_LONG).show();
                    loadOrders(); // Recargar la lista
                } else {
                    Toast.makeText(getContext(), "Error al cancelar el pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.frontend.models.Transaction> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}