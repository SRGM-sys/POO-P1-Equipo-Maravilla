package com.example.menuaplication.ui.juego;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.menuaplication.R;

/**
 * Actividad que representa la pantalla de inicio o bienvenida al juego de memoria.
 * Proporciona la interfaz para que el usuario comience una nueva partida o regrese al menú anterior.
 *
 * @author TheMatthias
 */
public class InicioJuegoActivity extends AppCompatActivity {

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

        btnIniciar.setOnClickListener( v -> {
            Intent intent = new Intent(InicioJuegoActivity.this, JuegoMemoriaActivity.class);
                startActivity(intent);
        });

        btnVolver.setOnClickListener(v -> {
            finish();
        });
    }
}