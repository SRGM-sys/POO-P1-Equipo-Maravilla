package com.example.menuaplication.ui.juego;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.menuaplication.R;

public class InicioJuegoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_juego);

        Button btnIniciar = findViewById(R.id.btn_iniciar_juego);

        btnIniciar.setOnClickListener( v -> {
            Intent intent = new Intent(InicioJuegoActivity.this, JuegoMemoriaActivity.class);
                startActivity(intent);
                // No ponemos finish() aquí si queremos que al volver atrás regrese a esta pantalla
        });
    }
}