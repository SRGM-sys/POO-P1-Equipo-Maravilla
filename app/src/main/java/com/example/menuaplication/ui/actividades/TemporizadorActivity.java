package com.example.menuaplication.ui.actividades;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.menuaplication.R;
import com.example.menuaplication.data.RepositorioActividades;
import com.example.menuaplication.model.actividades.Actividad;
import com.example.menuaplication.model.actividades.SesionEnfoque;
import com.example.menuaplication.model.actividades.TecnicaEnfoque;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

/**
 * Pantalla que implementa el temporizador para las técnicas de estudio (Pomodoro y Deep Work).
 * Gestiona la cuenta regresiva, la interfaz visual dinámica y el registro de la sesión
 * al finalizar el tiempo.
 *
 * @author José Paladines
 * @version 1.0
 */
public class TemporizadorActivity extends AppCompatActivity {

    // Vistas
    private TextView tvTiempo, tvFrase, tvModoTitulo, tvNombreActividad;
    private ProgressBar progressBarTimer;
    private Button btnIniciar, btnPausar, btnFinalizarAhora, btnReiniciar;
    private ImageButton btnBack;
    private LinearLayout containerOpciones;

    // Timer
    private CountDownTimer timer;
    private long tiempoRestanteMillis;
    private long tiempoTotalInicialMillis;
    private boolean timerCorriendo = false;

    // Datos
    private Actividad actividadActual;
    private TecnicaEnfoque tecnicaActual;
    private int duracionTotalMinutos;

    /** Lista de frases motivadoras que se muestran aleatoriamente al usuario. */
    private final String[] frasesMotivadoras = {
            "¡Tú puedes con esto!",
            "Concéntrate en el ahora.",
            "Un paso a la vez.",
            "El éxito es la suma de pequeños esfuerzos.",
            "Hazlo con pasión.",
            "Mantén la calma y sigue.",
            "Tu mente es poderosa."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporizador);

        actividadActual = (Actividad) getIntent().getSerializableExtra("ACTIVIDAD_OBJ");
        tecnicaActual = (TecnicaEnfoque) getIntent().getSerializableExtra("TECNICA");

        // Vincular vistas
        tvTiempo = findViewById(R.id.tvTiempo);
        progressBarTimer = findViewById(R.id.progressBarTimer);
        tvFrase = findViewById(R.id.tvFrase);
        tvModoTitulo = findViewById(R.id.tvModoTitulo);
        tvNombreActividad = findViewById(R.id.tvNombreActividadTimer);
        containerOpciones = findViewById(R.id.containerOpciones);

        // Mostrar nombre de la actividad
        if (actividadActual != null) {
            tvNombreActividad.setText(actividadActual.getNombre());
        }

        // Botones
        btnIniciar = findViewById(R.id.btnStart);
        btnPausar = findViewById(R.id.btnPause);
        btnFinalizarAhora = findViewById(R.id.btnFinalizarAhora);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        btnBack = findViewById(R.id.btnBackTemporizador);

        // 1) BLOQUEO INICIAL: No permitir iniciar si es 0
        btnIniciar.setEnabled(false);
        btnIniciar.setAlpha(0.5f);

        configurarEstiloPorTecnica();
        generarBotonesDuracion();

        // Listeners
        btnIniciar.setOnClickListener(v -> iniciarTimer());

        // 2) Lógica Pausar/Reanudar
        btnPausar.setOnClickListener(v -> togglePausaResume());

        btnFinalizarAhora.setOnClickListener(v -> {
            if(timer != null) timer.cancel();
            guardarSesion();
        });

        // Lógica Reiniciar
        btnReiniciar.setOnClickListener(v -> reiniciarTimer());

