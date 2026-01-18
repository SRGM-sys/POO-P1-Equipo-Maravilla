package com.example.menuaplication.model.puzzle;

public class FichaPuzzle {
    private int numero;
    private boolean esVacia;

    public FichaPuzzle(int numero, boolean esVacia) {
        this.numero = numero;
        this.esVacia = esVacia;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isEsVacia() {
        return esVacia;
    }
}
