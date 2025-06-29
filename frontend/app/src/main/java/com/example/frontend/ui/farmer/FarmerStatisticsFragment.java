package com.example.frontend.ui.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.FarmerSensorReading;
import com.example.frontend.ui.adapters.FarmerSensorAdapter;
import com.example.frontend.ui.adapters.FarmerStatsAdapter;
import com.example.frontend.model.StatsItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FarmerStatisticsFragment extends Fragment {

    private RecyclerView recyclerSensors, recyclerStats;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_statistics, container, false);

        recyclerSensors = view.findViewById(R.id.recycler_sensors);
        recyclerStats = view.findViewById(R.id.recycler_stats);

        setupSensorList();
        setupStatsList();

        return view;
    }

    private void setupSensorList() {
        recyclerSensors.setLayoutManager(
            new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        List<FarmerSensorReading> sensors = Arrays.asList(
                new FarmerSensorReading("Temp. Invernadero", "24.6 °C", "Rango ideal: 22-26 °C"),
                new FarmerSensorReading("Humedad Suelo (Parcela 1)", "68%", "Rango ideal: 50-70%"),
                new FarmerSensorReading("Luminosidad", "450 lx", "Nivel medio de luz")
        );
        FarmerSensorAdapter adapter = new FarmerSensorAdapter(sensors, this::showSensorDetails);
        recyclerSensors.setAdapter(adapter);
    }

    private void setupStatsList() {
        List<StatsItem> statsItems = new ArrayList<>();
        statsItems.add(StatsItem.createGraph());
        statsItems.add(StatsItem.createKpi("Producto más vendido", "Tomate Cherry"));
        statsItems.add(StatsItem.createKpi("Ingresos esta semana", "134,50 €"));
        statsItems.add(StatsItem.createKpi("Pedidos entregados", "9"));

        FarmerStatsAdapter statsAdapter = new FarmerStatsAdapter(statsItems);
        recyclerStats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerStats.setAdapter(statsAdapter);
    }

    private void showSensorDetails(FarmerSensorReading sensor) {
        new FarmerSensorDetailsDialogFragment(sensor).show(getParentFragmentManager(), "sensor_detail");
    }
}
