package com.example.frontend;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frontend.ui.auth.LoginFragment;
import com.example.frontend.ui.auth.RegisterFragment;
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
import com.example.frontend.ui.UserSelectionFragment;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivityApp";
    
    private Button btnLogin;
    private Button btnRegister;
    private Button btnExistingUsers;
    private TextView footerText;
    
    // Componentes para el modo principal
    private String currentUserType = null;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout fragmentContainer;
    private SessionManager sessionManager;
    
    // Fragmentos para cada modo
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        // Asegurar que el status bar sea visible después de configurar la vista
        ensureStatusBarVisible();

        // Inicializar SessionManager
        sessionManager = new SessionManager(this);

        // Inicializar vistas primero
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnExistingUsers = findViewById(R.id.btn_existing_users);
        footerText = findViewById(R.id.footer_text);
        fragmentContainer = findViewById(R.id.fragment_container);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Verificar si hay una sesión activa al iniciar (después de inicializar las vistas)
        if (checkActiveSession()) {
            return; // Si hay sesión activa, no continuar con la configuración normal
        }
        
        // Botón de Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Botón de login clickeado");
                showLoginFragment();
            }
        });

        // Botón de Registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterFragment();
            }
        });

        // Botón de Usuarios Existentes (Demo)
        btnExistingUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserSelectionFragment();
            }
        });

        // Configurar el manejo del botón atrás
        setupBackPressHandler();
        
        // Verificar si el usuario ya está en un modo específico
        checkCurrentMode();
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            private long lastBackPressTime = 0;
            private static final long DOUBLE_BACK_PRESS_INTERVAL = 2000; // 2 segundos
            
            @Override
            public void handleOnBackPressed() {
                // Si hay una sesión activa, nunca volver a WelcomeActivity
                if (sessionManager.isLoggedIn() && currentUserType != null) {
                    long currentTime = System.currentTimeMillis();
                    
                    // Si es el primer retroceso o han pasado más de 2 segundos
                    if (lastBackPressTime == 0 || (currentTime - lastBackPressTime) > DOUBLE_BACK_PRESS_INTERVAL) {
                        lastBackPressTime = currentTime;
                        Toast.makeText(WelcomeActivity.this, "Presiona retroceso nuevamente para salir", Toast.LENGTH_SHORT).show();
                    } else {
                        // Segundo retroceso en menos de 2 segundos - confirmar salida
                        new AlertDialog.Builder(WelcomeActivity.this)
                                .setTitle("Salir de EcoMarket")
                                .setMessage("¿Estás seguro de que quieres salir de la aplicación?")
                                .setPositiveButton("Sí", (dialog, which) -> {
                                    finish();
                                    System.exit(0);
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                } else if (currentUserType != null) {
                    // Si estamos en un modo específico pero sin sesión, volver a la selección
                    backToModeSelection();
                } else if (currentFragment instanceof LoginFragment || currentFragment instanceof RegisterFragment) {
                    // Si estamos en login/registro, volver a la selección de modo
                    backToModeSelection();
                } else {
                    // Confirmar salida de la aplicación (sin sesión activa)
                    new AlertDialog.Builder(WelcomeActivity.this)
                            .setTitle("Salir de EcoMarket")
                            .setMessage("¿Estás seguro de que quieres salir de la aplicación?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                finish();
                                System.exit(0);
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            }
        });
    }

    /**
     * Verifica si hay una sesión activa y navega automáticamente al modo correspondiente
     * @return true si hay sesión activa y se navegó automáticamente, false en caso contrario
     */
    private boolean checkActiveSession() {
        if (sessionManager.isLoggedIn()) {
            String userRole = sessionManager.getUserRole();
            String userName = sessionManager.getUserName();
            
            Log.d(TAG, "Sesión activa encontrada para usuario: " + userName + " con rol: " + userRole);
            
            if (userRole != null) {
                // Navegar directamente al modo correspondiente
                switchToMode(userRole);
                return true;
            }
        }
        return false;
    }

    private void checkCurrentMode() {
        // Verificar si ya estamos en un modo específico
        if (currentUserType != null) {
            switchToMode(currentUserType);
        }
    }

    public void switchToMode(String userType) {
        Log.d(TAG, "Cambiando a modo: " + userType);
        currentUserType = userType;

        // Ocultar la selección de modo
        hideModeSelection();

        // Mostrar la navegación del modo
        showModeNavigation(userType);

        // Cargar el fragmento inicial del modo
        loadInitialFragment(userType);
    }

    private void hideModeSelection() {
        // Ocultar elementos de selección de modo
        findViewById(R.id.mode_selection_container).setVisibility(View.GONE);
        // Mostrar contenedor de fragmentos
        fragmentContainer.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);
        // Ocultar footer cuando se está en un modo específico (la bottom navigation lo reemplaza)
        if (footerText != null) {
            footerText.setVisibility(View.GONE);
        }
    }

    private void showModeNavigation(String userType) {
        bottomNavigationView.setVisibility(View.VISIBLE);
        
        // Limpiar el menú anterior antes de cargar el nuevo
        bottomNavigationView.getMenu().clear();
        Log.d(TAG, "Menú limpiado, cargando menú para: " + userType);
        
        // Configurar el menú según el tipo de usuario
        int menuResId;
        switch (userType) {
            case "consumer":
                menuResId = R.menu.bottom_nav_consumer;
                break;
            case "farmer":
                menuResId = R.menu.bottom_nav_farmer;
                break;
            case "supermarket":
                menuResId = R.menu.bottom_nav_supermarket;
                break;
            default:
                return;
        }
        
        bottomNavigationView.inflateMenu(menuResId);
        setupNavigationListener(userType);
    }

    private void setupNavigationListener(String userType) {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            switch (userType) {
                case "consumer":
                    if (itemId == R.id.navigation_consumer_products) {
                        selectedFragment = new ConsumerProductsFragment();
                    } else if (itemId == R.id.navigation_consumer_purchases) {
                        selectedFragment = new ConsumerPurchasesFragment();
                    } else if (itemId == R.id.navigation_consumer_profile) {
                        selectedFragment = new ConsumerProfileFragment();
                    }
                    break;
                case "farmer":
                    if (itemId == R.id.navigation_farmer_stock) {
                        selectedFragment = new FarmerStockFragment();
                    } else if (itemId == R.id.navigation_farmer_orders) {
                        selectedFragment = new FarmerOrdersFragment();
                    } else if (itemId == R.id.navigation_farmer_statistics) {
                        selectedFragment = new FarmerStatisticsFragment();
                    } else if (itemId == R.id.navigation_farmer_profile) {
                        selectedFragment = new FarmerProfileFragment();
                    }
                    break;
                case "supermarket":
                    if (itemId == R.id.navigation_supermarket_suppliers) {
                        selectedFragment = new SupermarketSuppliersFragment();
                    } else if (itemId == R.id.navigation_supermarket_inventory) {
                        selectedFragment = new SupermarketInventoryFragment();
                    } else if (itemId == R.id.navigation_supermarket_profile) {
                        selectedFragment = new SupermarketProfileFragment();
                    }
                    break;
            }

            if (selectedFragment != null) {
                switchFragment(selectedFragment);
            }
            return true;
        });
    }

    private void loadInitialFragment(String userType) {
        Fragment initialFragment = null;
        
        switch (userType) {
            case "consumer":
                initialFragment = new ConsumerProductsFragment();
                break;
            case "farmer":
                initialFragment = new FarmerStockFragment();
                break;
            case "supermarket":
                initialFragment = new SupermarketInventoryFragment();
                break;
        }
        
        if (initialFragment != null) {
            switchFragment(initialFragment);
            // Seleccionar el primer ítem del menú
            bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(0).getItemId());
        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
        
        currentFragment = fragment;
    }

    public void showLoginFragment() {
        Log.d(TAG, "showLoginFragment() llamado");
        // Mostrar fragmento de login
        LoginFragment loginFragment = new LoginFragment();
        switchFragment(loginFragment);
        
        // Ocultar elementos de selección
        findViewById(R.id.mode_selection_container).setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE);
        // Mostrar footer en el fragmento de login
        if (footerText != null) {
            footerText.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "Fragmento de login mostrado");
    }

    public void showRegisterFragment() {
        // Mostrar fragmento de registro
        RegisterFragment registerFragment = new RegisterFragment();
        switchFragment(registerFragment);
        
        // Ocultar elementos de selección
        findViewById(R.id.mode_selection_container).setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE);
        // Mostrar footer en el fragmento de registro
        if (footerText != null) {
            footerText.setVisibility(View.VISIBLE);
        }
    }

    private void backToModeSelection() {
        currentUserType = null;
        currentFragment = null;
        
        // Mostrar selección de modo
        findViewById(R.id.mode_selection_container).setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        // Mostrar footer cuando se está en la pantalla principal
        if (footerText != null) {
            footerText.setVisibility(View.VISIBLE);
        }
        
        // Limpiar fragmentos
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void showUserSelectionFragment() {
        // Mostrar fragmento de selección de usuarios
        UserSelectionFragment userSelectionFragment = new UserSelectionFragment();
        switchFragment(userSelectionFragment);
        
        // Ocultar elementos de selección
        findViewById(R.id.mode_selection_container).setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.GONE);
        // Mostrar footer en el fragmento de selección de usuarios
        if (footerText != null) {
            footerText.setVisibility(View.VISIBLE);
        }
    }

    public void logout() {
        // Cerrar sesión
        sessionManager.logout();
        
        // Limpiar estado
        currentUserType = null;
        currentFragment = null;
        
        // Mostrar menú principal (WelcomeActivity)
        showMainMenu();
        
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        
        Log.d(TAG, "Sesión cerrada, regresando a WelcomeActivity");
    }

    public void showMainMenu() {
        // Mostrar selección de modo
        findViewById(R.id.mode_selection_container).setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        // Mostrar footer cuando se está en el menú principal
        if (footerText != null) {
            footerText.setVisibility(View.VISIBLE);
        }
        
        // Limpiar fragmento actual
        if (currentFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(currentFragment);
            transaction.commit();
            currentFragment = null;
        }
    }
    
    /**
     * Asegura que el status bar sea visible y tenga el comportamiento correcto
     */
    private void ensureStatusBarVisible() {
        try {
            // Configurar colores del status bar y navigation bar
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.primary));
            
            // Asegurar que no esté en modo fullscreen
            getWindow().getDecorView().setSystemUiVisibility(
                getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_FULLSCREEN
            );
            
            // Configurar flags para layout estable
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            getWindow().getDecorView().setSystemUiVisibility(flags);
            
            Log.d(TAG, "Status bar configurado correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error al configurar status bar: " + e.getMessage());
        }
    }
}
