package com.example.menuaplication.model.hidratacion;

// Comentario de prueba
// Otro comentario de prueba

public class RegistroAgua {
    private int cantidadMl;
    private String hora;  // Formato "10:30 AM"
    private String fecha; // Formato "19/01/2026"

    public RegistroAgua(int cantidadMl, String hora, String fecha) {
        this.cantidadMl = cantidadMl;
        this.hora = hora;
        this.fecha = fecha;
    }

    public int getCantidadMl() { return cantidadMl; }
    public String getHora() { return hora; }
    public String getFecha() { return fecha; }

}
