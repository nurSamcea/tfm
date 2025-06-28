package com.example.frontend.ui.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        Log.d(TAG, "onCreate: Iniciando la aplicación");
        View view = inflater.inflate(R.layout.fragment_consumer_profile, container, false);

        // Título
        TextView profileTitle = view.findViewById(R.id.profileTitle);
        profileTitle.setText("Perfil");

        // Nombre de usuario (puedes cambiarlo por el nombre real del usuario)
        TextView profileName = view.findViewById(R.id.profileName);
        profileName.setText("Consumidor Demo");

        // Botón de volver
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Puedes añadir aquí listeners para las opciones del perfil si lo deseas

        return view;
    }
} 