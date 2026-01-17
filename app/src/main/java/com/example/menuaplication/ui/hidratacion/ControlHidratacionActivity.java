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
import com.example.menuaplication.data.RepositorioHidratacion;
import com.example.menuaplication.model.hidratacion.RegistroAgua;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Actividad principal para el módulo de Control de Hidratación.
 *
 * Esta pantalla permite al usuario gestionar su consumo diario de agua. Sus funciones principales incluyen:
 * - Visualizar el progreso diario mediante una barra circular y porcentajes.
 * - Registrar nuevas ingestas de agua con hora específica.
 * - Consultar el historial de registros filtrando por fecha mediante un calendario.
 * - Establecer metas diarias personalizadas que se guardan independientemente para cada día.
 *
 * Utiliza {@link RepositorioHidratacion} para la persistencia de datos y componentes de Material Design
 * para la selección de fechas y horas.
 *
 * @author SRGM
 * @version 1.0
 */
public class ControlHidratacionActivity extends AppCompatActivity {

    // --- VISTAS (UI) ---
    /** Muestra la fecha actualmente seleccionada en formato legible. */
    private TextView tvFechaSeleccionada;
    /** Muestra el porcentaje de cumplimiento de la meta diaria. */
    private TextView tvPorcentaje;
    /** Muestra la meta establecida para el día seleccionado. */
    private TextView tvMetaDiaria;
    /** Muestra la suma total de mililitros consumidos en el día. */
    private TextView tvTotalConsumido;
    /** Barra de progreso visual circular que representa el avance hacia la meta. */
    private ProgressBar progressBar;
    /** Lista para mostrar el detalle de cada ingesta de agua del día. */
    private RecyclerView rvRegistros;
    /** Botón para abrir el diálogo de registrar nueva ingesta. */
    private Button btnRegistrar;
    /** Botón para abrir el diálogo de configuración de meta. */
    private Button btnEstablecerMeta;

    // --- DATOS Y LÓGICA ---
    /** Adaptador para gestionar la lista de registros en el RecyclerView. */
    private RegistroAguaAdapter adapter;
    /**
     * Fecha seleccionada actualmente en formato "dd/MM/yyyy".
     * Se usa como clave para consultar y guardar datos en el repositorio.
     */
    private String fechaActualString;

    /**
     * Instancia del repositorio para acceder a los datos persistentes de hidratación.
     */
    private RepositorioHidratacion repositorio;

    /**
     * Método de ciclo de vida llamado cuando se crea la actividad.
     * Inicializa el repositorio, las vistas, la configuración del RecyclerView, los eventos y carga la UI inicial.
     *
     * @param savedInstanceState Estado guardado de la actividad (si existe).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_hidratacion);

        // 1. Inicializar Repositorio
        repositorio = RepositorioHidratacion.getInstance(this);

        // 2. Inicializar Vistas
        inicializarVistas();

        // 3. Configurar RecyclerView
        configurarRecyclerView();

        // 4. Configurar Eventos
        configurarEventos();

        // 5. Cargar UI
        actualizarUI();
    }

    /**
     * Vincula los objetos de vista con los elementos del archivo XML layout.
     * Establece la fecha actual por defecto al iniciar la pantalla.
     */
    private void inicializarVistas() {
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);
        tvPorcentaje = findViewById(R.id.tvPorcentaje);
        tvMetaDiaria = findViewById(R.id.tvMetaDiaria);
        tvTotalConsumido = findViewById(R.id.tvTotalConsumido);
        progressBar = findViewById(R.id.progressBarHidratacion);
        rvRegistros = findViewById(R.id.rvRegistrosAgua);
        btnRegistrar = findViewById(R.id.btnRegistrarAgua);
        btnEstablecerMeta = findViewById(R.id.btnEstablecerMeta);

        // Ponemos la fecha de hoy por defecto automáticamente
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        fechaActualString = sdf.format(new Date());

