package com.example.frontend.ui.farmer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.Product;
import com.example.frontend.ui.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FarmerProductsFragment extends Fragment {
    private static final String TAG = "FarmerProductsFragment";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> products;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando el fragmento");
        View view = inflater.inflate(R.layout.fragment_farmer_products, container, false);

        recyclerView = view.findViewById(R.id.products_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        products = new ArrayList<>();
        adapter = new ProductAdapter(product -> {
            // Aquí puedes navegar a un editor o mostrar un diálogo para modificar si lo deseas.
        });
        recyclerView.setAdapter(adapter);

        // Botón: Añadir producto
        view.findViewById(R.id.btn_add_product).setOnClickListener(v -> showAddProductDialog());

        // Botón: Dictado por voz (placeholder)
        view.findViewById(R.id.btn_voice_input).setOnClickListener(v ->
                Toast.makeText(getContext(), "Función de dictado próximamente", Toast.LENGTH_SHORT).show());

        // Botón: Escanear etiqueta (placeholder)
        view.findViewById(R.id.btn_scan_product).setOnClickListener(v ->
                Toast.makeText(getContext(), "Función de escáner próximamente", Toast.LENGTH_SHORT).show());

        loadSampleProducts();

        return view;
    }

    private void loadSampleProducts() {
        products.add(new Product("1", "Tomates Orgánicos", "Tomates frescos de cultivo ecológico", 2.99, 50, 6));
        products.add(new Product("2", "Lechuga", "Lechuga fresca de temporada", 1.99, 30, 20));
        products.add(new Product("3", "Zanahorias", "Zanahorias orgánicas", 1.50, 40, 10));
        products.add(new Product("4", "Manzanas", "Manzanas de producción local", 2.50, 60, 40));
        adapter.submitList(new ArrayList<>(products));
    }

    private void showAddProductDialog() {
        // Entradas de texto
        EditText inputName = new EditText(getContext());
        inputName.setHint("Nombre del producto");

        EditText inputPrice = new EditText(getContext());
        inputPrice.setHint("Precio por unidad (€)");
        inputPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        EditText inputStock = new EditText(getContext());
        inputStock.setHint("Stock disponible");
        inputStock.setInputType(InputType.TYPE_CLASS_NUMBER);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 24);
        layout.addView(inputName);
        layout.addView(inputPrice);
        layout.addView(inputStock);

        new AlertDialog.Builder(getContext())
                .setTitle("Añadir nuevo producto")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String name = inputName.getText().toString().trim();
                    String priceStr = inputPrice.getText().toString().trim();
                    String stockStr = inputStock.getText().toString().trim();

                    if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                        Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceStr);
                        int stock = Integer.parseInt(stockStr);

                        Product newProduct = new Product(
                                String.valueOf(products.size() + 1),
                                name,
                                "", // descripción vacía por ahora
                                price,
                                stock,
                                0 // distancia = 0 por defecto
                        );

                        products.add(newProduct);
                        adapter.submitList(new ArrayList<>(products));
                        Toast.makeText(getContext(), "Producto añadido", Toast.LENGTH_SHORT).show();

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Error en los valores numéricos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
