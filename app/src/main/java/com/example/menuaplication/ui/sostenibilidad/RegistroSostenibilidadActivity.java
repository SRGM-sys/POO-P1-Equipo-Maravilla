package com.example.menuaplication.ui.sostenibilidad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.menuaplication.R;
import com.example.menuaplication.model.sostenibilidad.RegistroSostenibilidad;
import java.time.LocalDate;
import com.example.menuaplication.ui.sostenibilidad.ResumenSostenibilidadActivity;

public class RegistroSostenibilidadActivity extends AppCompatActivity {

    private CheckBox cbTransporte, cbImpresiones, cbEnvases, cbReciclaje;
    private TextView tvFecha;
    private Button btnGuardar, btnVerResumen;
    private ImageButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_sostenibilidad);

        inicializarVistas();

        // Fecha automática
        tvFecha.setText("Fecha: " + LocalDate.now().toString());

        configurarListeners();
    }

    private void inicializarVistas() {
        cbTransporte = findViewById(R.id.cb_transporte);
        cbImpresiones = findViewById(R.id.cb_impresiones);
        cbEnvases = findViewById(R.id.cb_envases);
        cbReciclaje = findViewById(R.id.cb_reciclaje);
        tvFecha = findViewById(R.id.tv_fecha_actual);
        btnGuardar = findViewById(R.id.btn_guardar_eco);
        btnVerResumen = findViewById(R.id.btn_ver_resumen);
        btnVolver = findViewById(R.id.btn_volver_sost);
    }

    private void configurarListeners() {
        btnVolver.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> {
            RegistroSostenibilidad registro = new RegistroSostenibilidad(LocalDate.now());
            registro.setUsoTransporteSostenible(cbTransporte.isChecked());
            registro.setEvitoImpresiones(cbImpresiones.isChecked());
            registro.setEvitoEnvasesDescartables(cbEnvases.isChecked());
            registro.setSeparoResiduos(cbReciclaje.isChecked());

            int puntos = registro.getPuntosDia();
            Toast.makeText(this, "¡Genial! Has ganado " + puntos + " Eco-Puntos hoy.", Toast.LENGTH_LONG).show();

            // Aquí en el futuro llamaremos al Repositorio para guardar
        });

        btnVerResumen.setOnClickListener(v -> {
            startActivity(new Intent(this, ResumenSostenibilidadActivity.class));
        });
    }
}