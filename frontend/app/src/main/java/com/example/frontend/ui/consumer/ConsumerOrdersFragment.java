package com.example.frontend.ui.consumer;

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
import com.example.frontend.model.ConsumerOrder;
import com.example.frontend.model.SupermarketOrder;
import com.example.frontend.models.Transaction;
import com.example.frontend.ui.adapters.ConsumerOrderAdapter;
import com.example.frontend.ui.adapters.SupermarketOrderAdapter;
import com.example.frontend.ui.dialogs.OrderDetailsDialog;
import com.example.frontend.api.ApiService;
import com.example.frontend.network.ApiClient;
import com.example.frontend.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsumerOrdersFragment extends Fragment {
    private static final String TAG = "ConsumerOrdersFragment";

    private RecyclerView recyclerView;
    private SupermarketOrderAdapter adapter;
    private List<SupermarketOrder> orderList;
    private List<SupermarketOrder> allOrders;
    private SessionManager sessionManager;
    
    // Botones de filtro de estado
    private Button filterAll, filterPending, filterInProgress, filterDelivered, filterCancelled;
    private String currentStatusFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando ConsumerOrdersFragment");
        View view = inflater.inflate(R.layout.fragment_consumer_orders, container, false);

        initializeViews(view);
        setupRecyclerView();
        setupFilters();
        loadOrders();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_orders);
        
        // Inicializar SessionManager
        sessionManager = new SessionManager(requireContext());
        
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
        adapter = new SupermarketOrderAdapter(orderList, this::showOrderDetails, new SupermarketOrderAdapter.OnOrderActionListener() {
            @Override
            public void onCancelOrder(SupermarketOrder order) {
                cancelOrder(order);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
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

    private void loadOrders() {
        Log.d(TAG, "loadOrders: Cargando pedidos del consumidor");
        
        allOrders.clear();
        
        // Obtener ID del consumidor desde la sesión
        Integer consumerId = sessionManager.getUserId();
        if (consumerId == null) {
            Log.e(TAG, "No se pudo obtener el ID del consumidor");
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del consumidor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cargar pedidos reales desde la API
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<Transaction>> call = api.getBuyerOrders(consumerId, "consumer");
        
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allOrders.clear();
                    
                    for (Transaction transaction : response.body()) {
                        // Convertir Transaction a SupermarketOrder
                        List<String> products = new ArrayList<>();
                        // orderDetails es un String JSON, por ahora usamos datos básicos
                        products.add("Productos del pedido");
                        
                        SupermarketOrder order = new SupermarketOrder(
                            transaction.getId(),
                            transaction.getSellerName() != null ? transaction.getSellerName() : "Vendedor " + transaction.getSellerId(),
                            products,
                            transaction.getCreatedAt() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(transaction.getCreatedAt()) : "N/A",
                            String.format("%.2f €", transaction.getTotalPrice()),
                            transaction.getStatus(),
                            "FROM_SELLER"
                        );
                        allOrders.add(order);
                    }
                    
                    // Aplicar filtro actual
                    filterByStatus(currentStatusFilter);
                    Log.d(TAG, "Pedidos del consumidor cargados: " + allOrders.size());
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

    private void showOrderDetails(SupermarketOrder order) {
        Log.d(TAG, "showOrderDetails: " + order.getClientOrSupplier());
        
        String orderTypeText = "FROM_SELLER".equals(order.getOrderType()) ? "Pedido de Vendedor" : "Pedido";
        
        Toast.makeText(getContext(), 
            orderTypeText + "\n" +
            "Vendedor: " + order.getClientOrSupplier() + "\n" +
            "Productos: " + String.join(", ", order.getProducts()) + "\n" +
            "Fecha: " + order.getDeliveryDate() + "\n" +
            "Total: " + order.getTotal() + "\n" +
            "Estado: " + order.getStatus(),
            Toast.LENGTH_LONG).show();
    }

    private void cancelOrder(SupermarketOrder order) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Cancelar Pedido")
                .setMessage("¿Estás seguro de que quieres cancelar este pedido?")
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
                    Toast.makeText(getContext(), "Pedido cancelado correctamente.", Toast.LENGTH_LONG).show();
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
