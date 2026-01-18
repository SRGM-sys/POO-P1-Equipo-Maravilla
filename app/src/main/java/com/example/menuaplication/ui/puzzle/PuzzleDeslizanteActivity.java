package com.example.menuaplication.ui.puzzle; // Paquete nuevo

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.model.puzzle.FichaPuzzle; // Importación correcta

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PuzzleDeslizanteActivity extends AppCompatActivity {

    private RecyclerView rvTablero;
    private Button btnReiniciar;
    private PuzzleAdapter adapter;
    private List<FichaPuzzle> fichas;

    // Lógica: 5 columnas x 7 filas = 35 celdas
    private static final int COLUMNAS = 5;
    private static final int FILAS = 7;
    private static final int TOTAL_FICHAS = COLUMNAS * FILAS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_deslizante);

        inicializarVistas();
        configurarJuego();
    }

    private void inicializarVistas() {
        rvTablero = findViewById(R.id.rv_tablero_puzzle);
        btnReiniciar = findViewById(R.id.btn_reiniciar_juego);

        btnReiniciar.setOnClickListener(v -> mezclarTablero());
    }

    private void configurarJuego() {
        fichas = new ArrayList<>();
        // Llenamos fichas del 1 al 34
        for (int i = 1; i < TOTAL_FICHAS; i++) {
            fichas.add(new FichaPuzzle(i, false));
        }
        // La 35 es la vacía (0)
        fichas.add(new FichaPuzzle(0, true));

        adapter = new PuzzleAdapter(fichas, this::onFichaClick);
        rvTablero.setLayoutManager(new GridLayoutManager(this, COLUMNAS));
        rvTablero.setAdapter(adapter);

        mezclarTablero();
    }

    private void onFichaClick(int posicionClick) {
        int posicionVacia = obtenerPosicionVacia();

        if (esMovimientoValido(posicionClick, posicionVacia)) {
            // Intercambiar en la lista de datos
            Collections.swap(fichas, posicionClick, posicionVacia);

            // Avisar al adaptador para animar/refrescar
            adapter.notifyItemChanged(posicionClick);
            adapter.notifyItemChanged(posicionVacia);

            if (verificarVictoria()) {
                Toast.makeText(this, "¡Felicidades! Completaste el puzzle.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private int obtenerPosicionVacia() {
        for (int i = 0; i < fichas.size(); i++) {
            if (fichas.get(i).isEsVacia()) {
                return i;
            }
        }
        return -1;
    }

    private boolean esMovimientoValido(int posClick, int posVacia) {
        int filaClick = posClick / COLUMNAS;
        int colClick = posClick % COLUMNAS;
        int filaVacia = posVacia / COLUMNAS;
        int colVacia = posVacia % COLUMNAS;

        // Distancia Manhattan == 1 (Arriba, Abajo, Izq, Der)
        return (Math.abs(filaClick - filaVacia) + Math.abs(colClick - colVacia)) == 1;
    }

    private void mezclarTablero() {
        Collections.shuffle(fichas);
        adapter.actualizarDatos(fichas);
    }

    private boolean verificarVictoria() {
        for (int i = 0; i < TOTAL_FICHAS - 1; i++) {
            if (fichas.get(i).getNumero() != (i + 1)) {
                return false;
            }
        }
        return true;
    }
}