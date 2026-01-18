package com.example.menuaplication.model.juego;

/**
 * Clase modelo que representa una carta individual en el juego de memoria.
 * Almacena el identificador de la imagen asociada y los estados actuales de la carta
 * (si está volteada o si ya ha sido encontrada/emparejada).
 *
 * @author TheMatthias
 */
public class TarjetaMemoria {

    private int imagenResId; // El ID del drawable (R.drawable.ic_brain)
    private boolean volteada;
    private boolean encontrada;


    /**
     * Constructor para crear una nueva tarjeta de memoria.
     * Inicializa la tarjeta con una imagen específica y establece sus estados
     * 'volteada' y 'encontrada' como falsos por defecto.
     *
     * @param imagenResId El ID del recurso drawable (R.drawable) que se mostrará en la carta.
     */
    public TarjetaMemoria(int imagenResId) {

        this.imagenResId = imagenResId;
        this.volteada = false;
        this.encontrada = false;
    }


    /**
     * Obtiene el ID del recurso de imagen asociado a la carta.
     *
     * @return El ID del recurso drawable (int).
     */
    public int getImagenResId() { return imagenResId; }


    /**
     * Verifica si la carta está actualmente volteada (boca arriba).
     *
     * @return true si la carta está mostrando su imagen, false si está mostrando el reverso.
     */
    public boolean isVolteada() { return volteada; }


    /**
     * Cambia el estado de visualización de la carta.
     *
     * @param volteada true para voltear la carta boca arriba, false para ponerla boca abajo.
     */
    public void setVolteada(boolean volteada) { this.volteada = volteada; }


    /**
     * Verifica si la carta ya ha sido emparejada exitosamente con su par.
     *
     * @return true si la carta ya fue encontrada y debe permanecer visible, false en caso contrario.
     */
    public boolean isEncontrada() { return encontrada; }


    /**
     * Marca la carta como encontrada una vez que se ha hecho coincidir con su par.
     * Esto generalmente bloquea la carta para que no pueda ser interactuada nuevamente.
     *
     * @param encontrada true para marcar la carta como emparejada.
     */
    public void setEncontrada(boolean encontrada) { this.encontrada = encontrada; }
}