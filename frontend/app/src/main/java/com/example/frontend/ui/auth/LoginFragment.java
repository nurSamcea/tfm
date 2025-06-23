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
import com.example.frontend.databinding.FragmentLoginBinding;
import com.example.frontend.model.Farmer;
import com.example.frontend.model.Supermarket;
import com.example.frontend.model.User;
import com.example.frontend.model.LoginRequest;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import javax.inject.Inject;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    
    @Inject
    ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListeners();
    }

    private void setupListeners() {
        binding.buttonLogin.setOnClickListener(v -> performLogin());
        binding.buttonRegister.setOnClickListener(v -> 
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_login_to_register));
    }

    private void performLogin() {
        String email = binding.editTextEmail.getText().toString();
        String password = binding.editTextPassword.getText().toString();
        String userType = getUserType();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonLogin.setEnabled(false);

        apiService.login(new LoginRequest(email, password, userType))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.buttonLogin.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            navigateToHome(response.body());
                        } else {
                            Toast.makeText(requireContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.buttonLogin.setEnabled(true);
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

    private void navigateToHome(User user) {
        int actionId;
        if (user instanceof Farmer) {
            actionId = R.id.action_login_to_farmer_home;
        } else if (user instanceof Supermarket) {
            actionId = R.id.action_login_to_supermarket_home;
        } else {
            actionId = R.id.action_login_to_consumer_home;
        }

        Navigation.findNavController(requireView()).navigate(actionId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 