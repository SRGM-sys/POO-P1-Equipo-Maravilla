package com.example.menuaplication.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate; // Para el modo claro

import com.example.menuaplication.ui.menu.MainActivity;
import com.example.menuaplication.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Forzamos modo claro (igual que en tu menú)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_splash);

        // 2. Ocultar la barra superior para que se vea pantalla completa
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 3. Temporizador de 3 segundos (3000 milisegundos)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Código que se ejecuta después del tiempo
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                // IMPORTANTE: Matar la SplashActivity para que si el usuario
                // da click en "Atrás", no vuelva a ver la pantalla de carga.
                finish();
            }
        }, 3000); // <-- Aquí cambias el tiempo si quieres
    }
}