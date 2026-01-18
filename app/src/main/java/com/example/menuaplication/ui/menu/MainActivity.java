package com.example.menuaplication.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.example.menuaplication.R;
import com.example.menuaplication.ui.actividades.ListaActividadesActivity;
import com.example.menuaplication.ui.hidratacion.ControlHidratacionActivity;
import com.example.menuaplication.ui.buscaminas.BuscaminasActivity;
import com.example.menuaplication.ui.juego.InicioJuegoActivity;
import com.google.android.material.button.MaterialButton;
import com.example.menuaplication.ui.sostenibilidad.RegistroSostenibilidadActivity;
import com.example.menuaplication.ui.juego.JuegoMemoriaActivity;
import com.example.menuaplication.ui.sostenibilidad.ResumenSostenibilidadActivity;

/**
 * Actividad principal que sirve como menú de navegación central de la aplicación.
 * <p>
 * Esta clase presenta un panel ("Dashboard") con tarjetas interactivas que permiten
 * al usuario navegar hacia los diferentes módulos funcionales:
 * <ul>
 * <li>Gestión de Actividades y Tareas.</li>
 * <li>Control de Hidratación.</li>
 * <li>Registro de Sostenibilidad (Eco).</li>
 * <li>Juego de Memoria.</li>
 * <li>Juego de Buscaminas Halloween.</li>
 * </ul>
 * También gestiona opciones generales como ver los créditos o salir de la aplicación.
 * </p>
 *
 * @author SRGM
 * @version 1.1
 */
public class MainActivity extends AppCompatActivity {

    /** Botón de texto para mostrar el diálogo de créditos. */
    private TextView btnCreditos;

    /** Tarjeta de navegación hacia el módulo de Gestión de Actividades. */
    private CardView cardActivities;

    /** Tarjeta de navegación hacia el módulo de Control de Hidratación. */
    private CardView cardHydration;

    /** Tarjeta de navegación hacia el módulo de Sostenibilidad (Eco). */
    private CardView cardEco;

    /** Tarjeta de navegación hacia el módulo de Juegos (Memoria). */
    private CardView cardGame;

    /** Tarjeta de navegación hacia el módulo de Juego (Buscaminas). */
    private CardView cardBuscaminas;

    /** Botón para cerrar y salir completamente de la aplicación. */
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

    /**
     * Vincula las variables de la clase con los componentes visuales definidos en el archivo XML.
     */
    private void inicializarVistas() {
        cardActivities = findViewById(R.id.cardActivities);
        cardHydration = findViewById(R.id.cardHydration);
        cardEco = findViewById(R.id.cardEco);
        cardGame = findViewById(R.id.cardGame);
        cardBuscaminas = findViewById(R.id.cardBuscaminas); // Inicialización del Buscaminas
        btnExit = findViewById(R.id.btnExit);
        btnCreditos = findViewById(R.id.btnCreditos);
    }

    /**
     * Configura los eventos de clic (Listeners) para cada tarjeta y botón de la interfaz.
     */
    private void configurarListeners() {
        // --- GESTIÓN DE ACTIVIDADES ---
        if (cardActivities != null) {
            cardActivities.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ListaActividadesActivity.class);
                startActivity(intent);
            });
        }

        // --- PARTE HIDRATACIÓN ---
        if (cardHydration != null) {
            cardHydration.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ControlHidratacionActivity.class);
                startActivity(intent);
            });
        }

        // --- PARTE ECO ---
        if (cardEco != null) {
            cardEco.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, ResumenSostenibilidadActivity.class));
            });
        }

        // --- PARTE JUEGO MEMORIA ---
        if (cardGame != null) {
            cardGame.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, InicioJuegoActivity.class);
                startActivity(intent);
            });
        }

        // --- PARTE BUSCAMINAS HALLOWEEN ---
        if (cardBuscaminas != null) {
            cardBuscaminas.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, BuscaminasActivity.class);
                startActivity(intent);
            });
        }

        // --- SALIR ---
        if (btnExit != null) {
            btnExit.setOnClickListener(v -> {
                finishAffinity();
                System.exit(0);
            });
        }

        // --- CRÉDITOS ---
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