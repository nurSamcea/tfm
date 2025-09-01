package com.example.frontend;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.frontend.ui.auth.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_container);
        
        // Cargar el fragmento de login
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        }
    }
}
