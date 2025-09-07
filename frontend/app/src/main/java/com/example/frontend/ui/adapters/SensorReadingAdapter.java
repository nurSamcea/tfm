package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.models.SensorReading;
import java.util.List;

public class SensorReadingAdapter extends RecyclerView.Adapter<SensorReadingAdapter.ReadingViewHolder> {
    
    private final List<SensorReading> readings;
    
    public SensorReadingAdapter(List<SensorReading> readings) {
        this.readings = readings;
    }
    
    @NonNull
    @Override
    public ReadingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sensor_reading, parent, false);
        return new ReadingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ReadingViewHolder holder, int position) {
        SensorReading reading = readings.get(position);
        holder.bind(reading);
    }
    
    @Override
    public int getItemCount() {
        return readings.size();
    }
    
    class ReadingViewHolder extends RecyclerView.ViewHolder {
        private final TextView textTimestamp;
        private final TextView textTemperature;
        private final TextView textHumidity;
        private final TextView textQuality;
        private final View qualityIndicator;
        
        public ReadingViewHolder(@NonNull View itemView) {
            super(itemView);
            textTimestamp = itemView.findViewById(R.id.text_timestamp);
            textTemperature = itemView.findViewById(R.id.text_temperature);
            textHumidity = itemView.findViewById(R.id.text_humidity);
            textQuality = itemView.findViewById(R.id.text_quality);
            qualityIndicator = itemView.findViewById(R.id.quality_indicator);
        }
        
        public void bind(SensorReading reading) {
            textTimestamp.setText(reading.getTimestampDisplay());
            textTemperature.setText(reading.getTemperatureDisplay());
            textHumidity.setText(reading.getHumidityDisplay());
            textQuality.setText(reading.getQualityDisplay());
            textQuality.setTextColor(reading.getQualityColor());
            qualityIndicator.setBackgroundColor(reading.getQualityColor());
        }
    }
}