        // 3) Listener botón atrás
        btnBack.setOnClickListener(v -> {
            if(timer != null) timer.cancel();
            finish();
        });
    }

    /**
     * Ajusta los colores y títulos de la pantalla según la técnica elegida (Naranja para Pomodoro, Morado para Deep Work).
     */
    private void configurarEstiloPorTecnica() {
        progressBarTimer.setMax(100);
        progressBarTimer.setProgress(0);

        if (tecnicaActual == TecnicaEnfoque.POMODORO) {
            tvModoTitulo.setText("POMODORO");
            tvModoTitulo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.pomodoro_orange)));
        } else {
            tvModoTitulo.setText("DEEP WORK");
            tvModoTitulo.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.deepwork_purple)));
        }
        tvModoTitulo.setTextColor(Color.WHITE);
    }

    /**
     * Crea dinámicamente los botones de selección de tiempo (ej. 25m, 50m)
     * basándose en la técnica seleccionada. Aplica estilos programáticamente para
     * controlar el padding y el ancho mínimo.
     */
    private void generarBotonesDuracion() {
        containerOpciones.removeAllViews();
        int[] minutos = (tecnicaActual == TecnicaEnfoque.POMODORO) ? new int[]{25, 5, 15} : new int[]{45, 60, 90};

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (int) (48 * getResources().getDisplayMetrics().density)
        );
        params.setMargins(8, 0, 8, 0);

        // Define el espacio interno que quieres (ej. 16dp)
        int paddingSides = (int) (25 * getResources().getDisplayMetrics().density);

        // --- BOTÓN TEST 10s ---
        Button btnTest = new Button(this);
        btnTest.setText("10s");
        btnTest.setLayoutParams(params);

        // LAS 3 LÍNEAS MÁGICAS:
        btnTest.setMinimumWidth(0); // 1. Rompe la regla del ancho mínimo
        btnTest.setMinWidth(0);
        btnTest.setBackgroundResource(R.drawable.bg_boton_opcion); // 2. Fondo primero
        btnTest.setPadding(paddingSides, 0, paddingSides, 0); // 3. Padding al final

        btnTest.setTextColor(Color.parseColor("#455A64"));
        btnTest.setTypeface(null, Typeface.BOLD);
        btnTest.setOnClickListener(v -> prepararTimer(10000, 1));
        containerOpciones.addView(btnTest);

        // --- BOTONES DEL CICLO ---
        for (int min : minutos) {
            Button btn = new Button(this);
            btn.setText(min + "m");
            btn.setLayoutParams(params);

            // LAS 3 LÍNEAS MÁGICAS OTRA VEZ:
            btn.setMinimumWidth(0);
            btn.setMinWidth(0);
            btn.setBackgroundResource(R.drawable.bg_boton_opcion);
            btn.setPadding(paddingSides, 0, paddingSides, 0);

            btn.setTextColor(Color.parseColor("#455A64"));
            btn.setTypeface(null, Typeface.BOLD);
            btn.setOnClickListener(v -> prepararTimer(min * 60 * 1000L, min));
            containerOpciones.addView(btn);
        }
    }

    /**
     * Prepara el temporizador con el tiempo seleccionado pero no lo inicia.
     * Actualiza la interfaz para mostrar el tiempo listo.
     */
    private void prepararTimer(long millis, int minutosReales) {
        tiempoRestanteMillis = millis;
        tiempoTotalInicialMillis = millis;
        duracionTotalMinutos = minutosReales;

        actualizarTextoTimer();
        progressBarTimer.setProgress(0);

        // Habilitar botón Iniciar
        btnIniciar.setEnabled(true);
        btnIniciar.setAlpha(1.0f);
        btnIniciar.setVisibility(View.VISIBLE);

        // Ocultar controles de sesión activa
        btnPausar.setVisibility(View.GONE);
        btnFinalizarAhora.setVisibility(View.GONE);
        btnReiniciar.setVisibility(View.GONE);
        containerOpciones.setVisibility(View.VISIBLE);

        tvFrase.setText("Listo: " + minutosReales + " min de enfoque.");
        tvFrase.setTextColor(Color.parseColor("#78909C"));

        if (timer != null) timer.cancel();
        timerCorriendo = false;
    }

    private void iniciarTimer() {
        // Frase aleatoria
        Random random = new Random();
        String frase = frasesMotivadoras[random.nextInt(frasesMotivadoras.length)];
        tvFrase.setText(frase);
        tvFrase.setTextColor(Color.parseColor("#263238"));

        // Ocultar botón Iniciar y opciones
        btnIniciar.setVisibility(View.GONE);
        containerOpciones.setVisibility(View.INVISIBLE);

        // Mostrar botones de control
        btnPausar.setVisibility(View.VISIBLE);
        btnPausar.setText("PAUSAR"); // Estado inicial
        btnPausar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFB300")));

        btnFinalizarAhora.setVisibility(View.VISIBLE);
        btnReiniciar.setVisibility(View.VISIBLE);

        crearYArrancarCountDown();
    }

    // Método auxiliar para crear el timer
    private void crearYArrancarCountDown() {
        timer = new CountDownTimer(tiempoRestanteMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestanteMillis = millisUntilFinished;
                actualizarTextoTimer();
                actualizarBarraCircular();
            }

            @Override
            public void onFinish() {
                timerCorriendo = false;
                progressBarTimer.setProgress(100);
                tvTiempo.setText("00:00");
                tvFrase.setText("¡Sesión completada!");
                btnPausar.setVisibility(View.GONE);
                guardarSesion();
            }
        }.start();
        timerCorriendo = true;
    }

    private void togglePausaResume() {
        if (timerCorriendo) {
            // PAUSAR
            if(timer != null) timer.cancel();
            timerCorriendo = false;

            btnPausar.setText("REANUDAR");
            btnPausar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Verde
            tvFrase.setText("Tiempo pausado");
        } else {
            // REANUDAR
            crearYArrancarCountDown();

            btnPausar.setText("PAUSAR");
            btnPausar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFB300"))); // Amarillo
            tvFrase.setText("¡Sigue así!");
        }
    }

    private void reiniciarTimer() {
        if(timer != null) timer.cancel();
        timerCorriendo = false;

        // Volver al estado "preparado"
        prepararTimer(tiempoTotalInicialMillis, duracionTotalMinutos);
        tvFrase.setText("Reiniciado. Pulsa Iniciar.");
    }

    private void actualizarTextoTimer() {
        int totalSeconds = (int) Math.ceil(tiempoRestanteMillis / 1000.0);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        tvTiempo.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void actualizarBarraCircular() {
        if (tiempoTotalInicialMillis > 0) {
            long tiempoPasado = tiempoTotalInicialMillis - tiempoRestanteMillis;
            int progreso = (int) (((float) tiempoPasado / tiempoTotalInicialMillis) * 100);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBarTimer.setProgress(progreso, true);
            } else {
                progressBarTimer.setProgress(progreso);
            }
        }
    }

    /**
     * Guarda la sesión finalizada en el historial de la actividad.
     * Calcula y actualiza el nuevo porcentaje de avance basado en el tiempo invertido.
     */
    private void guardarSesion() {
        if (actividadActual != null) {
            SesionEnfoque nuevaSesion = new SesionEnfoque(LocalDateTime.now(), duracionTotalMinutos, tecnicaActual, true);
            actividadActual.agregarSesion(nuevaSesion);

            int minutosInvertidos = actividadActual.getMinutosInvertidos();
            int minutosEstimados = actividadActual.getTiempoEstimadoMinutos();

            if (minutosEstimados > 0) {
                double nuevoPorcentaje = ((double) minutosInvertidos / minutosEstimados) * 100.0;
                if (nuevoPorcentaje > 100) nuevoPorcentaje = 100;
                actividadActual.setPorcentajeAvance(nuevoPorcentaje);
            }

            RepositorioActividades.getInstance().actualizarActividad(actividadActual);
            Toast.makeText(this, "¡Excelente! Guardado.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, ListaActividadesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}