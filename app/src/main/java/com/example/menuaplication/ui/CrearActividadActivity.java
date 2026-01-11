package com.example.menuaplication.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menuaplication.R;
import com.example.menuaplication.data.Repositorio;
import com.example.menuaplication.model.ActividadAcademica;
import com.example.menuaplication.model.ActividadPersonal;
import com.example.menuaplication.model.Prioridad;
import com.example.menuaplication.model.TipoAcademica;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class CrearActividadActivity extends AppCompatActivity {

    // Componentes de la interfaz
    private ImageButton btnBack;
    private RadioGroup rgTipo;
    private RadioButton rbAcademica, rbPersonal;
    private TextInputEditText etNombre, etDesc, etFechaVencimiento, etMinutos, etAsignatura, etLugar;
    private Spinner spinnerPrioridad, spinnerTipoAcademica;
    private LinearLayout layoutAcademica, layoutPersonal;
    private Button btnGuardar;

    // Lógica de fechas
    private Calendar calendarioSeleccionado = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_actividad);

        inicializarVistas();
        configurarSpinners();
        configurarEventos();
    }

    private void inicializarVistas() {
        btnBack = findViewById(R.id.btnBackCrear);
        rgTipo = findViewById(R.id.rgTipo);
        rbAcademica = findViewById(R.id.rbAcademica);
        rbPersonal = findViewById(R.id.rbPersonal);

        etNombre = findViewById(R.id.etNombre);
        etDesc = findViewById(R.id.etDesc);
        etFechaVencimiento = findViewById(R.id.etFechaVencimiento); // El nuevo campo
        etMinutos = findViewById(R.id.etMinutos);

        spinnerPrioridad = findViewById(R.id.spinnerPrioridad);
        spinnerTipoAcademica = findViewById(R.id.spinnerTipoAcademica);

        layoutAcademica = findViewById(R.id.layoutAcademica);
        layoutPersonal = findViewById(R.id.layoutPersonal);

        etAsignatura = findViewById(R.id.etAsignatura);
        etLugar = findViewById(R.id.etLugar);

        btnGuardar = findViewById(R.id.btnGuardar);
    }

    private void configurarSpinners() {
        // Spinner Prioridad
        spinnerPrioridad.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, Prioridad.values()));

        // Spinner Tipo Académica (usando el Enum TipoAcademica)
        spinnerTipoAcademica.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, TipoAcademica.values()));
    }

    private void configurarEventos() {
        // Botón volver
        btnBack.setOnClickListener(v -> finish());

        // Selector de Fecha y Hora
        etFechaVencimiento.setOnClickListener(v -> mostrarSelectorFecha());

        // Lógica para mostrar/ocultar campos según tipo de actividad
        rgTipo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAcademica) {
                layoutAcademica.setVisibility(View.VISIBLE);
                layoutPersonal.setVisibility(View.GONE);
            } else {
                layoutAcademica.setVisibility(View.GONE);
                layoutPersonal.setVisibility(View.VISIBLE);
            }
        });

        // Botón Guardar
        btnGuardar.setOnClickListener(v -> guardarActividad());
    }

    private void mostrarSelectorFecha() {
        Calendar c = Calendar.getInstance();
        int anio = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendarioSeleccionado.set(Calendar.YEAR, year);
            calendarioSeleccionado.set(Calendar.MONTH, month);
            calendarioSeleccionado.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mostrarSelectorHora();
        }, anio, mes, dia);
        datePicker.show();
    }

    private void mostrarSelectorHora() {
        Calendar c = Calendar.getInstance();
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minuto = c.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendarioSeleccionado.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendarioSeleccionado.set(Calendar.MINUTE, minute);
            actualizarCampoFecha();
        }, hora, minuto, true);
        timePicker.show();
    }

    private void actualizarCampoFecha() {
        String formato = "dd/MM/yyyy HH:mm";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(formato, Locale.getDefault());
        etFechaVencimiento.setText(sdf.format(calendarioSeleccionado.getTime()));
    }

    private void guardarActividad() {
        // Validaciones generales
        String nombre = etNombre.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String fechaStr = etFechaVencimiento.getText().toString().trim();
        String minutosStr = etMinutos.getText().toString().trim();

        if (nombre.isEmpty()) { etNombre.setError("Requerido"); return; }
        if (fechaStr.isEmpty()) { etFechaVencimiento.setError("Requerido"); return; }
        if (minutosStr.isEmpty()) { etMinutos.setError("Requerido"); return; }

        try {
            // Conversiones de datos comunes
            int tiempoEstimado = Integer.parseInt(minutosStr);
            Prioridad prioridad = (Prioridad) spinnerPrioridad.getSelectedItem();

            LocalDateTime fechaVencimiento = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                fechaVencimiento = LocalDateTime.parse(fechaStr, formatter);
            }

            // Guardar según el tipo seleccionado
            if (rbAcademica.isChecked()) {
                String asignatura = etAsignatura.getText().toString().trim();
                if (asignatura.isEmpty()) { etAsignatura.setError("Requerido"); return; }

                TipoAcademica tipo = (TipoAcademica) spinnerTipoAcademica.getSelectedItem();

                ActividadAcademica nueva = new ActividadAcademica(
                        nombre, desc, fechaVencimiento, prioridad, tiempoEstimado,
                        asignatura, tipo
                );
                Repositorio.getInstance().agregarActividad(nueva);

            } else {
                // Es Personal
                String lugar = etLugar.getText().toString().trim();
                if (lugar.isEmpty()) { etLugar.setError("Requerido"); return; }

                ActividadPersonal nueva = new ActividadPersonal(
                        nombre, desc, fechaVencimiento, prioridad, tiempoEstimado,
                        lugar
                );
                Repositorio.getInstance().agregarActividad(nueva);
            }

            Toast.makeText(this, "Actividad Guardada", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}