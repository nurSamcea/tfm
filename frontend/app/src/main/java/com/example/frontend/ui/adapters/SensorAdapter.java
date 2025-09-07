package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.models.Sensor;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {
    
    private final List<Sensor> sensors;
    private final OnSensorClickListener clickListener;
    
    public interface OnSensorClickListener {
        void onSensorClick(Sensor sensor);
    }
    
    public SensorAdapter(List<Sensor> sensors, OnSensorClickListener clickListener) {
        this.sensors = sensors;
        this.clickListener = clickListener;
    }
    
    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor_card, parent, false);
        return new SensorViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        Sensor sensor = sensors.get(position);
        holder.bind(sensor);
    }
    
    @Override
    public int getItemCount() {
        return sensors.size();
    }
    
    class SensorViewHolder extends RecyclerView.ViewHolder {
        private final TextView textSensorName;
        private final TextView textSensorType;
        private final TextView textStatus;
        private final TextView textLastSeen;
        private final View statusIndicator;
        
        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            textSensorName = itemView.findViewById(R.id.text_sensor_name);
            textSensorType = itemView.findViewById(R.id.text_sensor_type);
            textStatus = itemView.findViewById(R.id.text_status);
            textLastSeen = itemView.findViewById(R.id.text_last_seen);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onSensorClick(sensors.get(position));
                }
            });
        }
        
        public void bind(Sensor sensor) {
            textSensorName.setText(sensor.getName());
            textSensorType.setText(getSensorTypeDisplay(sensor.getSensorType()));
            textStatus.setText(sensor.getStatusDisplay());
            textStatus.setTextColor(sensor.getStatusColor());
            statusIndicator.setBackgroundColor(sensor.getStatusColor());
            
            if (sensor.getLastSeen() != null) {
                textLastSeen.setText("Última vez: " + formatLastSeen(sensor.getLastSeen()));
            } else {
                textLastSeen.setText("Nunca conectado");
            }
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
                    return minutes + " min";
                } else if (minutes < 1440) { // 24 horas
                    return (minutes / 60) + " h";
                } else {
                    return (minutes / 1440) + " días";
                }
            } catch (Exception e) {
                return "Desconocido";
            }
        }
    }
}
