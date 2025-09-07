package com.example.frontend.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.StatsItem;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class FarmerStatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<StatsItem> items;
    private static final int TYPE_GRAPH = 0;
    private static final int TYPE_KPI = 1;

    public FarmerStatsAdapter(List<StatsItem> items) {
        this.items = items;
    }
    
    public void updateStats(List<StatsItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType() == StatsItem.Type.GRAPH ? TYPE_GRAPH : TYPE_KPI;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_GRAPH) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stats_graph, parent, false);
            return new GraphViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stat_card, parent, false);
            return new KpiViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_GRAPH) {
            ((GraphViewHolder) holder).bind();
        } else {
            ((KpiViewHolder) holder).bind(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class GraphViewHolder extends RecyclerView.ViewHolder {
        BarChart barChart;
        GraphViewHolder(@NonNull View itemView) {
            super(itemView);
            barChart = itemView.findViewById(R.id.bar_chart);
        }
        void bind() {
            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0, 12f));
            entries.add(new BarEntry(1, 8f));
            entries.add(new BarEntry(2, 14f));
            entries.add(new BarEntry(3, 10f));
            entries.add(new BarEntry(4, 9f));
            entries.add(new BarEntry(5, 7f));
            entries.add(new BarEntry(6, 13f));

            BarDataSet dataSet = new BarDataSet(entries, "Ingresos diarios");
            dataSet.setColor(Color.parseColor("#3F51B5"));
            dataSet.setValueTextColor(Color.BLACK);
            dataSet.setValueTextSize(12f);

            BarData data = new BarData(dataSet);
            barChart.setData(data);

            String[] days = {"L", "M", "X", "J", "V", "S", "D"};
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return days[(int) value % days.length];
                }
            });
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);

            YAxis leftAxis = barChart.getAxisLeft();
            barChart.getAxisRight().setEnabled(false);
            barChart.getDescription().setEnabled(false);
            barChart.invalidate();
        }
    }

    static class KpiViewHolder extends RecyclerView.ViewHolder {
        TextView title, value;
        KpiViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txt_stat_title);
            value = itemView.findViewById(R.id.txt_stat_value);
        }
        void bind(StatsItem item) {
            title.setText(item.getTitle());
            value.setText(item.getValue());
        }
    }
} 