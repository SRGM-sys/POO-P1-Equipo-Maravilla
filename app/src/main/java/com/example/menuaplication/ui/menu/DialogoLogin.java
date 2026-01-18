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

/**
 * Clase encargada de gestionar el diálogo de inicio de sesión de la aplicación.
 * <p>
 * Muestra un cuadro de diálogo personalizado (Alert Dialog) que solicita un nombre de usuario
 * y una contraseña específica. Gestiona la validación de credenciales y utiliza una interfaz
 * de callback para notificar a la actividad principal cuando el inicio de sesión es exitoso.
 * </p>
 *
 * @author SRGM
 * @version 1.0
 */
public class DialogoLogin {

    /**
     * Interfaz de comunicación (Callback) para notificar el resultado del login.
     * <p>
     * Esta interfaz debe ser implementada por la Activity o fragmento que invoca el diálogo
     * para recibir el nombre del usuario una vez autenticado correctamente.
     * </p>
     */
    public interface LoginListener {
        /**
         * Se invoca cuando el usuario ha ingresado las credenciales correctas.
         *
         * @param usuario El nombre del usuario ingresado en el campo de texto.
         */
        void onSesionIniciada(String usuario);
    }

    /**
     * Construye y muestra el diálogo de inicio de sesión en pantalla.
     * <p>
     * Infla el diseño {@code dialog_login}, configura el fondo transparente y asigna
     * la lógica de validación al botón de entrar. La contraseña requerida está
     * codificada ("Maravilloso67"). Si la validación es correcta, se guarda la sesión
     * mediante {@link GestorSesion} y se dispara el listener.
     * </p>
     *
     * @param context  El contexto necesario para crear el {@link AlertDialog} (generalmente {@code this} desde una Activity).
     * @param listener La instancia de {@link LoginListener} que recibirá la notificación de éxito.
     */
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
            if (password.equals("Lumen")) {
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