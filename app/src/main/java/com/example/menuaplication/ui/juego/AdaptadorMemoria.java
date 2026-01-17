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

    private List<TarjetaMemoria> tarjetas;
    private final OnCartaClickListener listener;

    public interface OnCartaClickListener {
        void onCartaClick(int posicion);
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

        if (tarjeta.isEstaDescubierta() || tarjeta.isEstaEmparejada()) {
            // Mostrar imagen real
            holder.ivContenido.setImageResource(tarjeta.getImagenRecurso());
            holder.ivContenido.clearColorFilter(); // Quitar tinte blanco
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else {
            // Mostrar reverso (oculto)
            holder.ivContenido.setImageResource(R.drawable.ic_lumen); // Tu logo como reverso
            holder.ivContenido.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.game_accent));
        }

        holder.itemView.setOnClickListener(v -> listener.onCartaClick(position));
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
            ivContenido = itemView.findViewById(R.id.iv_contenido_tarjeta);
            card = itemView.findViewById(R.id.card_tarjeta);
        }
    }
}