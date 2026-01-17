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

/**
 * Adaptador para el RecyclerView encargado de mostrar la lista de registros de agua.
 * Esta clase actúa como un puente entre los datos (una lista de objetos {@link RegistroAgua})
 * y la interfaz visual (el RecyclerView). Se encarga de crear las vistas para cada elemento
 * de la lista y de vincular los datos correspondientes a esas vistas.
 *
 * @author SRGM
 * @version 1.0
 */
public class RegistroAguaAdapter extends RecyclerView.Adapter<RegistroAguaAdapter.ViewHolder> {

    /**
     * Lista de registros que se mostrarán en la interfaz.
     */
    private List<RegistroAgua> listaRegistros;

    /**
     * Constructor del adaptador.
     *
     * @param listaRegistros La lista inicial de registros de agua a mostrar.
     */
    public RegistroAguaAdapter(List<RegistroAgua> listaRegistros) {
        this.listaRegistros = listaRegistros;
    }

    /**
     * Actualiza la lista de datos del adaptador y refresca la vista.
     * Este método es útil cuando se cambia la fecha seleccionada o se agrega un nuevo registro,
     * permitiendo que el RecyclerView muestre la información más reciente.
     * Llama a {@code notifyDataSetChanged()} para forzar el redibujado.
     *
     * @param nuevaLista La nueva lista de objetos {@link RegistroAgua} que reemplazará a la anterior.
     */
    public void actualizarLista(List<RegistroAgua> nuevaLista) {
        this.listaRegistros = nuevaLista;
        notifyDataSetChanged();
    }

    /**
     * Llamado cuando el RecyclerView necesita un nuevo {@link ViewHolder} del tipo dado para representar un elemento.
     * Este método infla el diseño XML {@code item_registro_agua} que define la apariencia
     * de un registro individual en la lista.
     *
     * @param parent   El ViewGroup en el que se añadirá la nueva vista después de vincularse a una posición del adaptador.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo ViewHolder que contiene la vista del elemento.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_registro_agua, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Llamado por el RecyclerView para mostrar los datos en la posición especificada.
     * Este método actualiza el contenido de {@link ViewHolder#itemView} para reflejar
     * el elemento en la posición dada. Aquí asignamos los valores de cantidad (ml) y hora
     * a los TextViews correspondientes.
     *
     * @param holder   El ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroAgua registro = listaRegistros.get(position);
        holder.tvCantidad.setText(registro.getCantidadMl() + " ml");
        holder.tvHora.setText(registro.getHora());
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos que tiene el adaptador.
     *
     * @return El tamaño de la lista de registros.
     */
    @Override
    public int getItemCount() {
        return listaRegistros.size();
    }

    /**
     * Clase interna que describe la vista de un elemento y metadatos sobre su lugar dentro del RecyclerView.
     * Mantiene referencias a los componentes visuales (TextViews) para evitar llamadas
     * repetitivas a {@code findViewById}, mejorando el rendimiento del desplazamiento.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /** TextView para mostrar la cantidad de agua en mililitros. */
        TextView tvCantidad;
        /** TextView para mostrar la hora del registro. */
        TextView tvHora;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView La vista raíz del elemento inflado (el layout del item).
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCantidad = itemView.findViewById(R.id.tvCantidadItem);
            tvHora = itemView.findViewById(R.id.tvHoraItem);
        }
    }
}