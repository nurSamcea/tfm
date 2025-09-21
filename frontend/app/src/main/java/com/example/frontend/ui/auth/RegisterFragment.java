package com.example.frontend.ui.auth;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.WelcomeActivity;
import com.example.frontend.R;
import com.example.frontend.api.ApiService;
import com.example.frontend.api.ApiClient;
import com.example.frontend.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private AutoCompleteTextView roleSpinner;
    private EditText entityNameInput;
    private TextView entityNameLabel;
    private TextView locationTitleLabel;
    private LinearLayout locationLayout;
    private EditText addressInput;
    private Button searchAddressButton;
    private EditText locationLatInput;
    private EditText locationLonInput;
    private Button registerButton;
    private Button backToLoginButton;
    private ProgressBar progressBar;
    private TextView errorText;
    
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        
        // Inicializar servicios
        apiService = ApiClient.getApiService(requireContext());
        sessionManager = new SessionManager(requireContext());
        
        // Inicializar vistas
        nameInput = view.findViewById(R.id.name_input);
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        confirmPasswordInput = view.findViewById(R.id.confirm_password_input);
        roleSpinner = view.findViewById(R.id.role_spinner);
        entityNameInput = view.findViewById(R.id.entity_name_input);
        entityNameLabel = view.findViewById(R.id.entity_name_label);
        locationTitleLabel = view.findViewById(R.id.location_title_label);
        locationLayout = view.findViewById(R.id.location_layout);
        addressInput = view.findViewById(R.id.address_input);
        searchAddressButton = view.findViewById(R.id.search_address_button);
        locationLatInput = view.findViewById(R.id.location_lat_input);
        locationLonInput = view.findViewById(R.id.location_lon_input);
        registerButton = view.findViewById(R.id.register_button);
        backToLoginButton = view.findViewById(R.id.back_to_login_button);
        progressBar = view.findViewById(R.id.progress_bar);
        errorText = view.findViewById(R.id.error_text);
        
        // Configurar spinner de roles
        setupRoleSpinner();
        
        // Configurar listeners
        registerButton.setOnClickListener(v -> performRegister());
        backToLoginButton.setOnClickListener(v -> navigateToLogin());
        searchAddressButton.setOnClickListener(v -> searchAddress());
        
        return view;
    }

    private void setupRoleSpinner() {
        // Crear opciones más descriptivas para el usuario
        String[] roleLabels = {"Consumidor", "Agricultor", "Supermercado"};
        String[] roleValues = {"consumer", "farmer", "supermarket"};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            roleLabels
        );
        roleSpinner.setAdapter(adapter);
        roleSpinner.setText(roleLabels[0], false); // Establecer valor por defecto
        roleSpinner.setThreshold(0); // Mostrar opciones inmediatamente al hacer clic
        roleSpinner.setKeyListener(null); // Deshabilitar entrada de teclado
        roleSpinner.setFocusable(false); // Hacer que no sea focusable para forzar el dropdown
        
        // Listener para mostrar/ocultar campos según el rol
        roleSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedRole = roleValues[position];
            updateFieldsVisibility(selectedRole);
        });
        
        // Listener para cuando se hace clic en el campo
        roleSpinner.setOnClickListener(v -> {
            roleSpinner.showDropDown();
        });
        
        // También escuchar cambios de texto para detectar selección manual
        roleSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                roleSpinner.showDropDown();
            } else {
                String selectedText = roleSpinner.getText().toString();
                String selectedRole = getRoleFromLabel(selectedText);
                updateFieldsVisibility(selectedRole);
            }
        });
        
        // Mostrar campos iniciales
        updateFieldsVisibility("consumer");
    }

    private void updateFieldsVisibility(String role) {
        if ("farmer".equals(role) || "supermarket".equals(role)) {
            // Mostrar campos obligatorios para agricultores y supermercados
            entityNameLabel.setVisibility(View.VISIBLE);
            entityNameInput.setVisibility(View.VISIBLE);
            locationTitleLabel.setVisibility(View.VISIBLE);
            locationLayout.setVisibility(View.VISIBLE);
            
            // Actualizar etiquetas para indicar que son obligatorios
            entityNameLabel.setText("Nombre de la empresa/entidad *");
            locationTitleLabel.setText("Ubicación *");
            
            // Limpiar campos de ubicación cuando se cambia el rol
            addressInput.setText("");
            locationLatInput.setText("");
            locationLonInput.setText("");
            
        } else {
            // Ocultar campos para consumidores
            entityNameLabel.setVisibility(View.GONE);
            entityNameInput.setVisibility(View.GONE);
            locationTitleLabel.setVisibility(View.GONE);
            locationLayout.setVisibility(View.GONE);
        }
    }

    private void performRegister() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        
        // Obtener el rol correcto basado en la selección del spinner
        String roleLabel = roleSpinner.getText().toString();
        String role = getRoleFromLabel(roleLabel);
        
        String entityName = entityNameInput.getText().toString().trim();
        String latStr = locationLatInput.getText().toString().trim();
        String lonStr = locationLonInput.getText().toString().trim();
        
        // Debug: Log de coordenadas para diagnóstico
        Log.d(TAG, "Validating coordinates - lat: '" + latStr + "', lon: '" + lonStr + "'");
        
        // Validaciones
        if (name.isEmpty()) {
            showError("Por favor ingresa tu nombre");
            return;
        }
        
        if (email.isEmpty()) {
            showError("Por favor ingresa tu email");
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Por favor ingresa un email válido");
            return;
        }
        
        if (password.isEmpty()) {
            showError("Por favor ingresa una contraseña");
            return;
        }
        
        if (password.length() < 6) {
            showError("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Las contraseñas no coinciden");
            return;
        }
        
        // Validar entidad para agricultores y supermercados
        if (("farmer".equals(role) || "supermarket".equals(role)) && entityName.isEmpty()) {
            showError("Por favor ingresa el nombre de tu empresa o entidad");
            return;
        }
        
        // Validar ubicación para agricultores y supermercados
        if ("farmer".equals(role) || "supermarket".equals(role)) {
            String address = addressInput.getText().toString().trim();
            if (address.isEmpty()) {
                showError("Por favor ingresa una dirección para buscar las coordenadas");
                return;
            }
            
            if (latStr.isEmpty() || lonStr.isEmpty()) {
                showError("Por favor busca la dirección para obtener las coordenadas");
                return;
            }
            
            try {
                // Limpiar y normalizar las coordenadas
                latStr = latStr.trim().replace(",", ".");
                lonStr = lonStr.trim().replace(",", ".");
                
                double lat = Double.parseDouble(latStr);
                double lon = Double.parseDouble(lonStr);
                
                // Validar rangos de coordenadas
                if (lat < -90 || lat > 90) {
                    showError("La latitud debe estar entre -90 y 90");
                    return;
                }
                if (lon < -180 || lon > 180) {
                    showError("La longitud debe estar entre -180 y 180");
                    return;
                }
                
                // Verificar que las coordenadas no sean cero (indicaría error en geocodificación)
                if (lat == 0.0 && lon == 0.0) {
                    showError("Las coordenadas obtenidas no son válidas. Busca la dirección nuevamente");
                    return;
                }
                
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing coordinates: lat=" + latStr + ", lon=" + lonStr, e);
                showError("Error en las coordenadas. Busca la dirección nuevamente");
                return;
            }
        }
        
        // Mostrar loading
        setLoading(true);
        hideError();
        
        // Crear request con todos los campos
        ApiService.RegisterRequest registerRequest = new ApiService.RegisterRequest(name, email, password, role);
        registerRequest.entity_name = entityName.isEmpty() ? null : entityName;
        
        // Procesar ubicación si se proporciona
        if (!latStr.isEmpty() && !lonStr.isEmpty()) {
            double lat = Double.parseDouble(latStr);
            double lon = Double.parseDouble(lonStr);
            registerRequest.location_lat = lat;
            registerRequest.location_lon = lon;
        }
        
        // Realizar petición
        apiService.register(registerRequest).enqueue(new Callback<ApiService.RegisterResponse>() {
            @Override
            public void onResponse(Call<ApiService.RegisterResponse> call, Response<ApiService.RegisterResponse> response) {
                setLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.RegisterResponse registerResponse = response.body();
                    
                    Toast.makeText(requireContext(), 
                        "¡Cuenta creada exitosamente! Ahora puedes iniciar sesión", 
                        Toast.LENGTH_LONG).show();
                    
                    // Navegar al login
                    navigateToLogin();
                    
                } else {
                    String errorMessage = "Error en el registro";
                    if (response.code() == 400) {
                        errorMessage = "El email ya está registrado";
                    } else if (response.code() >= 500) {
                        errorMessage = "Error del servidor. Intenta más tarde";
                    }
                    showError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiService.RegisterResponse> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "Register error", t);
                showError("Error de conexión. Verifica tu internet");
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        registerButton.setEnabled(!loading);
        backToLoginButton.setEnabled(!loading);
        nameInput.setEnabled(!loading);
        emailInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
        confirmPasswordInput.setEnabled(!loading);
        roleSpinner.setEnabled(!loading);
        entityNameInput.setEnabled(!loading);
        addressInput.setEnabled(!loading);
        searchAddressButton.setEnabled(!loading);
        locationLatInput.setEnabled(!loading);
        locationLonInput.setEnabled(!loading);
        
        // Restaurar configuración del spinner cuando no está en loading
        if (!loading) {
            roleSpinner.setKeyListener(null);
            roleSpinner.setFocusable(false);
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorText.setVisibility(View.GONE);
    }

    private void navigateToLogin() {
        // Mostrar fragmento de login en WelcomeActivity
        if (getActivity() instanceof WelcomeActivity) {
            ((WelcomeActivity) getActivity()).showLoginFragment();
        }
    }
    
    private void searchAddress() {
        String address = addressInput.getText().toString().trim();
        
        if (address.isEmpty()) {
            showError("Por favor ingresa una dirección para buscar");
            return;
        }
        
        // Mostrar loading en el botón de búsqueda
        searchAddressButton.setEnabled(false);
        searchAddressButton.setText("Buscando...");
        hideError();
        
        // Ejecutar geocodificación en un hilo separado
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                
                // Volver al hilo principal para actualizar la UI
                requireActivity().runOnUiThread(() -> {
                    searchAddressButton.setEnabled(true);
                    searchAddressButton.setText("Buscar");
                    
                    if (addresses != null && !addresses.isEmpty()) {
                        Address foundAddress = addresses.get(0);
                        double latitude = foundAddress.getLatitude();
                        double longitude = foundAddress.getLongitude();
                        
                        // Actualizar los campos de coordenadas con formato consistente
                        String latFormatted = String.format(Locale.US, "%.6f", latitude);
                        String lonFormatted = String.format(Locale.US, "%.6f", longitude);
                        
                        locationLatInput.setText(latFormatted);
                        locationLonInput.setText(lonFormatted);
                        
                        // Debug: Log de coordenadas obtenidas
                        Log.d(TAG, "Coordinates found - lat: " + latFormatted + ", lon: " + lonFormatted);
                        
                        // Mostrar mensaje de éxito
                        String fullAddress = foundAddress.getAddressLine(0);
                        if (fullAddress != null) {
                            Toast.makeText(requireContext(), 
                                "Dirección encontrada: " + fullAddress, 
                                Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireContext(), 
                                "Coordenadas encontradas", 
                                Toast.LENGTH_SHORT).show();
                        }
                        
                    } else {
                        showError("No se pudo encontrar la dirección. Intenta con una dirección más específica");
                    }
                });
                
            } catch (IOException e) {
                // Volver al hilo principal para mostrar error
                requireActivity().runOnUiThread(() -> {
                    searchAddressButton.setEnabled(true);
                    searchAddressButton.setText("Buscar");
                    Log.e(TAG, "Error en geocodificación", e);
                    showError("Error al buscar la dirección. Verifica tu conexión a internet");
                });
            } catch (Exception e) {
                // Volver al hilo principal para mostrar error
                requireActivity().runOnUiThread(() -> {
                    searchAddressButton.setEnabled(true);
                    searchAddressButton.setText("Buscar");
                    Log.e(TAG, "Error inesperado en geocodificación", e);
                    showError("Error inesperado al buscar la dirección");
                });
            }
        }).start();
    }

    private String getRoleFromLabel(String roleLabel) {
        switch (roleLabel) {
            case "Agricultor":
                return "farmer";
            case "Supermercado":
                return "supermarket";
            case "Consumidor":
            default:
                return "consumer";
        }
    }
} 