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

    // Getters y Setters... (Mismo código que ya tenías)
    public void setUsoTransporteSostenible(boolean usoTransporteSostenible) { this.usoTransporteSostenible = usoTransporteSostenible; }
    public boolean isUsoTransporteSostenible() { return usoTransporteSostenible; }
    // ... resto de getters y setters ...

    public int getPuntosDia() {
        int puntos = 0;
        if (usoTransporteSostenible) puntos++;
        if (evitoImpresiones) puntos++;
        if (evitoEnvasesDescartables) puntos++;
        if (separoResiduos) puntos++;
        return puntos;
    }
}