package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.frontend.ui.auth.LoginFragment;
import com.example.frontend.ui.consumer.ConsumerProductsFragment;
import com.example.frontend.ui.consumer.ConsumerProfileFragment;
import com.example.frontend.ui.consumer.ConsumerPurchasesFragment;
import com.example.frontend.ui.farmer.FarmerOrdersFragment;
import com.example.frontend.ui.farmer.FarmerProfileFragment;
import com.example.frontend.ui.farmer.FarmerStatisticsFragment;
import com.example.frontend.ui.farmer.FarmerStockFragment;
import com.example.frontend.ui.supermarket.SupermarketInventoryFragment;
import com.example.frontend.ui.supermarket.SupermarketProfileFragment;
import com.example.frontend.ui.supermarket.SupermarketSuppliersFragment;
import com.example.frontend.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String userType = null;
    private BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "=== MAIN ACTIVITY ===");
        Log.d(TAG, "onCreate iniciado");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Super.onCreate() completado");
        
        // Inicializar SessionManager
        Log.d(TAG, "Inicializando SessionManager...");
        sessionManager = new SessionManager(this);
        Log.d(TAG, "SessionManager inicializado");
        
        // Obtener el tipo de usuario del Intent (si viene de WelcomeActivity)
        Intent intent = getIntent();
        Log.d(TAG, "Intent recibido: " + (intent != null ? "OK" : "NULL"));
        
        if (intent != null && intent.hasExtra("userType")) {
            userType = intent.getStringExtra("userType");
            Log.d(TAG, "User type from intent: " + userType);
            showMainScreen();
            return;
        }
        
        // Verificar si el usuario está logueado
        Log.d(TAG, "Verificando si el usuario está logueado...");
        boolean isLoggedIn = sessionManager.isLoggedIn();
        Log.d(TAG, "Usuario logueado: " + isLoggedIn);
        
        if (!isLoggedIn) {
            Log.d(TAG, "Usuario no logueado, mostrando pantalla de login");
            showLoginScreen();
            return;
        }
        
        // Usuario logueado, mostrar pantalla principal
        Log.d(TAG, "Usuario logueado, mostrando pantalla principal");
        showMainScreen();
    }

    private void showLoginScreen() {
        Log.d(TAG, "showLoginScreen: Mostrando pantalla de login");
        setContentView(R.layout.activity_login);
        
        // Configurar botones de login temporal (para desarrollo)
        Button btnConsumer = findViewById(R.id.btn_consumer);
        Button btnFarmer = findViewById(R.id.btn_farmer);
        Button btnSupermarket = findViewById(R.id.btn_supermarket);
        TextView skipLogin = findViewById(R.id.skip_login);

        skipLogin.setOnClickListener(v -> {
            Log.d(TAG, "Skip login clicked");
            userType = "consumer";
            showMainScreen();
        });

        btnConsumer.setOnClickListener(v -> {
            Log.d(TAG, "Consumer button clicked");
            userType = "consumer";
            showMainScreen();
        });

        btnFarmer.setOnClickListener(v -> {
            Log.d(TAG, "Farmer button clicked");
            userType = "farmer";
            showMainScreen();
        });

        btnSupermarket.setOnClickListener(v -> {
            Log.d(TAG, "Supermarket button clicked");
            userType = "supermarket";
            showMainScreen();
        });
    }

    private void showMainScreen() {
        Log.d(TAG, "=== MOSTRANDO PANTALLA PRINCIPAL ===");
        try {
            Log.d(TAG, "showMainScreen: Showing main screen for user type: " + userType);
            setContentView(R.layout.activity_main);
            Log.d(TAG, "Layout de actividad principal establecido");
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            Log.d(TAG, "BottomNavigationView encontrado: " + (bottomNavigationView != null ? "OK" : "NULL"));

            // Obtener el tipo de usuario de la sesión si no está definido
            if (userType == null && sessionManager.isLoggedIn()) {
                Log.d(TAG, "UserType es null, obteniendo de SessionManager...");
                userType = sessionManager.getUserRole();
                Log.d(TAG, "UserType obtenido de SessionManager: " + userType);
            }

            // Configurar el menú según el tipo de usuario
            Log.d(TAG, "Configurando menú para userType: " + userType);
            int menuResId;
            switch (userType) {
                case "consumer":
                    menuResId = R.menu.bottom_nav_consumer;
                    Log.d(TAG, "Menú configurado: CONSUMER");
                    break;
                case "farmer":
                    menuResId = R.menu.bottom_nav_farmer;
                    Log.d(TAG, "Menú configurado: FARMER");
                    break;
                case "supermarket":
                case "retailer":
                    menuResId = R.menu.bottom_nav_supermarket;
                    Log.d(TAG, "Menú configurado: SUPERMARKET/RETAILER");
                    break;
                default:
                    Log.e(TAG, "showMainScreen: Invalid user type: " + userType);
                    return;
            }

            Log.d(TAG, "Inflando menú con ID: " + menuResId);
            bottomNavigationView.inflateMenu(menuResId);
            Log.d(TAG, "Configurando listener de navegación...");
            bottomNavigationView.setOnItemSelectedListener(item -> {
                try {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();
                    Log.d(TAG, "Navigation item selected: " + itemId);

                    // Manejar la navegación según el tipo de usuario
                    if (userType.equals("consumer")) {
                        if (itemId == R.id.navigation_consumer_products) {
                            selectedFragment = new ConsumerProductsFragment();
                        } else if (itemId == R.id.navigation_consumer_purchases) {
                            selectedFragment = new ConsumerPurchasesFragment();
                        } else if (itemId == R.id.navigation_consumer_profile) {
                            selectedFragment = new ConsumerProfileFragment();
                        }
                    } else if (userType.equals("farmer")) {
                        if (itemId == R.id.navigation_farmer_stock) {
                            selectedFragment = new FarmerStockFragment();
                        } else if (itemId == R.id.navigation_farmer_orders) {
                            selectedFragment = new FarmerOrdersFragment();
                        } else if (itemId == R.id.navigation_farmer_statistics) {
                            selectedFragment = new FarmerStatisticsFragment();
                        } else if (itemId == R.id.navigation_farmer_profile) {
                            selectedFragment = new FarmerProfileFragment();
                        }
                    } else if (userType.equals("supermarket") || userType.equals("retailer")) {
                        if (itemId == R.id.navigation_supermarket_suppliers) {
                            selectedFragment = new SupermarketSuppliersFragment();
                        } else if (itemId == R.id.navigation_supermarket_inventory) {
                            selectedFragment = new SupermarketInventoryFragment();
                        } else if (itemId == R.id.navigation_supermarket_profile) {
                            selectedFragment = new SupermarketProfileFragment();
                        }
                    }

                    if (selectedFragment != null) {
                        Log.d(TAG, "Replacing fragment: " + selectedFragment.getClass().getSimpleName());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.nav_host_fragment, selectedFragment)
                                .commit();
                    } else {
                        Log.e(TAG, "No fragment selected for item ID: " + itemId);
                    }
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error in navigation: " + e.getMessage(), e);
                    return false;
                }
            });

            // Seleccionar el primer ítem por defecto
            int firstItemId = bottomNavigationView.getMenu().getItem(0).getItemId();
            Log.d(TAG, "Seleccionando primer ítem del menú: " + firstItemId);
            bottomNavigationView.setSelectedItemId(firstItemId);
            
            Log.d(TAG, "=== PANTALLA PRINCIPAL CONFIGURADA ===");
            Log.d(TAG, "UserType: " + userType);
            Log.d(TAG, "Menú inflado: " + menuResId);
            Log.d(TAG, "BottomNavigationView configurado");
            Log.d(TAG, "Listener de navegación configurado");
            Log.d(TAG, "Primer ítem seleccionado");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in showMainScreen: " + e.getMessage(), e);
        }
    }

    public void logout() {
        sessionManager.logout();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity destroyed");
    }
}
