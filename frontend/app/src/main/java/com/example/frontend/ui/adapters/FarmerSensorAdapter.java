package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.FarmerSensorReading;

import java.util.List;

public class FarmerSensorAdapter extends RecyclerView.Adapter<FarmerSensorAdapter.SensorViewHolder> {

    public interface OnSensorClickListener {
        void onClick(FarmerSensorReading sensor);
    }

    private final List<FarmerSensorReading> sensorList;
    private final OnSensorClickListener listener;

    public FarmerSensorAdapter(List<FarmerSensorReading> sensorList, OnSensorClickListener listener) {
        this.sensorList = sensorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_farmer_sensor, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        FarmerSensorReading sensor = sensorList.get(position);
        holder.name.setText(sensor.getName());
        holder.value.setText(sensor.getValue());
        holder.moreButton.setOnClickListener(v -> listener.onClick(sensor));
    }

    @Override
    public int getItemCount() {
        return sensorList.size();
    }

    static class SensorViewHolder extends RecyclerView.ViewHolder {
        TextView name, value;
        Button moreButton;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sensor_name);
            value = itemView.findViewById(R.id.sensor_value);
            moreButton = itemView.findViewById(R.id.sensor_more_button);
        }
    }
}
