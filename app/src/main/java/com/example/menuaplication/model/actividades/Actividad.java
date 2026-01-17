package com.example.menuaplication.model.actividades;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Clase abstracta base que representa una actividad genérica dentro de la aplicación.
 * Implementa {@link Serializable} para permitir su persistencia en archivos locales.
 * Contiene los atributos y comportamientos comunes para cualquier tipo de actividad,
 * como la gestión de IDs, nombre, descripción, fechas y el historial de sesiones de enfoque.
 *
 * <p>Esta clase gestiona automáticamente el estado de la actividad (Pendiente, En Progreso, Completada)
 * basándose en el porcentaje de avance.</p>
 *
 * @author José Paladines
 * @version 1.0
 */
public abstract class Actividad implements Serializable {

    // Lógica para ID Autoincremental
    /** Contador estático para generar identificadores únicos automáticamente. */
    private static int contadorIds = 1;

    /** Identificador único de la actividad. */
    protected int id;

    /** Título o nombre corto de la actividad. */
    protected String nombre;

    /** Descripción detallada de la actividad. */
    protected String descripcion;

    /** Fecha y hora límite para finalizar la actividad. */
    protected LocalDateTime fechaVencimiento;

    /** Nivel de importancia de la actividad. */
    protected Prioridad prioridad;

    /** Estado actual del ciclo de vida de la actividad (PENDIENTE, EN_PROGRESO, COMPLETADA). */
    protected EstadoActividad estado;

    /** Valor numérico del 0.0 al 100.0 que representa el progreso. */
    protected double porcentajeAvance;

    /** Tiempo estimado en minutos que el usuario planea dedicar. */
    protected int tiempoEstimadoMinutos;

    /** Lista que almacena el historial de sesiones de trabajo (Pomodoro/Deep Work) realizadas. */
    protected ArrayList<SesionEnfoque> historialSesiones;

    /**
     * Constructor principal para crear una nueva actividad.
     * Asigna automáticamente un ID único e inicializa el estado como PENDIENTE.
     *
     * @param nombre               Nombre de la actividad.
     * @param descripcion          Descripción detallada.
     * @param fechaVencimiento     Fecha límite de entrega o realización.
     * @param prioridad            Prioridad (ALTA, MEDIA, BAJA).
     * @param tiempoEstimadoMinutos Tiempo estimado en minutos.
     */
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

    /**
     * Agrega una sesión de enfoque finalizada al historial de la actividad.
     *
     * @param sesion Objeto {@link SesionEnfoque} con los datos del tiempo trabajado.
     */
    public void agregarSesion(SesionEnfoque sesion) {
        this.historialSesiones.add(sesion);
    }

    /**
     * Calcula el total de minutos invertidos en esta actividad sumando
     * la duración de todas las sesiones completadas exitosamente.
     *
     * @return Total de minutos trabajados (entero).
     */
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

    /** @return El ID único de la actividad. */
    public int getId() { return id; }

    /** @return El nombre de la actividad. */
    public String getNombre() { return nombre; }

    /** @return La fecha y hora de vencimiento. */
    public LocalDateTime getFechaVencimiento() { return fechaVencimiento; }

    /** @return La prioridad asignada. */
    public Prioridad getPrioridad() { return prioridad; }

    /** @return El porcentaje de avance actual (0-100). */
    public double getPorcentajeAvance() { return porcentajeAvance; }

    /** @return La descripción de la actividad. */
    public String getDescripcion() { return descripcion; }

    /** @return Los minutos estimados originalmente. */
    public int getTiempoEstimadoMinutos() { return tiempoEstimadoMinutos; }

    /** @return La lista de sesiones de enfoque registradas. */
    public ArrayList<SesionEnfoque> getHistorialSesiones() { return historialSesiones; }

    /** @return El estado actual calculado de la actividad. */
    public EstadoActividad getEstado() { return estado; }

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

    /**
     * Actualiza el porcentaje de avance y modifica automáticamente el estado de la actividad.
     * <ul>
     * <li>Si es >= 100: Estado COMPLETADA.</li>
     * <li>Si es > 0 y < 100: Estado EN_PROGRESO.</li>
     * <li>Si es 0: Estado PENDIENTE.</li>
     * </ul>
     *
     * @param porcentajeAvance Nuevo valor de porcentaje.
     */
    public void setPorcentajeAvance(double porcentajeAvance) {
        this.porcentajeAvance = porcentajeAvance;
        if(porcentajeAvance >= 100) this.estado = EstadoActividad.COMPLETADA;
        else if(porcentajeAvance > 0) this.estado = EstadoActividad.EN_PROGRESO;
        else this.estado = EstadoActividad.PENDIENTE;
    }

    /**
     * Método estático para restaurar el contador de IDs al cargar datos desde el disco.
     * Evita ids duplicados al reiniciar la aplicación.
     *
     * @param ultimoId El último ID utilizado.
     */
    public static void setContadorIds(int ultimoId) {
        contadorIds = ultimoId;
    }

    /**
     * Obtiene una etiqueta legible que identifica el tipo de actividad concreta.
     *
     * @return Cadena con el tipo (ej. "Académica" o "Personal").
     */
    public abstract String getTipoEtiqueta();
}