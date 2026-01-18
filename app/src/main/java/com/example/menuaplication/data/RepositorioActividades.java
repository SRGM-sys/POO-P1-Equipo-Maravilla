package com.example.menuaplication.data;

import android.content.Context;
import android.util.Log;

import com.example.menuaplication.model.actividades.Actividad;
import com.example.menuaplication.model.actividades.ActividadAcademica;
import com.example.menuaplication.model.actividades.ActividadPersonal;
import com.example.menuaplication.model.actividades.Prioridad;
import com.example.menuaplication.model.actividades.SesionEnfoque;
import com.example.menuaplication.model.actividades.TecnicaEnfoque;
import com.example.menuaplication.model.actividades.TipoAcademica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de la persistencia y gestión de datos de las actividades.
 * Implementa el patrón de diseño Singleton para garantizar que exista una única
 * fuente de verdad para la lista de actividades en toda la aplicación.
 *
 * Esta clase maneja:</p>
 * <ul>
 * <li>El almacenamiento local mediante serialización de objetos (archivo .ser).</li>
 * <li>Operaciones CRUD (Crear, Leer, Actualizar, Eliminar) en memoria y disco.</li>
 * <li>La sincronización de IDs autoincrementales al reiniciar la app.</li>
 * </ul>
 *
 * @author José Paladines
 * @version 1.0
 */
public class RepositorioActividades {

    /** Instancia única de la clase (Singleton). */
    private static RepositorioActividades instance;

    /** Lista en memoria que contiene todas las actividades (Académicas y Personales). */
    private List<Actividad> listaActividades;

    /** Contexto de la aplicación necesario para acceder al almacenamiento interno. */
    private Context context;

    /** Nombre del archivo donde se guardan los datos serializados. */
    private static final String NOMBRE_ARCHIVO = "actividades_data.ser";

    /**
     * Constructor privado para evitar instanciación externa.
     * Inicializa la lista y carga los datos existentes desde el archivo.
     *
     * @param context Contexto de la aplicación.
     */
    private RepositorioActividades(Context context) {
        this.context = context.getApplicationContext();
        this.listaActividades = new ArrayList<>();
        cargarDesdeArchivo();
    }

    /**
     * Obtiene la instancia única del repositorio.
     * Si no existe, crea una nueva. Es el método principal de acceso.
     *
     * @param context Contexto necesario para la primera inicialización.
     * @return La instancia de {@link RepositorioActividades}.
     */
    public static RepositorioActividades getInstance(Context context) {
        if (instance == null) {
            instance = new RepositorioActividades(context);
        }
        return instance;
    }

    /**
     * Obtiene la instancia actual si ya fue inicializada previamente.
     * Útil para llamar al repositorio desde lugares donde no se tiene fácil acceso al contexto,
     * siempre y cuando se tenga la certeza de que ya fue creado antes.
     *
     * @return La instancia existente.
     * @throws RuntimeException Si se llama antes de inicializar con contexto.
     */
    public static RepositorioActividades getInstance() {
        if (instance == null) {
            throw new RuntimeException("Debes inicializar el repositorio con getInstance(Context) primero");
        }
        return instance;
    }

    // --- MÉTODOS DE ARCHIVOS ---

