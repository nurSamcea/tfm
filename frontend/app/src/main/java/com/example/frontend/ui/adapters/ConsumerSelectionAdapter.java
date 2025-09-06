package com.example.frontend.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.User;

import java.util.List;

public class ConsumerSelectionAdapter extends RecyclerView.Adapter<ConsumerSelectionAdapter.ConsumerViewHolder> {

    private List<User> consumers;
    private OnConsumerSelectionListener listener;

    public interface OnConsumerSelectionListener {
        void onConsumerSelected(User consumer);
    }

    public ConsumerSelectionAdapter(List<User> consumers) {
        this.consumers = consumers;
    }

    public void setOnConsumerSelectionListener(OnConsumerSelectionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConsumerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_consumer_selection, parent, false);
        return new ConsumerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsumerViewHolder holder, int position) {
        User consumer = consumers.get(position);
        holder.bind(consumer);
    }

    @Override
    public int getItemCount() {
        return consumers.size();
    }

    public void updateConsumers(List<User> newConsumers) {
        this.consumers = newConsumers;
        notifyDataSetChanged();
    }

    class ConsumerViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView emailTextView;
        private TextView locationTextView;

        public ConsumerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.consumer_name);
            emailTextView = itemView.findViewById(R.id.consumer_email);
            locationTextView = itemView.findViewById(R.id.consumer_location);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onConsumerSelected(consumers.get(position));
                    }
                }
            });
        }

        public void bind(User consumer) {
            nameTextView.setText(consumer.getName());
            emailTextView.setText(consumer.getEmail());
            
            if (consumer.getLocation() != null) {
                locationTextView.setText(String.format("Ubicación: %.4f, %.4f", 
                    consumer.getLocation().getLatitude(), consumer.getLocation().getLongitude()));
            } else {
                locationTextView.setText("Ubicación no disponible");
            }
        }
    }
}
