package com.example.menuaplication.model;

import java.time.LocalDateTime;

public class ActividadAcademica extends Actividad {
    private String asignatura;
    private TipoAcademica tipo;

    public ActividadAcademica(String nombre, String descripcion, LocalDateTime fechaVencimiento, Prioridad prioridad, int tiempoEstimadoMinutos, String asignatura, TipoAcademica tipo) {
        super(nombre, descripcion, fechaVencimiento, prioridad, tiempoEstimadoMinutos);
        this.asignatura = asignatura;
        this.tipo = tipo;
    }

    // --- Getters ---
    public String getAsignatura() { return asignatura; }
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