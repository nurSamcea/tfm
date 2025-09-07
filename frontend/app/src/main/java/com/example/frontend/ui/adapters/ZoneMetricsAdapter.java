package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frontend.R;
import com.example.frontend.models.FarmerDashboard;
import java.util.ArrayList;
import java.util.List;

public class ZoneMetricsAdapter extends RecyclerView.Adapter<ZoneMetricsAdapter.ZoneViewHolder> {
    
    private List<FarmerDashboard.ZoneSummary> zones = new ArrayList<>();
    
    public void updateZones(List<FarmerDashboard.ZoneSummary> newZones) {
        this.zones = newZones != null ? newZones : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_zone_metrics, parent, false);
        return new ZoneViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder holder, int position) {
        FarmerDashboard.ZoneSummary zone = zones.get(position);
        holder.bind(zone);
    }
    
    @Override
    public int getItemCount() {
        return zones.size();
    }
    
    static class ZoneViewHolder extends RecyclerView.ViewHolder {
        private TextView tvZoneName, tvDescription, tvTotalSensors, tvOnlineSensors, tvOfflineSensors, tvActiveAlerts;
        
        public ZoneViewHolder(@NonNull View itemView) {
            super(itemView);
            tvZoneName = itemView.findViewById(R.id.tv_zone_name);
            tvDescription = itemView.findViewById(R.id.tv_zone_description);
            tvTotalSensors = itemView.findViewById(R.id.tv_total_sensors);
            tvOnlineSensors = itemView.findViewById(R.id.tv_online_sensors);
            tvOfflineSensors = itemView.findViewById(R.id.tv_offline_sensors);
            tvActiveAlerts = itemView.findViewById(R.id.tv_active_alerts);
        }
        
        public void bind(FarmerDashboard.ZoneSummary zone) {
            tvZoneName.setText(zone.zone_name);
            tvDescription.setText(zone.description);
            tvTotalSensors.setText(String.valueOf(zone.total_sensors));
            tvOnlineSensors.setText(String.valueOf(zone.online_sensors));
            tvOfflineSensors.setText(String.valueOf(zone.offline_sensors));
            tvActiveAlerts.setText(String.valueOf(zone.active_alerts));
            
            // Cambiar color de alertas según cantidad
            if (zone.active_alerts > 0) {
                tvActiveAlerts.setTextColor(itemView.getContext().getResources().getColor(R.color.error_color));
            } else {
                tvActiveAlerts.setTextColor(itemView.getContext().getResources().getColor(R.color.success_color));
            }
        }
    }
}
