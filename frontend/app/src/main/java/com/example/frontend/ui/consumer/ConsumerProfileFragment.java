package com.example.frontend.ui.consumer;

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

public class ConsumerProfileFragment extends Fragment {
    private static final String TAG = "Curr.ERROR ConsumerProfileFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando la aplicación ConsumerProfileFragment");
        View view = inflater.inflate(R.layout.fragment_consumer_profile, container, false);

        // Título
        TextView profileTitle = view.findViewById(R.id.profileTitle);
        profileTitle.setText("Consumidor");

        // Nombre de usuario (puedes cambiarlo por el nombre real del usuario)
        TextView profileName = view.findViewById(R.id.profileName);
        profileName.setText("Laura Sanz");

        View personalInfo = view.findViewById(R.id.optionPersonalInfo);
        ImageView persIcon = personalInfo.findViewById(R.id.optionIcon);
        TextView persText = personalInfo.findViewById(R.id.optionText);
        persIcon.setImageResource(R.drawable.ic_user);
        persText.setText("Personal information");

        View histOrders = view.findViewById(R.id.optionOrders);
        ImageView orderIcon = histOrders.findViewById(R.id.optionIcon);
        TextView orderText = histOrders.findViewById(R.id.optionText);
        orderIcon.setImageResource(R.drawable.ic_orders);
        orderText.setText("Historical orders");


        // Botón de volver
        Button buttonBack = view.findViewById(R.id.buttonBackConsumerToMain);
        buttonBack.setOnClickListener(v -> requireActivity().recreate());
        // Puedes añadir aquí listeners para las opciones del perfil si lo deseas

        return view;
    }
} 