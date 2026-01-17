package com.example.menuaplication.data;

import android.content.Context;
import com.example.menuaplication.model.hidratacion.RegistroAgua;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase encargada de la persistencia de datos relacionados con el control de hidratación.
 * Implementa el patrón Singleton para asegurar una única instancia de acceso a los datos
 * en toda la aplicación. Gestiona el almacenamiento local mediante serialización de objetos
 * en un archivo interno.
 *
 * Almacena dos tipos principales de información:
 * - El historial de consumo de agua organizado por fechas.
 * - Las metas diarias de consumo personalizadas por fecha.
 *
 * @author SRGM
 * @version 1.0
 */
public class RepositorioHidratacion {

    /** Instancia única de la clase (Singleton). */
    private static RepositorioHidratacion instance;

    /** Contexto de la aplicación necesario para acceder al sistema de archivos. */
    private Context context;

    /** Nombre del archivo donde se serializan los datos. */
    private static final String NOMBRE_ARCHIVO = "hidratacion_data.ser";

    /**
     * Mapa que almacena el historial de registros.
     * Clave: Fecha (String). Valor: Lista de objetos {@link RegistroAgua}.
     */
    private Map<String, List<RegistroAgua>> historialAgua;

    /**
     * Mapa que almacena las metas diarias personalizadas.
     * Clave: Fecha (String). Valor: Meta en ml (Integer).
     */
    private Map<String, Integer> metasDiarias;

    /**
     * Constructor privado para prevenir instanciación directa.
     * Inicializa las estructuras de datos y carga la información del archivo si existe.
     *
     * @param context El contexto de la aplicación.
     */
    private RepositorioHidratacion(Context context) {
        this.context = context.getApplicationContext();
        this.historialAgua = new HashMap<>();
        this.metasDiarias = new HashMap<>();
        cargarDesdeArchivo();
    }

    /**
     * Obtiene la instancia única del repositorio.
     * Si la instancia no existe, la crea. Si ya existe, devuelve la actual.
     *
     * @param context El contexto desde donde se invoca (se usará getApplicationContext()).
     * @return La instancia única de {@link RepositorioHidratacion}.
     */
    public static RepositorioHidratacion getInstance(Context context) {
        if (instance == null) {
            instance = new RepositorioHidratacion(context);
        }
        return instance;
    }

    // --- MÉTODOS PÚBLICOS (API) ---

    /**
     * Obtiene la lista de registros de agua para una fecha específica.
     *
     * @param fecha La fecha en formato String (ej. "19/01/2026").
     * @return Una lista de {@link RegistroAgua}. Si no hay registros, devuelve una lista vacía.
     */
    public List<RegistroAgua> getRegistros(String fecha) {
        if (historialAgua.containsKey(fecha)) {
            return historialAgua.get(fecha);
        }
        return new ArrayList<>();
    }

    /**
     * Agrega un nuevo registro de consumo de agua a una fecha y guarda los cambios.
     *
     * @param fecha    La fecha correspondiente al registro.
     * @param registro El objeto {@link RegistroAgua} con los detalles del consumo.
     */
    public void agregarRegistro(String fecha, RegistroAgua registro) {
        if (!historialAgua.containsKey(fecha)) {
            historialAgua.put(fecha, new ArrayList<>());
        }
        historialAgua.get(fecha).add(registro);
        guardarEnArchivo(); // ¡Guardar automáticamente!
    }

    /**
     * Obtiene la meta de consumo diario para una fecha específica.
     *
     * @param fecha La fecha consultada.
     * @return La meta en mililitros. Si no se ha establecido una meta específica,
     * devuelve el valor por defecto de 2000 ml.
     */
    public int getMeta(String fecha) {
        if (metasDiarias.containsKey(fecha)) {
            return metasDiarias.get(fecha);
        }
        return 2000; // Valor por defecto
    }

    /**
     * Establece y guarda una meta personalizada para una fecha específica.
     *
     * @param fecha La fecha para la cual se establece la meta.
     * @param meta  La cantidad meta en mililitros.
     */
    public void setMeta(String fecha, int meta) {
        metasDiarias.put(fecha, meta);
        guardarEnArchivo(); // ¡Guardar automáticamente!
    }

    // --- MÉTODOS DE ARCHIVOS (MAGIA OSCURA) ---

    /**
     * Guarda el estado actual de los mapas de datos en el archivo local.
     * Utiliza {@link ObjectOutputStream} para serializar los objetos.
     */
    private void guardarEnArchivo() {
        File archivo = new File(context.getExternalFilesDir(null), NOMBRE_ARCHIVO);
        try (FileOutputStream fos = new FileOutputStream(archivo);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // Guardamos los dos mapas en orden
            oos.writeObject(historialAgua);
            oos.writeObject(metasDiarias);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga los datos desde el archivo local a la memoria.
     * Intenta leer los mapas en el mismo orden en que fueron guardados.
     * Si el archivo no existe o ocurre un error de lectura/clase no encontrada,
     * inicializa los mapas vacíos para evitar errores de ejecución (NullPointerException).
     */
    @SuppressWarnings("unchecked")
    private void cargarDesdeArchivo() {
        File archivo = new File(context.getExternalFilesDir(null), NOMBRE_ARCHIVO);
        if (archivo.exists()) {
            try (FileInputStream fis = new FileInputStream(archivo);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                // Leemos en el MISMO orden que guardamos
                historialAgua = (Map<String, List<RegistroAgua>>) ois.readObject();
                metasDiarias = (Map<String, Integer>) ois.readObject();
            } catch (Exception e) {
                // Si falla, iniciamos vacíos
                historialAgua = new HashMap<>();
                metasDiarias = new HashMap<>();
            }
        }
    }
}