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

import com.example.frontend.MainActivity;
import com.example.frontend.R;
import com.example.frontend.api.AuthService;
import com.example.frontend.api.LoginRequest;
import com.example.frontend.api.LoginResponse;
import com.example.frontend.api.RetrofitClient;
import com.example.frontend.utils.SessionManager;
import com.example.frontend.utils.JwtDecoder;

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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        
        // Inicializar servicios
        authService = RetrofitClient.getInstance(requireContext()).getAuthService();
        sessionManager = new SessionManager(requireContext());
        
        // Verificar si ya está logueado
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
            return view;
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
        LoginRequest loginRequest = new LoginRequest(email, password);
        
        // Realizar petición
        authService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    // Decodificar token JWT para obtener información del usuario
                    String token = loginResponse.getAccessToken();
                    JwtDecoder.JwtPayload payload = JwtDecoder.decode(token);
                    
                    // Guardar sesión con información del token
                    sessionManager.createLoginSession(
                        token,
                        payload != null ? payload.userId : 0,
                        email,
                        payload != null ? payload.role : "consumer",
                        payload != null ? payload.name : email.split("@")[0]
                    );
                    
                    // Navegar a la actividad principal
                    navigateToMainActivity();
                    
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
            public void onFailure(Call<LoginResponse> call, Throwable t) {
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

    private void navigateToMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToRegister() {
        // TODO: Implementar navegación al registro
        Toast.makeText(requireContext(), "Funcionalidad de registro próximamente", Toast.LENGTH_SHORT).show();
    }
} 