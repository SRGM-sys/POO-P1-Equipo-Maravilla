package com.example.menuaplication.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase encargada de gestionar la sesión del usuario en la aplicación.
 * <p>
 * Utiliza {@link SharedPreferences} para almacenar de forma persistente
 * el nombre del usuario y la URI de su foto de perfil, permitiendo mantener
 * la sesión activa y la personalización entre cierres de la aplicación.
 * </p>
 *
 * @author SRGM
 * @version 1.1
 */
public class GestorSesion {

    /** Nombre del archivo de preferencias compartidas. */
    private static final String PREF_NAME = "MisPreferenciasLumen";

    /** Clave para almacenar y recuperar el nombre del usuario. */
    private static final String KEY_USER = "nombre_usuario";

    /** Clave para almacenar y recuperar la URI de la foto de perfil. */
    private static final String KEY_PHOTO = "foto_usuario";

    private SharedPreferences prefs;

    /**
     * Constructor de la clase GestorSesion.
     * Inicializa el acceso al archivo de preferencias en modo privado.
     *
     * @param context El contexto de la aplicación, necesario para acceder a SharedPreferences.
     */
    public GestorSesion(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Guarda el nombre del usuario en las preferencias para iniciar una sesión.
     * <p>
     * Este método escribe de forma asíncrona usando {@code apply()}.
     * </p>
     *
     * @param nombre El nombre del usuario que se desea guardar.
     */
    public void guardarUsuario(String nombre) {
        prefs.edit().putString(KEY_USER, nombre).apply();
    }

    /**
     * Guarda la URI de la foto de perfil del usuario en las preferencias.
     * <p>
     * Almacena la ruta de la imagen (convertida a String) para que pueda ser
     * recuperada y mostrada en futuros inicios de sesión.
     * </p>
     *
     * @param uriFoto La cadena de texto que representa la URI de la imagen seleccionada.
     */
    public void guardarFoto(String uriFoto) {
        prefs.edit().putString(KEY_PHOTO, uriFoto).apply();
    }

    /**
     * Obtiene la URI de la foto de perfil almacenada actualmente.
     *
     * @return La URI de la foto como String, o {@code null} si no se ha guardado ninguna foto.
     */
    public String getFoto() {
        return prefs.getString(KEY_PHOTO, null);
    }

    /**
     * Cierra la sesión actual del usuario.
     * <p>
     * Elimina todos los datos almacenados en el archivo de preferencias de esta sesión
     * (incluyendo usuario y foto).
     * </p>
     */
    public void cerrarSesion() {
        prefs.edit().clear().apply();
    }

    /**
     * Obtiene el nombre del usuario almacenado actualmente.
     *
     * @return El nombre del usuario si existe una sesión, o {@code null} si no hay sesión activa.
     */
    public String getUsuario() {
        return prefs.getString(KEY_USER, null);
    }

    /**
     * Verifica si existe una sesión activa.
     *
     * @return {@code true} si hay un usuario guardado (no es nulo),
     * {@code false} en caso contrario.
     */
    public boolean haySesionActiva() {
        return getUsuario() != null;
    }
}