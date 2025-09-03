package com.example.frontend.ui.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;

public class SupermarketProfileFragment extends Fragment {
    private static final String TAG = "Curr.ERROR SupermarketProfileFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando la aplicación");
        View view = inflater.inflate(R.layout.fragment_supermarket_profile, container, false);

        // Nombre de usuario (puedes cambiarlo por el nombre real del usuario)
        TextView profileName = view.findViewById(R.id.profileName);
        profileName.setText("Ensigna");

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

        // Botón de volver
        Button buttonBack = view.findViewById(R.id.buttonBackSupermarketToMain);
        buttonBack.setOnClickListener(v -> requireActivity().recreate());

        return view;
    }
} 