package com.example.menuaplication.ui.juego;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.menuaplication.R;
import com.example.menuaplication.model.juego.TarjetaMemoria;
import java.util.List;

/**
 * Adaptador personalizado para gestionar la visualización de las cartas en el juego de memoria.
 * Se encarga de inflar el diseño de cada carta y actualizar su estado visual (volteada, encontrada u oculta)
 * dentro del RecyclerView.
 *
 * @author TheMatthias
 */

public class AdaptadorMemoria extends RecyclerView.Adapter<AdaptadorMemoria.ViewHolder> {

    private final List<TarjetaMemoria> tarjetas;
    private final OnCartaClickListener listener;


    /**
     * Interfaz para gestionar los eventos de clic en las cartas.
     * Permite la comunicación entre el adaptador y la Activity/Fragment que contiene la lógica del juego.
     */
    public interface OnCartaClickListener {
        /**
         * Se invoca cuando el usuario hace clic en una carta.
         *
         * @param tarjeta  El objeto TarjetaMemoria que fue seleccionado.
         * @param posicion La posición del ítem en el adaptador.
         */
        void onCartaClick(TarjetaMemoria tarjeta, int posicion);
    }


    /**
     * Constructor del adaptador.
     *
     * @param tarjetas Lista de objetos TarjetaMemoria que se mostrarán en la grilla.
     * @param listener Listener para manejar la interacción del usuario.
     */
    public AdaptadorMemoria(List<TarjetaMemoria> tarjetas, OnCartaClickListener listener) {
        this.tarjetas = tarjetas;
        this.listener = listener;
    }



    /**
     * Crea una nueva instancia de ViewHolder inflando el layout XML correspondiente.
     *
     * @param parent   El ViewGroup al que se añadirá la nueva vista.
     * @param viewType El tipo de vista (no utilizado en este caso ya que todas son iguales).
     * @return Una nueva instancia de ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarjeta_memoria, parent, false);
        return new ViewHolder(view);
    }


    /**
     * Vincula los datos de una TarjetaMemoria con las vistas del ViewHolder.
     * Gestiona la lógica visual para mostrar el reverso o el anverso de la carta según su estado.
     *
     * @param holder   El ViewHolder que debe ser actualizado.
     * @param position La posición del ítem dentro del conjunto de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TarjetaMemoria tarjeta = tarjetas.get(position);

        if (tarjeta.isVolteada() || tarjeta.isEncontrada()) {

            holder.ivContenido.setImageResource(tarjeta.getImagenResId());
            holder.ivContenido.clearColorFilter();
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.itemView.setClickable(false);

        } else {

            holder.ivContenido.setImageResource(R.drawable.ic_lumen);
            holder.ivContenido.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.game_accent));
            holder.itemView.setClickable(true);
        }

        holder.itemView.setOnClickListener(v -> listener.onCartaClick(tarjeta, position));
    }


    /**
     * Devuelve el número total de elementos en el conjunto de datos.
     *
     * @return El tamaño de la lista de tarjetas.
     */
    @Override
    public int getItemCount() {
        return tarjetas.size();
    }


    /**
     * ViewHolder que contiene las referencias a las vistas de cada ítem de carta.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivContenido;
        CardView card;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView La vista raíz del ítem.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asegúrate que estos IDs existan en 'item_tarjeta_memoria.xml'
            ivContenido = itemView.findViewById(R.id.iv_contenido_tarjeta);
            card = itemView.findViewById(R.id.card_tarjeta);
        }
    }
}