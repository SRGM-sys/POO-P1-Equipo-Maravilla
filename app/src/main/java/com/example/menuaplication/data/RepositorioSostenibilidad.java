package com.example.menuaplication.data;

import android.content.Context;
import com.example.menuaplication.model.sostenibilidad.RegistroSostenibilidad;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase encargada de la gestión y persistencia de los datos de sostenibilidad.
 * <p>
 * Esta clase implementa el patrón de diseño <b>Singleton</b> para garantizar una única instancia
 * de acceso a los datos en toda la aplicación. Se encarga de almacenar, recuperar y filtrar
 * los registros de actividades sostenibles (Eco-Puntos), persistiendo la información en el
 * almacenamiento interno del dispositivo mediante serialización de objetos.
 * </p>
 * <p>
 * Incluye lógica para inicializar datos por defecto para fechas específicas del año 2026.
 * </p>
 *
 * @author erwxn
 * @version 1.0
 */
public class RepositorioSostenibilidad {

    /** Nombre del archivo binario donde se serializan los datos. */
    private static final String FILE_NAME = "sostenibilidad_data.ser";

    /** Instancia única de la clase (Singleton). */
    private static RepositorioSostenibilidad instance;

    /** Lista en memoria que mantiene los registros cargados. */
    private List<RegistroSostenibilidad> registros;

    /** Contexto de la aplicación necesario para operaciones de entrada/salida de archivos. */
    private Context context;

    /**
     * Constructor privado para prevenir la instanciación directa.
     * Inicializa la lista, carga los datos del archivo y asegura la existencia de datos de prueba.
     *
     * @param context Contexto de la aplicación.
     */
    private RepositorioSostenibilidad(Context context) {
        this.context = context;
        this.registros = new ArrayList<>();
        cargarDatos();

        // REQUERIMIENTO: Datos por defecto (17 y 18 de Enero 2026)
        inicializarDatosPorDefecto();
    }

    /**
     * Obtiene la instancia única del repositorio.
     * Si no existe, crea una nueva instancia.
     *
     * @param context Contexto de la aplicación.
     * @return La instancia única de {@link RepositorioSostenibilidad}.
     */
    public static RepositorioSostenibilidad getInstance(Context context) {
        if (instance == null) {
            instance = new RepositorioSostenibilidad(context);
        }
        return instance;
    }

    /**
     * Genera y guarda registros automáticos para el 17 y 18 de enero de 2026 si no existen.
     * Este método asegura que la aplicación tenga datos de muestra para visualizar en el resumen.
     */
    private void inicializarDatosPorDefecto() {
        LocalDate fecha1 = LocalDate.of(2026, 1, 17);
        LocalDate fecha2 = LocalDate.of(2026, 1, 18);

        if (obtenerRegistro(fecha1) == null) {
            RegistroSostenibilidad r1 = new RegistroSostenibilidad(fecha1);
            r1.setUsoTransporteSostenible(true);
            r1.setSeparoResiduos(true);
            guardarRegistro(r1);
        }

        if (obtenerRegistro(fecha2) == null) {
            RegistroSostenibilidad r2 = new RegistroSostenibilidad(fecha2);
            r2.setEvitoImpresiones(true);
            r2.setEvitoEnvasesDescartables(true);
            r2.setUsoTransporteSostenible(true);
            r2.setSeparoResiduos(true); // Un día perfecto para el ejemplo
            guardarRegistro(r2);
        }
    }

    /**
     * Busca un registro de sostenibilidad específico por fecha.
     *
     * @param fecha La fecha {@link LocalDate} a buscar.
     * @return El objeto {@link RegistroSostenibilidad} encontrado, o {@code null} si no existe registro para esa fecha.
     */
    public RegistroSostenibilidad obtenerRegistro(LocalDate fecha) {
        for (RegistroSostenibilidad r : registros) {
            if (r.getFecha().isEqual(fecha)) return r;
        }
        return null;
    }

    /**
     * Filtra y devuelve una lista de registros que se encuentran dentro de un rango de fechas (inclusive).
     * Utilizado principalmente para generar los resúmenes semanales.
     *
     * @param inicio Fecha de inicio del rango.
     * @param fin Fecha de fin del rango.
     * @return Una lista de {@link RegistroSostenibilidad} que cumplen con el criterio de fecha.
     */
    public List<RegistroSostenibilidad> obtenerRegistrosEnRango(LocalDate inicio, LocalDate fin) {
        List<RegistroSostenibilidad> resultado = new ArrayList<>();
        for (RegistroSostenibilidad r : registros) {
            // Verifica si la fecha es igual o posterior al inicio Y igual o anterior al fin
            if ((r.getFecha().isEqual(inicio) || r.getFecha().isAfter(inicio)) &&
                    (r.getFecha().isEqual(fin) || r.getFecha().isBefore(fin))) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    /**
     * Guarda un nuevo registro o actualiza uno existente.
     * <p>
     * Si ya existe un registro para la fecha indicada, este se elimina y se reemplaza por el nuevo.
     * Posteriormente, se invoca la persistencia en archivo para asegurar que los datos no se pierdan.
     * </p>
     *
     * @param nuevo El objeto {@link RegistroSostenibilidad} a guardar.
     */
    public void guardarRegistro(RegistroSostenibilidad nuevo) {
        RegistroSostenibilidad existente = obtenerRegistro(nuevo.getFecha());
        if (existente != null) registros.remove(existente);

        registros.add(nuevo);
        guardarEnArchivo();
    }

    /**
     * Serializa la lista completa de registros y la escribe en el almacenamiento interno del dispositivo.
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
     * Deserializa y carga la lista de registros desde el almacenamiento interno.
     * Si el archivo no existe o ocurre un error de lectura, inicializa una lista vacía.
     */
    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            registros = (ArrayList<RegistroSostenibilidad>) ois.readObject();
        } catch (Exception e) {
            registros = new ArrayList<>();
        }
    }
}