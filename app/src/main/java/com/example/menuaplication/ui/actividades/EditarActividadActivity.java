package com.example.menuaplication.ui.actividades;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback; // IMPORTANTE: Nueva importación
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.menuaplication.R;
import com.example.menuaplication.data.RepositorioActividades;
import com.example.menuaplication.model.actividades.Actividad;
import com.example.menuaplication.model.actividades.ActividadAcademica;
import com.example.menuaplication.model.actividades.ActividadPersonal;
import com.example.menuaplication.model.actividades.Prioridad;
import com.example.menuaplication.model.actividades.TipoAcademica;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EditarActividadActivity extends AppCompatActivity {

    private EditText etNombre, etDescripcion, etMateria, etLugar, etTiempoEstimado, tvFecha, tvHora;
    private Spinner spinnerTipoActividad, spinnerPrioridad, spinnerTipoAcademica;
    private Button btnGuardar;
    private ImageButton btnBack;
    private LinearLayout layoutAcademica, layoutPersonal;

    private LocalDateTime fechaSeleccionada;
    private Actividad actividadAEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_actividad);

        // 1. Recuperar la actividad enviada desde DetalleActividad
        actividadAEditar = (Actividad) getIntent().getSerializableExtra("actividad_a_editar");

        if (actividadAEditar == null) {
            Toast.makeText(this, "Error al cargar la actividad", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inicializarVistas();
        setupSpinners();
        setupDateAndTimePickers();
        cargarDatosDeActividad(); // Rellenar campos

        btnGuardar.setOnClickListener(v -> guardarCambios());

        // --- MANEJO MODERNO DEL BOTÓN ATRÁS ---
        // Esto reemplaza al método onBackPressed() antiguo y evita el error
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mostrarDialogoSalida();
            }
        });

        // Configurar la flecha de la UI para que use el mismo sistema
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void inicializarVistas() {
        spinnerTipoActividad = findViewById(R.id.spinnerTipoActividad);
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);

        layoutAcademica = findViewById(R.id.layoutAcademica);
        etMateria = findViewById(R.id.etMateria);
        spinnerTipoAcademica = findViewById(R.id.spinnerTipoAcademica);

        layoutPersonal = findViewById(R.id.layoutPersonal);
        etLugar = findViewById(R.id.etLugar);

        tvFecha = findViewById(R.id.tvFecha);
        tvHora = findViewById(R.id.tvHora);
        spinnerPrioridad = findViewById(R.id.spinnerPrioridad);
        etTiempoEstimado = findViewById(R.id.etTiempoEstimado);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnBack = findViewById(R.id.btnBackEditar); // Asegúrate que el ID en XML sea btnBackEditar
    }

    private void setupSpinners() {
        // Tipo Actividad
        ArrayAdapter<CharSequence> adapterTipo = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Académica", "Personal"});
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoActividad.setAdapter(adapterTipo);
        // ESTA LÍNEA ES LA CLAVE: Deshabilitarlo en Java asegura que no se expanda
        spinnerTipoActividad.setEnabled(false);
        // También puedes forzar que no sea clickable
        spinnerTipoActividad.setClickable(false);

        // Prioridad
        ArrayAdapter<Prioridad> adapterPrioridad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, Prioridad.values());
        adapterPrioridad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridad.setAdapter(adapterPrioridad);

        // Tipo Académica
        ArrayAdapter<TipoAcademica> adapterTipoAcademica = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TipoAcademica.values());
        adapterTipoAcademica.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAcademica.setAdapter(adapterTipoAcademica);
    }

    private void setupDateAndTimePickers() {
        if (actividadAEditar != null) {
            fechaSeleccionada = actividadAEditar.getFechaVencimiento();
        } else {
            fechaSeleccionada = LocalDateTime.now();
        }

        tvFecha.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        fechaSeleccionada = fechaSeleccionada.withYear(year).withMonth(month + 1).withDayOfMonth(dayOfMonth);
                        tvFecha.setText(fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    }, fechaSeleccionada.getYear(), fechaSeleccionada.getMonthValue() - 1, fechaSeleccionada.getDayOfMonth());
            datePicker.show();
        });

        tvHora.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> {
                        fechaSeleccionada = fechaSeleccionada.withHour(hourOfDay).withMinute(minute);
                        tvHora.setText(fechaSeleccionada.format(DateTimeFormatter.ofPattern("HH:mm")));
                    }, fechaSeleccionada.getHour(), fechaSeleccionada.getMinute(), true);
            timePicker.show();
        });
    }

    @SuppressWarnings("unchecked")
    private void cargarDatosDeActividad() {
        // Datos comunes
        etNombre.setText(actividadAEditar.getNombre());
        etDescripcion.setText(actividadAEditar.getDescripcion());
        etTiempoEstimado.setText(String.valueOf(actividadAEditar.getTiempoEstimadoMinutos()));

        tvFecha.setText(fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        tvHora.setText(fechaSeleccionada.format(DateTimeFormatter.ofPattern("HH:mm")));

        // Seleccionar Prioridad en Spinner
        ArrayAdapter<Prioridad> adapterP = (ArrayAdapter<Prioridad>) spinnerPrioridad.getAdapter();
        if (adapterP != null) {
            int posicionP = adapterP.getPosition(actividadAEditar.getPrioridad());
            spinnerPrioridad.setSelection(posicionP);
        }

        // Datos específicos según tipo
        if (actividadAEditar instanceof ActividadAcademica) {
            spinnerTipoActividad.setSelection(0); // Académica
            layoutAcademica.setVisibility(View.VISIBLE);
            layoutPersonal.setVisibility(View.GONE);

            ActividadAcademica academica = (ActividadAcademica) actividadAEditar;
            etMateria.setText(academica.getAsignatura());

            ArrayAdapter<TipoAcademica> adapterTA = (ArrayAdapter<TipoAcademica>) spinnerTipoAcademica.getAdapter();
            if (adapterTA != null) {
                int posicionTA = adapterTA.getPosition(academica.getTipo());
                spinnerTipoAcademica.setSelection(posicionTA);
            }

        } else if (actividadAEditar instanceof ActividadPersonal) {
            spinnerTipoActividad.setSelection(1); // Personal
            layoutAcademica.setVisibility(View.GONE);
            layoutPersonal.setVisibility(View.VISIBLE);

            ActividadPersonal personal = (ActividadPersonal) actividadAEditar;
            etLugar.setText(personal.getLugar());
        }
    }

    private void guardarCambios() {
        // Validaciones
        if (etNombre.getText().toString().trim().isEmpty()) {
            etNombre.setError("Nombre requerido");
            return;
        }
        if (etTiempoEstimado.getText().toString().trim().isEmpty()) {
            etTiempoEstimado.setError("Requerido");
            return;
        }

        // Actualizar datos comunes
        actividadAEditar.setNombre(etNombre.getText().toString().trim());
        actividadAEditar.setDescripcion(etDescripcion.getText().toString().trim());
        actividadAEditar.setFechaVencimiento(fechaSeleccionada);
        actividadAEditar.setPrioridad((Prioridad) spinnerPrioridad.getSelectedItem());

        try {
            actividadAEditar.setTiempoEstimadoMinutos(Integer.parseInt(etTiempoEstimado.getText().toString().trim()));
        } catch (NumberFormatException e) {
            etTiempoEstimado.setError("Número inválido");
            return;
        }

        // Actualizar datos específicos
        if (actividadAEditar instanceof ActividadAcademica) {
            ActividadAcademica aa = (ActividadAcademica) actividadAEditar;
            if (etMateria.getText().toString().trim().isEmpty()) {
                etMateria.setError("Materia requerida");
                return;
            }
            aa.setAsignatura(etMateria.getText().toString().trim());
            aa.setTipo((TipoAcademica) spinnerTipoAcademica.getSelectedItem());

        } else if (actividadAEditar instanceof ActividadPersonal) {
            ActividadPersonal ap = (ActividadPersonal) actividadAEditar;
            if (etLugar.getText().toString().trim().isEmpty()) {
                etLugar.setError("Lugar requerido");
                return;
            }
            ap.setLugar(etLugar.getText().toString().trim());
        }

        // Guardar
        RepositorioActividades.getInstance().actualizarActividad(actividadAEditar);

        Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();

        // Devolver resultado
        Intent resultIntent = new Intent();
        resultIntent.putExtra("actividad_actualizada", actividadAEditar);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // Método extraído para mostrar el diálogo
    private void mostrarDialogoSalida() {
        new AlertDialog.Builder(this)
                .setTitle("Descartar cambios")
                .setMessage("No se han aplicado los cambios. ¿Está seguro que quiere salir?")
                .setPositiveButton("Salir", (dialog, which) -> {
                    // Aquí usamos finish() directamente, ya que queremos cerrar la actividad
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}