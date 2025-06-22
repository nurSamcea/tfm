package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.ui.adapters.SupplierProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class SupermarketSuppliersFragment extends Fragment {
    private static final String TAG = "SupermarketSuppliersFragment";

    private EditText searchView;
    private RecyclerView recyclerSuggestions;
    private RecyclerView recyclerResults;
    private SupplierProductAdapter suggestionsAdapter;
    private SupplierProductAdapter resultsAdapter;
    private List<Product> suggestionList = new ArrayList<>();
    private List<Product> resultList = new ArrayList<>();
    private TextView filtersActive;
    private ImageButton btnFilters;
    private List<Product> cartItems = new ArrayList<>();
    private TextView cartSheetItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando la aplicación");
        View view = inflater.inflate(R.layout.fragment_supermarket_suppliers, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerSuggestions = view.findViewById(R.id.recyclerSuggestions);
        recyclerResults = view.findViewById(R.id.recyclerResults);
        filtersActive = view.findViewById(R.id.textFiltersActive);
        btnFilters = view.findViewById(R.id.btnFilters);
        cartSheetItems = view.findViewById(R.id.cart_sheet_items);
        Button btnViewFullOrder = view.findViewById(R.id.btn_view_full_order);
        Button btnConfirmOrder = view.findViewById(R.id.btn_confirm_order);

        recyclerSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerResults.setLayoutManager(new LinearLayoutManager(getContext()));

        suggestionsAdapter = new SupplierProductAdapter(suggestionList);
        resultsAdapter = new SupplierProductAdapter(resultList, product -> {
            cartItems.add(product);
            actualizarVistaCarrito();
            Toast.makeText(getContext(), product.getName() + " añadido al carrito", Toast.LENGTH_SHORT).show();
        });

        recyclerSuggestions.setAdapter(suggestionsAdapter);
        recyclerResults.setAdapter(resultsAdapter);

        mockSuggestions();
        mockResults();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarResultados(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnFilters.setOnClickListener(v -> {
            // Abrir diálogo de filtros
        });
        btnViewFullOrder.setOnClickListener(v -> Toast.makeText(getContext(), "Ver pedido completo (no implementado)", Toast.LENGTH_SHORT).show());
        btnConfirmOrder.setOnClickListener(v -> Toast.makeText(getContext(), "Pedido confirmado (no implementado)", Toast.LENGTH_SHORT).show());
        actualizarVistaCarrito();
        return view;
    }

    private void filtrarResultados(String query) {
        List<Product> filtrados = new ArrayList<>();
        for (Product p : resultList) {
            if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                filtrados.add(p);
            }
        }
        resultsAdapter.updateData(filtrados);
    }

    private void mockSuggestions() {
        suggestionList.add(new Product("1", "Tomate cherry", "Sugerencia", 1.80, 3, 1));
        suggestionList.add(new Product("2", "Lechuga romana", "Sugerencia", 0.95, 5, 2));
        suggestionsAdapter.notifyDataSetChanged();
    }

    private void mockResults() {
        resultList.add(new Product("3", "Tomate cherry · EcoHuerta", "", 1.80, 0, 1));
        resultList.add(new Product("4", "Tomate cherry · FrutasSur", "", 1.65, 0, 2));
        resultList.add(new Product("5", "Tomate pera · CampoFresco", "", 1.90, 0, 3));
        resultsAdapter.notifyDataSetChanged();
    }

    private void actualizarVistaCarrito() {
        if (cartSheetItems == null) return;
        if (cartItems.isEmpty()) {
            cartSheetItems.setText("(Vacío)");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Product p : cartItems) {
                sb.append("1x ").append(p.getName());
                if (p.getFarmerId() != null) sb.append(" · ").append(p.getFarmerId());
                sb.append("\n");
            }
            cartSheetItems.setText(sb.toString().trim());
        }
    }
}

