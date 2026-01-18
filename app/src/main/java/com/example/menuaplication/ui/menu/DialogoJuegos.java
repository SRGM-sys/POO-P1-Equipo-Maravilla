package com.example.menuaplication.ui.menu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Toast;
import com.example.menuaplication.R;
import com.example.menuaplication.ui.buscaminas.BuscaminasActivity;
import com.example.menuaplication.ui.puzzle.PuzzleDeslizanteActivity;
import com.example.menuaplication.ui.raya.TresEnRayaActivity;

/**
 * Clase de utilidad encargada de gestionar la navegación hacia los minijuegos.
 * <p>
 * Esta clase muestra un cuadro de diálogo modal (popup) que permite al usuario
 * seleccionar entre las diferentes opciones de entretenimiento disponibles en la aplicación:
 * Buscaminas, Tres en Raya y Puzzle Deslizante.
 * </p>
 *
 * @author SRGM
 * @version 1.0
 */
public class DialogoJuegos {

    /**
     * Muestra el diálogo de selección de juegos en la pantalla.
     * <p>
     * Configura la apariencia del diálogo (fondo transparente) y asigna los listeners
     * a los botones correspondientes para iniciar las actividades de cada juego.
     * </p>
     *
     * @param context El contexto desde donde se invoca el diálogo (generalmente la Activity actual),
     * necesario para crear el Dialog e iniciar los Intents.
     */
    public static void mostrar(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_seleccion_juegos);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Referencias a los botones del layout
        View btnJuego1 = dialog.findViewById(R.id.btnNuevoJuego1);
        View btnJuego2 = dialog.findViewById(R.id.btnNuevoJuego2);
        View btnJuego3 = dialog.findViewById(R.id.btnNuevoJuego3);

        // --- JUEGO 1: BUSCAMINAS ---
        btnJuego1.setOnClickListener(v -> {
            Intent intent = new Intent(context, BuscaminasActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });

        // --- JUEGO 2: TRES EN RAYA ---
        btnJuego2.setOnClickListener(v -> {
            Intent intent = new Intent(context, TresEnRayaActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });

        // --- JUEGO 3: PUZZLE DESLIZANTE ---
        btnJuego3.setOnClickListener(v -> {
            Intent intent = new Intent(context, PuzzleDeslizanteActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }
}