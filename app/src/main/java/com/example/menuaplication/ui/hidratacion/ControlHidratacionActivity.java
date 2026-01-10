package com.example.menuaplication.ui.hidratacion;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.example.menuaplication.model.RegistroAgua;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ControlHidratacionActivity extends AppCompatActivity {

    // Vistas
    private TextView tvFechaSeleccionada, tvPorcentaje, tvMetaDiaria, tvTotalConsumido;
    private ProgressBar progressBar;
    private RecyclerView rvRegistros;
    private Button btnRegistrar, btnEstablecerMeta;

    // Datos y Lógica
    private RegistroAguaAdapter adapter;
    private String fechaActualString; // Clave para el mapa (ej: "19/01/2026")
    private int metaDiaria = 2000;

    // "Base de Datos" temporal en memoria
    // Mapa: Clave es la FECHA -> Valor es la LISTA de vasos de agua
    private Map<String, List<RegistroAgua>> baseDeDatosLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_hidratacion);

        // 1. Inicializar Componentes
        inicializarVistas();

        // 2. Inicializar Datos (Simulación de DB)
        baseDeDatosLocal = new HashMap<>();
        cargarDatosPorDefecto(); // Tus datos del 19 de Enero

        // 3. Configurar RecyclerView
        configurarRecyclerView();

        // 4. Configurar Listeners (Clics)
        configurarEventos();

        // 5. Cargar interfaz inicial
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

        // Establecer fecha inicial (Puedes poner la de hoy o la fija que pediste)
        fechaActualString = "19/01/2026";
        tvFechaSeleccionada.setText(fechaActualString);
    }

    private void cargarDatosPorDefecto() {
        // Creamos la lista para el 19 de Enero
        List<RegistroAgua> listaEnero19 = new ArrayList<>();
        listaEnero19.add(new RegistroAgua(250, "08:00 AM", "19/01/2026"));
        listaEnero19.add(new RegistroAgua(500, "10:00 AM", "19/01/2026"));

        // Guardamos en el Mapa
        baseDeDatosLocal.put("19/01/2026", listaEnero19);
    }

    private void configurarRecyclerView() {
        rvRegistros.setLayoutManager(new LinearLayoutManager(this));
        // Inicializamos con una lista vacía, luego updateUI la llena
        adapter = new RegistroAguaAdapter(new ArrayList<>());
        rvRegistros.setAdapter(adapter);
    }

    private void configurarEventos() {
        // CAMBIAR FECHA
        findViewById(R.id.cardFecha).setOnClickListener(v -> mostrarSelectorFecha());

        // ESTABLECER META
        btnEstablecerMeta.setOnClickListener(v -> mostrarDialogoMeta());

        // AGREGAR AGUA
        btnRegistrar.setOnClickListener(v -> mostrarDialogoAgregarAgua());
    }

    private void actualizarUI() {
        // 1. Obtener la lista de la fecha actual
        List<RegistroAgua> registrosDelDia = baseDeDatosLocal.get(fechaActualString);

        if (registrosDelDia == null) {
            // Si no hay registros ese día, creamos una lista vacía para evitar errores
            registrosDelDia = new ArrayList<>();
        }

        // 2. Actualizar el adaptador (Lista visual)
        adapter.actualizarLista(registrosDelDia);

        // 3. Calcular totales
        int totalMl = 0;
        for (RegistroAgua reg : registrosDelDia) {
            totalMl += reg.getCantidadMl();
        }

        // 4. Actualizar textos
        tvTotalConsumido.setText(totalMl + " ml");
        tvMetaDiaria.setText(metaDiaria + " ml");

        // 5. Calcular porcentaje (Máximo 100%)
        int porcentaje = 0;
        if (metaDiaria > 0) {
            porcentaje = (totalMl * 100) / metaDiaria;
        }

        // Bloqueo visual para que la barra no se salga si tomaste más de la cuenta
        int progresoBarra = Math.min(porcentaje, 100);

        progressBar.setProgress(progresoBarra);
        tvPorcentaje.setText(porcentaje + "%"); // El texto sí puede decir 120%, pero la barra se queda llena
    }

    // --- DIÁLOGOS Y LÓGICA DE NEGOCIO ---

    private void mostrarSelectorFecha() {
        // Lógica simple para extraer día, mes año del string actual
        String[] partes = fechaActualString.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]) - 1; // Enero es 0
        int anio = Integer.parseInt(partes[2]);

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Formatear a dos dígitos (ej: 05 en vez de 5)
            String nuevoDia = String.format(Locale.getDefault(), "%02d", dayOfMonth);
            String nuevoMes = String.format(Locale.getDefault(), "%02d", month + 1);

            fechaActualString = nuevoDia + "/" + nuevoMes + "/" + year;
            tvFechaSeleccionada.setText(fechaActualString);

            // MAGIA: Al cambiar la fecha, actualizamos la UI y "aparecen/desaparecen" datos
            actualizarUI();

        }, anio, mes, dia);
        datePicker.show();
    }

    private void mostrarDialogoMeta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_meta, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText etMeta = view.findViewById(R.id.etNuevaMeta);
        Button btnGuardar = view.findViewById(R.id.btnGuardarMeta);
        Button btnCancelar = view.findViewById(R.id.btnCancelarMeta);

        btnGuardar.setOnClickListener(v -> {
            String metaStr = etMeta.getText().toString();
            if (!metaStr.isEmpty()) {
                metaDiaria = Integer.parseInt(metaStr);
                actualizarUI(); // Recalcular porcentaje con nueva meta
                dialog.dismiss();
            } else {
                etMeta.setError("Ingresa un valor");
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
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

        // Lógica del Reloj
        final String[] horaSeleccionada = {""}; // Array de 1 elemento para poder modificarlo dentro del listener
        tvHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (timePicker, hourOfDay, minute) -> {
                String amPm = (hourOfDay < 12) ? "AM" : "PM";
                int hora12 = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                if (hora12 == 0) hora12 = 12; // Ajuste para mediodía/medianoche

                String horaStr = String.format(Locale.getDefault(), "%02d:%02d %s", hora12, minute, amPm);
                tvHora.setText(horaStr);
                horaSeleccionada[0] = horaStr;
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
        });

        btnGuardar.setOnClickListener(v -> {
            String cantidadStr = etCantidad.getText().toString();
            if (cantidadStr.isEmpty() || horaSeleccionada[0].isEmpty()) {
                Toast.makeText(this, "Completa todos los datos", Toast.LENGTH_SHORT).show();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            RegistroAgua nuevoRegistro = new RegistroAgua(cantidad, horaSeleccionada[0], fechaActualString);

            // AGREGAR A LA "BASE DE DATOS"
            if (!baseDeDatosLocal.containsKey(fechaActualString)) {
                baseDeDatosLocal.put(fechaActualString, new ArrayList<>());
            }
            baseDeDatosLocal.get(fechaActualString).add(nuevoRegistro);

            actualizarUI(); // Refrescar lista y barra
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}