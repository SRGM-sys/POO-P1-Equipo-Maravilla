package com.example.menuaplication.ui.menu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Toast;
import com.example.menuaplication.R;

public class DialogoJuegos {

    public static void mostrar(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_seleccion_juegos);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Referencias a los 3 botones NUEVOS
        View btnJuego1 = dialog.findViewById(R.id.btnNuevoJuego1);
        View btnJuego2 = dialog.findViewById(R.id.btnNuevoJuego2);
        View btnJuego3 = dialog.findViewById(R.id.btnNuevoJuego3);

        // --- ESPACIO PARA EL NUEVO JUEGO 1 ---
        btnJuego1.setOnClickListener(v -> {
            // AQUÍ PONDREMOS EL INTENT DEL JUEGO 1
            Toast.makeText(context, "Creando el Juego 1...", Toast.LENGTH_SHORT).show();
            // context.startActivity(new Intent(context, NuevoJuego1Activity.class));
            // dialog.dismiss();
        });

        // --- ESPACIO PARA EL NUEVO JUEGO 2 ---
        btnJuego2.setOnClickListener(v -> {
            Toast.makeText(context, "Creando el Juego 2...", Toast.LENGTH_SHORT).show();
        });

        // --- ESPACIO PARA EL NUEVO JUEGO 3 ---
        btnJuego3.setOnClickListener(v -> {
            Toast.makeText(context, "Creando el Juego 3...", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}