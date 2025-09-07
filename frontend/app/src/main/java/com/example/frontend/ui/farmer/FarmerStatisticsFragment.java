package com.example.frontend.ui.farmer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.frontend.R;
import com.example.frontend.model.FarmerSensorReading;
import com.example.frontend.models.Sensor;
import com.example.frontend.models.SensorReading;
import com.example.frontend.models.SensorAlert;
import com.example.frontend.models.SensorZone;
import com.example.frontend.ui.adapters.FarmerSensorAdapter;
import com.example.frontend.ui.adapters.FarmerStatsAdapter;
import com.example.frontend.ui.adapters.SensorAdapter;
import com.example.frontend.model.StatsItem;
import com.example.frontend.services.ApiClient;
import com.example.frontend.services.SensorApiService;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmerStatisticsFragment extends Fragment {
    private static final String TAG = "FarmerStatisticsFragment";

    private RecyclerView recyclerSensors, recyclerStats;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SensorApiService sensorApiService;
    private List<Sensor> sensors = new ArrayList<>();
    private List<SensorAlert> activeAlerts = new ArrayList<>();
    private SensorAdapter sensorAdapter;
    private FarmerStatsAdapter statsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando la aplicación");
        View view = inflater.inflate(R.layout.fragment_farmer_statistics, container, false);

        recyclerSensors = view.findViewById(R.id.recycler_sensors);
        recyclerStats = view.findViewById(R.id.recycler_stats);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

        // Inicializar servicio API sin autenticación (como en productos)
        sensorApiService = ApiClient.getClient().create(SensorApiService.class);

        setupSwipeRefresh();
        setupSensorList();
        setupStatsList();
        loadSensorData();

        return view;
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadSensorData);
        swipeRefreshLayout.setColorSchemeResources(
            R.color.primary_color,
            R.color.accent_color
        );
    }

    private void setupSensorList() {
        recyclerSensors.setLayoutManager(
            new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        sensorAdapter = new SensorAdapter(sensors, this::showSensorDetails);
        recyclerSensors.setAdapter(sensorAdapter);
    }

    private void setupStatsList() {
        List<StatsItem> statsItems = new ArrayList<>();
        statsItems.add(StatsItem.createGraph());
        statsItems.add(StatsItem.createKpi("Sensores Online", "0/0"));
        statsItems.add(StatsItem.createKpi("Alertas Activas", "0"));
        statsItems.add(StatsItem.createKpi("Temperatura Promedio", "N/A"));
        statsItems.add(StatsItem.createKpi("Humedad Promedio", "N/A"));

        statsAdapter = new FarmerStatsAdapter(statsItems);
        recyclerStats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerStats.setAdapter(statsAdapter);
    }

    private void loadSensorData() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        // Cargar sensores
        Call<List<Sensor>> sensorsCall = sensorApiService.getSensors(null, null);
        try {
            android.util.Log.d("FarmerStats", "Request URL (Retrofit): " + sensorsCall.request().url());
        } catch (Exception ignored) {}
        sensorsCall.enqueue(new Callback<List<Sensor>>() {
            @Override
            public void onResponse(Call<List<Sensor>> call, Response<List<Sensor>> response) {
                android.util.Log.d("FarmerStats", "Respuesta sensores: code=" + response.code() + " message=" + response.message());
                if (response.isSuccessful() && response.body() != null) {
                    sensors.clear();
                    sensors.addAll(response.body());
                    sensorAdapter.notifyDataSetChanged();
                    updateStats();
                } else {
                    Toast.makeText(getContext(), "Error al cargar sensores (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    try {
                        android.util.Log.e("FarmerStats", "Sensors error " + response.code() + ": " + (response.errorBody() != null ? response.errorBody().string() : "sin body"));
                    } catch (Exception ignored) {}
                    if (response.code() == 401) {
                        // reintentar contra endpoint público de demo
                        Call<List<Sensor>> publicCall = ApiClient.getClient().create(SensorApiService.class)
                                .getSensorsPublic();
                        publicCall.enqueue(new Callback<List<Sensor>>() {
                            @Override
                            public void onResponse(Call<List<Sensor>> call2, Response<List<Sensor>> resp2) {
                                if (resp2.isSuccessful() && resp2.body() != null) {
                                    sensors.clear();
                                    sensors.addAll(resp2.body());
                                    sensorAdapter.notifyDataSetChanged();
                                    updateStats();
                                }
                                loadActiveAlerts();
                            }
                            @Override
                            public void onFailure(Call<List<Sensor>> call2, Throwable t2) {
                                loadActiveAlerts();
                            }
                        });
                        return;
                    }
                }
                loadActiveAlerts();
            }

            @Override
            public void onFailure(Call<List<Sensor>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                android.util.Log.e("FarmerStats", "Sensors failure URL=" + call.request().url(), t);
                loadActiveAlerts();
            }
        });
    }

    private void loadActiveAlerts() {
        Call<List<SensorAlert>> alertsCall = sensorApiService.getActiveAlerts();
        alertsCall.enqueue(new Callback<List<SensorAlert>>() {
            @Override
            public void onResponse(Call<List<SensorAlert>> call, Response<List<SensorAlert>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activeAlerts.clear();
                    activeAlerts.addAll(response.body());
                    updateStats();
                } else if (response.code() == 401) {
                    // fallback a endpoint público
                    ApiClient.getClient().create(SensorApiService.class)
                        .getActiveAlertsPublic()
                        .enqueue(new Callback<List<SensorAlert>>() {
                            @Override
                            public void onResponse(Call<List<SensorAlert>> call2, Response<List<SensorAlert>> resp2) {
                                if (resp2.isSuccessful() && resp2.body() != null) {
                                    activeAlerts.clear();
                                    activeAlerts.addAll(resp2.body());
                                    updateStats();
                                }
                                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                            }
                            @Override
                            public void onFailure(Call<List<SensorAlert>> call2, Throwable t2) {
                                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    return;
                }
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<SensorAlert>> call, Throwable t) {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void updateStats() {
        if (statsAdapter == null) return;

        // Calcular estadísticas
        int onlineSensors = 0;
        int totalSensors = sensors.size();
        double totalTemp = 0;
        double totalHumidity = 0;
        int tempReadings = 0;
        int humidityReadings = 0;

        for (Sensor sensor : sensors) {
            if (sensor.isOnline()) {
                onlineSensors++;
            }
        }

        // Actualizar estadísticas en el adapter
        List<StatsItem> statsItems = new ArrayList<>();
        statsItems.add(StatsItem.createGraph());
        statsItems.add(StatsItem.createKpi("Sensores Online", onlineSensors + "/" + totalSensors));
        statsItems.add(StatsItem.createKpi("Alertas Activas", String.valueOf(activeAlerts.size())));
        
        if (tempReadings > 0) {
            statsItems.add(StatsItem.createKpi("Temperatura Promedio", String.format("%.1f°C", totalTemp / tempReadings)));
        } else {
            statsItems.add(StatsItem.createKpi("Temperatura Promedio", "N/A"));
        }
        
        if (humidityReadings > 0) {
            statsItems.add(StatsItem.createKpi("Humedad Promedio", String.format("%.1f%%", totalHumidity / humidityReadings)));
        } else {
            statsItems.add(StatsItem.createKpi("Humedad Promedio", "N/A"));
        }

        statsAdapter.updateStats(statsItems);
    }

    private void showSensorDetails(Sensor sensor) {
        // Crear diálogo con detalles del sensor
        new SensorDetailsDialogFragment(sensor).show(getParentFragmentManager(), "sensor_detail");
    }
}
