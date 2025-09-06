package com.example.frontend.ui.farmer;

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

public class FarmerProfileFragment extends Fragment {
    private static final String TAG = "FarmerProfileFragment";
    
    private SessionManager sessionManager;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando FarmerProfileFragment");
        View view = inflater.inflate(R.layout.fragment_farmer_profile, container, false);

        // Inicializar SessionManager
        sessionManager = new SessionManager(requireContext());

        // Información del usuario desde la sesión
        TextView profileName = view.findViewById(R.id.profileName);
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String userRole = sessionManager.getUserRole();
        
        profileName.setText(userName != null ? userName : "Agricultor");
        
        // Mostrar email del usuario
        TextView profileEmail = view.findViewById(R.id.profileEmail);
        if (profileEmail != null) {
            profileEmail.setText(userEmail != null ? userEmail : "agricultor@email.com");
        }

        // Configurar opciones del perfil
        setupProfileOptions(view);

        // Botón de logout
        Button logoutButton = view.findViewById(R.id.logout_button);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> performLogout());
        }

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
            if (orderText != null) orderText.setText("Historial de Pedidos");
            
            histOrders.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Historial de pedidos próximamente", Toast.LENGTH_SHORT).show();
            });
        }

        // Certificaciones
        View certifications = view.findViewById(R.id.optionCertifications);
        if (certifications != null) {
            ImageView certificationIcon = certifications.findViewById(R.id.optionIcon);
            TextView certificationsText = certifications.findViewById(R.id.optionText);
            if (certificationIcon != null) certificationIcon.setImageResource(R.drawable.ic_certification);
            if (certificationsText != null) certificationsText.setText("Certificaciones");
            
            certifications.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Certificaciones próximamente", Toast.LENGTH_SHORT).show();
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