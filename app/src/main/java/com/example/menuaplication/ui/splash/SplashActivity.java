package com.example.menuaplication.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.menuaplication.ui.menu.MainActivity;
import com.example.menuaplication.R;
import com.example.menuaplication.data.Repositorio; // <--- 1. NO OLVIDES ESTE IMPORT

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // -----------------------------------------------------------
        // 2. AQUÍ INICIALIZAMOS EL REPOSITORIO
        // Al pasarle 'this', le damos el contexto para que encuentre
        // la carpeta de archivos y cargue los datos guardados.
        // -----------------------------------------------------------
        Repositorio.getInstance(this);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}