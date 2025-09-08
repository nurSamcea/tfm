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
    private Button filterAll, filterInProgress, filterDelivered, filterCancelled;
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
        filterInProgress = view.findViewById(R.id.filter_in_progress);
        filterDelivered = view.findViewById(R.id.filter_delivered);
        filterCancelled = view.findViewById(R.id.filter_cancelled);

        orderList = new ArrayList<>();
        allOrders = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new FarmerOrderAdapter(orderList, this::showOrderDetails, new FarmerOrderAdapter.OnOrderActionListener() {
            @Override
            public void onCancelOrder(FarmerOrder order) {
                cancelOrder(order);
            }
            @Override
            public void onDeliverOrder(FarmerOrder order) {
                deliverOrder(order);
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
        if (!isAdded() || getContext() == null) {
            return;
        }
        button.setBackgroundResource(R.drawable.filter_background);
        button.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void setSelectedFilterButtonStyle(Button button) {
        if (!isAdded() || getContext() == null) {
            return;
        }
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
                if (!isAdded() || getContext() == null) {
                    return;
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    allOrders.clear();
                    
                    for (Transaction transaction : response.body()) {
                        // Convertir Transaction a FarmerOrder
                        List<String> products = new ArrayList<>();
                        // orderDetails es un String JSON, por ahora usamos datos básicos
                        products.add("Productos del pedido");
                        
                        String formattedDate = "N/A";
                        if (transaction.getCreatedAt() != null) {
                            java.text.SimpleDateFormat dateTimeFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
                            formattedDate = dateTimeFormat.format(transaction.getCreatedAt());
                        }

                        FarmerOrder order = new FarmerOrder(
                            transaction.getId(),
                            transaction.getBuyerName() != null ? transaction.getBuyerName() : "Comprador " + transaction.getBuyerId(),
                            products,
                            formattedDate,
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
                if (!isAdded() || getContext() == null) {
                    return;
                }
                Log.e(TAG, "Error de conexión: " + t.getMessage());
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOrderDetails(FarmerOrder order) {
        if (!isAdded() || getContext() == null) {
            return;
        }
        loadTransactionDetails(order.getTransactionId());
    }

    private void loadTransactionDetails(int transactionId) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getTransactionById(transactionId).enqueue(new Callback<com.example.frontend.models.Transaction>() {
            @Override
            public void onResponse(Call<com.example.frontend.models.Transaction> call, Response<com.example.frontend.models.Transaction> response) {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    com.example.frontend.models.Transaction transaction = response.body();
                    com.example.frontend.ui.dialogs.OrderReceiptDialog dialog = new com.example.frontend.ui.dialogs.OrderReceiptDialog(requireContext(), transaction);
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), "No se pudieron cargar los detalles del pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.frontend.models.Transaction> call, Throwable t) {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                Toast.makeText(getContext(), "Error al cargar los detalles: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cancelOrder(FarmerOrder order) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Cancelar Pedido")
                .setMessage("¿Estás seguro de que quieres cancelar este pedido? El stock se restaurará.")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    // Actualización optimista en UI
                    String previousStatus = order.getStatus();
                    order.setStatus("cancelled");
                    adapter.notifyDataSetChanged();
                    cancelOrderTransaction(order.getTransactionId(), previousStatus, order);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelOrderTransaction(int transactionId, String previousStatus, FarmerOrder orderRef) {
        ApiService api = ApiClient.getClient().create(ApiService.class);

        int userId = sessionManager.getUserId();
        String userType = sessionManager.getUserRole();
        if (userType == null) userType = "farmer";

        Call<com.example.frontend.models.Transaction> call = api.cancelTransaction(transactionId, userId, userType);
        call.enqueue(new Callback<com.example.frontend.models.Transaction>() {
            @Override
            public void onResponse(Call<com.example.frontend.models.Transaction> call, Response<com.example.frontend.models.Transaction> response) {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Pedido cancelado correctamente. Stock restaurado.", Toast.LENGTH_LONG).show();
                    loadOrders(); // Recargar la lista
                } else {
                    // Revertir si falla
                    orderRef.setStatus(previousStatus);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Error al cancelar el pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.frontend.models.Transaction> call, Throwable t) {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                // Revertir si falla
                orderRef.setStatus(previousStatus);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deliverOrder(FarmerOrder order) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirmar Entrega")
                .setMessage("¿Marcar este pedido como entregado? El stock pasará al comprador.")
                .setPositiveButton("Sí, entregar", (dialog, which) -> {
                    // Actualización optimista en UI
                    String previousStatus = order.getStatus();
                    order.setStatus("delivered");
                    adapter.notifyDataSetChanged();
                    deliverOrderTransaction(order.getTransactionId(), previousStatus, order);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deliverOrderTransaction(int transactionId, String previousStatus, FarmerOrder orderRef) {
        ApiService api = ApiClient.getClient().create(ApiService.class);

        int userId = sessionManager.getUserId();
        String userType = sessionManager.getUserRole();
        if (userType == null) userType = "farmer";

        Call<com.example.frontend.models.Transaction> call = api.deliverTransaction(transactionId, userId, userType);
        call.enqueue(new Callback<com.example.frontend.models.Transaction>() {
            @Override
            public void onResponse(Call<com.example.frontend.models.Transaction> call, Response<com.example.frontend.models.Transaction> response) {
                if (!isAdded() || getContext() == null) {
                    return;
                }

                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Pedido marcado como entregado. Stock transferido.", Toast.LENGTH_LONG).show();
                    loadOrders();
                } else {
                    // Revertir si falla
                    orderRef.setStatus(previousStatus);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Error al entregar el pedido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.example.frontend.models.Transaction> call, Throwable t) {
                if (!isAdded() || getContext() == null) {
                    return;
                }
                // Revertir si falla
                orderRef.setStatus(previousStatus);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
