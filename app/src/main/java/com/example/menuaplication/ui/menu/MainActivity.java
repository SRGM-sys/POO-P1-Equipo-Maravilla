package com.example.menuaplication.ui.menu; // Asegúrate de usar tu paquete

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.example.menuaplication.R;
import com.example.menuaplication.ui.hidratacion.ControlHidratacionActivity;
import com.google.android.material.button.MaterialButton;

/*
Lumen
"Significa 'claridad' en latín. La app sirve para despejar el caos mental, organizar tareas y
mantener el foco. Básicamente, pone el foco en lo importante del día."
 */

public class MainActivity extends AppCompatActivity {
    private TextView btnCreditos;

    private CardView cardActivities, cardHydration, cardEco, cardGame;
    private MaterialButton btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        // Ocultar la barra de título superior para que se vea el diseño full screen
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        inicializarVistas();
        configurarListeners();
    }

    private void inicializarVistas() {
        cardActivities = findViewById(R.id.cardActivities);
        cardHydration = findViewById(R.id.cardHydration);
        cardEco = findViewById(R.id.cardEco);
        cardGame = findViewById(R.id.cardGame);
        btnExit = findViewById(R.id.btnExit);
        btnCreditos = findViewById(R.id.btnCreditos);
    }


    private void configurarListeners() {
        /*
        // Navegación a Actividad 1: Gestión de Actividades
        cardActivities.setOnClickListener(v -> {
            // Reemplaza 'GestionActividadesActivity.class' con el nombre real de tu clase
            Intent intent = new Intent(MainActivity.this, GestionActividadesActivity.class);
            startActivity(intent);
        });
        */

        // Navegación a Actividad 2: Hidratación
        cardHydration.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ControlHidratacionActivity.class);
            startActivity(intent);
        });

        /*
        // Navegación a Actividad 3: Sostenibilidad
        cardEco.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroSostenibilidadActivity.class);
            startActivity(intent);
        });

        // Navegación a Actividad 4: Juego
        cardGame.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JuegoMemoriaActivity.class);
            startActivity(intent);
        });
        */



    // Botón de Salir
        btnExit.setOnClickListener(v -> {
            // Opción A: Cerrar solo esta actividad
            // finish();

            // Opción B: Cerrar toda la app completamente (Recomendada para botón "Salir")
            finishAffinity();
            System.exit(0);
        });

        btnCreditos.setOnClickListener(v -> {
            mostrarDialogoCreditos();
        });
    }

    private void mostrarDialogoCreditos() {
        // Crear el diálogo
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_creditos);

        // Esto es CRUCIAL para que se vean los bordes redondeados:
        // Le dice a Android que el fondo "cuadrado" por defecto sea transparente
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        dialog.show();
    }
}