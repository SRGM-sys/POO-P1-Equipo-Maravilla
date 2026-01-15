package com.example.menuaplication.ui.sostenibilidad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.menuaplication.R;
import com.example.menuaplication.ui.menu.MainActivity;

public class ResumenSostenibilidadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_sostenibilidad);

        // Mentor Tip: Habilitar el botón "Atrás" en la barra superior si existe
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Resumen Eco");
        }

        // Encontrar el botón (usando el ID nuevo y estandarizado)
        Button btnVolverInicio = findViewById(R.id.btn_volver_inicio);

        // Configurar el click
        btnVolverInicio.setOnClickListener(v -> {
            // Intent explícito para volver al menú principal
            Intent intent = new Intent(ResumenSostenibilidadActivity.this, MainActivity.class);
            // Flags para limpiar la pila de actividades (para que no puedas volver atrás al resumen)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Cierra esta actividad
        });
    }

    // Para que la flecha de la barra superior funcione
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}