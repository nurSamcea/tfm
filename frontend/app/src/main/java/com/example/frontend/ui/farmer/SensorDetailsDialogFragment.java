package com.example.frontend.ui.farmer;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.models.Sensor;
import com.example.frontend.models.SensorReading;
import com.example.frontend.models.SensorAlert;
import com.example.frontend.services.ApiClient;
import com.example.frontend.services.SensorApiService;
import com.example.frontend.ui.adapters.SensorReadingAdapter;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SensorDetailsDialogFragment extends DialogFragment {
    
    private Sensor sensor;
    private SensorApiService sensorApiService;
    private List<SensorReading> recentReadings = new ArrayList<>();
    private List<SensorAlert> sensorAlerts = new ArrayList<>();
    
    // Views
    private TextView textSensorName;
    private TextView textSensorType;
    private TextView textStatus;
    private TextView textLocation;
    private TextView textLastSeen;
    private TextView textFirmwareVersion;
    private RecyclerView recyclerReadings;
    private RecyclerView recyclerAlerts;
    private Button btnClose;
    private Button btnRefresh;
    
    public SensorDetailsDialogFragment(Sensor sensor) {
        this.sensor = sensor;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
        sensorApiService = ApiClient.getClient().create(SensorApiService.class);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sensor_details, container, false);
        
        initViews(view);
        setupRecyclerViews();
        loadSensorData();
        
        return view;
    }
    
    private void initViews(View view) {
        textSensorName = view.findViewById(R.id.text_sensor_name);
        textSensorType = view.findViewById(R.id.text_sensor_type);
        textStatus = view.findViewById(R.id.text_status);
        textLocation = view.findViewById(R.id.text_location);
        textLastSeen = view.findViewById(R.id.text_last_seen);
        textFirmwareVersion = view.findViewById(R.id.text_firmware_version);
        recyclerReadings = view.findViewById(R.id.recycler_readings);
        recyclerAlerts = view.findViewById(R.id.recycler_alerts);
        btnClose = view.findViewById(R.id.btn_close);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        
        btnClose.setOnClickListener(v -> dismiss());
        btnRefresh.setOnClickListener(v -> loadSensorData());
        
        // Configurar datos del sensor
        textSensorName.setText(sensor.getName());
        textSensorType.setText(getSensorTypeDisplay(sensor.getSensorType()));
        textStatus.setText(sensor.getStatusDisplay());
        textStatus.setTextColor(sensor.getStatusColor());
        textLocation.setText(sensor.getLocationDescription() != null ? 
            sensor.getLocationDescription() : "Ubicación no especificada");
        textLastSeen.setText(sensor.getLastSeen() != null ? 
            formatLastSeen(sensor.getLastSeen()) : "Nunca conectado");
        textFirmwareVersion.setText(sensor.getFirmwareVersion() != null ? 
            sensor.getFirmwareVersion() : "Desconocida");
    }
    
    private void setupRecyclerViews() {
        // RecyclerView para lecturas recientes
        recyclerReadings.setLayoutManager(new LinearLayoutManager(getContext()));
        SensorReadingAdapter readingsAdapter = new SensorReadingAdapter(recentReadings);
        recyclerReadings.setAdapter(readingsAdapter);
        
        // RecyclerView para alertas
        recyclerAlerts.setLayoutManager(new LinearLayoutManager(getContext()));
        // TODO: Crear adapter para alertas
    }
    
    private void loadSensorData() {
        // Cargar lecturas recientes
        Call<List<SensorReading>> readingsCall = sensorApiService.getSensorReadings(
            sensor.getId(), 10, 24 // 10 lecturas de las últimas 24 horas
        );
        
        readingsCall.enqueue(new Callback<List<SensorReading>>() {
            @Override
            public void onResponse(Call<List<SensorReading>> call, Response<List<SensorReading>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recentReadings.clear();
                    recentReadings.addAll(response.body());
                    recyclerReadings.getAdapter().notifyDataSetChanged();
                }
            }
            
            @Override
            public void onFailure(Call<List<SensorReading>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar lecturas", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Cargar alertas del sensor
        Call<List<SensorAlert>> alertsCall = sensorApiService.getSensorAlerts(
            "active", null, 168 // Alertas activas de la última semana
        );
        
        alertsCall.enqueue(new Callback<List<SensorAlert>>() {
            @Override
            public void onResponse(Call<List<SensorAlert>> call, Response<List<SensorAlert>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sensorAlerts.clear();
                    // Filtrar alertas de este sensor
                    for (SensorAlert alert : response.body()) {
                        if (alert.getSensorId() == sensor.getId()) {
                            sensorAlerts.add(alert);
                        }
                    }
                    // TODO: Actualizar adapter de alertas
                }
            }
            
            @Override
            public void onFailure(Call<List<SensorAlert>> call, Throwable t) {
                // Error silencioso para alertas
            }
        });
    }
    
    private String getSensorTypeDisplay(String sensorType) {
        if (sensorType == null) return "Desconocido";
        
        switch (sensorType.toLowerCase()) {
            case "temperature":
                return "Temperatura";
            case "humidity":
                return "Humedad";
            case "temperature_humidity":
                return "Temperatura/Humedad";
            case "gas":
                return "Gas";
            case "light":
                return "Luz";
            case "soil_moisture":
                return "Humedad del Suelo";
            case "ph":
                return "pH";
            default:
                return sensorType;
        }
    }
    
    private String formatLastSeen(String lastSeen) {
        try {
            java.time.Instant instant = java.time.Instant.parse(lastSeen);
            java.time.LocalDateTime localDateTime = java.time.LocalDateTime.ofInstant(
                instant, java.time.ZoneId.systemDefault()
            );
            java.time.Duration duration = java.time.Duration.between(
                localDateTime, java.time.LocalDateTime.now()
            );
            
            long minutes = duration.toMinutes();
            if (minutes < 1) {
                return "Ahora";
            } else if (minutes < 60) {
                return minutes + " minutos";
            } else if (minutes < 1440) { // 24 horas
                return (minutes / 60) + " horas";
            } else {
                return (minutes / 1440) + " días";
            }
        } catch (Exception e) {
            return "Desconocido";
        }
    }
}
