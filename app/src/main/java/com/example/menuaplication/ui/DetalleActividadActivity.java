package com.example.menuaplication.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

    // Constante para identificar la petición de editar
    private static final int REQUEST_CODE_EDITAR = 1001;

    private Actividad actividad;
    private TextView tvTitulo, tvDesc, tvDetalleExtra, tvAvance, tvId, tvTiempoEst, tvTiempoInv;
    private ProgressBar pbAvance;
    private LinearLayout layoutHistorialContainer;
    private Button btnPomodoro, btnDeepWork, btnAvance, btnEliminar, btnEditar; // Añadido btnEditar

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

        // VINCULACIÓN DEL NUEVO BOTÓN
        btnEditar = findViewById(R.id.btnEditarActividad);

        // Configurar botón volver
        ImageButton btnBack = findViewById(R.id.btnBackDetalle);
        if(btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        cargarDatos();

        // Listeners
        btnPomodoro.setOnClickListener(v -> irATemporizador(TecnicaEnfoque.POMODORO));
        btnDeepWork.setOnClickListener(v -> irATemporizador(TecnicaEnfoque.DEEP_WORK));

        // Ahora estos métodos abren los Dialogs personalizados
        btnAvance.setOnClickListener(v -> mostrarDialogoAvance());
        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());

        // LÓGICA BOTÓN EDITAR (NUEVO)
        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(DetalleActividadActivity.this, EditarActividadActivity.class);
            intent.putExtra("actividad_a_editar", actividad); // Enviamos la actividad actual
            startActivityForResult(intent, REQUEST_CODE_EDITAR); // Iniciamos esperando respuesta
        });
    }

    // MÉTODO PARA CAPTURAR LOS CAMBIOS AL VOLVER DE EDITAR (NUEVO)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDITAR && resultCode == RESULT_OK && data != null) {
            // Recuperar la actividad modificada del Intent de respuesta
            Actividad actividadModificada = (Actividad) data.getSerializableExtra("actividad_actualizada");
            if (actividadModificada != null) {
                this.actividad = actividadModificada;
                cargarDatos(); // Actualizar la UI con los nuevos datos (nombre, desc, fecha, etc.)
                Toast.makeText(this, "Actividad actualizada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos por si volvimos del Temporizador (que guarda cambios en disco)
        // Buscamos la versión más reciente en el repositorio por si acaso
        for (Actividad a : Repositorio.getInstance().getActividades()) {
            if (a.getId() == actividad.getId()) {
                this.actividad = a;
                break;
            }
        }
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
            colorEstado = Color.parseColor("#4CAF50");
            btnAvance.setVisibility(View.GONE);
            btnPomodoro.setEnabled(false);
            btnDeepWork.setEnabled(false);
        } else if (porcentaje > 0) {
            estadoTexto = "EN PROGRESO";
            colorEstado = Color.parseColor("#FF9800");
            btnAvance.setVisibility(View.VISIBLE);
        } else {
            estadoTexto = "PENDIENTE";
            colorEstado = Color.parseColor("#757575");
            btnAvance.setVisibility(View.VISIBLE);
        }

        tvAvance.setText(estadoTexto + " (" + porcentaje + "%)");
        tvAvance.setTextColor(colorEstado);

        // 4. Polimorfismo
        if (actividad instanceof ActividadAcademica) {
            ActividadAcademica ac = (ActividadAcademica) actividad;
            String info = "Materia: " + ac.getAsignatura() + "\n" +
                    "Tipo: " + ac.getTipo() + "\n" +
                    "Prioridad: " + ac.getPrioridad() + "\n" +
                    "Vence: " + ac.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            tvDetalleExtra.setText(info);
        } else if (actividad instanceof ActividadPersonal) {
            ActividadPersonal ap = (ActividadPersonal) actividad;
            String info = "Lugar: " + ap.getLugar() + "\n" +
                    "Prioridad: " + ap.getPrioridad() + "\n" +
                    "Vence: " + ap.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            tvDetalleExtra.setText(info);
        }

        // 5. Historial de Sesiones
        layoutHistorialContainer.removeAllViews();
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
                View view = inflater.inflate(R.layout.item_sesion_historial, layoutHistorialContainer, false);

                TextView tvTecnica = view.findViewById(R.id.tvTecnicaSesion);
                TextView tvFecha = view.findViewById(R.id.tvFechaSesion);
                TextView tvDuracion = view.findViewById(R.id.tvDuracionSesion);
                ImageView img = view.findViewById(R.id.imgTipoSesion);

                tvTecnica.setText(sesion.getTecnica().toString().replace("_", " "));
                tvFecha.setText(sesion.getFechaHora().format(timeFormatter));
                tvDuracion.setText(sesion.getDuracionMinutos() + " min");

                if (sesion.getTecnica() == TecnicaEnfoque.POMODORO) {
                    img.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEBEE")));
                    img.setColorFilter(Color.parseColor("#D32F2F"));
                } else {
                    img.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EDE7F6")));
                    img.setColorFilter(Color.parseColor("#673AB7"));
                }
                layoutHistorialContainer.addView(view);
            }
        }
    }

    // --- DIALOGO 1: REGISTRAR AVANCE ---
    private void mostrarDialogoAvance() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_registrar_avance);
        dialog.setCancelable(true);

        TextView tvNombre = dialog.findViewById(R.id.tvDialogActividadNombre);
        TextView tvActual = dialog.findViewById(R.id.tvDialogAvanceActual);
        EditText etNuevo = dialog.findViewById(R.id.etDialogNuevoAvance);
        Button btnCancel = dialog.findViewById(R.id.btnCancelarAvance);
        Button btnGuardar = dialog.findViewById(R.id.btnGuardarAvance);

        tvNombre.setText(actividad.getNombre());
        tvActual.setText("Avance actual: " + (int) actividad.getPorcentajeAvance() + "%");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String input = etNuevo.getText().toString().trim();
            if (TextUtils.isEmpty(input)) {
                etNuevo.setError("Ingresa un valor");
                return;
            }
            try {
                double nuevoAvance = Double.parseDouble(input.replace(",", "."));
                if (nuevoAvance < 0 || nuevoAvance > 100) {
                    etNuevo.setError("Debe ser entre 0 y 100");
                    return;
                }
                dialog.dismiss();
                // Llamamos a la confirmación
                mostrarConfirmacionAvance(nuevoAvance);

            } catch (NumberFormatException e) {
                etNuevo.setError("Número inválido");
            }
        });

        dialog.show();
        configurarVentanaTransparente(dialog);
    }

    // --- DIALOGO 2: CONFIRMAR AVANCE ---
    private void mostrarConfirmacionAvance(double nuevoAvance) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmar_avance);

        TextView tvMensaje = dialog.findViewById(R.id.tvMensajeConfirmacion);
        Button btnNo = dialog.findViewById(R.id.btnNoConfirmar);
        Button btnSi = dialog.findViewById(R.id.btnSiConfirmar);

        tvMensaje.setText("¿Estás seguro que quieres que la actividad " + actividad.getNombre() +
                " tenga un progreso del " + (int) nuevoAvance + "%?");

        btnNo.setOnClickListener(v -> dialog.dismiss());

        btnSi.setOnClickListener(v -> {
            actividad.setPorcentajeAvance(nuevoAvance);
            Repositorio.getInstance().actualizarActividad(actividad);
            cargarDatos(); // Refrescar pantalla
            Toast.makeText(this, "Progreso actualizado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
        configurarVentanaTransparente(dialog);
    }

    // --- DIALOGO 3: ELIMINAR ACTIVIDAD ---
    private void mostrarDialogoEliminar() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_eliminar_actividad);

        TextView tvMensaje = dialog.findViewById(R.id.tvMensajeEliminar);
        Button btnCancelar = dialog.findViewById(R.id.btnCancelarEliminar);
        Button btnEliminar = dialog.findViewById(R.id.btnAceptarEliminar);

        tvMensaje.setText("¿Estás seguro de eliminar la actividad " + actividad.getNombre() + "?");

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnEliminar.setOnClickListener(v -> {
            Repositorio.getInstance().eliminarActividad(actividad);
            Toast.makeText(this, "Actividad eliminada", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            finish(); // Volver a la lista
        });

        dialog.show();
        configurarVentanaTransparente(dialog);
    }

    // Método auxiliar para evitar código repetido de transparencia
    private void configurarVentanaTransparente(Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void irATemporizador(TecnicaEnfoque tecnica) {
        Intent intent = new Intent(this, TemporizadorActivity.class);
        intent.putExtra("TECNICA", tecnica);
        intent.putExtra("ACTIVIDAD_OBJ", actividad);
        startActivity(intent);
    }
}