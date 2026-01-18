package com.example.menuaplication.ui.puzzle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.model.puzzle.FichaPuzzle; // OJO: Importamos desde el nuevo paquete model.puzzle

import java.util.List;

public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.PuzzleViewHolder> {

    private List<FichaPuzzle> listaFichas;
    private OnFichaClickListener listener;

    public interface OnFichaClickListener {
        void onFichaClick(int posicion);
    }

    public PuzzleAdapter(List<FichaPuzzle> listaFichas, OnFichaClickListener listener) {
        this.listaFichas = listaFichas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PuzzleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ficha_puzzle, parent, false);
        return new PuzzleViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return listaFichas.size();
    }

    public void actualizarDatos(List<FichaPuzzle> nuevasFichas) {
        this.listaFichas = nuevasFichas;
        notifyDataSetChanged();
    }

    static class PuzzleViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumero;

        public PuzzleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumero = itemView.findViewById(R.id.tv_numero_ficha);
        }
    }
}