    /**
     * Guarda la lista actual de actividades en el almacenamiento interno del dispositivo.
     * Utiliza serialización de Java para escribir el objeto List completo.
     */
    private void guardarEnArchivo() {
        File archivo = new File(context.getExternalFilesDir(null), NOMBRE_ARCHIVO);
        try (FileOutputStream fos = new FileOutputStream(archivo);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(listaActividades);
            Log.d("RepositorioActividades", "Datos guardados en: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            Log.e("RepositorioActividades", "Error al guardar: " + e.getMessage());
        }
    }

    /**
     * Carga la lista de actividades desde el archivo local.
     * Si el archivo existe, deserializa los datos y actualiza el contador de IDs.
     * Si no existe (primera ejecución), llama a {@link #inicializarApp()} para crear datos de prueba.
     */
    @SuppressWarnings("unchecked")
    private void cargarDesdeArchivo() {
        File archivo = new File(context.getExternalFilesDir(null), NOMBRE_ARCHIVO);
        if (archivo.exists()) {
            try (FileInputStream fis = new FileInputStream(archivo);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                listaActividades = (List<Actividad>) ois.readObject();

                // --- CORRECCIÓN DEL ERROR DE IDs ---
                // Calculamos cuál es el ID más alto actual y actualizamos el contador estático
                actualizarContadorDeIds();

            } catch (IOException | ClassNotFoundException e) {
                inicializarApp();
            }
        } else {
            inicializarApp();
        }
    }

    /**
     * Sincroniza el contador estático de la clase {@link Actividad}.
     * Busca el ID más alto en la lista cargada y configura el generador de IDs
     * para que la próxima actividad tenga (ID máximo + 1), evitando duplicados.
     */
    private void actualizarContadorDeIds() {
        int maxId = 0;
        if (listaActividades != null) {
            for (Actividad a : listaActividades) {
                if (a.getId() > maxId) {
                    maxId = a.getId();
                }
            }
        }
        // El contador debe ser el máximo + 1 para la próxima actividad
        Actividad.setContadorIds(maxId + 1);
    }

    /**
     * Método auxiliar para generar datos de prueba (Mock Data) cuando la aplicación
     * se inicia por primera vez y no hay datos guardados.
     */
    private void inicializarApp() {
        listaActividades.clear();

        // Reiniciamos el contador manualmente para los datos de prueba
        Actividad.setContadorIds(1);

        listaActividades.add(new ActividadPersonal(
                "Cita Médica", "Chequeo general",
                LocalDateTime.of(2026, 1, 20, 10, 0),
                Prioridad.ALTA, 60, "Hospital Kennedy"
        ));

        listaActividades.add(new ActividadAcademica(
                "Tarea Estadística", "Taller 4",
                LocalDateTime.of(2026, 1, 19, 23, 59),
                Prioridad.MEDIA, 120, "Estadística", TipoAcademica.TAREA
        ));

        ActividadAcademica proyecto = new ActividadAcademica(
                "Proyecto POO", "Desarrollo App",
                LocalDateTime.of(2026, 1, 30, 23, 59),
                Prioridad.ALTA, 300, "POO", TipoAcademica.PROYECTO
        );
        proyecto.agregarSesion(new SesionEnfoque(LocalDateTime.of(2026, 1, 15, 10, 0), 25, TecnicaEnfoque.POMODORO, true));
        proyecto.agregarSesion(new SesionEnfoque(LocalDateTime.of(2026, 1, 16, 11, 0), 25, TecnicaEnfoque.POMODORO, true));
        proyecto.setPorcentajeAvance(70.0);
        listaActividades.add(proyecto);

        listaActividades.add(new ActividadAcademica(
                "Examen POO", "Parcial 1",
                LocalDateTime.of(2026, 1, 23, 14, 0),
                Prioridad.ALTA, 120, "POO", TipoAcademica.EXAMEN
        ));

        guardarEnArchivo();

        // También actualizamos el contador después de inicializar
        actualizarContadorDeIds();
    }

    // --- MÉTODOS PÚBLICOS ---

    /**
     * Obtiene la lista completa de actividades gestionadas.
     * @return Lista de objetos {@link Actividad}.
     */
    public List<Actividad> getListaActividades() { return listaActividades; }

    /**
     * Alias para {@link #getListaActividades()}.
     * @return Lista de objetos {@link Actividad}.
     */
    public List<Actividad> getActividades() { return listaActividades; }

    /**
     * Agrega una nueva actividad al repositorio y guarda los cambios en disco.
     * @param actividad La nueva actividad a registrar.
     */
    public void agregarActividad(Actividad actividad) {
        listaActividades.add(actividad);
        guardarEnArchivo();
    }

    /**
     * Actualiza una actividad existente en la lista.
     * Busca la actividad por su ID único. Si la encuentra, la reemplaza con la versión modificada.
     * Si no la encuentra (caso raro), la agrega como nueva.
     * Guarda los cambios en disco inmediatamente.
     *
     * @param actividadModificada Objeto actividad con los datos actualizados.
     */
    public void actualizarActividad(Actividad actividadModificada) {
        boolean encontrado = false;
        for (int i = 0; i < listaActividades.size(); i++) {
            if (listaActividades.get(i).getId() == actividadModificada.getId()) {
                listaActividades.set(i, actividadModificada);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            listaActividades.add(actividadModificada);
        }
        guardarEnArchivo();
    }

    /**
     * Elimina una actividad del repositorio basándose en su ID.
     * Guarda los cambios en disco inmediatamente.
     *
     * @param actividad La actividad a eliminar.
     */
    public void eliminarActividad(Actividad actividad) {
        listaActividades.removeIf(a -> a.getId() == actividad.getId());
        guardarEnArchivo();
    }
}