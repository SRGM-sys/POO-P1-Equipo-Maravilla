package com.example.menuaplication.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SesionEnfoque implements Serializable {
    private LocalDateTime fechaHora;
    private int duracionMinutos;
    private TecnicaEnfoque tecnica;
    private boolean completada;

    public SesionEnfoque(LocalDateTime fechaHora, int duracionMinutos, TecnicaEnfoque tecnica, boolean completada) {
        this.fechaHora = fechaHora;
        this.duracionMinutos = duracionMinutos;
        this.tecnica = tecnica;
        this.completada = completada;
    }

    // Getters and Setters
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public TecnicaEnfoque getTecnica() { return tecnica; }
    public boolean isCompletada() { return completada; }

    @Override
    public String toString() {
        return tecnica + " (" + duracionMinutos + " min) - " + (completada ? "OK" : "Incompleta");
    }
}