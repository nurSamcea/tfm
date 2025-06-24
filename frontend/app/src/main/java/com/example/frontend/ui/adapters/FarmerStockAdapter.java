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
        TextView textTitle, textInfo, textPlus;
        ImageView imageFood;

        public ViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textInfo = itemView.findViewById(R.id.text_info);
            textPlus = itemView.findViewById(R.id.text_plus);
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

        holder.textTitle.setText(product.getName());
        holder.textInfo.setText("Stock: " + product.getStock() + " · " + product.getPrice());

        // Lógica de color y texto para la fecha de recolección
        String harvestDate = product.getHarvestDate();
        String todayStr = sdf.format(new Date());
        int color;
        String plusText;
        try {
            Date date = sdf.parse(harvestDate);
            Date today = sdf.parse(todayStr);
            if (date != null && today != null) {
                if (date.before(today)) {
                    color = Color.parseColor("#2E7D32"); // verde
                    plusText = "Recolectado " + harvestDate;
                } else if (date.equals(today)) {
                    color = Color.parseColor("#7B5EFF"); // morado
                    plusText = "En recolección " + harvestDate;
                } else {
                    color = Color.parseColor("#FF9800"); // naranja
                    plusText = "Pendiente de recolectar " + harvestDate;
                }
            } else {
                color = Color.GRAY;
                plusText = "Fecha desconocida";
            }
        } catch (ParseException e) {
            color = Color.GRAY;
            plusText = "Fecha inválida";
        }
        holder.textPlus.setText(plusText);
        holder.textPlus.setTextColor(color);

        // Lógica de color para el stock
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
            holder.textInfo.setTextColor(Color.parseColor("#B61515")); // rojo
        } else {
            holder.textInfo.setTextColor(Color.parseColor("#222222")); // color normal
        }

        // Puedes cambiar esto por Glide/Picasso si tienes imágenes reales
        holder.imageFood.setImageResource(R.drawable.icon_profile); // o cualquier imagen temporal

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
