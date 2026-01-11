package com.example.menuaplication.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.menuaplication.R;
import com.example.menuaplication.data.Repositorio;
import com.example.menuaplication.model.Actividad;
import com.example.menuaplication.model.ActividadAcademica;
import com.example.menuaplication.model.ActividadPersonal;
import com.example.menuaplication.model.SesionEnfoque;
import com.example.menuaplication.model.TecnicaEnfoque;

import java.time.format.DateTimeFormatter;

public class DetalleActividadActivity extends AppCompatActivity {

    private Actividad actividad;
    private TextView tvTitulo, tvDesc, tvDetalleExtra, tvAvance, tvId, tvTiempoEst, tvTiempoInv;
    private ProgressBar pbAvance;
    // Usamos un LinearLayout dentro del ScrollView para el historial
    private LinearLayout layoutHistorialContainer;
    private Button btnPomodoro, btnDeepWork, btnAvance, btnEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_actividad);

        // Recuperar el objeto
        actividad = (Actividad) getIntent().getSerializableExtra("ACTIVIDAD_EXTRA");

        // Vincular Vistas
        tvId = findViewById(R.id.tvIdActividad);
        tvTitulo = findViewById(R.id.tvTituloDetalle);
        tvDesc = findViewById(R.id.tvDescDetalle);
        tvDetalleExtra = findViewById(R.id.tvExtraDetalle);
        tvAvance = findViewById(R.id.tvAvanceDetalle);
        tvTiempoEst = findViewById(R.id.tvTiempoEstimado);
        tvTiempoInv = findViewById(R.id.tvTiempoInvertido);
        pbAvance = findViewById(R.id.pbDetalle);
        layoutHistorialContainer = findViewById(R.id.layoutHistorialContainer);

        btnPomodoro = findViewById(R.id.btnPomodoro);
        btnDeepWork = findViewById(R.id.btnDeepWork);
        btnAvance = findViewById(R.id.btnRegistrarAvance);
        btnEliminar = findViewById(R.id.btnEliminar);

        // Configurar botón volver (flecha)
        ImageButton btnBack = findViewById(R.id.btnBackDetalle);
        if(btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        cargarDatos();

        // Listeners
        btnPomodoro.setOnClickListener(v -> irATemporizador(TecnicaEnfoque.POMODORO));
        btnDeepWork.setOnClickListener(v -> irATemporizador(TecnicaEnfoque.DEEP_WORK));
        btnAvance.setOnClickListener(v -> mostrarDialogoAvance());
        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargamos los datos al volver del temporizador por si hubo cambios
        cargarDatos();
    }

    private void cargarDatos() {
        if (actividad == null) return;

        // 1. Datos Básicos
        tvId.setText("ID: " + actividad.getId());
        tvTitulo.setText(actividad.getNombre());
        tvDesc.setText(actividad.getDescripcion());

        // 2. Tiempos
        tvTiempoEst.setText("Estimado: " + actividad.getTiempoEstimadoMinutos() + " min");
        tvTiempoInv.setText("Invertido: " + actividad.getMinutosInvertidos() + " min");

        // 3. Estado y Barra de Progreso
        int porcentaje = (int) actividad.getPorcentajeAvance();
        pbAvance.setProgress(porcentaje);

        String estadoTexto = "";
        int colorEstado = Color.BLACK;

        if (porcentaje >= 100) {
            estadoTexto = "¡COMPLETADA!";
            colorEstado = Color.parseColor("#4CAF50"); // Verde
            btnAvance.setVisibility(View.GONE);
            btnPomodoro.setEnabled(false);
            btnDeepWork.setEnabled(false);
        } else if (porcentaje > 0) {
            estadoTexto = "EN PROGRESO";
            colorEstado = Color.parseColor("#FF9800"); // Naranja
            btnAvance.setVisibility(View.VISIBLE);
        } else {
            estadoTexto = "PENDIENTE";
            colorEstado = Color.parseColor("#757575"); // Gris
            btnAvance.setVisibility(View.VISIBLE);
        }

        tvAvance.setText(estadoTexto + " (" + porcentaje + "%)");
        tvAvance.setTextColor(colorEstado);

        // 4. Polimorfismo (Datos específicos)
        if (actividad instanceof ActividadAcademica) {
            ActividadAcademica ac = (ActividadAcademica) actividad;
            String info = "Materia: " + ac.getAsignatura() + "\n" +
                    "Tipo: " + ac.getTipo() + "\n" +
                    "Prioridad: " + ac.getPrioridad();
            tvDetalleExtra.setText(info);
        } else if (actividad instanceof ActividadPersonal) {
            ActividadPersonal ap = (ActividadPersonal) actividad;
            String info = "Lugar: " + ap.getLugar() + "\n" +
                    "Prioridad: " + ap.getPrioridad();
            tvDetalleExtra.setText(info);
        }

        // 5. Historial de Sesiones (Visualmente mejorado)
        layoutHistorialContainer.removeAllViews(); // Limpiar lista anterior
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm");

        if (actividad.getHistorialSesiones().isEmpty()) {
            TextView vacio = new TextView(this);
            vacio.setText("No has registrado sesiones de trabajo aún.");
            vacio.setTextColor(Color.GRAY);
            vacio.setPadding(8, 8, 8, 8);
            layoutHistorialContainer.addView(vacio);
        } else {
            LayoutInflater inflater = LayoutInflater.from(this);

            for (SesionEnfoque sesion : actividad.getHistorialSesiones()) {
                // Inflamos el XML 'item_sesion_historial' que creaste en el paso anterior
                // Si te da error en R.layout.item_sesion_historial, asegúrate de haber creado el XML
                View view = inflater.inflate(R.layout.item_sesion_historial, layoutHistorialContainer, false);

                TextView tvTecnica = view.findViewById(R.id.tvTecnicaSesion);
                TextView tvFecha = view.findViewById(R.id.tvFechaSesion);
                TextView tvDuracion = view.findViewById(R.id.tvDuracionSesion);
                ImageView img = view.findViewById(R.id.imgTipoSesion);

                // Llenar datos
                tvTecnica.setText(sesion.getTecnica().toString().replace("_", " "));
                tvFecha.setText(sesion.getFechaHora().format(timeFormatter));
                tvDuracion.setText(sesion.getDuracionMinutos() + " min");

                // Cambiar icono/color según técnica
                if (sesion.getTecnica() == TecnicaEnfoque.POMODORO) {
                    // Rojo suave para Pomodoro
                    img.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEBEE")));
                    img.setColorFilter(Color.parseColor("#D32F2F"));
                } else {
                    // Morado suave para Deep Work
                    img.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EDE7F6")));
                    img.setColorFilter(Color.parseColor("#673AB7"));
                }

                layoutHistorialContainer.addView(view);
            }
        }
    }

    private void mostrarDialogoAvance() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Progreso");
        builder.setMessage("Ingresa el porcentaje total acumulado (0-100%):");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Ej: " + ((int)actividad.getPorcentajeAvance()));
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String texto = input.getText().toString();
            if (!texto.isEmpty()) {
                double nuevoAvance = Double.parseDouble(texto);
                if (nuevoAvance < 0) nuevoAvance = 0;
                if (nuevoAvance > 100) nuevoAvance = 100;

                actividad.setPorcentajeAvance(nuevoAvance);
                Repositorio.getInstance().actualizarActividad(actividad);
                cargarDatos();
                Toast.makeText(this, "Progreso actualizado", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar Actividad?")
                .setMessage("Estás a punto de eliminar '" + actividad.getNombre() + "'.\nEsta acción no se puede deshacer.")
                .setPositiveButton("SÍ, ELIMINAR", (dialog, which) -> {
                    Repositorio.getInstance().eliminarActividad(actividad);
                    Toast.makeText(this, "Actividad eliminada", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra esta pantalla y vuelve a la lista
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void irATemporizador(TecnicaEnfoque tecnica) {
        Intent intent = new Intent(this, TemporizadorActivity.class);
        intent.putExtra("TECNICA", tecnica);
        intent.putExtra("ACTIVIDAD_OBJ", actividad);
        startActivity(intent);
    }
}