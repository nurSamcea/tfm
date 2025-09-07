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
import com.example.frontend.ui.dialogs.OrderReceiptDialog;
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
    private Button filterAll, filterInProgress, filterDelivered, filterCancelled;
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

    @Override
    public void onResume() {
        super.onResume();
        // Recargar pedidos cuando el fragmento se vuelve visible
        // Esto asegura que se muestren las compras recién creadas
        Log.d(TAG, "onResume: Recargando pedidos del consumidor");
        loadOrders();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_orders);
        
        // Inicializar SessionManager
        sessionManager = new SessionManager(requireContext());
        
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
                Log.d(TAG, "Respuesta recibida - Código: " + response.code() + ", Exitoso: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    allOrders.clear();
                    Log.d(TAG, "Transacciones recibidas: " + response.body().size());
                    
                    for (Transaction transaction : response.body()) {
                        Log.d(TAG, "Procesando transacción ID: " + transaction.getId() + 
                              ", Buyer ID: " + transaction.getBuyerId() + 
                              ", Seller ID: " + transaction.getSellerId() +
                              ", Status: " + transaction.getStatus());
                        
                        // Convertir Transaction a SupermarketOrder
                        List<String> products = new ArrayList<>();
                        // orderDetails es un String JSON, por ahora usamos datos básicos
                        products.add("Productos del pedido");
                        
                        // Determinar el icono según el tipo de vendedor
                        String sellerIcon = getSellerIcon(transaction.getSellerType());
                        String sellerName = sellerIcon + " " + (transaction.getSellerName() != null ? transaction.getSellerName() : "Vendedor " + transaction.getSellerId());
                        
                        // Formatear fecha con hora
                        String formattedDate = "N/A";
                        if (transaction.getCreatedAt() != null) {
                            java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
                            formattedDate = dateTimeFormat.format(transaction.getCreatedAt());
                        }
                        
                        SupermarketOrder order = new SupermarketOrder(
                            transaction.getId(),
                            sellerName,
                            products,
                            formattedDate,
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
                    Log.e(TAG, "Error al cargar pedidos: " + response.code() + ", Mensaje: " + response.message());
                    if (response.body() == null) {
                        Log.e(TAG, "Response body es null");
                    }
                    Toast.makeText(getContext(), "Error al cargar pedidos: " + response.code(), Toast.LENGTH_SHORT).show();
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
        
        // Mostrar loading y cargar los detalles de la transacción
        Toast.makeText(getContext(), "Cargando detalles del pedido...", Toast.LENGTH_SHORT).show();
        loadTransactionDetails(order.getTransactionId());
    }
    
    private void loadTransactionDetails(int transactionId) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        
        api.getTransactionById(transactionId).enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Transaction transaction = response.body();
                    OrderReceiptDialog dialog = new OrderReceiptDialog(requireContext(), transaction);
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), "No se pudieron cargar los detalles del pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Log.e(TAG, "Error al obtener detalles de la transacción: " + t.getMessage());
                Toast.makeText(getContext(), "Error al cargar los detalles del pedido", Toast.LENGTH_SHORT).show();
            }
        });
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
    
    private String getSellerIcon(String sellerType) {
        if (sellerType == null) return "🏪";
        
        switch (sellerType.toLowerCase()) {
            case "farmer":
                return "🌾"; // Icono para agricultor
            case "supermarket":
                return "🏪"; // Icono para supermercado
            case "consumer":
                return "👤"; // Icono para consumidor
            default:
                return "🏪"; // Icono por defecto
        }
    }
}
