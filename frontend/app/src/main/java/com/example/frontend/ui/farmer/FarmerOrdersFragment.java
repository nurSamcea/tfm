package com.example.frontend.ui.farmer;

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
import com.example.frontend.model.FarmerOrder;
import com.example.frontend.models.Transaction;
import com.example.frontend.ui.adapters.FarmerOrderAdapter;
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

public class FarmerOrdersFragment extends Fragment {
    private static final String TAG = "FarmerOrdersFragment";

    private RecyclerView recyclerView;
    private FarmerOrderAdapter adapter;
    private List<FarmerOrder> orderList;
    private List<FarmerOrder> allOrders;
    private SessionManager sessionManager;
    
    // Botones de filtro de estado
    private Button filterAll, filterPending, filterInProgress, filterDelivered, filterCancelled;
    private String currentStatusFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando FarmerOrdersFragment");
        View view = inflater.inflate(R.layout.fragment_farmer_orders, container, false);

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
        filterInProgress = view.findViewById(R.id.filter_on_way);
        filterDelivered = view.findViewById(R.id.filter_done);
        filterCancelled = null; // No existe en el layout

        orderList = new ArrayList<>();
        allOrders = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new FarmerOrderAdapter(orderList, this::showOrderDetails, new FarmerOrderAdapter.OnOrderActionListener() {
            @Override
            public void onAcceptOrder(FarmerOrder order) {
                acceptOrder(order);
            }

            @Override
            public void onDeliverOrder(FarmerOrder order) {
                deliverOrder(order);
            }

            @Override
            public void onCancelOrder(FarmerOrder order) {
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
        // filterCancelled no existe en el layout, se omite
    }

    private void filterByStatus(String status) {
        Log.d(TAG, "filterByStatus: " + status);
        
        orderList.clear();
        
        if ("all".equals(status)) {
            orderList.addAll(allOrders);
        } else {
            for (FarmerOrder order : allOrders) {
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
        // filterCancelled no existe en el layout
        
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
                // filterCancelled no existe en el layout, no se puede seleccionar
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
        Log.d(TAG, "loadOrders: Cargando pedidos del agricultor");
        
        allOrders.clear();
        
        // Obtener ID del agricultor desde la sesión
        Integer farmerId = sessionManager.getUserId();
        if (farmerId == null) {
            Log.e(TAG, "No se pudo obtener el ID del agricultor");
            Toast.makeText(getContext(), "Error: No se pudo obtener el ID del agricultor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cargar pedidos reales desde la API
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<List<Transaction>> call = api.getSellerOrders(farmerId, "farmer");
        
        call.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allOrders.clear();
                    
                    for (Transaction transaction : response.body()) {
                        // Convertir Transaction a FarmerOrder
                        List<String> products = new ArrayList<>();
                        // orderDetails es un String JSON, por ahora usamos datos básicos
                        products.add("Productos del pedido");
                        
                        FarmerOrder order = new FarmerOrder(
                            transaction.getId(),
                            transaction.getBuyerName() != null ? transaction.getBuyerName() : "Comprador " + transaction.getBuyerId(),
                            products,
                            transaction.getCreatedAt() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(transaction.getCreatedAt()) : "N/A",
                            String.format("%.2f €", transaction.getTotalPrice()),
                            transaction.getStatus()
                        );
                        allOrders.add(order);
                    }
                    
                    // Aplicar filtro actual
                    filterByStatus(currentStatusFilter);
                    Log.d(TAG, "Pedidos del agricultor cargados: " + allOrders.size());
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

    private void showOrderDetails(FarmerOrder order) {
        new FarmerOrderDetailsDialogFragment(order).show(getParentFragmentManager(), "details");
    }

    private void acceptOrder(FarmerOrder order) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Aceptar Pedido")
                .setMessage("¿Estás seguro de que quieres aceptar este pedido? El stock se reservará.")
                .setPositiveButton("Sí, aceptar", (dialog, which) -> {
                    updateOrderStatus(order.getTransactionId(), "in_progress");
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deliverOrder(FarmerOrder order) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Entregar Pedido")
                .setMessage("¿Estás seguro de que quieres marcar este pedido como entregado? El stock se transferirá al comprador.")
                .setPositiveButton("Sí, entregar", (dialog, which) -> {
                    deliverOrderToBuyer(order.getTransactionId());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cancelOrder(FarmerOrder order) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Cancelar Pedido")
                .setMessage("¿Estás seguro de que quieres cancelar este pedido? El stock se restaurará.")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    cancelOrderTransaction(order.getTransactionId());
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void updateOrderStatus(int transactionId, String status) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        
        // Crear el objeto de actualización de estado
        ApiService.StatusUpdateRequest statusUpdate = new ApiService.StatusUpdateRequest(status);
        
        Call<com.example.frontend.models.Transaction> call = api.updateTransactionStatus(transactionId, statusUpdate);
        call.enqueue(new Callback<com.example.frontend.models.Transaction>() {
            @Override
            public void onResponse(Call<com.example.frontend.models.Transaction> call, Response<com.example.frontend.models.Transaction> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Pedido actualizado correctamente", Toast.LENGTH_SHORT).show();
                    loadOrders(); // Recargar la lista
                } else {
                    Toast.makeText(getContext(), "Error al actualizar el pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.frontend.models.Transaction> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deliverOrderToBuyer(int transactionId) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        
        Call<com.example.frontend.models.Transaction> call = api.deliverTransaction(transactionId);
        call.enqueue(new Callback<com.example.frontend.models.Transaction>() {
            @Override
            public void onResponse(Call<com.example.frontend.models.Transaction> call, Response<com.example.frontend.models.Transaction> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Pedido entregado correctamente. Stock transferido al comprador.", Toast.LENGTH_LONG).show();
                    loadOrders(); // Recargar la lista
                } else {
                    Toast.makeText(getContext(), "Error al entregar el pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.frontend.models.Transaction> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelOrderTransaction(int transactionId) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        
        Call<com.example.frontend.models.Transaction> call = api.cancelTransaction(transactionId);
        call.enqueue(new Callback<com.example.frontend.models.Transaction>() {
            @Override
            public void onResponse(Call<com.example.frontend.models.Transaction> call, Response<com.example.frontend.models.Transaction> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Pedido cancelado correctamente. Stock restaurado.", Toast.LENGTH_LONG).show();
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
