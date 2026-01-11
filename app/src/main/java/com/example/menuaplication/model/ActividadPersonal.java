package com.example.menuaplication.model;

import java.time.LocalDateTime;

public class ActividadPersonal extends Actividad {
    private String lugar;

    public ActividadPersonal(String nombre, String descripcion, LocalDateTime fechaVencimiento, Prioridad prioridad, int tiempoEstimadoMinutos, String lugar) {
        super(nombre, descripcion, fechaVencimiento, prioridad, tiempoEstimadoMinutos);
        this.lugar = lugar;
    }

    public String getLugar() { return lugar; }

    @Override
    public String getTipoEtiqueta() {
        return "Personal";
    }
}