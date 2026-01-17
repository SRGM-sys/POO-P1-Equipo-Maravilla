package com.example.menuaplication.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.example.menuaplication.R;
// TU IMPORTACIÓN IMPORTANTE
import com.example.menuaplication.ui.actividades.ListaActividadesActivity;
import com.example.menuaplication.ui.hidratacion.ControlHidratacionActivity;
import com.google.android.material.button.MaterialButton;
import com.example.menuaplication.ui.sostenibilidad.RegistroSostenibilidadActivity;
import com.example.menuaplication.ui.juego.JuegoMemoriaActivity;

public class MainActivity extends AppCompatActivity {
    private TextView btnCreditos;
    private CardView cardActivities, cardHydration, cardEco, cardGame;
    private MaterialButton btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        inicializarVistas();
        configurarListeners();
    }

    private void inicializarVistas() {
        // Asegúrate de que estos IDs existan en tu XML activity_main
        cardActivities = findViewById(R.id.cardActivities);
        cardHydration = findViewById(R.id.cardHydration);
        cardEco = findViewById(R.id.cardEco);
        cardGame = findViewById(R.id.cardGame);
        btnExit = findViewById(R.id.btnExit);
        btnCreditos = findViewById(R.id.btnCreditos);
    }

    private void configurarListeners() {
        // --- TU PARTE: GESTIÓN DE ACTIVIDADES ---
        if (cardActivities != null) {
            cardActivities.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ListaActividadesActivity.class);
                startActivity(intent);
            });
        }

        // --- PARTE HIDRATACIÓN (COMPAÑERO) ---
        if (cardHydration != null) {
            cardHydration.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ControlHidratacionActivity.class);
                startActivity(intent);
            });
        }

        // --- PARTE ECO ---
        if (cardEco != null) {
    cardEco.setOnClickListener(v -> {
        Intent intent = new Intent(MainActivity.this, RegistroSostenibilidadActivity.class);
        startActivity(intent);
    });
        }

        // --- PARTE JUEGO ---

        if (cardGame != null) {
            cardGame.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, JuegoMemoriaActivity.class);
                startActivity(intent);
            });
        }

        if (btnExit != null) {
            btnExit.setOnClickListener(v -> {
                finishAffinity();
                System.exit(0);
            });
        }

        if (btnCreditos != null) {
            btnCreditos.setOnClickListener(v -> mostrarDialogoCreditos());
        }
    }

    private void mostrarDialogoCreditos() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_creditos);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();
    }
}