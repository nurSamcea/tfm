package com.example.frontend;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class EcoMarketApplication extends Application {
    private static final String TAG = "EcoMarketApplication";

    @Override
    public void onCreate() {
        try {
            super.onCreate();
            Log.d(TAG, "onCreate: Iniciando la aplicación");
            
            // Inicializar componentes de la aplicación
            initializeApp();
            
            Log.d(TAG, "onCreate: Aplicación inicializada correctamente");
        } catch (Exception e) {
            Log.e(TAG, "onCreate: Error al inicializar la aplicación", e);
        }
    }

    private void initializeApp() {
        try {
            // Configurar el modo estricto para detectar problemas
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build());
            }
            
            // Inicializar otros componentes aquí
            Log.d(TAG, "initializeApp: Componentes inicializados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "initializeApp: Error al inicializar componentes", e);
            throw e;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate: Aplicación terminada");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "onLowMemory: Memoria baja detectada");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.w(TAG, "onTrimMemory: Nivel de memoria: " + level);
    }
} 