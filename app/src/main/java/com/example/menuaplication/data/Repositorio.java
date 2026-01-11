package com.example.menuaplication.data;

import android.content.Context;
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

    private Repositorio(Context context) {
        this.context = context.getApplicationContext();
        this.listaActividades = new ArrayList<>();
        cargarDesdeArchivo();
    }

    public static Repositorio getInstance(Context context) {
        if (instance == null) {
            instance = new Repositorio(context);
        }
        return instance;
    }

    public static Repositorio getInstance() {
        if (instance == null) {
            throw new RuntimeException("Debes inicializar el repositorio con getInstance(Context) primero");
        }
        return instance;
    }

    // --- MÉTODOS DE ARCHIVOS ---

    private void guardarEnArchivo() {
        File archivo = new File(context.getExternalFilesDir(null), NOMBRE_ARCHIVO);
        try (FileOutputStream fos = new FileOutputStream(archivo);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(listaActividades);
            Log.d("Repositorio", "Datos guardados en: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            Log.e("Repositorio", "Error al guardar: " + e.getMessage());
        }
    }

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

    // Método auxiliar para sincronizar el contador de IDs
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

    private void inicializarApp() {
        listaActividades.clear();

        // Reiniciamos el contador manualmente para los datos de prueba
        Actividad.setContadorIds(1);

        listaActividades.add(new ActividadPersonal(
                "Cita Médica", "Chequeo general",
                LocalDateTime.of(2025, 1, 20, 10, 0),
                Prioridad.ALTA, 60, "Hospital Kennedy"
        ));

        listaActividades.add(new ActividadAcademica(
                "Tarea Estadística", "Taller 4",
                LocalDateTime.of(2025, 1, 19, 23, 59),
                Prioridad.MEDIA, 120, "Estadística", TipoAcademica.TAREA
        ));

        ActividadAcademica proyecto = new ActividadAcademica(
                "Proyecto POO", "Desarrollo App",
                LocalDateTime.of(2025, 1, 30, 23, 59),
                Prioridad.ALTA, 300, "POO", TipoAcademica.PROYECTO
        );
        proyecto.agregarSesion(new SesionEnfoque(LocalDateTime.of(2025, 1, 15, 10, 0), 25, TecnicaEnfoque.POMODORO, true));
        proyecto.agregarSesion(new SesionEnfoque(LocalDateTime.of(2025, 1, 16, 11, 0), 25, TecnicaEnfoque.POMODORO, true));
        proyecto.setPorcentajeAvance(70.0);
        listaActividades.add(proyecto);

        listaActividades.add(new ActividadAcademica(
                "Examen POO", "Parcial 1",
                LocalDateTime.of(2025, 1, 23, 14, 0),
                Prioridad.ALTA, 120, "POO", TipoAcademica.EXAMEN
        ));

        guardarEnArchivo();

        // También actualizamos el contador después de inicializar
        actualizarContadorDeIds();
    }

    // --- MÉTODOS PÚBLICOS ---
    public List<Actividad> getListaActividades() { return listaActividades; }
    public List<Actividad> getActividades() { return listaActividades; }

    public void agregarActividad(Actividad actividad) {
        listaActividades.add(actividad);
        guardarEnArchivo();
    }

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

    public void eliminarActividad(Actividad actividad) {
        listaActividades.removeIf(a -> a.getId() == actividad.getId());
        guardarEnArchivo();
    }
}