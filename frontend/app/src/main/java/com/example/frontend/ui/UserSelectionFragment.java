package com.example.frontend.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.WelcomeActivity;
import com.example.frontend.api.ApiService;
import com.example.frontend.api.ApiClient;
import com.example.frontend.model.User;
import com.example.frontend.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSelectionFragment extends Fragment implements UserSelectionAdapter.OnUserSelectedListener {
    
    private static final String TAG = "UserSelectionFragment";
    
    private Spinner roleSpinner;
    private RecyclerView usersRecyclerView;
    private Button btnContinue;
    private Button btnBack;
    private ProgressBar progressBar;
    
    private UserSelectionAdapter adapter;
    private List<User> users = new ArrayList<>();
    private User selectedUser = null;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_selection, container, false);
        
        // Inicializar servicios
        apiService = ApiClient.getApiService(requireContext());
        sessionManager = new SessionManager(requireContext());
        
        // Inicializar vistas
        roleSpinner = view.findViewById(R.id.role_spinner);
        usersRecyclerView = view.findViewById(R.id.users_recycler_view);
        btnContinue = view.findViewById(R.id.btn_continue);
        btnBack = view.findViewById(R.id.btn_back);
        progressBar = view.findViewById(R.id.progress_bar);
        
        // Configurar spinner de roles
        setupRoleSpinner();
        
        // Configurar RecyclerView
        setupRecyclerView();
        
        // Configurar listeners
        setupListeners();
        
        // Cargar usuarios del primer rol por defecto
        loadUsers("consumer");
        
        return view;
    }

    private void setupRoleSpinner() {
        String[] roles = {"consumer", "farmer", "supermarket"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            roles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
        
        // Listener para cambio de rol
        roleSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedRole = roles[position];
                loadUsers(selectedRole);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new UserSelectionAdapter(users, this);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        usersRecyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnContinue.setOnClickListener(v -> {
            if (selectedUser != null) {
                // Simular login con usuario seleccionado
                simulateLogin(selectedUser);
            } else {
                Toast.makeText(requireContext(), "Por favor selecciona un usuario", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnBack.setOnClickListener(v -> {
            // Navegar de vuelta
            if (getActivity() instanceof WelcomeActivity) {
                ((WelcomeActivity) getActivity()).showMainMenu();
            }
        });
    }

    private void loadUsers(String role) {
        showLoading(true);
        
        Call<List<User>> call = apiService.getUsersByRole(role);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    users = response.body();
                    adapter.updateUsers(users);
                    selectedUser = null;
                    updateContinueButton();
                    
                    if (users.isEmpty()) {
                        Toast.makeText(requireContext(), 
                            "No hay usuarios disponibles para este rol", 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), 
                        "Error al cargar usuarios", 
                        Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Error cargando usuarios", t);
                Toast.makeText(requireContext(), 
                    "Error de conexión", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        roleSpinner.setEnabled(!loading);
        btnContinue.setEnabled(!loading);
        btnBack.setEnabled(!loading);
    }

    private void updateContinueButton() {
        btnContinue.setEnabled(selectedUser != null);
    }

    @Override
    public void onUserSelected(User user) {
        selectedUser = user;
        updateContinueButton();
    }

    private void simulateLogin(User user) {
        // Crear una sesión simulada con el usuario seleccionado
        sessionManager.createLoginSession(
            "demo_token_" + user.getId(), // Token simulado
            user.getId(),
            user.getEmail(),
            user.getRole(),
            user.getName()
        );
        
        Toast.makeText(requireContext(), 
            "Sesión iniciada como: " + user.getName(), 
            Toast.LENGTH_SHORT).show();
        
        // Navegar a la actividad principal
        if (getActivity() instanceof WelcomeActivity) {
            ((WelcomeActivity) getActivity()).switchToMode(user.getRole());
        }
    }
}
