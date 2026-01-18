package com.example.menuaplication.ui.puzzle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.model.puzzle.FichaPuzzle;

import java.util.List;

/**
 * Adaptador personalizado para el RecyclerView que muestra el tablero del Puzzle Deslizante.
 * Gestiona la visualización de las fichas y detecta las interacciones del usuario.
 *
 * @author TheMatthias
 */
public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.PuzzleViewHolder> {

    private List<FichaPuzzle> listaFichas;
    private OnFichaClickListener listener;

    /**
     * Interfaz para manejar los eventos de clic en las fichas del puzzle.
     */
    public interface OnFichaClickListener {
        /**
         * Se llama cuando el usuario hace clic en una ficha.
         *
         * @param posicion La posición de la ficha en la lista del adaptador.
         */
        void onFichaClick(int posicion);
    }


    /**
     * Constructor del adaptador.
     *
     * @param listaFichas Lista inicial de objetos FichaPuzzle.
     * @param listener    Listener para gestionar los clics en los elementos.
     */
    public PuzzleAdapter(List<FichaPuzzle> listaFichas, OnFichaClickListener listener) {
        this.listaFichas = listaFichas;
        this.listener = listener;
    }


    /**
     * Crea una nueva instancia de ViewHolder inflando el diseño del item.
     *
     * @param parent   El ViewGroup padre.
     * @param viewType El tipo de vista (no utilizado en este caso).
     * @return Una nueva instancia de PuzzleViewHolder.
     */
    @NonNull
    @Override
    public PuzzleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ficha_puzzle, parent, false);
        return new PuzzleViewHolder(view);
    }


    /**
     * Vincula los datos de una FichaPuzzle con las vistas del ViewHolder.
     * Controla la visibilidad de la ficha vacía y asigna el número correspondiente.
     *
     * @param holder   El ViewHolder a actualizar.
     * @param position La posición del elemento en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull PuzzleViewHolder holder, int position) {
        FichaPuzzle ficha = listaFichas.get(position);

        if (ficha.isEsVacia()) {
            holder.itemView.setVisibility(View.INVISIBLE);
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.tvNumero.setText(String.valueOf(ficha.getNumero()));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFichaClick(holder.getAdapterPosition());
            }
        });
    }

    /**
     * Obtiene el número total de elementos en el adaptador.
     *
     * @return El tamaño de la lista de fichas.
     */
    @Override
    public int getItemCount() {
        return listaFichas.size();
    }


    /**
     * Actualiza la lista de datos del adaptador y notifica los cambios a la vista.
     *
     * @param nuevasFichas La nueva lista de objetos FichaPuzzle.
     */
    public void actualizarDatos(List<FichaPuzzle> nuevasFichas) {
        this.listaFichas = nuevasFichas;
        notifyDataSetChanged();
    }


    /**
     * ViewHolder que mantiene las referencias a las vistas de cada item del puzzle.
     */
    static class PuzzleViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumero;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView La vista raíz del item.
         */
        public PuzzleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumero = itemView.findViewById(R.id.tv_numero_ficha);
        }
    }
}