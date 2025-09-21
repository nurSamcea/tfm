package com.example.frontend.ui.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import com.example.frontend.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PersonalInfoFragment extends Fragment {
    private static final String TAG = "PersonalInfoFragment";
    
    private SessionManager sessionManager;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando PersonalInfoFragment");
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        // Inicializar SessionManager
        sessionManager = new SessionManager(requireContext());

        // Configurar botón de volver
        setupBackButton(view);

        // Cargar información del usuario
        loadUserInfo(view);

        return view;
    }

    private void setupBackButton(View view) {
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private void loadUserInfo(View view) {
        // Obtener información de la sesión
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String userRole = sessionManager.getUserRole();

        // Configurar información básica
        TextView profileName = view.findViewById(R.id.profileName);
        TextView profileEmail = view.findViewById(R.id.profileEmail);
        
        if (profileName != null) {
            profileName.setText(userName != null ? userName : "Usuario");
        }
        
        if (profileEmail != null) {
            profileEmail.setText(userEmail != null ? userEmail : "usuario@email.com");
        }

        // Configurar información detallada
        TextView fullName = view.findViewById(R.id.fullName);
        TextView email = view.findViewById(R.id.email);
        TextView phone = view.findViewById(R.id.phone);
        TextView address = view.findViewById(R.id.address);
        TextView registrationDate = view.findViewById(R.id.registrationDate);

        if (fullName != null) {
            fullName.setText(userName != null ? userName : "Nombre no disponible");
        }

        if (email != null) {
            email.setText(userEmail != null ? userEmail : "Email no disponible");
        }

        if (phone != null) {
            // Por ahora usamos un teléfono de ejemplo, en una implementación real
            // esto vendría de la base de datos del usuario
            phone.setText("+34 123 456 789");
        }

        if (address != null) {
            // Por ahora usamos una dirección de ejemplo, en una implementación real
            // esto vendría de la base de datos del usuario
            address.setText("Calle Mayor 123, Madrid");
        }

        if (registrationDate != null) {
            // Por ahora usamos la fecha actual, en una implementación real
            // esto vendría de la base de datos del usuario
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
            registrationDate.setText(sdf.format(new Date()));
        }

        // Configurar estadísticas (por ahora con datos de ejemplo)
        TextView totalOrders = view.findViewById(R.id.totalOrders);
        TextView totalFavorites = view.findViewById(R.id.totalFavorites);
        TextView totalSpent = view.findViewById(R.id.totalSpent);

        if (totalOrders != null) {
            totalOrders.setText("15"); // En una implementación real, esto vendría de la base de datos
        }

        if (totalFavorites != null) {
            totalFavorites.setText("8"); // En una implementación real, esto vendría de la base de datos
        }

        if (totalSpent != null) {
            totalSpent.setText("€245"); // En una implementación real, esto vendría de la base de datos
        }
    }
}
