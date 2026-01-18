package com.example.menuaplication.model.buscaminas;

public class Celda {
    private boolean esBomba;
    private boolean estaRevelada;
    private boolean estaMarcada; // Para poner una bandera/murciélago
    private int bombasAlrededor;

    public Celda() {
        this.esBomba = false;
        this.estaRevelada = false;
        this.estaMarcada = false;
        this.bombasAlrededor = 0;
    }

    // Getters y Setters
    public boolean isEsBomba() { return esBomba; }
    public void setEsBomba(boolean esBomba) { this.esBomba = esBomba; }
    public boolean isEstaRevelada() { return estaRevelada; }
    public void setEstaRevelada(boolean estaRevelada) { this.estaRevelada = estaRevelada; }
    public boolean isEstaMarcada() { return estaMarcada; }
    public void setEstaMarcada(boolean estaMarcada) { this.estaMarcada = estaMarcada; }
    public int getBombasAlrededor() { return bombasAlrededor; }
    public void setBombasAlrededor(int bombasAlrededor) { this.bombasAlrededor = bombasAlrededor; }

}
