package com.example.menuaplication.ui.hidratacion;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.model.hidratacion.RegistroAgua;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ControlHidratacionActivity extends AppCompatActivity {

    // Vistas
    private TextView tvFechaSeleccionada, tvPorcentaje, tvMetaDiaria, tvTotalConsumido;
    private ProgressBar progressBar;
    private RecyclerView rvRegistros;
    private Button btnRegistrar, btnEstablecerMeta;

    // Datos y Lógica
    private RegistroAguaAdapter adapter;
    private String fechaActualString; // Clave interna (ej: "19/01/2026")

    // --- CAMBIO 1: Eliminamos la variable única 'metaDiaria' y creamos un Mapa ---
    // private int metaDiaria = 2000; <--- ESTO YA NO SE USA ASI
    private Map<String, Integer> metasPorFecha; // Mapa: FECHA -> META (ej: "21/01/2026" -> 3000)
    private final int META_POR_DEFECTO = 2000;

    // "Base de Datos" temporal en memoria
    private Map<String, List<RegistroAgua>> baseDeDatosLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_hidratacion);

        inicializarVistas();

        // Inicializar Datos (Simulación de DB)
        baseDeDatosLocal = new HashMap<>();
        metasPorFecha = new HashMap<>(); // Inicializamos el mapa de metas

        cargarDatosPorDefecto();

        configurarRecyclerView();
        configurarEventos();
        actualizarUI();
    }

    private void inicializarVistas() {
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);
        tvPorcentaje = findViewById(R.id.tvPorcentaje);
        tvMetaDiaria = findViewById(R.id.tvMetaDiaria);
        tvTotalConsumido = findViewById(R.id.tvTotalConsumido);
        progressBar = findViewById(R.id.progressBarHidratacion);
        rvRegistros = findViewById(R.id.rvRegistrosAgua);
        btnRegistrar = findViewById(R.id.btnRegistrarAgua);
        btnEstablecerMeta = findViewById(R.id.btnEstablecerMeta);

        // Fecha inicial
        fechaActualString = "19/01/2026";
        actualizarTextoFechaVisual();
    }

    private void cargarDatosPorDefecto() {
        List<RegistroAgua> listaEnero19 = new ArrayList<>();
        listaEnero19.add(new RegistroAgua(250, "08:00 AM", "19/01/2026"));
        listaEnero19.add(new RegistroAgua(500, "10:00 AM", "19/01/2026"));
        baseDeDatosLocal.put("19/01/2026", listaEnero19);

        // Opcional: Si quieres que el 19 ya tenga una meta diferente por defecto
        // metasPorFecha.put("19/01/2026", 2500);
    }

    private void configurarRecyclerView() {
        rvRegistros.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RegistroAguaAdapter(new ArrayList<>());
        rvRegistros.setAdapter(adapter);
    }

    private void configurarEventos() {
        View cardFecha = findViewById(R.id.cardFecha);
        if(cardFecha != null) cardFecha.setOnClickListener(v -> mostrarSelectorFechaMaterial());
        else tvFechaSeleccionada.setOnClickListener(v -> mostrarSelectorFechaMaterial());

        btnEstablecerMeta.setOnClickListener(v -> mostrarDialogoMeta());
        btnRegistrar.setOnClickListener(v -> mostrarDialogoAgregarAgua());

        View btnVolver = findViewById(R.id.btnVolver);
        if(btnVolver != null) btnVolver.setOnClickListener(v -> finish());
    }

    private void actualizarUI() {
        // 1. Obtener registros (vasos de agua)
        List<RegistroAgua> registrosDelDia = baseDeDatosLocal.get(fechaActualString);
        if (registrosDelDia == null) registrosDelDia = new ArrayList<>();
        adapter.actualizarLista(registrosDelDia);

        // 2. Calcular total consumido
        int totalMl = 0;
        for (RegistroAgua reg : registrosDelDia) totalMl += reg.getCantidadMl();

        // --- CAMBIO 2: Obtener la META específica de ESTE día ---
        int metaDelDiaActual = META_POR_DEFECTO; // 2000 por defecto
        if (metasPorFecha.containsKey(fechaActualString)) {
            metaDelDiaActual = metasPorFecha.get(fechaActualString);
        }

        // 3. Actualizar textos con la meta correcta
        tvTotalConsumido.setText(totalMl + " ml");
        tvMetaDiaria.setText(metaDelDiaActual + " ml");

        // 4. Calcular porcentaje usando la meta dinámica
        int porcentaje = 0;
        if (metaDelDiaActual > 0) porcentaje = (totalMl * 100) / metaDelDiaActual;

        progressBar.setProgress(Math.min(porcentaje, 100));
        tvPorcentaje.setText(porcentaje + "%");
    }

    private void actualizarTextoFechaVisual() {
        try {
            SimpleDateFormat sdfInterno = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = sdfInterno.parse(fechaActualString);
            SimpleDateFormat sdfVisual = new SimpleDateFormat("d 'de' MMMM 'del' yyyy", new Locale("es", "ES"));

            if (fecha != null) {
                String fechaTexto = sdfVisual.format(fecha);
                String[] palabras = fechaTexto.split(" ");
                if (palabras.length >= 3) {
                    String mes = palabras[2];
                    palabras[2] = mes.substring(0, 1).toUpperCase() + mes.substring(1);
                    StringBuilder sb = new StringBuilder();
                    for (String s : palabras) sb.append(s).append(" ");
                    fechaTexto = sb.toString().trim();
                }
                tvFechaSeleccionada.setText(fechaTexto);
            }
        } catch (Exception e) {
            tvFechaSeleccionada.setText(fechaActualString);
        }
    }

    // --- SELECTORES Y DIÁLOGOS ---

    private void mostrarSelectorFechaMaterial() {
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(fechaActualString);
            if(date != null) today = date.getTime();
        } catch (Exception e) { e.printStackTrace(); }

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona fecha")
                .setSelection(today)
                .setTheme(R.style.TemaCalendarioVerde)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            fechaActualString = sdf.format(new Date(selection));

            actualizarTextoFechaVisual();
            actualizarUI(); // <--- Aquí se recalculará la meta para la nueva fecha
        });

        datePicker.show(getSupportFragmentManager(), "FECHA");
    }

    private void mostrarDialogoMeta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_meta, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText etMeta = view.findViewById(R.id.etNuevaMeta);

        // Opcional: Mostrar en el EditText la meta actual de ese día para editarla más fácil
        int metaActual = META_POR_DEFECTO;
        if (metasPorFecha.containsKey(fechaActualString)) {
            metaActual = metasPorFecha.get(fechaActualString);
        }
        etMeta.setText(String.valueOf(metaActual));

        view.findViewById(R.id.btnGuardarMeta).setOnClickListener(v -> {
            String metaStr = etMeta.getText().toString();
            if (!metaStr.isEmpty()) {
                int nuevaMeta = Integer.parseInt(metaStr);

                // --- CAMBIO 3: Guardar la meta SOLO para la fecha actual ---
                metasPorFecha.put(fechaActualString, nuevaMeta);

                actualizarUI(); // Refrescar la pantalla
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.btnCancelarMeta).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void mostrarDialogoAgregarAgua() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_agua, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText etCantidad = view.findViewById(R.id.etCantidadAgua);
        TextView tvHora = view.findViewById(R.id.tvSeleccionarHora);
        Button btnGuardar = view.findViewById(R.id.btnGuardarAgua);
        Button btnCancelar = view.findViewById(R.id.btnCancelarAgua);

        final String[] horaSeleccionada = {""};

        tvHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int horaActual = c.get(Calendar.HOUR_OF_DAY);
            int minutoActual = c.get(Calendar.MINUTE);

            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(horaActual)
                    .setMinute(minutoActual)
                    .setTitleText("Selecciona la hora")
                    .setTheme(R.style.TemaRelojVerde)
                    .build();

            picker.addOnPositiveButtonClickListener(dialogView -> {
                int newHour = picker.getHour();
                int newMinute = picker.getMinute();
                String amPm = (newHour < 12) ? "AM" : "PM";
                int hora12 = (newHour > 12) ? newHour - 12 : newHour;
                if (hora12 == 0) hora12 = 12;

                String horaStr = String.format(Locale.getDefault(), "%02d:%02d %s", hora12, newMinute, amPm);
                tvHora.setText(horaStr);
                horaSeleccionada[0] = horaStr;
            });

            picker.show(getSupportFragmentManager(), "HORA");
        });

        btnGuardar.setOnClickListener(v -> {
            String cantidadStr = etCantidad.getText().toString();
            if (cantidadStr.isEmpty() || horaSeleccionada[0].isEmpty()) {
                Toast.makeText(this, "Completa todos los datos", Toast.LENGTH_SHORT).show();
                return;
            }
            int cantidad = Integer.parseInt(cantidadStr);
            RegistroAgua nuevoRegistro = new RegistroAgua(cantidad, horaSeleccionada[0], fechaActualString);

            if (!baseDeDatosLocal.containsKey(fechaActualString)) {
                baseDeDatosLocal.put(fechaActualString, new ArrayList<>());
            }
            baseDeDatosLocal.get(fechaActualString).add(nuevoRegistro);

            actualizarUI();
            dialog.dismiss();
        });
        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}