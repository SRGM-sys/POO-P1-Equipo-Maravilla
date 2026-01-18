package com.example.menuaplication.data;

import android.content.Context;
import android.content.SharedPreferences;

public class GestorSesion {
    private static final String PREF_NAME = "MisPreferenciasLumen";
    private static final String KEY_USER = "nombre_usuario";
    private SharedPreferences prefs;

    public GestorSesion(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void guardarUsuario(String nombre) {
        prefs.edit().putString(KEY_USER, nombre).apply();
    }

    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }

    public String getUsuario() {
        return prefs.getString(KEY_USER, null);
    }

    public boolean haySesionActiva() {
        return getUsuario() != null;
    }
}