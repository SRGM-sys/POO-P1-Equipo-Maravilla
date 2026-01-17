package com.example.menuaplication.model.hidratacion;

import java.io.Serializable;

/**
 * Representa un registro individual de ingesta de agua dentro de la aplicación.
 * Esta clase almacena la cantidad de líquido consumido, así como la fecha y hora
 * específicas del consumo. Implementa {@link Serializable} para permitir que
 * los objetos de esta clase sean almacenados en archivos locales para la persistencia de datos.
 *
 * @author SRGM
 * @version 1.0
 */
public class RegistroAgua implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Cantidad de agua consumida en este registro, medida en mililitros (ml).
     */
    private int cantidadMl;

    /**
     * Hora en la que se realizó el consumo.
     * Se espera un formato de 12 horas con indicador AM/PM (ej. "10:30 AM").
     */
    private String hora;

    /**
     * Fecha en la que se realizó el consumo.
     * Se espera el formato "dd/MM/yyyy" (ej. "19/01/2026").
     */
    private String fecha;

    /**
     * Constructor para crear una nueva instancia de un registro de agua.
     *
     * @param cantidadMl La cantidad de agua consumida en mililitros.
     * @param hora       La hora del consumo en formato "HH:mm AM/PM".
     * @param fecha      La fecha del consumo en formato "dd/MM/yyyy".
     */
    public RegistroAgua(int cantidadMl, String hora, String fecha) {
        this.cantidadMl = cantidadMl;
        this.hora = hora;
        this.fecha = fecha;
    }

    /**
     * Obtiene la cantidad de agua de este registro.
     *
     * @return La cantidad de agua en mililitros.
     */
    public int getCantidadMl() {
        return cantidadMl;
    }

    /**
     * Obtiene la hora en la que se registró el consumo.
     *
     * @return Una cadena de texto con la hora (ej. "10:30 AM").
     */
    public String getHora() {
        return hora;
    }

    /**
     * Obtiene la fecha en la que se registró el consumo.
     *
     * @return Una cadena de texto con la fecha (ej. "19/01/2026").
     */
    public String getFecha() {
        return fecha;
    }
}