package com.example.frontend.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.frontend.WelcomeActivity;
import com.example.frontend.R;
import com.example.frontend.api.AuthService;
import com.example.frontend.api.RegisterRequest;
import com.example.frontend.api.RegisterResponse;
import com.example.frontend.api.RetrofitClient;
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
    private Button registerButton;
    private Button backToLoginButton;
    private ProgressBar progressBar;
    private TextView errorText;
    
    private AuthService authService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        
        // Inicializar servicios
        authService = RetrofitClient.getInstance(requireContext()).getAuthService();
        sessionManager = new SessionManager(requireContext());
        
        // Inicializar vistas
        nameInput = view.findViewById(R.id.name_input);
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        confirmPasswordInput = view.findViewById(R.id.confirm_password_input);
        roleSpinner = view.findViewById(R.id.role_spinner);
        registerButton = view.findViewById(R.id.register_button);
        backToLoginButton = view.findViewById(R.id.back_to_login_button);
        progressBar = view.findViewById(R.id.progress_bar);
        errorText = view.findViewById(R.id.error_text);
        
        // Configurar spinner de roles
        setupRoleSpinner();
        
        // Configurar listeners
        registerButton.setOnClickListener(v -> performRegister());
        backToLoginButton.setOnClickListener(v -> navigateToLogin());
        
        return view;
    }

    private void setupRoleSpinner() {
        String[] roles = {"consumer", "farmer", "supermarket"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            roles
        );
        roleSpinner.setAdapter(adapter);
        roleSpinner.setText(roles[0], false); // Establecer valor por defecto
    }

    private void performRegister() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String role = roleSpinner.getText().toString();
        
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
        
        // Mostrar loading
        setLoading(true);
        hideError();
        
        // Crear request
        RegisterRequest registerRequest = new RegisterRequest(name, email, password, role);
        
        // Realizar petición
        authService.register(registerRequest).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                setLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    
                    Toast.makeText(requireContext(), 
                        "Registro exitoso. Ahora puedes iniciar sesión", 
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
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
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
} 