package com.example.menuaplication.ui;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.model.Actividad;
import com.example.menuaplication.model.ActividadAcademica;
import com.example.menuaplication.model.Prioridad;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ActividadAdapter - versión funcional y consistente
 *
 * Requisitos:
 * - Usa Dialog (no AlertDialog.Builder) para diálogos personalizados (con excepción del menú de opciones).
 * - Forzar transparencia del Window y setLayout AFTER dialog.show().
 * - Manejo robusto de NumberFormatException.
 * - Abre confirmación inmediatamente después del dismiss() del diálogo de registro.
 *
 * IMPORTANTE: instancia este adapter desde una Activity pasando "this":
 *     ActividadAdapter adapter = new ActividadAdapter(miListaActividades, this);
 */
public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ViewHolder> {

    private List<Actividad> actividades;
    private final Activity activity; // Activity garantizada para crear Dialogs
    private static final String TAG = "ActividadAdapter";

    public ActividadAdapter(List<Actividad> actividades, Activity activity) {
        this.actividades = actividades;
        this.activity = activity;
    }

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

    @Override
    public void onBindViewHolder(@NonNull ActividadAdapter.ViewHolder holder, int position) {
        Actividad actividad = actividades.get(position);

        holder.tvNombre.setText(actividad.getNombre());

        String tipo = (actividad instanceof ActividadAcademica) ? "Académica" : "Personal";
        holder.tvTipo.setText(tipo);

        holder.progressBar.setProgress((int) actividad.getPorcentajeAvance());
        holder.tvPorcentaje.setText((int) actividad.getPorcentajeAvance() + "%");

        // Estado
        if (actividad.getPorcentajeAvance() >= 100) {
            holder.tvEstado.setText("COMPLETADA");
            holder.tvEstado.setTextColor(Color.parseColor("#4CAF50"));
        } else if (actividad.getPorcentajeAvance() > 0) {
            holder.tvEstado.setText("EN PROGRESO");
            holder.tvEstado.setTextColor(Color.parseColor("#FF9800"));
        } else {
            holder.tvEstado.setText("PENDIENTE");
            holder.tvEstado.setTextColor(Color.parseColor("#757575"));
        }

        // Prioridad
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        if (actividad.getFechaVencimiento() != null) {
            holder.tvFecha.setText("Vence: " + actividad.getFechaVencimiento().format(formatter));
        } else {
            holder.tvFecha.setText("Sin fecha");
        }

        // Click para detalle
        holder.itemView.setOnClickListener(v -> {
            Context vCtx = v.getContext();
            Intent intent = new Intent(vCtx, DetalleActividadActivity.class);
            intent.putExtra("ACTIVIDAD_EXTRA", actividad);
            vCtx.startActivity(intent);
        });

        // Long click -> opciones (usamos contexto del view para mayor seguridad)
        holder.itemView.setOnLongClickListener(v -> {
            mostrarOpciones(actividad, holder.getAdapterPosition(), v.getContext());
            return true;
        });
    }

    /**
     * Muestra un menú sencillo con dos opciones.
     * Usa viewContext (de la vista que disparó el evento) para asegurar que sea Activity.
     */
    private void mostrarOpciones(Actividad actividad, int position, Context viewContext) {
        Context ctx = (viewContext != null) ? viewContext : activity;
        if (!(ctx instanceof Activity)) {
            Log.e(TAG, "mostrarOpciones: contexto inválido");
            Toast.makeText(ctx, "Contexto inválido para mostrar opciones", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] options = {"Registrar Avance", "Eliminar Actividad"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(actividad.getNombre());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // PASAMOS el contexto del view para evitar BadTokenException
                mostrarDialogoAvance(actividad, position, ctx);
            } else {
                mostrarDialogoEliminar(actividad, position, ctx);
            }
        });
        builder.show();
    }

    // ---------------------------
    // DIALOG: Registrar Avance
    // ---------------------------
    private void mostrarDialogoAvance(Actividad actividad, int position, Context callerContext) {
        Context ctx = (callerContext instanceof Activity) ? callerContext : activity;
        if (!(ctx instanceof Activity)) {
            Log.e(TAG, "mostrarDialogoAvance: contexto inválido");
            Toast.makeText(ctx, "No se puede mostrar diálogo (contexto inválido).", Toast.LENGTH_LONG).show();
            return;
        }
        Activity dialogActivity = (Activity) ctx;

        try {
            final Dialog dialog = new Dialog(dialogActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);

            View view = LayoutInflater.from(dialogActivity).inflate(R.layout.dialog_registrar_avance, null);
            dialog.setContentView(view);

            TextView tvNombre = view.findViewById(R.id.tvDialogActividadNombre);
            TextView tvActual = view.findViewById(R.id.tvDialogAvanceActual);
            EditText etNuevo = view.findViewById(R.id.etDialogNuevoAvance);
            Button btnCancel = view.findViewById(R.id.btnCancelarAvance);
            Button btnGuardar = view.findViewById(R.id.btnGuardarAvance);

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
                    input = input.replace(",", ".");
                    try {
                        double nuevoAvance = Double.parseDouble(input);
                        if (nuevoAvance < 0 || nuevoAvance > 100) {
                            if (etNuevo != null) etNuevo.setError("Debe ser entre 0 y 100");
                            return;
                        }

                        // Cerrar dialog y abrir confirmación
                        dialog.dismiss();
                        mostrarConfirmacionAvance(actividad, position, nuevoAvance, callerContext);

                    } catch (NumberFormatException nfe) {
                        if (etNuevo != null) {
                            etNuevo.setError("Número inválido");
                            etNuevo.requestFocus();
                        }
                        Log.e(TAG, "Error parsing double: " + input, nfe);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error inesperado en guardar avance: " + ex.getMessage(), ex);
                        Toast.makeText(dialogActivity, "Error inesperado", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.w(TAG, "btnGuardarAvance no encontrado en dialog_registrar_avance.xml");
            }

            // Mostrar y luego ajustar window para que el fondo transparente y layout se apliquen correctamente
            try {
                dialog.show();
                Window w = dialog.getWindow();
                if (w != null) {
                    w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            } catch (WindowManager.BadTokenException bt) {
                Log.e(TAG, "BadTokenException mostrarDialogoAvance", bt);
                Toast.makeText(dialogActivity, "No se pudo mostrar el diálogo (token inválido).", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error al abrir dialogo registrar avance", e);
            Toast.makeText(activity, "Error al abrir diálogo", Toast.LENGTH_SHORT).show();
        }
    }

    // ---------------------------
    // DIALOG: Confirmación Avance
    // ---------------------------
    private void mostrarConfirmacionAvance(Actividad actividad, int position, double nuevoAvance, Context callerContext) {
        Context ctx = (callerContext instanceof Activity) ? callerContext : activity;
        if (!(ctx instanceof Activity)) {
            Log.e(TAG, "mostrarConfirmacionAvance: contexto inválido");
            Toast.makeText(ctx, "No se puede mostrar confirmación (contexto inválido).", Toast.LENGTH_LONG).show();
            return;
        }
        Activity dialogActivity = (Activity) ctx;

        try {
            final Dialog dialog = new Dialog(dialogActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View view = LayoutInflater.from(dialogActivity).inflate(R.layout.dialog_confirmar_avance, null);
            dialog.setContentView(view);

            TextView tvMensaje = view.findViewById(R.id.tvMensajeConfirmacion);
            Button btnNo = view.findViewById(R.id.btnNoConfirmar);
            Button btnSi = view.findViewById(R.id.btnSiConfirmar);

            if (tvMensaje != null) {
                tvMensaje.setText("¿Estás seguro que quieres que la actividad " + actividad.getNombre() +
                        " tenga un progreso del " + (int) nuevoAvance + "%?");
            }

            if (btnNo != null) btnNo.setOnClickListener(v -> dialog.dismiss());

            if (btnSi != null) {
                btnSi.setOnClickListener(v -> {
                    try {
                        actividad.setPorcentajeAvance(nuevoAvance);
                        if (position >= 0 && position < actividades.size()) {
                            notifyItemChanged(position);
                        } else {
                            notifyDataSetChanged();
                        }
                        Toast.makeText(dialogActivity, "Progreso actualizado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } catch (Exception ex) {
                        Log.e(TAG, "Error al confirmar avance", ex);
                        Toast.makeText(dialogActivity, "Error al confirmar avance", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.w(TAG, "btnSiConfirmar no encontrado en dialog_confirmar_avance.xml");
            }

            try {
                dialog.show();
                Window w = dialog.getWindow();
                if (w != null) {
                    w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            } catch (WindowManager.BadTokenException bt) {
                Log.e(TAG, "BadTokenException mostrarConfirmacionAvance", bt);
                Toast.makeText(dialogActivity, "No se pudo mostrar confirmación (token inválido).", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error en mostrarConfirmacionAvance", e);
            Toast.makeText(activity, "Error confirmación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ---------------------------
    // DIALOG: Eliminar Actividad
    // ---------------------------
    private void mostrarDialogoEliminar(Actividad actividad, int position, Context callerContext) {
        Context ctx = (callerContext instanceof Activity) ? callerContext : activity;
        if (!(ctx instanceof Activity)) {
            Log.e(TAG, "mostrarDialogoEliminar: contexto inválido");
            Toast.makeText(ctx, "No se puede mostrar diálogo (contexto inválido).", Toast.LENGTH_LONG).show();
            return;
        }
        Activity dialogActivity = (Activity) ctx;

        try {
            final Dialog dialog = new Dialog(dialogActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            View view = LayoutInflater.from(dialogActivity).inflate(R.layout.dialog_eliminar_actividad, null);
            dialog.setContentView(view);

            TextView tvMensaje = view.findViewById(R.id.tvMensajeEliminar);
            Button btnCancelar = view.findViewById(R.id.btnCancelarEliminar);
            Button btnEliminar = view.findViewById(R.id.btnAceptarEliminar);

            if (tvMensaje != null) tvMensaje.setText("¿Estás seguro de eliminar la actividad " + actividad.getNombre() + "?");

            if (btnCancelar != null) btnCancelar.setOnClickListener(v -> dialog.dismiss());

            if (btnEliminar != null) {
                btnEliminar.setOnClickListener(v -> {
                    try {
                        if (position >= 0 && position < actividades.size()) {
                            actividades.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, actividades.size());
                            Toast.makeText(dialogActivity, "Actividad eliminada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(dialogActivity, "Posición inválida", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    } catch (Exception ex) {
                        Log.e(TAG, "Error al eliminar actividad", ex);
                        Toast.makeText(dialogActivity, "Error al eliminar actividad", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.w(TAG, "btnAceptarEliminar no encontrado en dialog_eliminar_actividad.xml");
            }

            try {
                dialog.show();
                Window w = dialog.getWindow();
                if (w != null) {
                    w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            } catch (WindowManager.BadTokenException bt) {
                Log.e(TAG, "BadTokenException mostrarDialogoEliminar", bt);
                Toast.makeText(dialogActivity, "No se pudo mostrar diálogo eliminar (token inválido).", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error en mostrarDialogoEliminar", e);
            Toast.makeText(activity, "Error al mostrar eliminar: Verifica IDs XML", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return actividades != null ? actividades.size() : 0;
    }

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
