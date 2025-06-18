package com.example.frontend.ui.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        TextView title = view.findViewById(R.id.title);
        title.setText("Mi Perfil");
        Button buttonBack = view.findViewById(R.id.buttonBackToMain);
        buttonBack.setOnClickListener(v -> requireActivity().recreate());
        return view;
    }
} 