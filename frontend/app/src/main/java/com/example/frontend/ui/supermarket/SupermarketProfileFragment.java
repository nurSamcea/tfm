package com.example.frontend.ui.supermarket;

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

import com.example.frontend.R;
import com.example.frontend.WelcomeActivity;
import com.example.frontend.utils.SessionManager;

public class SupermarketProfileFragment extends Fragment {
    private static final String TAG = "Curr.ERROR SupermarketProfileFragment";
    private SessionManager sessionManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando la aplicación");
        View view = inflater.inflate(R.layout.fragment_supermarket_profile, container, false);

        // Inicializar SessionManager
        sessionManager = new SessionManager(requireContext());

        // Información del usuario desde la sesión
        TextView profileName = view.findViewById(R.id.profileName);
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        
        profileName.setText(userName != null ? userName : "Supermercado");

        View contactInformation = view.findViewById(R.id.optionPersonalInfo);
        ImageView persIcon = contactInformation.findViewById(R.id.optionIcon);
        TextView persText = contactInformation.findViewById(R.id.optionText);
        persIcon.setImageResource(R.drawable.ic_user);
        persText.setText("Contact information");

        View supplierOrdersHistory = view.findViewById(R.id.optionSupplierOrders);
        ImageView supplierIcon = supplierOrdersHistory.findViewById(R.id.optionIcon);
        TextView supplierText = supplierOrdersHistory.findViewById(R.id.optionText);
        supplierIcon.setImageResource(R.drawable.ic_orders);
        supplierText.setText("Supplier orders history");

        View customerOrdersHistory = view.findViewById(R.id.optionCustomerOrders);
        ImageView orderIcon = customerOrdersHistory.findViewById(R.id.optionIcon);
        TextView orderText = customerOrdersHistory.findViewById(R.id.optionText);
        orderIcon.setImageResource(R.drawable.ic_purchases);
        orderText.setText("Customer orders history");

        View certifications = view.findViewById(R.id.optionCertifications);
        ImageView certificationIcon = certifications.findViewById(R.id.optionIcon);
        TextView certificationsText = certifications.findViewById(R.id.optionText);
        certificationIcon.setImageResource(R.drawable.ic_certification);
        certificationsText.setText("Certifications");

        // Botón de logout
        Button logoutButton = view.findViewById(R.id.logout_button);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> performLogout());
        }

        return view;
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