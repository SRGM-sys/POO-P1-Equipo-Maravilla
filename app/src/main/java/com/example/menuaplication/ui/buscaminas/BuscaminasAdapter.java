package com.example.menuaplication.ui.buscaminas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.menuaplication.R;
import com.example.menuaplication.model.buscaminas.Celda;
import java.util.List;

public class BuscaminasAdapter extends RecyclerView.Adapter<BuscaminasAdapter.ViewHolder> {

    private List<Celda> celdas;
    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

    public interface OnItemClickListener { void onItemClick(int position); }
    public interface OnItemLongClickListener { void onItemLongClick(int position); }

    public BuscaminasAdapter(List<Celda> celdas, OnItemClickListener listener, OnItemLongClickListener longListener) {
        this.celdas = celdas;
        this.listener = listener;
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Celda celda = celdas.get(position);
        TextView tv = (TextView) holder.itemView;

        tv.setBackgroundResource(R.drawable.bg_boton_opcion); // Reutiliza tus estilos
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setTextSize(18);

        if (celda.isEstaRevelada()) {
            if (celda.isEsBomba()) {
                tv.setText("🎃");
                tv.setBackgroundColor(0xFFFF4444);
            } else {
                tv.setText(celda.getBombasAlrededor() == 0 ? "🕸️" : String.valueOf(celda.getBombasAlrededor()));
                tv.setBackgroundColor(0xFFEEEEEE);
            }
        } else if (celda.isEstaMarcada()) {
            tv.setText("🦇");
        } else {
            tv.setText("❓");
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            longListener.onItemLongClick(position);
            return true;
        });
    }

    @Override
    public int getItemCount() { return celdas.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) { super(itemView); }
    }
}