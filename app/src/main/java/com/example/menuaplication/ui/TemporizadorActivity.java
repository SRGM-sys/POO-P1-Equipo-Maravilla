package com.example.menuaplication.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menuaplication.R;
import com.example.menuaplication.data.Repositorio;
import com.example.menuaplication.model.Actividad;
import com.example.menuaplication.model.SesionEnfoque;
import com.example.menuaplication.model.TecnicaEnfoque;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

public class TemporizadorActivity extends AppCompatActivity {

    // Vistas
    private TextView tvTiempo, tvFrase, tvModoTitulo;
    private ProgressBar progressBarTimer;
    private Button btnIniciar, btnPausar, btnFinalizarAhora;
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

        // Vincular
        tvTiempo = findViewById(R.id.tvTiempo);
        progressBarTimer = findViewById(R.id.progressBarTimer);
        tvFrase = findViewById(R.id.tvFrase);
        tvModoTitulo = findViewById(R.id.tvModoTitulo);
        btnIniciar = findViewById(R.id.btnStart);
        btnPausar = findViewById(R.id.btnPause);
        btnFinalizarAhora = findViewById(R.id.btnFinalizarAhora);
        containerOpciones = findViewById(R.id.containerOpciones);

        configurarEstiloPorTecnica();
        generarBotonesDuracion();

        btnIniciar.setOnClickListener(v -> iniciarTimer());
        btnPausar.setOnClickListener(v -> pausarTimer());
        btnFinalizarAhora.setOnClickListener(v -> {
            if(timer != null) timer.cancel();
            guardarSesion();
        });
    }

    private void configurarEstiloPorTecnica() {
        progressBarTimer.setMax(100);
        progressBarTimer.setProgress(0);

        // Estilo del título: Fondo de color, Letras Blancas
        if (tecnicaActual == TecnicaEnfoque.POMODORO) {
            tvModoTitulo.setText("POMODORO");
            // Fondo Rojo Tomate Intenso
            tvModoTitulo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E53935")));
        } else {
            tvModoTitulo.setText("DEEP WORK");
            // Fondo Azul Profundo
            tvModoTitulo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A237E")));
        }
        // Aseguramos letras blancas siempre
        tvModoTitulo.setTextColor(Color.WHITE);
    }

    private void generarBotonesDuracion() {
        containerOpciones.removeAllViews();

        // Determinar tiempos según técnica
        int[] minutos = (tecnicaActual == TecnicaEnfoque.POMODORO) ? new int[]{25, 5, 15} : new int[]{45, 60, 90};

        // Parametros de layout para los botones
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (int) (48 * getResources().getDisplayMetrics().density) // Altura fija cómoda
        );
        params.setMargins(1, 0, 1, 0);

        // --- 1. BOTÓN DE PRUEBA (Recuperado) ---
        Button btnTest = new Button(this);
        btnTest.setText("10s");
        btnTest.setLayoutParams(params);
        btnTest.setBackgroundResource(R.drawable.bg_boton_opcion); // Mismo estilo elegante
        btnTest.setTextColor(Color.parseColor("#455A64"));
        btnTest.setTypeface(null, Typeface.BOLD);
        btnTest.setPadding(2, 0, 2, 0);
        btnTest.setMinHeight(0);
        btnTest.setMinimumHeight(0);
        // Acción: 10 segundos (10000 ms), cuenta como 1 minuto para registro
        btnTest.setOnClickListener(v -> prepararTimer(10000, 1));
        containerOpciones.addView(btnTest);

        // --- 2. BOTONES NORMALES ---
        for (int min : minutos) {
            Button btn = new Button(this);
            btn.setText(min + "m");
            btn.setLayoutParams(params);

            // Estilo visual
            btn.setBackgroundResource(R.drawable.bg_boton_opcion);
            btn.setTextColor(Color.parseColor("#455A64"));
            btn.setTypeface(null, Typeface.BOLD);
            btn.setPadding(2, 0, 2, 0);
            btn.setMinHeight(0);
            btn.setMinimumHeight(0);

            btn.setOnClickListener(v -> prepararTimer(min * 60 * 1000L, min));
            containerOpciones.addView(btn);
        }
    }

    private void prepararTimer(long millis, int minutosReales) {
        tiempoRestanteMillis = millis;
        tiempoTotalInicialMillis = millis;
        duracionTotalMinutos = minutosReales;

        actualizarTextoTimer();
        progressBarTimer.setProgress(0);

        btnIniciar.setVisibility(View.VISIBLE);
        btnPausar.setVisibility(View.GONE);
        btnFinalizarAhora.setVisibility(View.GONE);

        tvFrase.setText("Listo: " + minutosReales + " min de enfoque.");
        tvFrase.setTextColor(Color.parseColor("#78909C"));

        if (timer != null) timer.cancel();
    }

    private void iniciarTimer() {
        Random random = new Random();
        String frase = frasesMotivadoras[random.nextInt(frasesMotivadoras.length)];
        tvFrase.setText(frase);
        tvFrase.setTextColor(Color.parseColor("#263238"));

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
                guardarSesion();
            }
        }.start();

        timerCorriendo = true;

        btnIniciar.setVisibility(View.GONE);
        containerOpciones.setVisibility(View.INVISIBLE); // Ocultar opciones

        btnPausar.setVisibility(View.VISIBLE);
        btnPausar.setEnabled(true);
        btnFinalizarAhora.setVisibility(View.VISIBLE);
    }

    private void pausarTimer() {
        if(timer != null) timer.cancel();
        timerCorriendo = false;

        btnIniciar.setVisibility(View.VISIBLE);
        btnIniciar.setText("Reanudar");
        btnPausar.setVisibility(View.GONE);

        tvFrase.setText("Tiempo pausado");
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

            Repositorio.getInstance().actualizarActividad(actividadActual);
            Toast.makeText(this, "¡Excelente! Guardado.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, ListaActividadesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}