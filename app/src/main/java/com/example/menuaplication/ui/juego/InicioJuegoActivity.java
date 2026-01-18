package com.example.menuaplication.ui.juego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.menuaplication.R;
import java.util.Locale;


/**
 * Actividad que representa la pantalla de inicio o bienvenida al juego de memoria.
 * Proporciona la interfaz para que el usuario comience una nueva partida o regrese al menú anterior.
 *
 * @author TheMatthias
 */
public class InicioJuegoActivity extends AppCompatActivity {

    private TextView tvRecord;


    /**
     * Inicializa la actividad, establece el diseño y configura los listeners de los botones.
     * Gestiona la navegación hacia la actividad principal del juego o el cierre de la pantalla actual.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_juego);

        Button btnIniciar = findViewById(R.id.btn_iniciar_juego);
        ImageButton btnVolver = findViewById(R.id.btn_volver_menu);
        tvRecord = findViewById(R.id.tv_record); // Vincular el nuevo TextView

        btnIniciar.setOnClickListener( v -> {
            Intent intent = new Intent(InicioJuegoActivity.this, JuegoMemoriaActivity.class);
            startActivity(intent);
        });

        btnVolver.setOnClickListener(v -> {
            finish();
        });
    }


    /**
     * Usamos onResume para actualizar el récord cada vez que volvemos del juego.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mostrarRecord();
    }


    private void mostrarRecord() {
        SharedPreferences prefs = getSharedPreferences("JuegoMemoriaPrefs", Context.MODE_PRIVATE);
        long mejorTiempo = prefs.getLong("mejor_tiempo", Long.MAX_VALUE);

        if (mejorTiempo != Long.MAX_VALUE) {
            int segundos = (int) (mejorTiempo / 1000);
            int minutos = segundos / 60;
            segundos = segundos % 60;
            tvRecord.setText(String.format(Locale.getDefault(), "🏆 Récord: %02d:%02d", minutos, segundos));
        } else {
            tvRecord.setText("🏆 Récord: --:--");
        }
    }
}