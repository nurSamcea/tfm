package com.example.frontend.ui.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.WelcomeActivity;
import com.example.frontend.R;
import com.example.frontend.utils.SessionManager;

public class ConsumerProfileFragment extends Fragment {
    private static final String TAG = "ConsumerProfileFragment";
    
    private SessionManager sessionManager;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando ConsumerProfileFragment");
        View view = inflater.inflate(R.layout.fragment_consumer_profile, container, false);

        // Inicializar SessionManager
        sessionManager = new SessionManager(requireContext());

        // Información del usuario desde la sesión
        TextView profileName = view.findViewById(R.id.profileName);
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String userRole = sessionManager.getUserRole();
        
        profileName.setText(userName != null ? userName : "Usuario");
        
        // Mostrar email del usuario
        TextView profileEmail = view.findViewById(R.id.profileEmail);
        if (profileEmail != null) {
            profileEmail.setText(userEmail != null ? userEmail : "usuario@email.com");
        }

        // Configurar opciones del perfil
        setupProfileOptions(view);

        // Botón de logout
        Button logoutButton = view.findViewById(R.id.logout_button);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> performLogout());
        }

        // Botón de volver
        Button buttonBack = view.findViewById(R.id.buttonBackConsumerToMain);
        buttonBack.setOnClickListener(v -> requireActivity().recreate());

        return view;
    }

    private void setupProfileOptions(View view) {
        // Información personal
        View personalInfo = view.findViewById(R.id.optionPersonalInfo);
        if (personalInfo != null) {
            ImageView persIcon = personalInfo.findViewById(R.id.optionIcon);
            TextView persText = personalInfo.findViewById(R.id.optionText);
            if (persIcon != null) persIcon.setImageResource(R.drawable.ic_user);
            if (persText != null) persText.setText("Información Personal");
            
            personalInfo.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Información personal próximamente", Toast.LENGTH_SHORT).show();
            });
        }

        // Historial de pedidos
        View histOrders = view.findViewById(R.id.optionOrders);
        if (histOrders != null) {
            ImageView orderIcon = histOrders.findViewById(R.id.optionIcon);
            TextView orderText = histOrders.findViewById(R.id.optionText);
            if (orderIcon != null) orderIcon.setImageResource(R.drawable.ic_orders);
            if (orderIcon != null) orderIcon.setImageResource(R.drawable.ic_orders);
            if (orderText != null) orderText.setText("Historial de Pedidos");
            
            histOrders.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Historial de pedidos próximamente", Toast.LENGTH_SHORT).show();
            });
        }

        // Preferencias
        View preferences = view.findViewById(R.id.optionPreferences);
        if (preferences != null) {
            ImageView prefIcon = preferences.findViewById(R.id.optionIcon);
            TextView prefText = preferences.findViewById(R.id.optionText);
            if (prefIcon != null) prefIcon.setImageResource(R.drawable.ic_settings);
            if (prefText != null) prefText.setText("Preferencias");
            
            preferences.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Preferencias próximamente", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void performLogout() {
        // Mostrar confirmación
        Toast.makeText(requireContext(), "Cerrando sesión...", Toast.LENGTH_SHORT).show();
        
        // Cerrar sesión
        if (getActivity() instanceof WelcomeActivity) {
            ((WelcomeActivity) getActivity()).logout();
        }
    }
} 