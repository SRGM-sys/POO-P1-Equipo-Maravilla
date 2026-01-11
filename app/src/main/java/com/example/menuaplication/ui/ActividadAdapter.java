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

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ViewHolder> {

    private List<Actividad> actividades;
    private final Activity activity;
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

        // Estado y Colores
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

        // Click corto -> Detalle
        holder.itemView.setOnClickListener(v -> {
            Context vCtx = v.getContext();
            Intent intent = new Intent(vCtx, DetalleActividadActivity.class);
            intent.putExtra("ACTIVIDAD_EXTRA", actividad);
            vCtx.startActivity(intent);
        });

        // Click largo -> Opciones (usando el contexto de la vista)
        holder.itemView.setOnLongClickListener(v -> {
            mostrarOpciones(actividad, holder.getAdapterPosition(), v.getContext());
            return true;
        });
    }

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

    // ---------------------------
    // DIALOG: Registrar Avance
    // ---------------------------
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
                        // Pasamos al diálogo de confirmación
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

    // ---------------------------
    // DIALOG: Confirmación Avance
    // ---------------------------
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

    // ---------------------------
    // DIALOG: Eliminar Actividad
    // ---------------------------
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