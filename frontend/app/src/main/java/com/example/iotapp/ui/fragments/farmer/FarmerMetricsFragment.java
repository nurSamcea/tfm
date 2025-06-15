package com.example.iotapp.ui.fragments.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class FarmerMetricsFragment extends Fragment {
    private LineChart waterChart;
    private PieChart chartCarbonFootprint;
    private TextView chartPesticideUsage;
    private TextView chartSoilHealth;
    private TextView chartBiodiversity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_metrics, container, false);
        
        // Inicializar vistas
        waterChart = view.findViewById(R.id.chartWaterUsage);
        chartCarbonFootprint = view.findViewById(R.id.chartCarbonFootprint);
        chartPesticideUsage = view.findViewById(R.id.chartPesticideUsage);
        chartSoilHealth = view.findViewById(R.id.chartSoilHealth);
        chartBiodiversity = view.findViewById(R.id.chartBiodiversity);
        
        // Configurar gráficos
        setupCharts();
        
        // Cargar datos
        loadMetrics();
        
        return view;
    }
    
    private void setupCharts() {
        // Configurar gráfico de ventas
        waterChart.getDescription().setText("Ventas por mes");
        waterChart.setDrawGridBackground(false);
        waterChart.getAxisRight().setEnabled(false);
        
        // Configurar gráfico de productos
        chartCarbonFootprint.getDescription().setText("Distribución de productos");
        chartCarbonFootprint.setDrawHoleEnabled(true);
        chartCarbonFootprint.setHoleColor(android.R.color.transparent);
    }
    
    private void loadMetrics() {
        // TODO: Implementar carga de datos desde el backend
        // Por ahora, añadimos datos de ejemplo
        
        // Datos de ventas
        List<Entry> salesEntries = new ArrayList<>();
        salesEntries.add(new Entry(0, 1000));
        salesEntries.add(new Entry(1, 1500));
        salesEntries.add(new Entry(2, 2000));
        salesEntries.add(new Entry(3, 1800));

        LineDataSet dataSet = new LineDataSet(salesEntries, "Ventas (€)");
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false); // opcional
        dataSet.setColor(getResources().getColor(R.color.teal_700, null));
        dataSet.setCircleColor(getResources().getColor(R.color.teal_700, null));

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        LineData salesData = new LineData(dataSets);
        waterChart.setData(salesData);
        waterChart.invalidate();

        //TODO: Crear LineDataSet con los datos
        waterChart.setData(salesData);
        waterChart.invalidate();
        
        // Datos de productos
        // TODO: Crear PieData con la distribución de productos
        
        // Actualizar textos
        chartPesticideUsage.setText("Total ventas: €6,300");
        chartSoilHealth.setText("Total pedidos: 45");
        chartBiodiversity.setText("Valoración media: 4.5/5");
    }
} 