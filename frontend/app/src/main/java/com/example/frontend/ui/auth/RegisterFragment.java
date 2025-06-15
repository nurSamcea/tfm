package com.example.frontend.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.frontend.R;
import com.example.frontend.api.ApiService;
import com.example.frontend.databinding.FragmentRegisterBinding;
import com.example.frontend.model.Location;
import com.example.frontend.model.User;
import com.example.frontend.model.RegisterRequest;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import javax.inject.Inject;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    
    @Inject
    ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListeners();
    }

    private void setupListeners() {
        binding.buttonRegister.setOnClickListener(v -> performRegister());
        binding.buttonCancel.setOnClickListener(v -> 
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_register_to_login));
    }

    private void performRegister() {
        String name = binding.editTextName.getText().toString();
        String email = binding.editTextEmail.getText().toString();
        String password = binding.editTextPassword.getText().toString();
        String address = binding.editTextAddress.getText().toString();
        String userType = getUserType();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
            Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto Location con coordenadas por defecto (se actualizarán después)
        Location location = new Location(0.0, 0.0, address);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonRegister.setEnabled(false);

        apiService.register(new RegisterRequest(name, email, password, userType, location))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.buttonRegister.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(requireContext(), R.string.success_register, Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView())
                                    .navigate(R.id.action_register_to_login);
                        } else {
                            Toast.makeText(requireContext(), R.string.error_register, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.buttonRegister.setEnabled(true);
                        Toast.makeText(requireContext(), R.string.error_network, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getUserType() {
        int selectedId = binding.radioGroupUserType.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_farmer) {
            return "FARMER";
        } else if (selectedId == R.id.radio_supermarket) {
            return "SUPERMARKET";
        } else {
            return "CONSUMER";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 