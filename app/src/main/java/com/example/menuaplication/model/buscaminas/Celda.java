package com.example.menuaplication.model.buscaminas;

/**
 * Representa una celda individual dentro del tablero del juego Buscaminas.
 * <p>
 * Cada celda encapsula su estado actual: si contiene una bomba, si ha sido revelada,
 * si ha sido marcada por el usuario y cuántas bombas tiene en sus celdas vecinas.
 * </p>
 *
 * @author José Paladines
 * @version 1.0
 */
public class Celda {

    /** Indica si esta celda contiene una bomba (o calabaza/fantasma en la temática). */
    private boolean esBomba;

    /** Indica si la celda ha sido descubierta por el jugador. */
    private boolean estaRevelada;

    /** Indica si el jugador ha colocado una marca (bandera/murciélago) sospechando que hay una bomba. */
    private boolean estaMarcada;

    /** Número de bombas que existen en las celdas adyacentes a esta. */
    private int bombasAlrededor;

    /**
     * Constructor por defecto de la clase Celda.
     * <p>
     * Inicializa la celda en un estado "limpio": sin bomba, oculta, sin marcas
     * y con 0 bombas alrededor.
     * </p>
     */
    public Celda() {
        this.esBomba = false;
        this.estaRevelada = false;
        this.estaMarcada = false;
        this.bombasAlrededor = 0;
    }

    // -----------------------------------------------------------------------------------
    // Getters y Setters
    // -----------------------------------------------------------------------------------

    /**
     * Verifica si la celda contiene una bomba.
     *
     * @return {@code true} si es una bomba, {@code false} en caso contrario.
     */
    public boolean isEsBomba() {
        return esBomba;
    }

    /**
     * Establece si esta celda debe contener una bomba.
     *
     * @param esBomba {@code true} para convertir esta celda en una bomba.
     */
    public void setEsBomba(boolean esBomba) {
        this.esBomba = esBomba;
    }

    /**
     * Verifica si la celda ya ha sido revelada (visible) para el jugador.
     *
     * @return {@code true} si está revelada, {@code false} si sigue oculta.
     */
    public boolean isEstaRevelada() {
        return estaRevelada;
    }

    /**
     * Cambia el estado de visibilidad de la celda.
     *
     * @param estaRevelada {@code true} para mostrar el contenido de la celda.
     */
    public void setEstaRevelada(boolean estaRevelada) {
        this.estaRevelada = estaRevelada;
    }

    /**
     * Verifica si la celda ha sido marcada (bandera/murciélago) por el usuario.
     *
     * @return {@code true} si la celda tiene una marca protectora.
     */
    public boolean isEstaMarcada() {
        return estaMarcada;
    }

    /**
     * Establece o quita la marca de la celda.
     * <p>
     * Se utiliza para evitar revelar accidentalmente una celda que el usuario
     * sospecha que contiene una bomba.
     * </p>
     *
     * @param estaMarcada {@code true} para poner la marca, {@code false} para quitarla.
     */
    public void setEstaMarcada(boolean estaMarcada) {
        this.estaMarcada = estaMarcada;
    }

    /**
     * Obtiene el número de bombas en las celdas vecinas.
     *
     * @return Un entero indicando la cantidad de amenazas adyacentes.
     */
    public int getBombasAlrededor() {
        return bombasAlrededor;
    }

    /**
     * Establece el número de bombas vecinas precalculado.
     *
     * @param bombasAlrededor Cantidad de bombas en el vecindario de esta celda.
     */
    public void setBombasAlrededor(int bombasAlrededor) {
        this.bombasAlrededor = bombasAlrededor;
    }
}