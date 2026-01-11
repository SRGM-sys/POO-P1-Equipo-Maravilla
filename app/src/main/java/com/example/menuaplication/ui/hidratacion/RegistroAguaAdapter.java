package com.example.menuaplication.ui.hidratacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.menuaplication.R;
import com.example.menuaplication.model.hidratacion.RegistroAgua;
import java.util.List;

public class RegistroAguaAdapter extends RecyclerView.Adapter<RegistroAguaAdapter.ViewHolder> {

    private List<RegistroAgua> listaRegistros;

    public RegistroAguaAdapter(List<RegistroAgua> listaRegistros) {
        this.listaRegistros = listaRegistros;
    }

    // Método para actualizar la lista cuando cambiamos de fecha
    public void actualizarLista(List<RegistroAgua> nuevaLista) {
        this.listaRegistros = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_registro_agua, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroAgua registro = listaRegistros.get(position);
        holder.tvCantidad.setText(registro.getCantidadMl() + " ml");
        holder.tvHora.setText(registro.getHora());
    }

    @Override
    public int getItemCount() {
        return listaRegistros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCantidad, tvHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCantidad = itemView.findViewById(R.id.tvCantidadItem);
            tvHora = itemView.findViewById(R.id.tvHoraItem);
        }
    }
}