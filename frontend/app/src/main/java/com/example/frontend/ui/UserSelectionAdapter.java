package com.example.frontend.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.User;

import java.util.List;

public class UserSelectionAdapter extends RecyclerView.Adapter<UserSelectionAdapter.UserViewHolder> {
    
    private List<User> users;
    private int selectedPosition = -1;
    private OnUserSelectedListener listener;

    public interface OnUserSelectedListener {
        void onUserSelected(User user);
    }

    public UserSelectionAdapter(List<User> users, OnUserSelectedListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_selection, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<User> newUsers) {
        this.users = newUsers;
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    public User getSelectedUser() {
        if (selectedPosition >= 0 && selectedPosition < users.size()) {
            return users.get(selectedPosition);
        }
        return null;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private RadioButton radioButton;
        private TextView userName;
        private TextView userEmail;
        private TextView userEntity;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio_button);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            userEntity = itemView.findViewById(R.id.user_entity);
        }

        public void bind(User user, int position) {
            userName.setText(user.getName());
            userEmail.setText(user.getEmail());
            
            // Mostrar nombre de entidad si existe
            if (user.getEntityName() != null && !user.getEntityName().isEmpty()) {
                userEntity.setText(user.getEntityName());
                userEntity.setVisibility(View.VISIBLE);
            } else {
                userEntity.setVisibility(View.GONE);
            }

            // Configurar radio button
            radioButton.setChecked(position == selectedPosition);
            
            // Configurar click en todo el item
            itemView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = position;
                
                // Notificar cambios
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                
                // Notificar al listener
                if (listener != null) {
                    listener.onUserSelected(user);
                }
            });
            
            // Configurar click en radio button
            radioButton.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = position;
                
                // Notificar cambios
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                
                // Notificar al listener
                if (listener != null) {
                    listener.onUserSelected(user);
                }
            });
        }
    }
}



