package com.example.menuaplication.model.actividades;

import java.time.LocalDateTime;

/**
 * Clase que representa una actividad relacionada con el ámbito educativo.
 * Extiende de {@link Actividad} e incluye atributos específicos como la asignatura
 * y el tipo de entrega (Tarea, Examen, Proyecto).
 *
 * @author José Paladines
 * @version 1.0
 */
public class ActividadAcademica extends Actividad {

    /** Nombre de la materia o asignatura (ej. "Cálculo", "Programación"). */
    private String asignatura;

    /** Tipo de actividad académica (TAREA, PROYECTO, EXAMEN). */
    private TipoAcademica tipo;

    /**
     * Constructor para crear una actividad académica.
     *
     * @param nombre               Nombre de la actividad.
     * @param descripcion          Descripción detallada.
     * @param fechaVencimiento     Fecha límite.
     * @param prioridad            Nivel de prioridad.
     * @param tiempoEstimadoMinutos Minutos estimados.
     * @param asignatura           Nombre de la materia.
     * @param tipo                 Tipo de entrega (Enum {@link TipoAcademica}).
     */
    public ActividadAcademica(String nombre, String descripcion, LocalDateTime fechaVencimiento, Prioridad prioridad, int tiempoEstimadoMinutos, String asignatura, TipoAcademica tipo) {
        super(nombre, descripcion, fechaVencimiento, prioridad, tiempoEstimadoMinutos);
        this.asignatura = asignatura;
        this.tipo = tipo;
    }

    // --- Getters ---

    /** @return El nombre de la asignatura. */
    public String getAsignatura() { return asignatura; }

    /** @return El tipo de actividad académica. */
    public TipoAcademica getTipo() { return tipo; }

    // --- Setters (Nuevos para la edición) ---

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public void setTipo(TipoAcademica tipo) {
        this.tipo = tipo;
    }

    @Override
    public String getTipoEtiqueta() {
        return "Académica";
    }
}