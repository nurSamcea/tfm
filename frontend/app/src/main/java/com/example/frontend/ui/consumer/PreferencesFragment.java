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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.frontend.R;
import com.example.frontend.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;

public class PreferencesFragment extends Fragment {
    private static final String TAG = "PreferencesFragment";
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1002;
    
    private SessionManager sessionManager;
    private ImageView profileImage;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Iniciando PreferencesFragment");
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        // Inicializar SessionManager
        sessionManager = new SessionManager(requireContext());

        // Configurar botón de volver
        setupBackButton(view);

        // Configurar imagen de perfil
        setupProfileImage(view);

        // Configurar preferencias
        setupPreferences(view);

        // Configurar botón de guardar
        setupSaveButton(view);

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

    private void setupProfileImage(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        MaterialButton btnChangePhoto = view.findViewById(R.id.btnChangePhoto);
        
        if (btnChangePhoto != null) {
            btnChangePhoto.setOnClickListener(v -> showImagePicker());
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

    private void setupPreferences(View view) {
        // Configurar chips de alergias
        ChipGroup allergiesGroup = view.findViewById(R.id.allergiesGroup);
        if (allergiesGroup != null) {
            // Aquí podrías cargar las preferencias guardadas del usuario
            // Por ahora, dejamos los chips sin seleccionar por defecto
        }

        // Configurar chips de dieta
        ChipGroup dietGroup = view.findViewById(R.id.dietGroup);
        if (dietGroup != null) {
            // Aquí podrías cargar las preferencias guardadas del usuario
        }

        // Configurar chips de origen
        ChipGroup originGroup = view.findViewById(R.id.originGroup);
        if (originGroup != null) {
            // Aquí podrías cargar las preferencias guardadas del usuario
        }

        // Configurar switches
        SwitchMaterial switchNotifications = view.findViewById(R.id.switchNotifications);
        SwitchMaterial switchLocation = view.findViewById(R.id.switchLocation);
        
        if (switchNotifications != null) {
            // Aquí podrías cargar la configuración guardada del usuario
            switchNotifications.setChecked(true);
        }
        
        if (switchLocation != null) {
            // Aquí podrías cargar la configuración guardada del usuario
            switchLocation.setChecked(true);
        }
    }

    private void setupSaveButton(View view) {
        MaterialButton btnSave = view.findViewById(R.id.btnSavePreferences);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> savePreferences(view));
        }
    }

    private void savePreferences(View view) {
        // Recopilar preferencias de alergias
        ChipGroup allergiesGroup = view.findViewById(R.id.allergiesGroup);
        StringBuilder allergies = new StringBuilder();
        if (allergiesGroup != null) {
            for (int i = 0; i < allergiesGroup.getChildCount(); i++) {
                Chip chip = (Chip) allergiesGroup.getChildAt(i);
                if (chip.isChecked()) {
                    if (allergies.length() > 0) allergies.append(",");
                    allergies.append(chip.getText().toString());
                }
            }
        }

        // Recopilar preferencias de dieta
        ChipGroup dietGroup = view.findViewById(R.id.dietGroup);
        StringBuilder diet = new StringBuilder();
        if (dietGroup != null) {
            for (int i = 0; i < dietGroup.getChildCount(); i++) {
                Chip chip = (Chip) dietGroup.getChildAt(i);
                if (chip.isChecked()) {
                    if (diet.length() > 0) diet.append(",");
                    diet.append(chip.getText().toString());
                }
            }
        }

        // Recopilar preferencias de origen
        ChipGroup originGroup = view.findViewById(R.id.originGroup);
        StringBuilder origin = new StringBuilder();
        if (originGroup != null) {
            for (int i = 0; i < originGroup.getChildCount(); i++) {
                Chip chip = (Chip) originGroup.getChildAt(i);
                if (chip.isChecked()) {
                    if (origin.length() > 0) origin.append(",");
                    origin.append(chip.getText().toString());
                }
            }
        }

        // Recopilar configuraciones
        SwitchMaterial switchNotifications = view.findViewById(R.id.switchNotifications);
        SwitchMaterial switchLocation = view.findViewById(R.id.switchLocation);
        
        boolean notificationsEnabled = switchNotifications != null && switchNotifications.isChecked();
        boolean locationEnabled = switchLocation != null && switchLocation.isChecked();

        // Aquí guardarías las preferencias en la base de datos o en SharedPreferences
        // Por ahora, solo mostramos un mensaje de confirmación
        Log.d(TAG, "Preferencias guardadas:");
        Log.d(TAG, "Alergias: " + allergies.toString());
        Log.d(TAG, "Dieta: " + diet.toString());
        Log.d(TAG, "Origen: " + origin.toString());
        Log.d(TAG, "Notificaciones: " + notificationsEnabled);
        Log.d(TAG, "Ubicación: " + locationEnabled);

        Toast.makeText(requireContext(), "Preferencias guardadas correctamente", Toast.LENGTH_SHORT).show();
        
        // Volver al perfil
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}