        actualizarTextoFechaVisual();
    }

    /**
     * Configura el {@link RecyclerView} con un {@link LinearLayoutManager} vertical
     * y asigna el adaptador vacío inicial.
     */
    private void configurarRecyclerView() {
        rvRegistros.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RegistroAguaAdapter(new ArrayList<>());
        rvRegistros.setAdapter(adapter);
    }

    /**
     * Configura los listeners (escuchadores) para los botones y elementos interactivos.
     * Define las acciones para cambiar fecha, establecer meta, agregar agua y volver atrás.
     */
    private void configurarEventos() {
        View cardFecha = findViewById(R.id.cardFecha);
        if(cardFecha != null) cardFecha.setOnClickListener(v -> mostrarSelectorFechaMaterial());
        else tvFechaSeleccionada.setOnClickListener(v -> mostrarSelectorFechaMaterial());

        btnEstablecerMeta.setOnClickListener(v -> mostrarDialogoMeta());
        btnRegistrar.setOnClickListener(v -> mostrarDialogoAgregarAgua());

        View btnVolver = findViewById(R.id.btnVolver);
        if(btnVolver != null) btnVolver.setOnClickListener(v -> finish());
    }

    /**
     * Actualiza toda la interfaz de usuario con los datos más recientes del repositorio.
     * Pasos que realiza:
     * 1. Obtiene la lista de registros y la meta para la fecha seleccionada.
     * 2. Actualiza el adaptador de la lista.
     * 3. Calcula el total consumido y el porcentaje de avance.
     * 4. Actualiza los textos y la barra de progreso.
     */
    private void actualizarUI() {
        // Pedimos los datos al repositorio para la fecha actual
        List<RegistroAgua> registrosDelDia = repositorio.getRegistros(fechaActualString);

        adapter.actualizarLista(registrosDelDia);

        int totalMl = 0;
        for (RegistroAgua reg : registrosDelDia) totalMl += reg.getCantidadMl();

        // Pedimos la meta al repositorio
        int metaDelDiaActual = repositorio.getMeta(fechaActualString);

        tvTotalConsumido.setText(totalMl + " ml");
        tvMetaDiaria.setText(metaDelDiaActual + " ml");

        int porcentaje = 0;
        if (metaDelDiaActual > 0) porcentaje = (totalMl * 100) / metaDelDiaActual;

        // La barra se llena hasta 100, aunque el porcentaje texto puede ser mayor
        progressBar.setProgress(Math.min(porcentaje, 100));
        tvPorcentaje.setText(porcentaje + "%");
    }

    /**
     * Formatea la fecha interna ("dd/MM/yyyy") a un formato visual más amigable
     * (ej. "21 de Enero del 2026") para mostrar en el encabezado.
     */
    private void actualizarTextoFechaVisual() {
        try {
            SimpleDateFormat sdfInterno = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = sdfInterno.parse(fechaActualString);
            SimpleDateFormat sdfVisual = new SimpleDateFormat("d 'de' MMMM 'del' yyyy", new Locale("es", "ES"));

            if (fecha != null) {
                String fechaTexto = sdfVisual.format(fecha);
                // Capitalizar la primera letra del mes para mejor estética
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

    // --- DIÁLOGOS Y MATERIAL PICKERS ---

    /**
     * Muestra un selector de fecha (MaterialDatePicker) estilizado.
     * Al seleccionar una fecha, actualiza la variable {@code fechaActualString} y refresca la UI.
     */
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
                .setTheme(R.style.TemaCalendarioVerde) // Estilo personalizado definido en themes.xml
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            fechaActualString = sdf.format(new Date(selection));

            actualizarTextoFechaVisual();
            actualizarUI();
        });

        datePicker.show(getSupportFragmentManager(), "FECHA");
    }

    /**
     * Muestra un cuadro de diálogo personalizado para editar la meta diaria de agua.
     * Permite ingresar un valor numérico y lo guarda en el repositorio asociado a la fecha actual.
     */
    private void mostrarDialogoMeta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_meta, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText etMeta = view.findViewById(R.id.etNuevaMeta);

        // Mostrar meta actual del repo en el campo de texto
        int metaActual = repositorio.getMeta(fechaActualString);
        etMeta.setText(String.valueOf(metaActual));

        view.findViewById(R.id.btnGuardarMeta).setOnClickListener(v -> {
            String metaStr = etMeta.getText().toString();
            if (!metaStr.isEmpty()) {
                int nuevaMeta = Integer.parseInt(metaStr);

                // Guardar en repositorio persistente
                repositorio.setMeta(fechaActualString, nuevaMeta);

                actualizarUI();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.btnCancelarMeta).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    /**
     * Muestra un cuadro de diálogo para registrar una nueva ingesta de agua.
     * Incluye un selector de hora (MaterialTimePicker) y un campo para la cantidad en ml.
     */
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

        // Configuración del selector de hora
        tvHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int horaActual = c.get(Calendar.HOUR_OF_DAY);
            int minutoActual = c.get(Calendar.MINUTE);

            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(horaActual)
                    .setMinute(minutoActual)
                    .setTitleText("Selecciona la hora")
                    .setTheme(R.style.TemaRelojVerde) // Estilo personalizado definido en themes.xml
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

        // Acción de guardar registro
        btnGuardar.setOnClickListener(v -> {
            String cantidadStr = etCantidad.getText().toString();
            if (cantidadStr.isEmpty() || horaSeleccionada[0].isEmpty()) {
                Toast.makeText(this, "Completa todos los datos", Toast.LENGTH_SHORT).show();
                return;
            }
            int cantidad = Integer.parseInt(cantidadStr);
            RegistroAgua nuevoRegistro = new RegistroAgua(cantidad, horaSeleccionada[0], fechaActualString);

            // Guardar en repositorio persistente
            repositorio.agregarRegistro(fechaActualString, nuevoRegistro);

            actualizarUI();
            dialog.dismiss();
        });
        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}