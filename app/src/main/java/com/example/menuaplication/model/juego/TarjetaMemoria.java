package com.example.menuaplication.model.juego;

public class TarjetaMemoria {
    private int id;
    private int imagenRecurso; // El R.drawable.algo
    private boolean estaDescubierta;
    private boolean estaEmparejada;

    public TarjetaMemoria(int id, int imagenRecurso) {
        this.id = id;
        this.imagenRecurso = imagenRecurso;
        this.estaDescubierta = false;
        this.estaEmparejada = false;
    }

    public int getId() { return id; }
    public int getImagenRecurso() { return imagenRecurso; }

    public boolean isEstaDescubierta() { return estaDescubierta; }
    public void setEstaDescubierta(boolean estaDescubierta) { this.estaDescubierta = estaDescubierta; }

    public boolean isEstaEmparejada() { return estaEmparejada; }
    public void setEstaEmparejada(boolean estaEmparejada) { this.estaEmparejada = estaEmparejada; }
}