package com.example.menuaplication.model.actividades;

/**
 * Enumerado que define los posibles estados del ciclo de vida de una actividad.
 *
 * @author José Paladines
 * @version 1.0
 */
public enum EstadoActividad {
    /** La actividad ha sido creada pero no se ha registrado progreso (0%). */
    PENDIENTE,

    /** La actividad tiene un progreso registrado mayor a 0% y menor a 100%. */
    EN_PROGRESO,

    /** La actividad ha alcanzado el 100% de progreso. */
    COMPLETADA
}