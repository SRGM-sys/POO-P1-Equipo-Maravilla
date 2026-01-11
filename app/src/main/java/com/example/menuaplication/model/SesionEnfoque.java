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

    @Override
    public String toString() {
        return tecnica + " (" + duracionMinutos + " min) - " + (completada ? "OK" : "Incompleta");
    }
}