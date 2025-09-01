package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;

import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegister;
    private LinearLayout cardConsumer;
    private LinearLayout cardFarmer;
    private LinearLayout cardSupermarket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Inicializar vistas
        initViews();
        
        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        cardConsumer = findViewById(R.id.card_consumer);
        cardFarmer = findViewById(R.id.card_farmer);
        cardSupermarket = findViewById(R.id.card_supermarket);
    }

    private void setupListeners() {
        // Botón de Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la pantalla de login
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Botón de Registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la pantalla de registro
                Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Modo Consumidor
        cardConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar al modo consumidor
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("userType", "consumer");
                startActivity(intent);
            }
        });

        // Modo Agricultor
        cardFarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar al modo agricultor
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("userType", "farmer");
                startActivity(intent);
            }
        });

        // Modo Supermercado
        cardSupermarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar al modo supermercado
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("userType", "supermarket");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Confirmar salida de la aplicación
        new androidx.appcompat.app.AlertDialog.Builder(this)
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
