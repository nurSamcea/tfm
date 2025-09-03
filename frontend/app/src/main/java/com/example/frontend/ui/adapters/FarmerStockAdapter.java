package com.example.frontend.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.FarmerProduct;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FarmerStockAdapter extends RecyclerView.Adapter<FarmerStockAdapter.ViewHolder> {

    private final List<FarmerProduct> productList;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());

    public FarmerStockAdapter(List<FarmerProduct> productList) {
        this.productList = productList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textPrice, textDate, textDiscount, textStatus;
        ImageView imageFood;

        public ViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textPrice = itemView.findViewById(R.id.text_price);
            textDate = itemView.findViewById(R.id.text_date);
            textDiscount = itemView.findViewById(R.id.text_discount);
            textStatus = itemView.findViewById(R.id.text_status);
            imageFood = itemView.findViewById(R.id.image_food);
        }
    }

    @NonNull
    @Override
    public FarmerStockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_farmer_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmerStockAdapter.ViewHolder holder, int position) {
        FarmerProduct product = productList.get(position);

        // Configurar nombre del producto
        holder.textTitle.setText(product.getName());
        
        // Configurar precio
        holder.textPrice.setText("€" + product.getPrice());
        
        // Configurar fecha de recolección
        String harvestDate = product.getHarvestDate();
        String todayStr = sdf.format(new Date());
        String dateText;
        int statusColor;
        String statusText;
        
        try {
            Date date = sdf.parse(harvestDate);
            Date today = sdf.parse(todayStr);
            if (date != null && today != null) {
                if (date.before(today)) {
                    dateText = "Recolectado " + harvestDate;
                    statusColor = Color.parseColor("#4CAF50"); // verde
                    statusText = "Disponible";
                } else if (date.equals(today)) {
                    dateText = "En recolección " + harvestDate;
                    statusColor = Color.parseColor("#2196F3"); // azul
                    statusText = "Procesando";
                } else {
                    dateText = "Pendiente de recolectar " + harvestDate;
                    statusColor = Color.parseColor("#FF9800"); // naranja
                    statusText = "Pendiente";
                }
            } else {
                dateText = "Fecha desconocida";
                statusColor = Color.GRAY;
                statusText = "Indefinido";
            }
        } catch (ParseException e) {
            dateText = "Fecha inválida";
            statusColor = Color.GRAY;
            statusText = "Error";
        }
        
        holder.textDate.setText(dateText);
        holder.textStatus.setText(statusText);
        holder.textStatus.setTextColor(Color.WHITE);
        
        // Configurar descuento (ejemplo: 20% OFF)
        holder.textDiscount.setText("20% OFF");
        
        // Configurar color de fondo del estado
        if (statusText.equals("Disponible")) {
            holder.textStatus.setBackgroundResource(R.drawable.status_available_background);
        } else if (statusText.equals("Procesando")) {
            holder.textStatus.setBackgroundResource(R.drawable.status_processing_background);
        } else if (statusText.equals("Pendiente")) {
            holder.textStatus.setBackgroundResource(R.drawable.status_out_of_stock_background);
        } else {
            holder.textStatus.setBackgroundResource(R.drawable.status_out_of_stock_background);
        }
        
        // Configurar color del precio según stock
        String stockStr = product.getStock().toLowerCase();
        boolean lowStock = false;
        if (stockStr.contains("kg")) {
            try {
                float kg = Float.parseFloat(stockStr.replace("kg", "").replace(",", ".").trim());
                if (kg < 5f) lowStock = true;
            } catch (Exception ignored) {}
                    } else if (stockStr.contains("caja")) {
                try {
                    String num = stockStr.replace("cajas", "").replace("caja", "").trim();
                    int cajas = Integer.parseInt(num);
                    if (cajas < 5) lowStock = true;
                } catch (Exception ignored) {}
            }
        
        if (lowStock) {
            holder.textPrice.setTextColor(Color.parseColor("#F44336")); // rojo para stock bajo
        } else {
            holder.textPrice.setTextColor(Color.parseColor("#4CAF50")); // verde para stock normal
        }

        // Configurar imagen del producto
        holder.imageFood.setImageResource(product.getImageResId());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
