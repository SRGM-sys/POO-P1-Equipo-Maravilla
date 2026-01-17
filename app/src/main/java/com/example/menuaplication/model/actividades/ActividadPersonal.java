package com.example.menuaplication.model.actividades;

import java.time.LocalDateTime;

/**
 * Clase que representa una actividad de índole personal.
 * Extiende de {@link Actividad} e incluye atributos específicos como el lugar
 * donde se realizará la actividad.
 *
 * @author José Paladines
 * @version 1.0
 */
public class ActividadPersonal extends Actividad {

    /** Ubicación o lugar donde se realiza la actividad (ej. "Gimnasio", "Casa"). */
    private String lugar;

    /**
     * Constructor para crear una actividad personal.
     *
     * @param nombre               Nombre de la actividad.
     * @param descripcion          Descripción detallada.
     * @param fechaVencimiento     Fecha límite o fecha del evento.
     * @param prioridad            Nivel de prioridad.
     * @param tiempoEstimadoMinutos Minutos estimados.
     * @param lugar                Lugar de realización.
     */
    public ActividadPersonal(String nombre, String descripcion, LocalDateTime fechaVencimiento, Prioridad prioridad, int tiempoEstimadoMinutos, String lugar) {
        super(nombre, descripcion, fechaVencimiento, prioridad, tiempoEstimadoMinutos);
        this.lugar = lugar;
    }

    // --- Getters ---

    /** @return El lugar de la actividad. */
    public String getLugar() { return lugar; }

    // --- Setters (Nuevos para la edición) ---

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    @Override
    public String getTipoEtiqueta() {
        return "Personal";
    }
}