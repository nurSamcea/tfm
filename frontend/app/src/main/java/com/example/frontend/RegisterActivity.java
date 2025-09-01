package com.example.frontend;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.frontend.ui.auth.RegisterFragment;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_container);
        
        // Cargar el fragmento de registro
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .commit();
        }
    }
}
