package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextWatcher;
import android.text.Editable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.model.SupermarketOrder;
import com.example.frontend.ui.adapters.SupplierProductAdapter;
import com.example.frontend.ui.adapters.SupermarketOrderAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SupermarketInventoryFragment extends Fragment {

    private static final String TAG = "SupermarketInventory";

    private RecyclerView recyclerSuggestions;
    private RecyclerView recyclerOrders;
    private SupplierProductAdapter suggestionsAdapter;
    private SupermarketOrderAdapter orderAdapter;

    private EditText searchOrder;
    private ChipGroup chipGroupFilters;

    private final List<SupermarketOrder> allOrders = new ArrayList<>();
    private final List<Product> suggestionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        try {
            Log.d(TAG, "Iniciando onCreateView");

            View view = inflater.inflate(R.layout.fragment_supermarket_inventory, container, false);

            // Referencias UI
            recyclerSuggestions = view.findViewById(R.id.recyclerSuggestions);
            recyclerOrders = view.findViewById(R.id.recycler_order_history);
            searchOrder = view.findViewById(R.id.search_order);
            chipGroupFilters = view.findViewById(R.id.chip_group_filters);

            // Layouts
            recyclerSuggestions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));

            // Adapters
            suggestionsAdapter = new SupplierProductAdapter(suggestionList);
            orderAdapter = new SupermarketOrderAdapter(allOrders, new SupermarketOrderAdapter.OnOrderActionListener() {
                @Override
                public void onChangeStatus(SupermarketOrder order) {
                    switch (order.getStatus()) {
                        case "Preparando pedido":
                            order.setStatus("En camino");
                            break;
                        case "En camino":
                            order.setStatus("Entregado");
                            break;
                    }
                    orderAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Estado actualizado a: " + order.getStatus(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onViewDetail(SupermarketOrder order) {
                    Toast.makeText(getContext(), "Detalle de " + order.getId(), Toast.LENGTH_SHORT).show();
                }
            });

            recyclerSuggestions.setAdapter(suggestionsAdapter);
            recyclerOrders.setAdapter(orderAdapter);

            // Mock data
            mockSuggestions();
            mockOrders();

            // Filtros dinámicos
            configurarFiltros();

            // Búsqueda
            searchOrder.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filtrarPedidos(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            Log.d(TAG, "Vista de inventario cargada correctamente");
            return view;

        } catch (Exception e) {
            Log.e(TAG, "Error en SupermarketInventoryFragment: " + e.getMessage(), e);
            return null;
        }
    }

    private void configurarFiltros() {
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            filtrarPedidos(searchOrder.getText().toString()); // Filtra según texto y chips activos
        });
    }

    private void filtrarPedidos(String query) {
        List<SupermarketOrder> filtrados = new ArrayList<>();
        for (SupermarketOrder order : allOrders) {
            boolean coincideTexto = query.isEmpty()
                    || order.getClientName().toLowerCase().contains(query.toLowerCase())
                    || order.getId().toLowerCase().contains(query.toLowerCase());

            boolean coincideFiltro = true;
            List<Integer> checked = chipGroupFilters.getCheckedChipIds();
            for (int id : checked) {
                Chip chip = chipGroupFilters.findViewById(id);
                String text = chip.getText().toString();

                if (text.equals("Pendientes") && order.getStatus().equals("Entregado")) {
                    coincideFiltro = false;
                }
                if (text.equals("Entregados") && !order.getStatus().equals("Entregado")) {
                    coincideFiltro = false;
                }
            }

            if (coincideTexto && coincideFiltro) {
                filtrados.add(order);
            }
        }

        // Ordenar si se selecciona filtro de fecha
        List<Integer> checked = chipGroupFilters.getCheckedChipIds();
        for (int id : checked) {
            Chip chip = chipGroupFilters.findViewById(id);
            if (chip.getText().toString().equals("Más recientes")) {
                Collections.sort(filtrados, (a, b) -> b.getOrderDate().compareTo(a.getOrderDate()));
            } else if (chip.getText().toString().equals("Más antiguos")) {
                Collections.sort(filtrados,new Comparator<SupermarketOrder>() {
                    @Override
                    public int compare(SupermarketOrder a, SupermarketOrder b) {
                        return a.getOrderDate().compareTo(b.getOrderDate());
                    }
                });
            }
        }

        orderAdapter.updateData(filtrados);
    }

    private void mockSuggestions() {
        suggestionList.add(new Product("1", "Tomate cherry", "Stock bajo", 1.80, 3, 1));
        suggestionList.add(new Product("2", "Lechuga romana", "Stock bajo", 0.95, 5, 2));
        suggestionsAdapter.notifyDataSetChanged();
    }

    private void mockOrders() {
        allOrders.clear();
        allOrders.add(new SupermarketOrder("C-001", "Ana López", new Date(), 42.30, "Preparando pedido", null, "Calle Mayor 12, Madrid"));
        allOrders.add(new SupermarketOrder("C-002", "Luis Pérez", new Date(), 67.80, "Entregado", null, "Calle Gran Vía 7, Madrid"));
        allOrders.add(new SupermarketOrder("C-003", "Marta Ruiz", new Date(), 35.10, "En camino", null, "Calle Toledo 5, Madrid"));
        allOrders.add(new SupermarketOrder("C-004", "Carlos Gómez", new Date(), 51.20, "Entregado", null, "Calle Alcalá 22, Madrid"));
        orderAdapter.notifyDataSetChanged();
    }
}
