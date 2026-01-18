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

/**
 * Adaptador personalizado para el RecyclerView que muestra el tablero del Buscaminas.
 * <p>
 * Esta clase es responsable de vincular los datos de cada {@link Celda} con su representación
 * visual en la pantalla. Gestiona los cambios de estado visual (cubierto/descubierto),
 * la visualización de iconos temáticos (calabazas, murciélagos) y la asignación de colores
 * específicos para los números de proximidad.
 * </p>
 *
 * @author José Paladines
 * @version 1.0
 */
public class BuscaminasAdapter extends RecyclerView.Adapter<BuscaminasAdapter.ViewHolder> {

    private List<Celda> celdas;
    private OnItemClickListener listener;
    private OnItemLongClickListener longListener;

    /**
     * Interfaz para gestionar los clics simples en las celdas (acción de descubrir).
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * Interfaz para gestionar los clics largos en las celdas (acción de marcar/desmarcar bandera).
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    /**
     * Constructor del adaptador.
     *
     * @param celdas       Lista de objetos {@link Celda} que representan el estado del tablero.
     * @param listener     Listener para manejar los eventos de clic corto (descubrir).
     * @param longListener Listener para manejar los eventos de clic largo (marcar).
     */
    public BuscaminasAdapter(List<Celda> celdas, OnItemClickListener listener, OnItemLongClickListener longListener) {
        this.celdas = celdas;
        this.listener = listener;
        this.longListener = longListener;
    }

    /**
     * Crea una nueva vista (ViewHolder) para un elemento de la cuadrícula.
     *
     * @param parent   El ViewGroup padre al que se añadirá la nueva vista.
     * @param viewType El tipo de vista de la nueva vista.
     * @return Un nuevo {@link ViewHolder} que contiene la vista de la celda.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Vincula los datos de una {@link Celda} específica con su vista correspondiente.
     * <p>
     * Aquí se aplica la lógica visual del juego:
     * <ul>
     * <li>Si está <b>revelada</b>: Muestra el fondo destapado. Si es bomba muestra una calabaza ("🎃"),
     * si es número muestra la cantidad con su color temático.</li>
     * <li>Si está <b>cubierta</b>: Muestra el fondo cubierto. Si está marcada muestra un murciélago ("🦇").</li>
     * </ul>
     * </p>
     *
     * @param holder   El ViewHolder que debe actualizarse.
     * @param position La posición del elemento dentro del conjunto de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Celda celda = celdas.get(position);
        TextView tv = (TextView) holder.itemView;

        // Configuración base de texto y estilo
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setTextSize(22); // Tamaño aumentado para mejor legibilidad
        tv.setTypeface(null, android.graphics.Typeface.BOLD); // Negrita para resaltar

        if (celda.isEstaRevelada()) {
            // --- ESTADO DESTAPADO ---
            // Usamos el recurso drawable para celdas destapadas (plano/hundido)
            tv.setBackgroundResource(R.drawable.bg_celda_destapada);

            if (celda.isEsBomba()) {
                tv.setText("🎃"); // Calabaza de Muerte (Icono de bomba)
            } else {
                int minas = celda.getBombasAlrededor();
                if (minas == 0) {
                    tv.setText(""); // Celda vacía sin peligros cercanos
                } else {
                    tv.setText(String.valueOf(minas));
                    // Asignamos color temático según el número de minas
                    tv.setTextColor(obtenerColorTematico(minas));
                }
            }
        } else {
            // --- ESTADO CUBIERTO ---
            // Usamos el recurso drawable para celdas cubiertas (botón normal)
            tv.setBackgroundResource(R.drawable.bg_celda_cubierta);

            if (celda.isEstaMarcada()) {
                tv.setText("🦇"); // Bandera Murciélago (Marca de usuario)
                tv.setTextColor(android.graphics.Color.BLACK);
            } else {
                tv.setText(""); // Celda oculta estándar
            }
        }

        // Asignación de Listeners
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
        holder.itemView.setOnLongClickListener(v -> {
            longListener.onItemLongClick(position);
            return true; // Indica que el evento fue consumido
        });
    }

    /**
     * Obtiene el color correspondiente para el número de minas adyacentes.
     * <p>
     * Utiliza una paleta de colores oscuros y temáticos (Halloween) para asegurar
     * la legibilidad sobre el fondo claro de la celda destapada.
     * </p>
     *
     * @param numero El número de minas alrededor (1-8).
     * @return El entero del color (ARGB) correspondiente.
     */
    private int obtenerColorTematico(int numero) {
        switch (numero) {
            case 1:
                return android.graphics.Color.parseColor("#673AB7"); // Morado Bruja
            case 2:
                return android.graphics.Color.parseColor("#E65100"); // Naranja Calabaza Oscuro
            case 3:
                return android.graphics.Color.parseColor("#2E7D32"); // Verde Zombie
            case 4:
                return android.graphics.Color.parseColor("#B71C1C"); // Rojo Sangre
            case 5:
                return android.graphics.Color.parseColor("#006064"); // Cian Oscuro Fantasmal
            default:
                return android.graphics.Color.BLACK; // Negro para el resto
        }
    }

    /**
     * Devuelve el número total de celdas en el tablero.
     *
     * @return El tamaño de la lista de celdas.
     */
    @Override
    public int getItemCount() { return celdas.size(); }

    /**
     * Clase interna ViewHolder que mantiene las referencias a las vistas de cada celda.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) { super(itemView); }
    }
}