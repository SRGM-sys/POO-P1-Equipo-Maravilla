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

public class DialogoJuegos {

    public static void mostrar(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_seleccion_juegos);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Referencias a los botones
        View btnJuego1 = dialog.findViewById(R.id.btnNuevoJuego1);
        View btnJuego2 = dialog.findViewById(R.id.btnNuevoJuego2);
        View btnJuego3 = dialog.findViewById(R.id.btnNuevoJuego3);

        // --- JUEGO 1 ---
        btnJuego1.setOnClickListener(v -> {
            Intent intent = new Intent(context, BuscaminasActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });

        // --- JUEGO 2: PUZZLE DESLIZANTE ---
        btnJuego2.setOnClickListener(v -> {

        });

        // --- JUEGO 3 ---
        btnJuego3.setOnClickListener(v -> {
            Intent intent = new Intent(context, PuzzleDeslizanteActivity.class);
            context.startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }
}