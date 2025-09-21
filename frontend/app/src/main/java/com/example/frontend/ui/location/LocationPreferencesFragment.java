package com.example.frontend.ui.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Fragment para gestionar las preferencias de ubicación del usuario.
 * Permite configurar la distancia máxima y otras opciones de ubicación.
 */
public class LocationPreferencesFragment extends Fragment {
    private static final String PREFS_NAME = "LocationPreferences";
    private static final String KEY_MAX_DISTANCE = "max_distance";
    private static final String KEY_SHOW_DISTANCE = "show_distance";
    private static final String KEY_SORT_BY_DISTANCE = "sort_by_distance";
    private static final double DEFAULT_MAX_DISTANCE = 10.0; // 10 km por defecto

    private SeekBar distanceSeekBar;
    private TextView distanceText;
    private SwitchMaterial showDistanceSwitch;
    private SwitchMaterial sortByDistanceSwitch;
    private Button saveButton;
    private Button cancelButton;

    private OnPreferencesSavedListener listener;

    /**
     * Interfaz para recibir callbacks cuando se guardan las preferencias.
     */
    public interface OnPreferencesSavedListener {
        void onPreferencesSaved(double maxDistanceKm, boolean showDistance, boolean sortByDistance);
        void onPreferencesCancelled();
    }

    /**
     * Obtiene la distancia máxima configurada por el usuario.
     * 
     * @param context Contexto de la aplicación
     * @return Distancia máxima en kilómetros
     */
    public static double getMaxDistance(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getFloat(KEY_MAX_DISTANCE, (float) DEFAULT_MAX_DISTANCE);
    }

    /**
     * Establece la distancia máxima para el usuario.
     * 
     * @param context Contexto de la aplicación
     * @param maxDistance Distancia máxima en kilómetros
     */
    public static void setMaxDistance(Context context, double maxDistance) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putFloat(KEY_MAX_DISTANCE, (float) maxDistance).apply();
    }

    /**
     * Obtiene la distancia máxima por defecto.
     * 
     * @return Distancia máxima por defecto en kilómetros
     */
    public static double getDefaultMaxDistance() {
        return DEFAULT_MAX_DISTANCE;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_preferences, container, false);
        
        initViews(view);
        loadCurrentPreferences();
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        distanceSeekBar = view.findViewById(R.id.distance_seekbar);
        distanceText = view.findViewById(R.id.distance_text);
        showDistanceSwitch = view.findViewById(R.id.show_distance_switch);
        sortByDistanceSwitch = view.findViewById(R.id.sort_by_distance_switch);
        saveButton = view.findViewById(R.id.save_button);
        cancelButton = view.findViewById(R.id.cancel_button);
    }

    private void loadCurrentPreferences() {
        Context context = requireContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Cargar distancia máxima (0-50 km, valor por defecto 10 km)
        double currentDistance = prefs.getFloat(KEY_MAX_DISTANCE, (float) DEFAULT_MAX_DISTANCE);
        int seekBarValue = (int) (currentDistance * 2); // 0-100 para 0-50 km
        distanceSeekBar.setProgress(seekBarValue);
        updateDistanceText(currentDistance);
        
        // Cargar otras preferencias
        showDistanceSwitch.setChecked(prefs.getBoolean(KEY_SHOW_DISTANCE, true));
        sortByDistanceSwitch.setChecked(prefs.getBoolean(KEY_SORT_BY_DISTANCE, true));
    }

    private void setupListeners() {
        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double distance = progress / 2.0; // Convertir a km
                updateDistanceText(distance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        saveButton.setOnClickListener(v -> savePreferences());
        cancelButton.setOnClickListener(v -> cancelPreferences());
    }

    private void updateDistanceText(double distance) {
        distanceText.setText(String.format("%.1f km", distance));
    }

    private void savePreferences() {
        Context context = requireContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Obtener valores actuales
        double maxDistance = distanceSeekBar.getProgress() / 2.0;
        boolean showDistance = showDistanceSwitch.isChecked();
        boolean sortByDistance = sortByDistanceSwitch.isChecked();
        
        // Guardar en SharedPreferences
        prefs.edit()
                .putFloat(KEY_MAX_DISTANCE, (float) maxDistance)
                .putBoolean(KEY_SHOW_DISTANCE, showDistance)
                .putBoolean(KEY_SORT_BY_DISTANCE, sortByDistance)
                .apply();
        
        // Notificar al listener
        if (listener != null) {
            listener.onPreferencesSaved(maxDistance, showDistance, sortByDistance);
        }
        
        Toast.makeText(context, "Preferencias guardadas", Toast.LENGTH_SHORT).show();
        
        // Volver atrás
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    private void cancelPreferences() {
        if (listener != null) {
            listener.onPreferencesCancelled();
        }
        
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    public void setOnPreferencesSavedListener(OnPreferencesSavedListener listener) {
        this.listener = listener;
    }
}
