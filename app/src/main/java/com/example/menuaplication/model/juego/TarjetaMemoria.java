package com.example.menuaplication.model.juego;

public class TarjetaMemoria {
    private int imagenResId; // El ID del drawable (R.drawable.ic_brain)
    private boolean volteada;
    private boolean encontrada;

    public TarjetaMemoria(int imagenResId) {
        this.imagenResId = imagenResId;
        this.volteada = false;
        this.encontrada = false;
    }

    // Getters y Setters necesarios
    public int getImagenResId() { return imagenResId; }
    public boolean isVolteada() { return volteada; }
    public void setVolteada(boolean volteada) { this.volteada = volteada; }
    public boolean isEncontrada() { return encontrada; }
    public void setEncontrada(boolean encontrada) { this.encontrada = encontrada; }
}