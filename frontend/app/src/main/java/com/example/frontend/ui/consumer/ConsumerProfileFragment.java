package com.example.frontend.ui.consumer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.frontend.WelcomeActivity;
import com.example.frontend.R;
import com.example.frontend.utils.SessionManager;

import java.io.IOException;

public class ConsumerProfileFragment extends Fragment {
    private static final String TAG = "ConsumerProfileFragment";
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1002;
    
    private SessionManager sessionManager;
    private ImageView profileImage;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Iniciando ConsumerProfileFragment");
        View view = inflater.inflate(R.layout.fragment_consumer_profile, container, false);

        // Inicializar SessionManager
        sessionManager = new SessionManager(requireContext());

        // Información del usuario desde la sesión
        TextView profileName = view.findViewById(R.id.profileName);
        TextView profileEmail = view.findViewById(R.id.profileEmail);
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String userRole = sessionManager.getUserRole();
        
        profileName.setText(userName != null ? userName : "Consumidor");
        profileEmail.setText(userEmail != null ? userEmail : "usuario@email.com");

        // Configurar imagen de perfil
        setupProfileImage(view);

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
                // Navegar al fragmento de información personal
                PersonalInfoFragment personalInfoFragment = new PersonalInfoFragment();
                getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, personalInfoFragment)
                    .addToBackStack(null)
                    .commit();
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
                // Navegar al fragmento de preferencias
                PreferencesFragment preferencesFragment = new PreferencesFragment();
                getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, preferencesFragment)
                    .addToBackStack(null)
                    .commit();
            });
        }
    }

    private void setupProfileImage(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        if (profileImage != null) {
            profileImage.setOnClickListener(v -> showImagePicker());
        }
    }

    private void showImagePicker() {
        // Verificar permisos de cámara
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), 
                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        // Crear intent para seleccionar imagen
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri imageUri = data.getData();
                if (imageUri != null && profileImage != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            requireContext().getContentResolver(), imageUri);
                        profileImage.setImageBitmap(bitmap);
                        Toast.makeText(requireContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Error al cargar la imagen", e);
                        Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePicker();
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
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