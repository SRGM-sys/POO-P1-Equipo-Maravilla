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

public class AdaptadorMemoria extends RecyclerView.Adapter<AdaptadorMemoria.ViewHolder> {

    private final List<TarjetaMemoria> tarjetas;
    private final OnCartaClickListener listener;

    // INTERFAZ: La actualizamos para enviar el Objeto y la Posición
    // Esto es vital para que coincida con el método 'manejarClickTarjeta' de tu Activity
    public interface OnCartaClickListener {
        void onCartaClick(TarjetaMemoria tarjeta, int posicion);
    }

    public AdaptadorMemoria(List<TarjetaMemoria> tarjetas, OnCartaClickListener listener) {
        this.tarjetas = tarjetas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tarjeta_memoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TarjetaMemoria tarjeta = tarjetas.get(position);

        // LÓGICA VISUAL:
        // Si está volteada (la acabamos de tocar) o encontrada (ya es par), mostramos la imagen real.
        if (tarjeta.isVolteada() || tarjeta.isEncontrada()) {

            // 1. Mostrar la imagen del cerebro, hoja, etc.
            holder.ivContenido.setImageResource(tarjeta.getImagenResId());
            holder.ivContenido.clearColorFilter(); // Quitamos el filtro blanco para ver colores originales

            // 2. Fondo blanco para que destaque la imagen
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));

            // IMPORTANTE: Desactivamos el click si ya está volteada/encontrada para evitar errores
            holder.itemView.setClickable(false);

        } else {
            // ESTADO OCULTO (Reverso):

            // 1. Mostramos tu logo 'Lumen' como reverso
            holder.ivContenido.setImageResource(R.drawable.ic_lumen);

            // 2. Le ponemos tinte blanco para que contraste con el fondo de color
            holder.ivContenido.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));

            // 3. Fondo del color del juego (game_accent)
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.game_accent));

            // Habilitamos el click
            holder.itemView.setClickable(true);
        }

        // Listener: Al hacer click, notificamos a la Activity qué carta fue
        holder.itemView.setOnClickListener(v -> listener.onCartaClick(tarjeta, position));
    }

    @Override
    public int getItemCount() {
        return tarjetas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivContenido;
        CardView card;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Asegúrate que estos IDs existan en 'item_tarjeta_memoria.xml'
            ivContenido = itemView.findViewById(R.id.iv_contenido_tarjeta);
            card = itemView.findViewById(R.id.card_tarjeta);
        }
    }
}