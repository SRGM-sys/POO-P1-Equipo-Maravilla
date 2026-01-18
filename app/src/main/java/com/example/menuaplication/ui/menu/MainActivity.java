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
import com.example.menuaplication.data.GestorSesion; // <--- Importante
import com.example.menuaplication.ui.actividades.ListaActividadesActivity;
import com.example.menuaplication.ui.hidratacion.ControlHidratacionActivity;
import com.example.menuaplication.ui.juego.InicioJuegoActivity;
import com.example.menuaplication.ui.sostenibilidad.ResumenSostenibilidadActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    // Vistas
    private TextView btnCreditos, tvBienvenida;
    private ImageView ivPerfilUsuario;
    private CardView cardActivities, cardHydration, cardEco, cardGame;
    private MaterialButton btnExit;
    private Button btnIniciarSesion, btnMenuJuegos;

    // Lógica delegada
    private GestorSesion gestorSesion;

    // Selector de Galería (Requisito de Android que debe estar aquí)
    private final ActivityResultLauncher<String> selectorGaleria = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && ivPerfilUsuario != null) ivPerfilUsuario.setImageURI(uri);
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // 1. Inicializar el Gestor (Carga datos guardados)
        gestorSesion = new GestorSesion(this);

        inicializarVistas();

        // 2. Actualizar la pantalla según si hay sesión o no
        actualizarInterfazSesion();

        configurarListeners();
    }

    private void inicializarVistas() {
        cardActivities = findViewById(R.id.cardActivities);
        cardHydration = findViewById(R.id.cardHydration);
        cardEco = findViewById(R.id.cardEco);
        cardGame = findViewById(R.id.cardGame);
        btnExit = findViewById(R.id.btnExit);
        btnCreditos = findViewById(R.id.btnCreditos);

        // Nuevos elementos
        tvBienvenida = findViewById(R.id.tvBienvenida);
        ivPerfilUsuario = findViewById(R.id.ivPerfilUsuario);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnMenuJuegos = findViewById(R.id.btnMenuJuegos);
    }

    private void configurarListeners() {
        // --- NAVEGACIÓN ESTÁNDAR ---
        if (cardActivities != null) cardActivities.setOnClickListener(v -> startActivity(new Intent(this, ListaActividadesActivity.class)));
        if (cardHydration != null) cardHydration.setOnClickListener(v -> startActivity(new Intent(this, ControlHidratacionActivity.class)));
        if (cardEco != null) cardEco.setOnClickListener(v -> startActivity(new Intent(this, ResumenSostenibilidadActivity.class)));
        if (cardGame != null) cardGame.setOnClickListener(v -> startActivity(new Intent(this, InicioJuegoActivity.class)));
        if (btnExit != null) btnExit.setOnClickListener(v -> { finishAffinity(); System.exit(0); });
        if (btnCreditos != null) btnCreditos.setOnClickListener(v -> mostrarDialogoCreditos());

        // --- DELEGACIÓN (AQUÍ ESTÁ LA MAGIA) ---

        // 1. Botón Juegos -> Llama a la clase externa
        if (btnMenuJuegos != null) {
            btnMenuJuegos.setOnClickListener(v -> DialogoJuegos.mostrar(this));
        }

        // 2. Botón Sesión -> Lógica inteligente
        if (btnIniciarSesion != null) {
            btnIniciarSesion.setOnClickListener(v -> manejarClickSesion());
        }

        // 3. Foto de Perfil -> Solo si hay sesión
        if (ivPerfilUsuario != null) {
            ivPerfilUsuario.setOnClickListener(v -> {
                if (gestorSesion.haySesionActiva()) selectorGaleria.launch("image/*");
            });
        }
    }

    private void manejarClickSesion() {
        if (gestorSesion.haySesionActiva()) {
            // SI YA ESTÁ LOGUEADO -> CERRAR SESIÓN
            gestorSesion.cerrarSesion();
            actualizarInterfazSesion();
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        } else {
            // SI NO -> ABRIR DIÁLOGO DE LOGIN EXTERNO
            DialogoLogin.mostrar(this, usuario -> {
                // Este código corre cuando el login fue exitoso
                actualizarInterfazSesion();
            });
        }
    }

    private void actualizarInterfazSesion() {
        if (gestorSesion.haySesionActiva()) {
            // MODO: LOGUEADO
            tvBienvenida.setText("Hola " + gestorSesion.getUsuario());
            ivPerfilUsuario.setVisibility(View.VISIBLE);
            btnIniciarSesion.setText("Cerrar Sesión");
            btnIniciarSesion.setBackgroundColor(android.graphics.Color.parseColor("#E57373")); // Rojo
        } else {
            // MODO: INVITADO
            tvBienvenida.setText("Hola Usuario");
            ivPerfilUsuario.setVisibility(View.GONE);
            btnIniciarSesion.setText("Iniciar Sesión");
            btnIniciarSesion.setBackgroundColor(getResources().getColor(R.color.primary_blue)); // Azul
        }
    }

    private void mostrarDialogoCreditos() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_creditos);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }
}