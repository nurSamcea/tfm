package com.example.frontend.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.frontend.api.ApiService;
import com.example.frontend.api.RetrofitClient;
import com.example.frontend.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar progressBar;
    private TextView errorText;
    
    private AuthService authService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "LoginFragment onCreateView() llamado");
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        
        // Inicializar servicios
        authService = RetrofitClient.getInstance(requireContext()).getAuthService();
        sessionManager = new SessionManager(requireContext());
        
        // No verificar automáticamente si está logueado
        // Permitir que el usuario siempre pueda acceder al formulario de login
        
        // Si ya está logueado, mostrar un mensaje informativo
        if (sessionManager.isLoggedIn()) {
            Toast.makeText(requireContext(), 
                "Ya tienes una sesión activa. Puedes hacer login con otra cuenta.", 
                Toast.LENGTH_LONG).show();
        }
        
        // Inicializar vistas
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);
        progressBar = view.findViewById(R.id.progress_bar);
        errorText = view.findViewById(R.id.error_text);
        
        // Configurar listeners
        loginButton.setOnClickListener(v -> performLogin());
        registerButton.setOnClickListener(v -> navigateToRegister());
        
        return view;
    }

    private void performLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        
        // Validaciones
        if (email.isEmpty()) {
            showError("Por favor ingresa tu email");
            return;
        }
        
        if (password.isEmpty()) {
            showError("Por favor ingresa tu contraseña");
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Por favor ingresa un email válido");
            return;
        }
        
        // Mostrar loading
        setLoading(true);
        hideError();
        
        // Crear request
        ApiService.LoginRequest loginRequest = new ApiService.LoginRequest(email, password);
        
        // Realizar petición
        authService.login(loginRequest).enqueue(new Callback<ApiService.LoginResponse>() {
            @Override
            public void onResponse(Call<ApiService.LoginResponse> call, Response<ApiService.LoginResponse> response) {
                setLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiService.LoginResponse loginResponse = response.body();
                    
                    // Si ya había una sesión activa, cerrarla primero
                    if (sessionManager.isLoggedIn()) {
                        sessionManager.logout();
                    }
                    
                    // Guardar nueva sesión con información del usuario
                    sessionManager.createLoginSession(
                        loginResponse.access_token,
                        loginResponse.user_id,
                        loginResponse.user_email,
                        loginResponse.user_role,
                        loginResponse.user_name
                    );
                    
                    Toast.makeText(requireContext(), 
                        "¡Bienvenido, " + loginResponse.user_name + "!", 
                        Toast.LENGTH_SHORT).show();
                    
                    // Navegar directamente al modo correspondiente según el rol
                    if (getActivity() instanceof WelcomeActivity) {
                        ((WelcomeActivity) getActivity()).switchToMode(loginResponse.user_role);
                    }
                    
                } else {
                    String errorMessage = "Credenciales inválidas";
                    if (response.code() == 401) {
                        errorMessage = "Email o contraseña incorrectos";
                    } else if (response.code() >= 500) {
                        errorMessage = "Error del servidor. Intenta más tarde";
                    }
                    showError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<ApiService.LoginResponse> call, Throwable t) {
                setLoading(false);
                Log.e(TAG, "Login error", t);
                showError("Error de conexión. Verifica tu internet");
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!loading);
        registerButton.setEnabled(!loading);
        emailInput.setEnabled(!loading);
        passwordInput.setEnabled(!loading);
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorText.setVisibility(View.GONE);
    }

    private void navigateToRegister() {
        // Mostrar fragmento de registro en WelcomeActivity
        if (getActivity() instanceof WelcomeActivity) {
            ((WelcomeActivity) getActivity()).showRegisterFragment();
        }
    }
}