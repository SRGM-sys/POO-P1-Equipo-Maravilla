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
 * </ul>
 * También gestiona opciones generales como ver los créditos o salir de la aplicación.
 * </p>
 *
 * @author SRGM
 * @version 1.0
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

    /** Botón para cerrar y salir completamente de la aplicación. */
    private MaterialButton btnExit;

    /**
     * Método de inicialización del ciclo de vida de la actividad.
     * <p>
     * Se encarga de:
     * 1. Establecer el diseño de la interfaz de usuario.
     * 2. Forzar el modo claro (desactiva el modo noche).
     * 3. Ocultar la barra de acción (ActionBar) predeterminada.
     * 4. Inicializar las vistas y configurar los listeners.
     * </p>
     *
     * @param savedInstanceState Estado guardado de la actividad, si existe.
     */
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
     * <p>
     * Asocia los IDs (ej. {@code R.id.cardActivities}) con los objetos {@link CardView} y {@link MaterialButton}.
     * </p>
     */
    private void inicializarVistas() {
        cardActivities = findViewById(R.id.cardActivities);
        cardHydration = findViewById(R.id.cardHydration);
        cardEco = findViewById(R.id.cardEco);
        cardGame = findViewById(R.id.cardGame);
        btnExit = findViewById(R.id.btnExit);
        btnCreditos = findViewById(R.id.btnCreditos);
    }

    /**
     * Configura los eventos de clic (Listeners) para cada tarjeta y botón de la interfaz.
     * Define la lógica de navegación mediante {@link Intent} explícitos hacia:
     * - {@link ListaActividadesActivity}
     * - {@link ControlHidratacionActivity}
     * - {@link RegistroSostenibilidadActivity}
     * - {@link JuegoMemoriaActivity}
     *
     * También configura la acción del botón de salida y el diálogo de créditos.
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

        // --- PARTE JUEGO ---
        if (cardGame != null) {
            cardGame.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, InicioJuegoActivity.class);
                startActivity(intent);
            });
        }

        // --- SALIR ---
        if (btnExit != null) {
            btnExit.setOnClickListener(v -> {
                finishAffinity(); // Cierra todas las actividades de la pila
                System.exit(0);   // Termina el proceso del sistema
            });
        }

        // --- CRÉDITOS ---
        if (btnCreditos != null) {
            btnCreditos.setOnClickListener(v -> mostrarDialogoCreditos());
        }
    }

    /**
     * Muestra un cuadro de diálogo modal con los créditos de la aplicación.
     * Infla el diseño {@code dialog_creditos} y lo muestra con un fondo transparente
     * para mantener la estética redondeada definida en el XML del diálogo.
     */
    private void mostrarDialogoCreditos() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_creditos);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();
    }
}