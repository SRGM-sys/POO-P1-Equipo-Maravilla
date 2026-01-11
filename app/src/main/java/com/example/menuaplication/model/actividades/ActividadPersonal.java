package com.example.menuaplication.model.actividades;

import java.time.LocalDateTime;

public class ActividadPersonal extends Actividad {
    private String lugar;

    public ActividadPersonal(String nombre, String descripcion, LocalDateTime fechaVencimiento, Prioridad prioridad, int tiempoEstimadoMinutos, String lugar) {
        super(nombre, descripcion, fechaVencimiento, prioridad, tiempoEstimadoMinutos);
        this.lugar = lugar;
    }

    // --- Getters ---
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