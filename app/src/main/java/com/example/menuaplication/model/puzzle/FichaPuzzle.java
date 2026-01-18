package com.example.menuaplication.model.puzzle;

/**
 * Representa una ficha individual dentro del tablero del Puzzle Deslizante.
 * Contiene información sobre el número que muestra y si es la casilla vacía.
 *
 * @author TheMatthias
 */
public class FichaPuzzle {

    private int numero;
    private boolean esVacia;

    /**
     * Representa una ficha individual dentro del tablero del Puzzle Deslizante.
     * Contiene información sobre el número que muestra y si es la casilla vacía.
     *
     * @author TheMatthias
     */
    public FichaPuzzle(int numero, boolean esVacia) {
        this.numero = numero;
        this.esVacia = esVacia;
    }


    /**
     * Obtiene el número asignado a esta ficha.
     *
     * @return El número de la ficha.
     */
    public int getNumero() {
        return numero;
    }


    /**
     * Verifica si esta ficha es el espacio vacío.
     *
     * @return true si es la ficha vacía, false en caso contrario.
     */
    public boolean isEsVacia() {
        return esVacia;
    }
}
