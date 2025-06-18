package com.example.frontend.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.adapters.SupermarketInventoryAdapter;
import com.example.frontend.model.Product;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SupermarketInventoryFragment extends Fragment implements SupermarketInventoryAdapter.OnInventoryActionListener {
    private RecyclerView recyclerView;
    private SupermarketInventoryAdapter adapter;
    private List<Product> products;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supermarket_inventory, container, false);
        
        recyclerView = view.findViewById(R.id.inventory_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Inicializar lista de productos (esto vendría de tu API/backend)
        products = new ArrayList<>();
        // TODO: Cargar productos reales desde el backend
        
        adapter = new SupermarketInventoryAdapter(products, this);
        recyclerView.setAdapter(adapter);
        
        return view;
    }

    @Override
    public void onUpdateStock(Product product) {
        showUpdateStockDialog(product);
    }

    @Override
    public void onUpdatePrice(Product product) {
        showUpdatePriceDialog(product);
    }

    @Override
    public void onViewMetrics(Product product) {
        // TODO: Implementar vista de métricas
        Toast.makeText(getContext(), "Métricas de " + product.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderMore(Product product) {
        showOrderMoreDialog(product);
    }

    private void showUpdateStockDialog(Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_stock, null);
        TextInputEditText stockInput = dialogView.findViewById(R.id.stockInput);
        stockInput.setText(String.valueOf(product.getStock()));

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Actualizar Stock")
            .setView(dialogView)
            .setPositiveButton("Actualizar", (dialog, which) -> {
                String newStockStr = stockInput.getText().toString();
                try {
                    int newStock = Integer.parseInt(newStockStr);
                    // TODO: Actualizar stock en el backend
                    product.setStock(newStock);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Stock actualizado", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Valor inválido", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void showUpdatePriceDialog(Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_price, null);
        TextInputEditText priceInput = dialogView.findViewById(R.id.priceInput);
        priceInput.setText(String.valueOf(product.getPrice()));

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Actualizar Precio")
            .setView(dialogView)
            .setPositiveButton("Actualizar", (dialog, which) -> {
                String newPriceStr = priceInput.getText().toString();
                try {
                    double newPrice = Double.parseDouble(newPriceStr);
                    // TODO: Actualizar precio en el backend
                    product.setPrice(newPrice);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Precio actualizado", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Valor inválido", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void showOrderMoreDialog(Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_order_more, null);
        TextInputEditText quantityInput = dialogView.findViewById(R.id.quantityInput);

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Realizar Pedido")
            .setView(dialogView)
            .setPositiveButton("Pedir", (dialog, which) -> {
                String quantityStr = quantityInput.getText().toString();
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    // TODO: Realizar pedido en el backend
                    Toast.makeText(getContext(), "Pedido realizado", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }
} 