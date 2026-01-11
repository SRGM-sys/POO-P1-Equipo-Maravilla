package com.example.menuaplication.model.actividades;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class Actividad implements Serializable {
    // Lógica para ID Autoincremental
    private static int contadorIds = 1;
    protected int id;

    protected String nombre;
    protected String descripcion;
    protected LocalDateTime fechaVencimiento;
    protected Prioridad prioridad;
    protected EstadoActividad estado;
    protected double porcentajeAvance;
    protected int tiempoEstimadoMinutos;
    protected ArrayList<SesionEnfoque> historialSesiones;

    public Actividad(String nombre, String descripcion, LocalDateTime fechaVencimiento, Prioridad prioridad, int tiempoEstimadoMinutos) {
        this.id = contadorIds++; // Asigna 1, luego 2, luego 3...
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaVencimiento = fechaVencimiento;
        this.prioridad = prioridad;
        this.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
        this.estado = EstadoActividad.PENDIENTE;
        this.porcentajeAvance = 0.0;
        this.historialSesiones = new ArrayList<>();
    }

    public void agregarSesion(SesionEnfoque sesion) {
        this.historialSesiones.add(sesion);
    }

    // Método para calcular minutos totales invertidos
    public int getMinutosInvertidos() {
        int total = 0;
        for (SesionEnfoque s : historialSesiones) {
            if (s.isCompletada()) {
                total += s.getDuracionMinutos();
            }
        }
        return total;
    }

    // --- Getters ---
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }
    public Prioridad getPrioridad() { return prioridad; }
    public double getPorcentajeAvance() { return porcentajeAvance; }
    public String getDescripcion() { return descripcion; }
    public int getTiempoEstimadoMinutos() { return tiempoEstimadoMinutos; }
    public ArrayList<SesionEnfoque> getHistorialSesiones() { return historialSesiones; }
    public EstadoActividad getEstado() { return estado; } // Agregado por si acaso se necesita leer el estado directamente

    // --- Setters (Nuevos para la edición) ---
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public void setTiempoEstimadoMinutos(int tiempoEstimadoMinutos) {
        this.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
    }

    public void setPorcentajeAvance(double porcentajeAvance) {
        this.porcentajeAvance = porcentajeAvance;
        if(porcentajeAvance >= 100) this.estado = EstadoActividad.COMPLETADA;
        else if(porcentajeAvance > 0) this.estado = EstadoActividad.EN_PROGRESO;
        else this.estado = EstadoActividad.PENDIENTE;
    }
    public static void setContadorIds(int ultimoId) {
        contadorIds = ultimoId;
    }
    public abstract String getTipoEtiqueta();
}