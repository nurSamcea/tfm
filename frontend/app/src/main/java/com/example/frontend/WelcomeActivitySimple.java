package com.example.frontend;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivitySimple extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_simple);
        
        Toast.makeText(this, "Aplicación iniciada correctamente", Toast.LENGTH_SHORT).show();
    }
}
