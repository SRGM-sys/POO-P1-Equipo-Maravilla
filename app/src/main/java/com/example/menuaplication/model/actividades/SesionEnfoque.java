package com.example.menuaplication.model.actividades;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Clase que representa un registro individual de tiempo dedicado a una actividad.
 * Se genera cuando el usuario utiliza el temporizador (Pomodoro o Deep Work).
 * Implementa {@link Serializable} para guardarse junto con la actividad.
 *
 * @author José Paladines
 * @version 1.0
 */
public class SesionEnfoque implements Serializable {

    /** Fecha y hora en la que finalizó la sesión. */
    private LocalDateTime fechaHora;

    /** Duración total de la sesión en minutos. */
    private int duracionMinutos;

    /** Técnica utilizada durante la sesión. */
    private TecnicaEnfoque tecnica;

    /** Indica si la sesión se terminó correctamente o fue interrumpida. */
    private boolean completada;

    /**
     * Constructor para registrar una nueva sesión de enfoque.
     *
     * @param fechaHora       Momento del registro.
     * @param duracionMinutos Tiempo transcurrido.
     * @param tecnica         Técnica usada (Pomodoro/Deep Work).
     * @param completada      True si el temporizador llegó a cero, False si se detuvo antes.
     */
    public SesionEnfoque(LocalDateTime fechaHora, int duracionMinutos, TecnicaEnfoque tecnica, boolean completada) {
        this.fechaHora = fechaHora;
        this.duracionMinutos = duracionMinutos;
        this.tecnica = tecnica;
        this.completada = completada;
    }

    // --- Getters ---
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public TecnicaEnfoque getTecnica() { return tecnica; }
    public boolean isCompletada() { return completada; }

    // --- Setters (Nuevos) ---
    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public void setTecnica(TecnicaEnfoque tecnica) {
        this.tecnica = tecnica;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    /**
     * Devuelve una representación en texto de la sesión para depuración o listados simples.
     * @return Cadena con formato "TECNICA (min) - Estado".
     */
    @Override
    public String toString() {
        return tecnica + " (" + duracionMinutos + " min) - " + (completada ? "OK" : "Incompleta");
    }
}