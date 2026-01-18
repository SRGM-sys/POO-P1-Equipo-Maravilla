package com.example.menuaplication.model.sostenibilidad;

import java.io.Serializable;
import java.time.LocalDate;

// IMPORTANTE: 'implements Serializable' es obligatorio para guardar en archivo
public class RegistroSostenibilidad implements Serializable {
    private static final long serialVersionUID = 1L; // Versión de serialización
    private LocalDate fecha;
    private boolean usoTransporteSostenible;
    private boolean evitoImpresiones;
    private boolean evitoEnvasesDescartables;
    private boolean separoResiduos;

    public RegistroSostenibilidad(LocalDate fecha) {
        this.fecha = fecha;
    }

    // Getters y Setters
    public LocalDate getFecha() { return fecha; }

    public boolean isUsoTransporteSostenible() { return usoTransporteSostenible; }
    public void setUsoTransporteSostenible(boolean val) { this.usoTransporteSostenible = val; }

    public boolean isEvitoImpresiones() { return evitoImpresiones; }
    public void setEvitoImpresiones(boolean val) { this.evitoImpresiones = val; }

    public boolean isEvitoEnvasesDescartables() { return evitoEnvasesDescartables; }
    public void setEvitoEnvasesDescartables(boolean val) { this.evitoEnvasesDescartables = val; }

    public boolean isSeparoResiduos() { return separoResiduos; }
    public void setSeparoResiduos(boolean val) { this.separoResiduos = val; }

    // Calcula puntos del día (0 a 4)
    public int getPuntosDia() {
        int puntos = 0;
        if (usoTransporteSostenible) puntos++;
        if (evitoImpresiones) puntos++;
        if (evitoEnvasesDescartables) puntos++;
        if (separoResiduos) puntos++;
        return puntos;
    }
}