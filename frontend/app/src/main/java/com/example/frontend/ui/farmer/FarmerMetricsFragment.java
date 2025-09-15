package com.example.frontend.ui.farmer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.frontend.R;
import com.example.frontend.models.FarmerDashboard;
import com.example.frontend.services.ApiClient;
import com.example.frontend.services.FarmerMetricsApiService;
import com.example.frontend.utils.RefreshConfig;
import com.example.frontend.utils.SessionManager;
 

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmerMetricsFragment extends Fragment {
    private static final String TAG = "FarmerMetricsFragment";
    
    // Eliminado SwipeRefreshLayout para simplificar el layout
    private TextView tvTotalZones, tvTotalSensors, tvOnlineSensors, tvOfflineSensors;
    private TextView tvAvgTemperature, tvAvgHumidity, tvAvgSoilMoisture, tvActiveAlerts;
    private TextView tvLastUpdate;
    private LinearLayout linearLayoutZones;
    
    private SessionManager sessionManager;
    
    private Handler autoRefreshHandler;
    private Runnable autoRefreshRunnable;
    private SimpleDateFormat timeFormat;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando FarmerMetricsFragment");
        View view = inflater.inflate(R.layout.fragment_farmer_metrics, container, false);
        
        sessionManager = new SessionManager(requireContext());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        autoRefreshHandler = new Handler(Looper.getMainLooper());
        
        initViews(view);
        // Swipe-to-refresh eliminado
        
        setupAutoRefresh();
        
        // Logs de debug
        logSessionInfo();
        logApiClientInfo();
        
        loadDashboard();
        
        return view;
    }
    
    private void logSessionInfo() {
        Log.d(TAG, "=== SESSION INFO ===");
        Log.d(TAG, "Is logged in: " + sessionManager.isLoggedIn());
        Log.d(TAG, "User ID: " + sessionManager.getUserId());
        Log.d(TAG, "User role: " + sessionManager.getUserRole());
        Log.d(TAG, "User name: " + sessionManager.getUserName());
        Log.d(TAG, "Token available: " + (sessionManager.getToken() != null));
        Log.d(TAG, "Full token: " + sessionManager.getFullToken());
        Log.d(TAG, "===================");
    }
    
    private void logApiClientInfo() {
        Log.d(TAG, "=== API CLIENT INFO ===");
        try {
            String baseUrl = com.example.frontend.utils.Constants.getBaseUrl();
            Log.d(TAG, "Base URL: " + baseUrl);
        } catch (Exception e) {
            Log.e(TAG, "Error getting base URL: " + e.getMessage());
        }
        Log.d(TAG, "======================");
    }
    
    private void initViews(View view) {
        // Contenedor raíz simple; ya no se usa SwipeRefreshLayout
        
        // Métricas principales
        tvTotalZones = view.findViewById(R.id.tv_total_zones);
        tvTotalSensors = view.findViewById(R.id.tv_total_sensors);
        tvOnlineSensors = view.findViewById(R.id.tv_online_sensors);
        tvOfflineSensors = view.findViewById(R.id.tv_offline_sensors);
        
        // Promedios
        tvAvgTemperature = view.findViewById(R.id.tv_avg_temperature);
        tvAvgHumidity = view.findViewById(R.id.tv_avg_humidity);
        tvAvgSoilMoisture = view.findViewById(R.id.tv_avg_soil_moisture);
        tvActiveAlerts = view.findViewById(R.id.tv_active_alerts);
        
        // Última actualización
        tvLastUpdate = view.findViewById(R.id.tv_last_update);
        
        // Lista de zonas
        linearLayoutZones = view.findViewById(R.id.linear_layout_zones);
        
        
    }
    
    // Swipe-to-refresh eliminado
    
    private void setupAutoRefresh() {
        if (!RefreshConfig.isAutoRefreshEnabled()) {
            Log.d(TAG, "Auto-refresh: Deshabilitado por configuración");
            return;
        }
        
        autoRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Auto-refresh: Actualizando datos automáticamente");
                loadDashboard();
                // Programar la siguiente actualización usando la configuración
                if (RefreshConfig.isAutoRefreshEnabled()) {
                    autoRefreshHandler.postDelayed(this, RefreshConfig.getRefreshInterval());
                }
            }
        };
        
        // Iniciar la actualización automática después del primer delay
        autoRefreshHandler.postDelayed(autoRefreshRunnable, RefreshConfig.getRefreshInterval());
    }
    
    private void updateLastUpdateTime() {
        String currentTime = timeFormat.format(new Date());
        tvLastUpdate.setText("Última actualización: " + currentTime);
    }
    
    private void stopAutoRefresh() {
        if (autoRefreshHandler != null && autoRefreshRunnable != null) {
            autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
        }
    }
    
    /**
     * Actualiza la configuración de actualización automática.
     */
    public void updateRefreshConfig(boolean enabled, int intervalSeconds) {
        RefreshConfig.setAutoRefreshEnabled(enabled);
        RefreshConfig.setRefreshIntervalSeconds(intervalSeconds);
        
        // Reiniciar la actualización automática con la nueva configuración
        stopAutoRefresh();
        if (enabled) {
            setupAutoRefresh();
        }
        
        Log.d(TAG, "Configuración actualizada - Habilitado: " + enabled + 
              ", Intervalo: " + RefreshConfig.getIntervalDescription());
    }
    
    
    
    private void loadDashboard() {
        Log.d(TAG, "loadDashboard: Iniciando carga de dashboard");
        // Indicador de refresco eliminado
        
        FarmerMetricsApiService apiService = ApiClient.getFarmerMetricsApiService();
        Log.d(TAG, "loadDashboard: ApiService creado: " + (apiService != null));
        
        int farmerId = sessionManager.getUserId();
        Log.d(TAG, "loadDashboard: Farmer ID: " + farmerId);
        
        Call<FarmerDashboard> call = apiService.getFarmerDashboard(farmerId);
        Log.d(TAG, "loadDashboard: Call creado: " + (call != null));
        
        call.enqueue(new Callback<FarmerDashboard>() {
            @Override
            public void onResponse(Call<FarmerDashboard> call, Response<FarmerDashboard> response) {
                Log.d(TAG, "onResponse: Respuesta recibida");
                Log.d(TAG, "onResponse: Código: " + response.code());
                Log.d(TAG, "onResponse: Exitoso: " + response.isSuccessful());
                Log.d(TAG, "onResponse: Body: " + (response.body() != null));
                
                // Indicador de refresco eliminado
                
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "onResponse: Actualizando dashboard");
                    updateDashboard(response.body());
                    updateLastUpdateTime();
                } else {
                    Log.e(TAG, "onResponse: Error en respuesta - Código: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "onResponse: Error body: " + errorBody);
                        } catch (Exception e) {
                            Log.e(TAG, "onResponse: Error leyendo error body: " + e.getMessage());
                        }
                    }
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error al cargar métricas: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            
            @Override
            public void onFailure(Call<FarmerDashboard> call, Throwable t) {
                Log.e(TAG, "onFailure: Error en la llamada", t);
                // Indicador de refresco eliminado
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    private void updateDashboard(FarmerDashboard dashboard) {
        Log.d(TAG, "updateDashboard: Actualizando UI con datos");
        Log.d(TAG, "updateDashboard: Total zonas: " + dashboard.total_zones);
        Log.d(TAG, "updateDashboard: Total sensores: " + dashboard.total_sensors);
        Log.d(TAG, "updateDashboard: Zonas en lista: " + dashboard.zones.size());
        
        // Actualizar métricas principales
        tvTotalZones.setText(String.valueOf(dashboard.total_zones));
        tvTotalSensors.setText(String.valueOf(dashboard.total_sensors));
        tvOnlineSensors.setText(String.valueOf(dashboard.online_sensors));
        tvOfflineSensors.setText(String.valueOf(dashboard.offline_sensors));
        
        // Actualizar promedios
        tvAvgTemperature.setText(String.format("%.1f°C", dashboard.summary.avg_temperature));
        tvAvgHumidity.setText(String.format("%.1f%%", dashboard.summary.avg_humidity));
        tvAvgSoilMoisture.setText(String.format("%.1f%%", dashboard.summary.avg_soil_moisture));
        tvActiveAlerts.setText(String.valueOf(dashboard.summary.active_alerts));
        
        // Actualizar lista de zonas con cards individuales
        updateZonesCards(dashboard.zones);
    }
    
    private void updateZonesCards(java.util.List<FarmerDashboard.ZoneSummary> zones) {
        Log.d(TAG, "updateZonesCards: Actualizando " + zones.size() + " zonas");
        
        // Verificar que el contexto esté disponible
        if (getContext() == null) {
            Log.e(TAG, "updateZonesCards: Context es null, no se pueden crear las cards");
            return;
        }
        
        // Limpiar layout anterior
        linearLayoutZones.removeAllViews();
        
        for (FarmerDashboard.ZoneSummary zone : zones) {
            Log.d(TAG, "updateZonesCards: Creando card para zona: " + zone.zone_name);
            
            View zoneCard = LayoutInflater.from(requireContext()).inflate(R.layout.item_zone_card, linearLayoutZones, false);
            
            // Configurar datos de la zona
            TextView tvZoneName = zoneCard.findViewById(R.id.tv_zone_name);
            TextView tvZoneSensors = zoneCard.findViewById(R.id.tv_zone_sensors);
            TextView tvZoneOnline = zoneCard.findViewById(R.id.tv_zone_online);
            TextView tvZoneAlerts = zoneCard.findViewById(R.id.tv_zone_alerts);
            TextView tvZoneTemperature = zoneCard.findViewById(R.id.tv_zone_temperature);
            TextView tvZoneHumidity = zoneCard.findViewById(R.id.tv_zone_humidity);
            TextView tvZoneSoilMoisture = zoneCard.findViewById(R.id.tv_zone_soil_moisture);
            
            tvZoneName.setText(zone.zone_name);
            tvZoneSensors.setText(String.valueOf(zone.total_sensors));
            tvZoneOnline.setText(String.valueOf(zone.online_sensors));
            tvZoneAlerts.setText(String.valueOf(zone.active_alerts));
            
            // Mostrar métricas ambientales reales
            tvZoneTemperature.setText(String.format("%.1f°C", zone.avg_temperature));
            tvZoneHumidity.setText(String.format("%.1f%%", zone.avg_humidity));
            tvZoneSoilMoisture.setText(String.format("%.1f%%", zone.avg_soil_moisture));
            
            // Agregar card al layout
            linearLayoutZones.addView(zoneCard);
        }
        
        Log.d(TAG, "updateZonesCards: Cards creadas exitosamente");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Reanudando actualización automática");
        // Reiniciar la actualización automática cuando el fragment se vuelve visible
        if (autoRefreshHandler != null && autoRefreshRunnable != null && RefreshConfig.isAutoRefreshEnabled()) {
            autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
            autoRefreshHandler.postDelayed(autoRefreshRunnable, RefreshConfig.getRefreshInterval());
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Pausando actualización automática");
        // Pausar la actualización automática cuando el fragment no es visible
        stopAutoRefresh();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: Limpiando recursos");
        // Limpiar recursos cuando se destruye la vista
        stopAutoRefresh();
    }
}