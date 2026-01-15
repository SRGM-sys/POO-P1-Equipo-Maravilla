package com.example.menuaplication.model.sostenibilidad;

import java.time.LocalDate;

public class RegistroSostenibilidad {
    private LocalDate fecha;
    private boolean usoTransporteSostenible;
    private boolean evitoImpresiones;
    private boolean evitoEnvasesDescartables;
    private boolean separoResiduos;

    public RegistroSostenibilidad(LocalDate fecha) {
        this.fecha = fecha;
    }

    //Getters y Setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public boolean isUsoTransporteSostenible() {
        return usoTransporteSostenible;
    }

    public void setUsoTransporteSostenible(boolean usoTransporteSostenible) {
        this.usoTransporteSostenible = usoTransporteSostenible;
    }

    public boolean isEvitoImpresiones() {
        return evitoImpresiones;
    }

    public void setEvitoImpresiones(boolean evitoImpresiones) {
        this.evitoImpresiones = evitoImpresiones;
    }

    public boolean isEvitoEnvasesDescartables() {
        return evitoEnvasesDescartables;
    }

    public void setEvitoEnvasesDescartables(boolean evitoEnvasesDescartables) {
        this.evitoEnvasesDescartables = evitoEnvasesDescartables;
    }

    public boolean isSeparoResiduos() {
        return separoResiduos;
    }

    public void setSeparoResiduos(boolean separoResiduos) {
        this.separoResiduos = separoResiduos;
    }

    public int getPuntosDia() {
        int puntos = 0;
        if (usoTransporteSostenible) puntos++;
        if (evitoImpresiones) puntos++;
        if (evitoEnvasesDescartables) puntos++;
        if (separoResiduos) puntos++;
        return puntos;
    }
}