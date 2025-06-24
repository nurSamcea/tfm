package com.example.frontend.ui.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.FarmerForecastItem;
import com.example.frontend.model.FarmerProduct;
import com.example.frontend.ui.adapters.FarmerStockAdapter;

import java.util.ArrayList;
import java.util.List;

public class FarmerStockFragment extends Fragment {

    private RecyclerView recyclerStock;
    private RecyclerView recyclerForecast;
    private FarmerStockAdapter stockAdapter;
    private List<FarmerProduct> stockList;
    private List<FarmerForecastItem> forecastList;

    private EditText searchInput;
    private ImageButton filterButton;
    private Button addProductButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_stock, container, false);

        searchInput = view.findViewById(R.id.search_stock);
        filterButton = view.findViewById(R.id.filter_button);
        addProductButton = view.findViewById(R.id.add_product_button);

        recyclerStock = view.findViewById(R.id.recycler_stock);

        // Datos simulados
        stockList = new ArrayList<>();
        stockList.add(new FarmerProduct("Tomate Cherry", true, "2 kg", "2.50 €/kg", "22/06"));
        stockList.add(new FarmerProduct("Lechuga Romana", false, "4 cajas", "1.10 €/unidad", "21/06"));
        stockList.add(new FarmerProduct("Berenjena Ecológica", false,"10 kg", "2.20€/kg",  "27/08"));
        stockList.add(new FarmerProduct("Pepino", false, "8 kg", "1.00€/kg","28/08"));

        // Adaptadores
        stockAdapter = new FarmerStockAdapter(stockList);
        recyclerStock.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerStock.setAdapter(stockAdapter);


        return view;
    }
}
