package com.example.menuaplication.ui.actividades;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.data.RepositorioActividades;
import com.example.menuaplication.model.actividades.Actividad;
import com.example.menuaplication.model.actividades.ActividadAcademica;
import com.example.menuaplication.model.actividades.Prioridad;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Adaptador para el RecyclerView que gestiona la visualización de la lista de actividades.
 * Se encarga de vincular los datos del modelo {@link Actividad} con las vistas del layout (item_actividad).
 *
 * <p>Incluye lógica visual para:</p>
 * <ul>
 * <li>Colorear el estado (Verde: Completada, Naranja: En Progreso, Gris: Pendiente).</li>
 * <li>Mostrar la prioridad con colores de fondo distintivos.</li>
 * <li>Gestionar eventos de clic (Detalles) y clic largo (Opciones rápidas).</li>
 * </ul>
 *
 * @author José Paladines
 * @version 1.0
 */
public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ViewHolder> {

    /** Lista de datos a mostrar. */
    private List<Actividad> actividades;

    /** Referencia a la actividad contenedora para manejo de contextos y diálogos. */
    private final Activity activity;

    private static final String TAG = "ActividadAdapter";

    /**
     * Constructor del adaptador.
     *
     * @param actividades Lista inicial de actividades.
     * @param activity    Actividad que contiene el RecyclerView.
     */
    public ActividadAdapter(List<Actividad> actividades, Activity activity) {
        this.actividades = actividades;
        this.activity = activity;
    }

    /**
     * Actualiza la lista de datos y notifica al RecyclerView para refrescar la vista.
     *
     * @param nuevasActividades Nueva lista filtrada u ordenada.
     */
    public void setActividades(List<Actividad> nuevasActividades) {
        this.actividades = nuevasActividades;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActividadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Vincula los datos de una actividad específica con los elementos visuales del ViewHolder.
     * Aquí reside la lógica de negocio visual (colores, textos dinámicos).
     *
     * @param holder   El ViewHolder a actualizar.
     * @param position La posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ActividadAdapter.ViewHolder holder, int position) {
        Actividad actividad = actividades.get(position);

        holder.tvNombre.setText(actividad.getNombre());

        // Identificar tipo mediante polimorfismo
        String tipo = (actividad instanceof ActividadAcademica) ? "Académica" : "Personal";
        holder.tvTipo.setText(tipo);

        holder.progressBar.setProgress((int) actividad.getPorcentajeAvance());
        holder.tvPorcentaje.setText((int) actividad.getPorcentajeAvance() + "%");

        // LÓGICA DE ESTADO (Sin vencidos, ya que se filtran antes)
        if (actividad.getPorcentajeAvance() >= 100) {
            holder.tvEstado.setText("COMPLETADA");
            holder.tvEstado.setTextColor(Color.parseColor("#4CAF50")); // Verde
        } else if (actividad.getPorcentajeAvance() > 0) {
            holder.tvEstado.setText("EN PROGRESO");
            holder.tvEstado.setTextColor(Color.parseColor("#FF9800")); // Naranja
        } else {
            holder.tvEstado.setText("PENDIENTE");
            holder.tvEstado.setTextColor(Color.parseColor("#757575")); // Gris
        }

        // Lógica visual de Prioridad
        holder.tvPrioridad.setText(String.valueOf(actividad.getPrioridad()));
        if (actividad.getPrioridad() == Prioridad.ALTA) {
            holder.tvPrioridad.setBackgroundColor(Color.parseColor("#FFEBEE"));
            holder.tvPrioridad.setTextColor(Color.RED);
        } else if (actividad.getPrioridad() == Prioridad.MEDIA) {
            holder.tvPrioridad.setBackgroundColor(Color.parseColor("#FFF3E0"));
            holder.tvPrioridad.setTextColor(Color.parseColor("#EF6C00"));
        } else {
            holder.tvPrioridad.setBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.tvPrioridad.setTextColor(Color.parseColor("#2E7D32"));
        }

        // Formateo de fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        if (actividad.getFechaVencimiento() != null) {
            holder.tvFecha.setText("Vence: " + actividad.getFechaVencimiento().format(formatter));
            holder.tvFecha.setTextColor(Color.parseColor("#757575"));
        } else {
            holder.tvFecha.setText("Sin fecha");
        }

        // Click corto -> Navegar a Detalle
        holder.itemView.setOnClickListener(v -> {
            Context vCtx = v.getContext();
            Intent intent = new Intent(vCtx, DetalleActividadActivity.class);
            intent.putExtra("ACTIVIDAD_EXTRA", actividad);
            vCtx.startActivity(intent);
        });

        // Click largo -> Mostrar opciones rápidas (Avance / Eliminar)
        holder.itemView.setOnLongClickListener(v -> {
            mostrarOpciones(actividad, holder.getAdapterPosition(), v.getContext());
            return true;
        });
    }

    /**
     * Muestra un cuadro de diálogo con opciones rápidas al mantener pulsada una actividad.
     */
    private void mostrarOpciones(Actividad actividad, int position, Context viewContext) {
        Context ctx = (viewContext != null) ? viewContext : activity;
        if (!(ctx instanceof Activity)) return;

        String[] options = {"Registrar Avance", "Eliminar Actividad"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(actividad.getNombre());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                mostrarDialogoAvance(actividad, position, ctx);
            } else {
                mostrarDialogoEliminar(actividad, position, ctx);
            }
        });
        builder.show();
    }

    // --- DIALOGOS ---

    /**
     * Muestra el diálogo personalizado para ingresar manualmente un porcentaje de avance.
     */
    private void mostrarDialogoAvance(Actividad actividad, int position, Context callerContext) {
        Context ctx = (callerContext instanceof Activity) ? callerContext : activity;
        if (!(ctx instanceof Activity)) return;
        Activity dialogActivity = (Activity) ctx;

        try {
            final Dialog dialog = new Dialog(dialogActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_registrar_avance);
            dialog.setCancelable(true);

            TextView tvNombre = dialog.findViewById(R.id.tvDialogActividadNombre);
            TextView tvActual = dialog.findViewById(R.id.tvDialogAvanceActual);
            EditText etNuevo = dialog.findViewById(R.id.etDialogNuevoAvance);
            Button btnCancel = dialog.findViewById(R.id.btnCancelarAvance);
            Button btnGuardar = dialog.findViewById(R.id.btnGuardarAvance);

            if (tvNombre != null) tvNombre.setText(actividad.getNombre());
            if (tvActual != null) tvActual.setText("Avance actual: " + (int) actividad.getPorcentajeAvance() + "%");

            if (btnCancel != null) btnCancel.setOnClickListener(v -> dialog.dismiss());

            if (btnGuardar != null) {
                btnGuardar.setOnClickListener(v -> {
                    String input = (etNuevo != null) ? etNuevo.getText().toString().trim() : "";
                    if (TextUtils.isEmpty(input)) {
                        if (etNuevo != null) etNuevo.setError("Ingresa un valor");
                        return;
                    }
                    try {
                        double nuevoAvance = Double.parseDouble(input.replace(",", "."));
                        if (nuevoAvance < 0 || nuevoAvance > 100) {
                            if (etNuevo != null) etNuevo.setError("Debe ser entre 0 y 100");
                            return;
                        }

                        dialog.dismiss();
                        mostrarConfirmacionAvance(actividad, position, nuevoAvance, callerContext);

                    } catch (NumberFormatException nfe) {
                        if (etNuevo != null) etNuevo.setError("Número inválido");
                    }
                });
            }

            dialog.show();
            configurarVentanaTransparente(dialog);

        } catch (Exception e) {
            Log.e(TAG, "Error al abrir dialogo registrar avance", e);
        }
    }

    /**
     * Diálogo de confirmación antes de guardar el nuevo avance en el repositorio.
     */
    private void mostrarConfirmacionAvance(Actividad actividad, int position, double nuevoAvance, Context callerContext) {
        Context ctx = (callerContext instanceof Activity) ? callerContext : activity;
        if (!(ctx instanceof Activity)) return;
        Activity dialogActivity = (Activity) ctx;

        try {
            final Dialog dialog = new Dialog(dialogActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_confirmar_avance);

            TextView tvMensaje = dialog.findViewById(R.id.tvMensajeConfirmacion);
            Button btnNo = dialog.findViewById(R.id.btnNoConfirmar);
            Button btnSi = dialog.findViewById(R.id.btnSiConfirmar);

            if (tvMensaje != null) {
                tvMensaje.setText("¿Estás seguro que quieres que la actividad " + actividad.getNombre() +
                        " tenga un progreso del " + (int) nuevoAvance + "%?");
            }

            if (btnNo != null) btnNo.setOnClickListener(v -> dialog.dismiss());

            if (btnSi != null) {
                btnSi.setOnClickListener(v -> {
                    actividad.setPorcentajeAvance(nuevoAvance);
                    // IMPORTANTE: Guardar en repositorio para que persista
                    RepositorioActividades.getInstance().actualizarActividad(actividad);

                    if (position >= 0 && position < actividades.size()) {
                        notifyItemChanged(position);
                    } else {
                        notifyDataSetChanged();
                    }
                    Toast.makeText(dialogActivity, "Progreso actualizado", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }

            dialog.show();
            configurarVentanaTransparente(dialog);

        } catch (Exception e) {
            Log.e(TAG, "Error en mostrarConfirmacionAvance", e);
        }
    }

    /**
     * Muestra diálogo de advertencia para eliminar definitivamente una actividad.
     */
    private void mostrarDialogoEliminar(Actividad actividad, int position, Context callerContext) {
        Context ctx = (callerContext instanceof Activity) ? callerContext : activity;
        if (!(ctx instanceof Activity)) return;
        Activity dialogActivity = (Activity) ctx;

        try {
            final Dialog dialog = new Dialog(dialogActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_eliminar_actividad);

            TextView tvMensaje = dialog.findViewById(R.id.tvMensajeEliminar);
            Button btnCancelar = dialog.findViewById(R.id.btnCancelarEliminar);
            Button btnEliminar = dialog.findViewById(R.id.btnAceptarEliminar);

            if (tvMensaje != null) tvMensaje.setText("¿Estás seguro de eliminar la actividad " + actividad.getNombre() + "?");

            if (btnCancelar != null) btnCancelar.setOnClickListener(v -> dialog.dismiss());

            if (btnEliminar != null) {
                btnEliminar.setOnClickListener(v -> {
                    // Primero eliminamos del repositorio (disco)
                    RepositorioActividades.getInstance().eliminarActividad(actividad);

                    // Luego actualizamos la lista visual
                    if (position >= 0 && position < actividades.size()) {
                        actividades.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, actividades.size());
                        Toast.makeText(dialogActivity, "Actividad eliminada", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                });
            }

            dialog.show();
            configurarVentanaTransparente(dialog);

        } catch (Exception e) {
            Log.e(TAG, "Error en mostrarDialogoEliminar", e);
        }
    }

    /**
     * Configura el fondo del diálogo para que sea transparente y permita esquinas redondeadas.
     */
    private void configurarVentanaTransparente(Dialog dialog) {
        Window w = dialog.getWindow();
        if (w != null) {
            w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public int getItemCount() {
        return actividades != null ? actividades.size() : 0;
    }

    /**
     * Clase interna que mantiene las referencias a las vistas de cada tarjeta (item).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTipo, tvPrioridad, tvEstado, tvPorcentaje, tvFecha;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreActividad);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvPrioridad = itemView.findViewById(R.id.tvPrioridad);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvPorcentaje = itemView.findViewById(R.id.tvPorcentaje);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            progressBar = itemView.findViewById(R.id.progressBarAvance);
        }
    }
}