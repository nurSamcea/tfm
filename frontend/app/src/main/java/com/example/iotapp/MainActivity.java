package com.example.iotapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.frontend.R;
import com.example.iotapp.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar la navegación
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Configurar la visibilidad de los elementos del menú según el tipo de usuario
        setupNavigationForUserType();
    }

    private void setupNavigationForUserType() {
        // TODO: Obtener el tipo de usuario actual desde SharedPreferences o la sesión
        User.UserType currentUserType = User.UserType.CONSUMER; // Ejemplo

        switch (currentUserType) {
            case CONSUMER:
                bottomNavigationView.inflateMenu(R.menu.consumer_nav_menu);
                break;
            case FARMER:
                bottomNavigationView.inflateMenu(R.menu.farmer_nav_menu);
                break;
            case SUPERMARKET:
                bottomNavigationView.inflateMenu(R.menu.supermarket_nav_menu);
                break;
        }
    }
} 