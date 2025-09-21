package com.example.frontend.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.FullTraceabilityResponse;
import com.example.frontend.model.Product;
import com.example.frontend.model.TraceabilityEvent;
import com.example.frontend.services.TraceabilityApiService;
import com.example.frontend.api.ApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductTraceabilityDialog extends DialogFragment {
    
    private static final String TAG = "ProductTraceabilityDialog";
    private Product product;
    private TraceabilityApiService traceabilityApiService;
    private ProgressBar progressBar;
    private ScrollView contentLayout;
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private List<TraceabilityEvent> events = new ArrayList<>();
    
    public static ProductTraceabilityDialog newInstance(Product product) {
        ProductTraceabilityDialog dialog = new ProductTraceabilityDialog();
        Bundle args = new Bundle();
        args.putSerializable("product", product);
        dialog.setArguments(args);
        return dialog;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate - Inicializando diálogo");
        
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable("product");
            if (product != null) {
                Log.d(TAG, "Producto recibido: " + product.getName() + " (ID: " + product.getId() + ")");
            } else {
                Log.e(TAG, "Producto es null en getArguments");
            }
        } else {
            Log.e(TAG, "getArguments es null");
        }
        
        traceabilityApiService = ApiClient.getClient().create(TraceabilityApiService.class);
        Log.d(TAG, "TraceabilityApiService inicializado");
        Log.d(TAG, "URL base de la API: " + com.example.frontend.utils.Constants.getBaseUrl());
    }
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_product_traceability, container, false);
        
        // Configurar RecyclerView
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventsAdapter = new EventsAdapter(events);
        eventsRecyclerView.setAdapter(eventsAdapter);
        
        // Configurar UI
        progressBar = view.findViewById(R.id.progress_bar);
        contentLayout = view.findViewById(R.id.content_layout);
        
        // Configurar botón cerrar
        ImageView closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dismiss());
        
        // Cargar datos de trazabilidad
        loadTraceabilityData();
        
        return view;
    }
    
    private void loadTraceabilityData() {
        Log.d(TAG, "Iniciando carga de datos de trazabilidad");
        
        if (product == null || product.getId() == null) {
            Log.e(TAG, "Producto o ID del producto es null");
            showError("Producto no válido");
            return;
        }
        
        Log.d(TAG, "Producto ID: " + product.getId());
        Log.d(TAG, "Producto nombre: " + product.getName());
        
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        
        try {
            int productId = Integer.parseInt(product.getId());
            Log.d(TAG, "Llamando API con productId: " + productId);
            Log.d(TAG, "URL completa: " + com.example.frontend.utils.Constants.getBaseUrl() + "consumer/products/" + productId + "/trace/json");
            
            Call<FullTraceabilityResponse> call = traceabilityApiService.getProductTraceability(productId);
            call.enqueue(new Callback<FullTraceabilityResponse>() {
                @Override
                public void onResponse(Call<FullTraceabilityResponse> call, Response<FullTraceabilityResponse> response) {
                    Log.d(TAG, "Respuesta recibida - Código: " + response.code());
                    Log.d(TAG, "Respuesta exitosa: " + response.isSuccessful());
                    Log.d(TAG, "Body es null: " + (response.body() == null));
                    
                    progressBar.setVisibility(View.GONE);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Mostrando datos de trazabilidad");
                        FullTraceabilityResponse traceabilityData = response.body();
                        displayTraceabilityData(traceabilityData);
                    } else {
                        String errorMessage = "Error al cargar la trazabilidad del producto";
                        if (!response.isSuccessful()) {
                            errorMessage += " - Código: " + response.code();
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "Error body: " + errorBody);
                                    errorMessage += " - Error: " + errorBody;
                                } catch (Exception e) {
                                    Log.e(TAG, "Error al leer error body", e);
                                }
                            }
                        }
                        Log.e(TAG, errorMessage);
                        showError(errorMessage);
                    }
                }
                
                @Override
                public void onFailure(Call<FullTraceabilityResponse> call, Throwable t) {
                    Log.e(TAG, "Error de conexión", t);
                    progressBar.setVisibility(View.GONE);
                    showError("Error de conexión: " + t.getMessage());
                }
            });
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error al convertir ID del producto a entero: " + product.getId(), e);
            progressBar.setVisibility(View.GONE);
            showError("ID del producto no válido: " + product.getId());
        }
    }
    
    private void displayTraceabilityData(FullTraceabilityResponse data) {
        Log.d(TAG, "Mostrando datos de trazabilidad");
        contentLayout.setVisibility(View.VISIBLE);
        
        // Mostrar información del producto
        TextView productName = getView().findViewById(R.id.product_name);
        TextView productCategory = getView().findViewById(R.id.product_category);
        TextView productPrice = getView().findViewById(R.id.product_price);
        
        if (data.getProduct() != null) {
            Log.d(TAG, "Producto en respuesta: " + data.getProduct().getName());
            productName.setText(data.getProduct().getName());
            productCategory.setText(data.getProduct().getCategory());
            productPrice.setText(String.format("€%.2f", data.getProduct().getPrice()));
        } else {
            Log.w(TAG, "Producto en respuesta es null");
        }
        
        // Mostrar resumen de trazabilidad
        if (data.getSummary() != null) {
            Log.d(TAG, "Mostrando resumen de trazabilidad");
            TextView totalDistance = getView().findViewById(R.id.total_distance);
            TextView totalTime = getView().findViewById(R.id.total_time);
            TextView qualityScore = getView().findViewById(R.id.quality_score);
            TextView sustainabilityScore = getView().findViewById(R.id.sustainability_score);
            TextView blockchainStatus = getView().findViewById(R.id.blockchain_status);
            
            totalDistance.setText(String.format("%.1f km", data.getSummary().getTotalDistanceKm()));
            totalTime.setText(String.format("%.1f horas", data.getSummary().getTotalTimeHours()));
            qualityScore.setText(String.format("%.1f/10", data.getSummary().getQualityScore()));
            sustainabilityScore.setText(String.format("%.1f/10", data.getSummary().getSustainabilityScore()));
            
            if (data.getSummary().getBlockchainVerified()) {
                blockchainStatus.setText("✓ Verificado en Blockchain");
                blockchainStatus.setTextColor(Color.GREEN);
            } else {
                blockchainStatus.setText("⚠ No verificado");
                blockchainStatus.setTextColor(Color.RED);
            }
        } else {
            Log.w(TAG, "Resumen de trazabilidad es null");
        }
        
        // Mostrar eventos
        if (data.getEvents() != null && !data.getEvents().isEmpty()) {
            Log.d(TAG, "Mostrando " + data.getEvents().size() + " eventos");
            events.clear();
            events.addAll(data.getEvents());
            eventsAdapter.notifyDataSetChanged();
        } else {
            Log.w(TAG, "No hay eventos de trazabilidad disponibles");
            // Mostrar mensaje de que no hay eventos
            events.clear();
            eventsAdapter.notifyDataSetChanged();
        }
    }
    
    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        dismiss();
    }
    
    // Adapter para los eventos
    private class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
        private List<TraceabilityEvent> events;
        
        public EventsAdapter(List<TraceabilityEvent> events) {
            this.events = events;
        }
        
        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_traceability_event, parent, false);
            return new EventViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            if (events.isEmpty()) {
                // Mostrar mensaje de que no hay eventos
                holder.showNoEventsMessage();
            } else {
                TraceabilityEvent event = events.get(position);
                holder.bind(event);
            }
        }
        
        @Override
        public int getItemCount() {
            return events.isEmpty() ? 1 : events.size(); // Mostrar 1 item si no hay eventos (para el mensaje)
        }
        
        class EventViewHolder extends RecyclerView.ViewHolder {
            private TextView eventType, timestamp, location, actor, blockchainStatus;
            private ImageView eventIcon;
            
            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                eventType = itemView.findViewById(R.id.event_type);
                timestamp = itemView.findViewById(R.id.timestamp);
                location = itemView.findViewById(R.id.location);
                actor = itemView.findViewById(R.id.actor);
                blockchainStatus = itemView.findViewById(R.id.blockchain_status);
                eventIcon = itemView.findViewById(R.id.event_icon);
            }
            
            public void bind(TraceabilityEvent event) {
                eventType.setText(getEventTypeDisplayName(event.getEventType()));
                timestamp.setText(formatTimestamp(event.getTimestamp()));
                location.setText(event.getLocationDescription() != null ? event.getLocationDescription() : "Ubicación no disponible");
                actor.setText(event.getActorType() != null ? event.getActorType() : "Actor no disponible");
                
                if (event.getIsVerified() != null && event.getIsVerified()) {
                    blockchainStatus.setText("✓ Verificado");
                    blockchainStatus.setTextColor(Color.GREEN);
                } else {
                    blockchainStatus.setText("⚠ No verificado");
                    blockchainStatus.setTextColor(Color.RED);
                }
                
                // Configurar icono según el tipo de evento
                setEventIcon(event.getEventType());
            }
            
            private void setEventIcon(String eventType) {
                int iconRes = R.drawable.ic_info; // Icono por defecto
                
                switch (eventType) {
                    case "PRODUCTION":
                        iconRes = R.drawable.ic_products;
                        break;
                    case "HARVEST":
                        iconRes = R.drawable.ic_products;
                        break;
                    case "TRANSPORT":
                        iconRes = R.drawable.ic_products;
                        break;
                    case "STORAGE":
                        iconRes = R.drawable.ic_products;
                        break;
                    case "SALE":
                        iconRes = R.drawable.ic_products;
                        break;
                    case "DELIVERY":
                        iconRes = R.drawable.ic_products;
                        break;
                }
                
                eventIcon.setImageResource(iconRes);
            }
            
            private String getEventTypeDisplayName(String eventType) {
                switch (eventType) {
                    case "PRODUCTION": return "Producción";
                    case "HARVEST": return "Cosecha";
                    case "TRANSPORT": return "Transporte";
                    case "STORAGE": return "Almacenamiento";
                    case "SALE": return "Venta";
                    case "DELIVERY": return "Entrega";
                    default: return eventType;
                }
            }
            
            private String formatTimestamp(Date timestamp) {
                if (timestamp == null) return "Fecha no disponible";
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                return sdf.format(timestamp);
            }
            
            public void showNoEventsMessage() {
                eventType.setText("Sin eventos de trazabilidad");
                timestamp.setText("");
                location.setText("Este producto no tiene datos de trazabilidad disponibles");
                actor.setText("");
                blockchainStatus.setText("⚠ Sin verificar");
                blockchainStatus.setTextColor(Color.RED);
                eventIcon.setImageResource(R.drawable.ic_info);
            }
        }
    }
}
