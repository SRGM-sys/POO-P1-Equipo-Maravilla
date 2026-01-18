package com.example.menuaplication.model.sostenibilidad;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa el conjunto de acciones ecológicas realizadas por un usuario en un día específico.
 * Esta clase actúa como el modelo de datos para el módulo de sostenibilidad, permitiendo
 * cuantificar el impacto ambiental positivo a través de un sistema de puntos.
 * * Implementa {@link Serializable} para garantizar que el progreso del usuario pueda
 * ser almacenado de forma persistente en el almacenamiento interno del dispositivo.
 *
 * @author erwxn
 * @version 1.0
 */
public class RegistroSostenibilidad implements Serializable {

    /** Número de versión para la serialización, asegura la compatibilidad durante la lectura del archivo. */
    private static final long serialVersionUID = 1L;

    /** Fecha correspondiente al registro de acciones. */
    private LocalDate fecha;

    /** Indica si el usuario utilizó medios de transporte ecológicos (bicicleta, caminar, etc.). */
    private boolean usoTransporteSostenible;

    /** Indica si el usuario evitó realizar impresiones en papel innecesarias. */
    private boolean evitoImpresiones;

    /** Indica si el usuario evitó el uso de plásticos de un solo uso o envases descartables. */
    private boolean evitoEnvasesDescartables;

    /** Indica si el usuario realizó una correcta gestión y separación de sus residuos. */
    private boolean separoResiduos;

    /**
     * Constructor para inicializar un registro vinculado a una fecha específica.
     *
     * @param fecha El objeto {@link LocalDate} que representa el día del registro.
     */
    public RegistroSostenibilidad(LocalDate fecha) {
        this.fecha = fecha;
    }

    // --- MÉTODOS DE ACCESO (GETTERS Y SETTERS) ---

    /** @return La fecha del registro. */
    public LocalDate getFecha() { return fecha; }

    /** @return true si se usó transporte sostenible. */
    public boolean isUsoTransporteSostenible() { return usoTransporteSostenible; }
    /** @param val Estado del uso de transporte sostenible. */
    public void setUsoTransporteSostenible(boolean val) { this.usoTransporteSostenible = val; }

    /** @return true si se evitaron impresiones. */
    public boolean isEvitoImpresiones() { return evitoImpresiones; }
    /** @param val Estado de la evitación de impresiones. */
    public void setEvitoImpresiones(boolean val) { this.evitoImpresiones = val; }

    /** @return true si se evitaron envases descartables. */
    public boolean isEvitoEnvasesDescartables() { return evitoEnvasesDescartables; }
    /** @param val Estado de la evitación de envases. */
    public void setEvitoEnvasesDescartables(boolean val) { this.evitoEnvasesDescartables = val; }

    /** @return true si se separaron los residuos. */
    public boolean isSeparoResiduos() { return separoResiduos; }
    /** @param val Estado de la separación de residuos. */
    public void setSeparoResiduos(boolean val) { this.separoResiduos = val; }

    /**
     * Calcula la puntuación ecológica total obtenida durante el día.
     * Cada acción sostenible realizada suma un punto al total diario.
     *
     * @return Un valor entero entre 0 y 4 que representa los Eco-Puntos del día.
     */
    public int getPuntosDia() {
        int puntos = 0;
        if (usoTransporteSostenible) puntos++;
        if (evitoImpresiones) puntos++;
        if (evitoEnvasesDescartables) puntos++;
        if (separoResiduos) puntos++;
        return puntos;
    }
}