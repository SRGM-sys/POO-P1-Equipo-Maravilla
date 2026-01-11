package com.example.menuaplication.data;
import com.example.menuaplication.model.*;
import com.example.menuaplication.model.Actividad;
import com.example.menuaplication.model.ActividadAcademica;
import com.example.menuaplication.model.ActividadPersonal;
import com.example.menuaplication.model.Prioridad;
import com.example.menuaplication.model.SesionEnfoque;
import com.example.menuaplication.model.TipoAcademica;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Repositorio {
    private static Repositorio instance;
    private List<Actividad> listaActividades;

    private Repositorio() {
        listaActividades = new ArrayList<>();
        cargarDatosIniciales();
    }

    public static Repositorio getInstance() {
        if (instance == null) {
            instance = new Repositorio();
        }
        return instance;
    }

    private void cargarDatosIniciales() {
        if (listaActividades.isEmpty()) {
            listaActividades.add(new ActividadPersonal("Cita Médica", "Chequeo general", LocalDateTime.of(2025, 1, 20, 10, 0), Prioridad.ALTA, 60, "Hospital Kennedy"));
            ActividadAcademica proyecto = new ActividadAcademica(
                    "Proyecto POO",
                    "Desarrollo App Android",
                    LocalDateTime.of(2025, 1, 30, 23, 59),
                    Prioridad.ALTA,
                    300,
                    "POO",
                    TipoAcademica.PROYECTO

            );
            // CORRECCIÓN SOLICITADA: Establecer avance al 70%
            proyecto.setPorcentajeAvance(70.0);
            proyecto.agregarSesion(new SesionEnfoque(LocalDateTime.of(2025, 1, 15, 10, 0, 0),70,TecnicaEnfoque.POMODORO, true)); // Día 1
            proyecto.agregarSesion(new SesionEnfoque(LocalDateTime.of(2025, 1, 16, 10, 0, 0),70,TecnicaEnfoque.POMODORO, true)); // Día 2
            listaActividades.add(proyecto);
            listaActividades.add(new ActividadAcademica("Tarea Estadística", "Taller 4", LocalDateTime.of(2025, 1, 19, 14, 0), Prioridad.MEDIA, 120, "Estadística", TipoAcademica.TAREA));
            listaActividades.add(new ActividadAcademica("Exámen POO", "Repaso Final", LocalDateTime.of(2025, 1, 23, 14, 0), Prioridad.MEDIA, 120, "POO", TipoAcademica.EXAMEN));
        }
    }

    public List<Actividad> getListaActividades() {
        return listaActividades;
    }

    public void agregarActividad(Actividad actividad) {
        listaActividades.add(actividad);
    }

    // NUEVO MÉTODO: Evita duplicados buscando por nombre
    public void actualizarActividad(Actividad actividadModificada) {
        for (int i = 0; i < listaActividades.size(); i++) {
            // Usamos el nombre como identificador único (asumiendo que no creas dos con el mismo nombre)
            if (listaActividades.get(i).getNombre().equals(actividadModificada.getNombre())) {
                listaActividades.set(i, actividadModificada);
                return;
            }
        }
        // Si no existe, la agregamos
        listaActividades.add(actividadModificada);
    }

    public void eliminarActividad(Actividad actividad) {
        // Eliminación segura por nombre para evitar problemas de referencias
        listaActividades.removeIf(a -> a.getNombre().equals(actividad.getNombre()));
    }
}