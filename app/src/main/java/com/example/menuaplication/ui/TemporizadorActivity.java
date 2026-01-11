package com.example.menuaplication.ui;

import android.content.Intent;
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

public class TemporizadorActivity extends AppCompatActivity {

    // Vistas
    private TextView tvTiempo, tvEstado, tvModoTitulo;
    private ProgressBar progressBarTimer; // NUEVO: Barra circular
    private Button btnIniciar, btnPausar, btnFinalizarAhora;
    private LinearLayout containerOpciones;

    // Variables del Timer
    private CountDownTimer timer;
    private long tiempoRestanteMillis;
    private long tiempoTotalInicialMillis; // NUEVO: Para calcular el porcentaje de la barra
    private boolean timerCorriendo = false;

    // Datos de negocio
    private Actividad actividadActual;
    private TecnicaEnfoque tecnicaActual;
    private int duracionTotalMinutos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporizador);

        // Recuperar datos del Intent
        actividadActual = (Actividad) getIntent().getSerializableExtra("ACTIVIDAD_OBJ");
        tecnicaActual = (TecnicaEnfoque) getIntent().getSerializableExtra("TECNICA");

        // Vincular vistas
        tvTiempo = findViewById(R.id.tvTiempo);
        progressBarTimer = findViewById(R.id.progressBarTimer); // Vincular barra
        tvEstado = findViewById(R.id.tvEstado);
        tvModoTitulo = findViewById(R.id.tvModoTitulo);
        btnIniciar = findViewById(R.id.btnStart);
        btnPausar = findViewById(R.id.btnPause);
        btnFinalizarAhora = findViewById(R.id.btnFinalizarAhora);
        containerOpciones = findViewById(R.id.containerOpciones);

        // Configuración inicial
        tvModoTitulo.setText(tecnicaActual == TecnicaEnfoque.POMODORO ? "Pomodoro" : "Deep Work");
        progressBarTimer.setMax(100);
        progressBarTimer.setProgress(0);

        generarBotonesDuracion();

        // Listeners
        btnIniciar.setOnClickListener(v -> iniciarTimer());
        btnPausar.setOnClickListener(v -> pausarTimer());

        btnFinalizarAhora.setOnClickListener(v -> {
            if(timer != null) timer.cancel();
            guardarSesion();
        });
    }

    private void generarBotonesDuracion() {
        containerOpciones.removeAllViews();

        // Determinar tiempos según técnica
        int[] minutos = (tecnicaActual == TecnicaEnfoque.POMODORO) ? new int[]{25, 5, 15} : new int[]{45, 60, 90};

        // Botón Test (10 segundos) - Útil para probar la barra
        Button btnTest = new Button(this);
        btnTest.setText("Test 10s");
        btnTest.setOnClickListener(v -> prepararTimer(10 * 1000, 1));
        containerOpciones.addView(btnTest);

        // Botones reales
        for (int min : minutos) {
            Button btn = new Button(this);
            btn.setText(min + "m");
            // Se le pasa milisegundos y los minutos "nominales" para el registro
            btn.setOnClickListener(v -> prepararTimer(min * 60 * 1000L, min));
            containerOpciones.addView(btn);
        }
    }

    private void prepararTimer(long millis, int minutosReales) {
        tiempoRestanteMillis = millis;
        tiempoTotalInicialMillis = millis; // Guardamos el total para calcular porcentaje
        duracionTotalMinutos = minutosReales;

        actualizarTextoTimer();
        progressBarTimer.setProgress(0); // Reiniciar barra

        btnIniciar.setEnabled(true);
        btnPausar.setEnabled(false);
        tvEstado.setText("Listo: " + minutosReales + " min");
        btnFinalizarAhora.setVisibility(View.INVISIBLE);

        // Si estaba corriendo otro, lo cancelamos
        if (timer != null) timer.cancel();
    }

    private void iniciarTimer() {
        // Usamos un intervalo de 100ms (en lugar de 1000) para que la barra se mueva suave
        timer = new CountDownTimer(tiempoRestanteMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestanteMillis = millisUntilFinished;

                // Actualizar texto y barra
                actualizarTextoTimer();
                actualizarBarraCircular();
            }

            @Override
            public void onFinish() {
                timerCorriendo = false;
                progressBarTimer.setProgress(100);
                tvTiempo.setText("00:00");
                guardarSesion();
            }
        }.start();

        timerCorriendo = true;
        btnIniciar.setEnabled(false);
        btnPausar.setEnabled(true);
        containerOpciones.setVisibility(View.GONE);
        btnFinalizarAhora.setVisibility(View.VISIBLE);
        tvEstado.setText("Enfócate...");
    }

    private void pausarTimer() {
        if(timer != null) timer.cancel();
        timerCorriendo = false;
        btnIniciar.setEnabled(true);
        btnPausar.setEnabled(false);
        tvEstado.setText("Pausado");
    }

    private void actualizarTextoTimer() {
        // Redondeo hacia arriba para que no muestre 00:00 antes de tiempo
        int totalSeconds = (int) Math.ceil(tiempoRestanteMillis / 1000.0);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        tvTiempo.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void actualizarBarraCircular() {
        // Lógica visual: Se llena a medida que pasa el tiempo
        if (tiempoTotalInicialMillis > 0) {
            long tiempoPasado = tiempoTotalInicialMillis - tiempoRestanteMillis;
            int progreso = (int) (((float) tiempoPasado / tiempoTotalInicialMillis) * 100);

            // Animación suave (API 24+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBarTimer.setProgress(progreso, true);
            } else {
                progressBarTimer.setProgress(progreso);
            }
        }
    }

    private void guardarSesion() {
        tvEstado.setText("Guardando...");

        // 1. Agregar Sesión al Historial (TU LÓGICA ORIGINAL)
        if (actividadActual != null) {
            SesionEnfoque nuevaSesion = new SesionEnfoque(LocalDateTime.now(), duracionTotalMinutos, tecnicaActual, true);
            actividadActual.agregarSesion(nuevaSesion);

            // 2. CÁLCULO AUTOMÁTICO DE AVANCE (TU LÓGICA ORIGINAL)
            int minutosInvertidos = actividadActual.getMinutosInvertidos();
            int minutosEstimados = actividadActual.getTiempoEstimadoMinutos();

            if (minutosEstimados > 0) {
                double nuevoPorcentaje = ((double) minutosInvertidos / minutosEstimados) * 100.0;
                if (nuevoPorcentaje > 100) nuevoPorcentaje = 100;
                actividadActual.setPorcentajeAvance(nuevoPorcentaje);
            }

            // 3. Actualizar en Repositorio
            Repositorio.getInstance().actualizarActividad(actividadActual);

            Toast.makeText(this, "Sesión guardada. Avance: " + (int)actividadActual.getPorcentajeAvance() + "%", Toast.LENGTH_LONG).show();

            // Volver a la Lista para refrescar
            Intent intent = new Intent(this, ListaActividadesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error: Actividad no encontrada", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}