package com.example.menuaplication.data;

import android.content.Context;
import com.example.menuaplication.model.sostenibilidad.RegistroSostenibilidad;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de la persistencia de datos relacionados con las actividades de sostenibilidad.
 * Implementa el patrón Singleton para garantizar un punto de acceso único a los datos
 * de eco-puntos en toda la aplicación. Gestiona el almacenamiento local mediante la
 * serialización de objetos en la memoria interna del dispositivo.
 *
 * Esta clase permite:
 * - Almacenar una lista histórica de registros de sostenibilidad.
 * - Recuperar registros basados en fechas específicas.
 * - Persistir de forma segura los cambios en el sistema de archivos de Android.
 *
 * @author erwxn
 * @version 1.0
 */
public class RepositorioSostenibilidad {

    /** Nombre del archivo binario donde se guardan los datos serializados. */
    private static final String FILE_NAME = "sostenibilidad_data.ser";

    /** Instancia única de la clase para el cumplimiento del patrón Singleton. */
    private static RepositorioSostenibilidad instance;

    /** Lista en memoria que contiene todos los registros de sostenibilidad. */
    private List<RegistroSostenibilidad> registros;

    /** Contexto de la aplicación necesario para la manipulación de archivos internos. */
    private Context context;

    /**
     * Constructor privado para restringir la instanciación externa.
     * Inicializa la estructura de datos y procede a cargar la información previa
     * desde el almacenamiento local.
     * * @param context El contexto de la aplicación.
     */
    private RepositorioSostenibilidad(Context context) {
        this.context = context;
        this.registros = new ArrayList<>();
        cargarDatos(); // Cargar datos al iniciar
    }

    /**
     * Proporciona acceso a la instancia única del repositorio.
     * Implementa inicialización perezosa (Lazy Initialization).
     *
     * @param context El contexto desde el cual se solicita la instancia.
     * @return La instancia única de {@link RepositorioSostenibilidad}.
     */
    public static RepositorioSostenibilidad getInstance(Context context) {
        if (instance == null) {
            instance = new RepositorioSostenibilidad(context);
        }
        return instance;
    }

    /**
     * Busca y recupera un registro de sostenibilidad para una fecha determinada.
     * * @param fecha Objeto {@link LocalDate} que representa el día a consultar.
     * @return El {@link RegistroSostenibilidad} correspondiente a la fecha;
     * nulo si no existe registro para ese día.
     */
    public RegistroSostenibilidad obtenerRegistro(LocalDate fecha) {
        for (RegistroSostenibilidad r : registros) {
            if (r.getFecha().isEqual(fecha)) return r;
        }
        return null;
    }

    /**
     * Guarda un nuevo registro o actualiza uno existente para la misma fecha.
     * Si ya existe un registro en la fecha indicada, lo reemplaza para evitar duplicados.
     * Los cambios se persisten inmediatamente en el archivo local.
     * * @param nuevo El objeto {@link RegistroSostenibilidad} con los datos a guardar.
     */
    public void guardarRegistro(RegistroSostenibilidad nuevo) {
        RegistroSostenibilidad existente = obtenerRegistro(nuevo.getFecha());
        if (existente != null) registros.remove(existente);

        registros.add(nuevo);
        guardarEnArchivo();
    }

    // --- MANEJO DE ARCHIVOS ---

    /**
     * Serializa la lista de registros y la guarda en el almacenamiento interno.
     * Utiliza {@link ObjectOutputStream} para convertir los objetos en una corriente de bytes.
     */
    private void guardarEnArchivo() {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(registros);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga los registros desde el archivo interno a la memoria RAM.
     * En caso de que el archivo no exista o ocurra un error de lectura,
     * se inicializa una lista vacía para asegurar la estabilidad de la aplicación.
     */
    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            registros = (ArrayList<RegistroSostenibilidad>) ois.readObject();
        } catch (Exception e) {
            // Si hay error (archivo no existe o versión incompatible), empezamos con lista limpia
            registros = new ArrayList<>();
        }
    }
}