package com.example.menuaplication.ui.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.menuaplication.R;
import com.example.menuaplication.data.GestorSesion;

public class DialogoLogin {

    // Interfaz para comunicarse con el MainActivity (Callback)
    public interface LoginListener {
        void onSesionIniciada(String usuario);
    }

    public static void mostrar(Context context, LoginListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_login, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText etUser = view.findViewById(R.id.etUsuarioLogin);
        EditText etPass = view.findViewById(R.id.etPasswordLogin);
        Button btnEntrar = view.findViewById(R.id.btnConfirmarLogin);

        btnEntrar.setOnClickListener(v -> {
            String usuario = etUser.getText().toString().trim();
            String password = etPass.getText().toString().trim();

            if (usuario.isEmpty()) {
                etUser.setError("Nombre requerido");
                return;
            }

            // --- AQUÍ VALIDAMOS LA CONTRASEÑA ---
            if (password.equals("Maravilloso67")) {
                // 1. Guardar sesión
                new GestorSesion(context).guardarUsuario(usuario);

                // 2. Avisar al Main
                if (listener != null) listener.onSesionIniciada(usuario);

                Toast.makeText(context, "¡Bienvenido " + usuario + "!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                etPass.setError("Contraseña incorrecta");
            }
        });

        dialog.show();
    }
}