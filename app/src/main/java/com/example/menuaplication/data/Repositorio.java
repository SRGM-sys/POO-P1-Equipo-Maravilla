package com.example.menuaplication.data;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.menuaplication.model.Actividad;
import com.example.menuaplication.model.ActividadAcademica;
import com.example.menuaplication.model.ActividadPersonal;
import com.example.menuaplication.model.Prioridad;
import com.example.menuaplication.model.SesionEnfoque;
import com.example.menuaplication.model.TecnicaEnfoque;
import com.example.menuaplication.model.TipoAcademica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Repositorio {
    private static Repositorio instance;
    private List<Actividad> listaActividades;
    private Context context;
    private static final String NOMBRE_ARCHIVO = "actividades_data.ser";

    // Constructor privado ahora recibe contexto
    private Repositorio(Context context) {
        this.context = context.getApplicationContext(); // Usamos ApplicationContext para evitar fugas de memoria
        this.listaActividades = new ArrayList<>();
        cargarDesdeArchivo(); // Intentar cargar datos guardados al iniciar
    }

    // Singleton: Ahora requiere Context la primera vez
    public static Repositorio getInstance(Context context) {
        if (instance == null) {
            instance = new Repositorio(context);
        }
        return instance;
    }

    // Sobrecarga para usar cuando ya estamos seguros que existe (opcional, por comodidad)
    public static Repositorio getInstance() {
        if (instance == null) {
            throw new RuntimeException("Debes inicializar el repositorio con getInstance(Context) primero");
        }
        return instance;
    }

    // --- MÉTODOS DE ARCHIVOS (SERIALIZACIÓN) ---

    private void guardarEnArchivo() {
        // Esto guarda en: /storage/emulated/0/Android/data/com.example.menuaplication/files/actividades_data.ser
        File archivo = new File(context.getExternalFilesDir(null), NOMBRE_ARCHIVO);

        try (FileOutputStream fos = new FileOutputStream(archivo);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(listaActividades);
            Log.d("Repositorio", "Datos guardados exitosamente en: " + archivo.getAbsolutePath());

        } catch (IOException e) {
            Log.e("Repositorio", "Error al guardar archivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarDesdeArchivo() {
        File archivo = new File(context.getExternalFilesDir(null), NOMBRE_ARCHIVO);

        if (archivo.exists()) {
            try (FileInputStream fis = new FileInputStream(archivo);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {

                listaActividades = (List<Actividad>) ois.readObject();
                Log.d("Repositorio", "Datos cargados exitosamente. Total: " + listaActividades.size());

            } catch (IOException | ClassNotFoundException e) {
                Log.e("Repositorio", "Error al leer archivo, se crearán datos nuevos: " + e.getMessage());
                inicializarApp(); // Si falla, cargamos los default
            }
        } else {
            Log.d("Repositorio", "No existe archivo previo. Cargando datos iniciales.");
            inicializarApp();
        }
    }

    // Este método cumple el requerimiento de "Carga de datos"
    private void inicializarApp() {
        listaActividades.clear(); // Limpiar por si acaso

        // 1. Actividad Personal (Cita médica 20 ene)
        listaActividades.add(new ActividadPersonal(
                "Cita Médica",
                "Chequeo general",
                LocalDateTime.of(2025, 1, 20, 10, 0), // Fecha requerida
                Prioridad.ALTA,
                60,
                "Hospital Kennedy"
        ));

        // 2. Tarea (19 enero)
        listaActividades.add(new ActividadAcademica(
                "Tarea Estadística",
                "Taller 4 de probabilidad",
                LocalDateTime.of(2025, 1, 19, 23, 59), // Fecha requerida
                Prioridad.MEDIA,
                120,
                "Estadística",
                TipoAcademica.TAREA
        ));

        // 3. Proyecto (70%, 30 ene, pomodoro en dos días)
        ActividadAcademica proyecto = new ActividadAcademica(
                "Proyecto POO",
                "Desarrollo App Android",
                LocalDateTime.of(2025, 1, 30, 23, 59), // Fecha requerida
                Prioridad.ALTA,
                300, // 5 horas estimadas
                "POO",
                TipoAcademica.PROYECTO
        );

        // Simular sesiones en dos días diferentes
        proyecto.agregarSesion(new SesionEnfoque(LocalDateTime.of(2025, 1, 15, 10, 0), 25, TecnicaEnfoque.POMODORO, true));
        proyecto.agregarSesion(new SesionEnfoque(LocalDateTime.of(2025, 1, 16, 11, 0), 25, TecnicaEnfoque.POMODORO, true));

        // CORRECCIÓN SOLICITADA: Establecer avance al 70% explícitamente
        proyecto.setPorcentajeAvance(70.0);

        listaActividades.add(proyecto);

        // 4. Examen (23 ene)
        listaActividades.add(new ActividadAcademica(
                "Examen POO",
                "Evaluación del Parcial 1",
                LocalDateTime.of(2025, 1, 23, 14, 0), // Fecha requerida
                Prioridad.ALTA,
                120,
                "POO",
                TipoAcademica.EXAMEN
        ));

        // Guardamos estos datos iniciales en el archivo por primera vez
        guardarEnArchivo();
    }

    public List<Actividad> getListaActividades() {
        return listaActividades;
    }

    public void agregarActividad(Actividad actividad) {
        listaActividades.add(actividad);
        guardarEnArchivo(); // <--- GUARDAR CADA VEZ QUE CAMBIA
    }

    public void actualizarActividad(Actividad actividadModificada) {
        boolean encontrado = false;
        for (int i = 0; i < listaActividades.size(); i++) {
            // Usamos ID si tuvieras, o nombre/referencia.
            // Como serializamos objetos completos, a veces es mejor reemplazar por índice si lo tienes,
            // pero aquí buscaremos por igualdad de objeto o nombre.
            Actividad actual = listaActividades.get(i);

            // Comparación simple por nombre para el ejemplo (idealmente usa un ID único)
            if (actual.getNombre().equals(actividadModificada.getNombre())) {
                listaActividades.set(i, actividadModificada);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            listaActividades.add(actividadModificada);
        }
        guardarEnArchivo(); // <--- GUARDAR CADA VEZ QUE CAMBIA
    }

    public void eliminarActividad(Actividad actividad) {
        listaActividades.removeIf(a -> a.getNombre().equals(actividad.getNombre()));
        guardarEnArchivo(); // <--- GUARDAR CADA VEZ QUE CAMBIA
    }
}