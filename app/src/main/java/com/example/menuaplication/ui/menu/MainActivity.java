package com.example.menuaplication.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.example.menuaplication.R;
import com.example.menuaplication.data.GestorSesion;
import com.example.menuaplication.ui.actividades.ListaActividadesActivity;
import com.example.menuaplication.ui.hidratacion.ControlHidratacionActivity;
import com.example.menuaplication.ui.juego.InicioJuegoActivity;
import com.example.menuaplication.ui.sostenibilidad.ResumenSostenibilidadActivity;
import com.google.android.material.button.MaterialButton;

/**
 * Actividad Principal (Dashboard) de la aplicación.
 * <p>
 * Esta clase funciona como el centro de navegación ("Hub") de la app. Gestiona:
 * <ul>
 * <li>La navegación a los módulos principales (Actividades, Hidratación, Sostenibilidad, Juegos).</li>
 * <li>La sesión del usuario (Inicio/Cierre de sesión y persistencia de datos).</li>
 * <li>La personalización del perfil (foto de usuario con permisos persistentes).</li>
 * </ul>
 * </p>
 *
 * @author SRGM
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    // --------------------------------------------------------------------------------------
    // SECCIÓN 1: VARIABLES Y COMPONENTES DE UI
    // --------------------------------------------------------------------------------------

    /** Botón para mostrar los créditos de la aplicación. */
    private TextView btnCreditos;

    /** Texto de bienvenida que cambia según el estado de la sesión. */
    private TextView tvBienvenida;

    /** Imagen de perfil del usuario. Permite cambiar la foto al hacer clic. */
    private ImageView ivPerfilUsuario;

    /** Tarjetas de navegación para los diferentes módulos. */
    private CardView cardActivities, cardHydration, cardEco, cardGame;

    /** Botón para cerrar completamente la aplicación. */
    private MaterialButton btnExit;

    /** Botón dinámico para iniciar o cerrar sesión. */
    private Button btnIniciarSesion;

    /** Botón para acceder al menú de minijuegos (Arcade). */
    private Button btnMenuJuegos;

    // --------------------------------------------------------------------------------------
    // SECCIÓN 2: LÓGICA Y HERRAMIENTAS
    // --------------------------------------------------------------------------------------

    /** Clase auxiliar para gestionar SharedPreferences y la persistencia de sesión. */
    private GestorSesion gestorSesion;

    /**
     * Lanzador de actividad para seleccionar una imagen de la galería.
     * <p>
     * <b>Nota importante:</b> Incluye la lógica para solicitar {@code takePersistableUriPermission}.
     * Esto es crucial para que la foto siga visible después de reiniciar el teléfono o la app,
     * ya que evita que Android revoque el permiso de lectura de la URI.
     * </p>
     */
    private final ActivityResultLauncher<String> selectorGaleria = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // A. Solicitar Permiso Persistente
                    try {
                        getContentResolver().takePersistableUriPermission(uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        e.printStackTrace(); // Manejo de error si el proveedor no soporta persistencia
                    }

                    // B. Guardar la URI en GestorSesion
                    gestorSesion.guardarFoto(uri.toString());

                    // C. Actualizar la vista inmediatamente
                    if (ivPerfilUsuario != null) ivPerfilUsuario.setImageURI(uri);
                }
            }
    );

    // --------------------------------------------------------------------------------------
    // SECCIÓN 3: CICLO DE VIDA DE LA ACTIVIDAD
    // --------------------------------------------------------------------------------------

    /**
     * Método de creación de la actividad.
     * <p>
     * Configura el tema, inicializa el gestor de sesión, vincula las vistas,
     * actualiza la interfaz según el estado del usuario y configura los listeners.
     * </p>
     *
     * @param savedInstanceState Estado guardado de la instancia anterior, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Forzamos modo claro para mantener consistencia visual
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        // Ocultar la ActionBar por defecto para usar diseño personalizado
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // 1. Inicializar el Gestor (Carga datos guardados)
        gestorSesion = new GestorSesion(this);

        inicializarVistas();

        // 2. Actualizar la pantalla según si hay sesión o no
        actualizarInterfazSesion();

        configurarListeners();
    }

    // --------------------------------------------------------------------------------------
    // SECCIÓN 4: CONFIGURACIÓN E INICIALIZACIÓN
    // --------------------------------------------------------------------------------------

    /**
     * Vincula las variables Java con los componentes del archivo XML (Layout).
     */
    private void inicializarVistas() {
        cardActivities = findViewById(R.id.cardActivities);
        cardHydration = findViewById(R.id.cardHydration);
        cardEco = findViewById(R.id.cardEco);
        cardGame = findViewById(R.id.cardGame);
        btnExit = findViewById(R.id.btnExit);
        btnCreditos = findViewById(R.id.btnCreditos);

        // Nuevos elementos de sesión
        tvBienvenida = findViewById(R.id.tvBienvenida);
        ivPerfilUsuario = findViewById(R.id.ivPerfilUsuario);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnMenuJuegos = findViewById(R.id.btnMenuJuegos);
    }

    /**
     * Configura los escuchadores de eventos (Listeners) para todos los botones y tarjetas.
     * Define la navegación y las acciones principales.
     */
    private void configurarListeners() {
        // --- NAVEGACIÓN A MÓDULOS ---
        if (cardActivities != null) cardActivities.setOnClickListener(v -> startActivity(new Intent(this, ListaActividadesActivity.class)));
        if (cardHydration != null) cardHydration.setOnClickListener(v -> startActivity(new Intent(this, ControlHidratacionActivity.class)));
        if (cardEco != null) cardEco.setOnClickListener(v -> startActivity(new Intent(this, ResumenSostenibilidadActivity.class)));
        if (cardGame != null) cardGame.setOnClickListener(v -> startActivity(new Intent(this, InicioJuegoActivity.class)));

        // --- ACCIONES GENERALES ---
        if (btnExit != null) btnExit.setOnClickListener(v -> {
            finishAffinity(); // Cierra todas las actividades de la pila
            System.exit(0);   // Fuerza el cierre del proceso
        });

        if (btnCreditos != null) btnCreditos.setOnClickListener(v -> mostrarDialogoCreditos());

        // --- LÓGICA DELEGADA A CLASES EXTERNAS ---

        // 1. Botón Juegos -> Llama al diálogo de selección de juegos
        if (btnMenuJuegos != null) {
            btnMenuJuegos.setOnClickListener(v -> DialogoJuegos.mostrar(this));
        }

        // 2. Botón Sesión -> Gestiona Login/Logout
        if (btnIniciarSesion != null) {
            btnIniciarSesion.setOnClickListener(v -> manejarClickSesion());
        }

        // 3. Foto de Perfil -> Selector de galería (Solo si hay sesión activa)
        if (ivPerfilUsuario != null) {
            ivPerfilUsuario.setOnClickListener(v -> {
                if (gestorSesion.haySesionActiva()) selectorGaleria.launch("image/*");
            });
        }
    }

    // --------------------------------------------------------------------------------------
    // SECCIÓN 5: LÓGICA DE SESIÓN Y UI
    // --------------------------------------------------------------------------------------

    /**
     * Gestiona la lógica del botón de sesión.
     * <p>
     * Si hay sesión activa, cierra la sesión y limpia los datos.
     * Si no hay sesión, muestra el {@link DialogoLogin} para autenticarse.
     * </p>
     */
    private void manejarClickSesion() {
        if (gestorSesion.haySesionActiva()) {
            // SI YA ESTÁ LOGUEADO -> CERRAR SESIÓN
            gestorSesion.cerrarSesion();
            actualizarInterfazSesion();
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        } else {
            // SI NO -> ABRIR DIÁLOGO DE LOGIN EXTERNO
            DialogoLogin.mostrar(this, usuario -> {
                // Callback: Este código se ejecuta cuando el login fue exitoso en el diálogo
                actualizarInterfazSesion();
            });
        }
    }

    /**
     * Actualiza la interfaz gráfica basándose en el estado actual de la sesión.
     * <p>
     * Cambia la visibilidad de elementos exclusivos para usuarios registrados (juegos, foto de perfil)
     * y actualiza el texto de bienvenida y del botón de sesión.
     * </p>
     */
    private void actualizarInterfazSesion() {
        if (gestorSesion.haySesionActiva()) {
            // --- MODO: LOGUEADO ---
            tvBienvenida.setText("Hola " + gestorSesion.getUsuario());
            ivPerfilUsuario.setVisibility(View.VISIBLE);

            // Recuperar y mostrar foto guardada
            String fotoGuardada = gestorSesion.getFoto();
            if (fotoGuardada != null) {
                ivPerfilUsuario.setImageURI(android.net.Uri.parse(fotoGuardada));
            } else {
                ivPerfilUsuario.setImageResource(R.drawable.ic_personal);
            }

            // Mostrar acceso al Arcade
            btnMenuJuegos.setVisibility(View.VISIBLE);

            // Cambiar texto a Cerrar Sesión
            btnIniciarSesion.setText("Cerrar Sesión");
        } else {
            // --- MODO: INVITADO ---
            tvBienvenida.setText("Hola Usuario");
            ivPerfilUsuario.setVisibility(View.GONE);

            // Ocultar acceso al Arcade
            btnMenuJuegos.setVisibility(View.GONE);

            // Cambiar texto a Iniciar Sesión
            btnIniciarSesion.setText("Iniciar Sesión");
        }
    }

    /**
     * Muestra un diálogo modal con los créditos de los desarrolladores.
     */
    private void mostrarDialogoCreditos() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_creditos);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
}