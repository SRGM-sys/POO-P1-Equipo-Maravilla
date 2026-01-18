package com.example.menuaplication.ui.puzzle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.model.puzzle.FichaPuzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Actividad principal para el juego de Puzzle Deslizante.
 * Gestiona la lógica del juego, la interacción con el usuario y el control del tablero.
 *
 * @author TheMatthias
 */
public class PuzzleDeslizanteActivity extends AppCompatActivity {

    private RecyclerView rvTablero;
    private Button btnReiniciar;
    private ImageButton btnVolver;
    private PuzzleAdapter adapter;
    private List<FichaPuzzle> fichas;

    private static final int COLUMNAS = 5;
    private static final int FILAS = 7;
    private static final int TOTAL_FICHAS = COLUMNAS * FILAS;


    /**
     * Método de creación de la actividad.
     * Inicializa la interfaz y configura el estado inicial del juego.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_deslizante);

        inicializarVistas();
        configurarJuego();
    }


    /**
     * Inicializa los componentes de la interfaz de usuario y asigna los escuchadores de eventos.
     */
    private void inicializarVistas() {
        rvTablero = findViewById(R.id.rv_tablero_puzzle);
        btnReiniciar = findViewById(R.id.btn_reiniciar_juego);
        btnVolver = findViewById(R.id.btn_volver_menu);

        btnReiniciar.setOnClickListener(v -> mezclarTablero());

        btnVolver.setOnClickListener(v -> finish());
    }


    /**
     * Configura la lógica inicial del juego.
     * Crea la lista de fichas, configura el RecyclerView con un GridLayoutManager
     * y mezcla el tablero para comenzar.
     */
    private void configurarJuego() {
        fichas = new ArrayList<>();

        for (int i = 1; i < TOTAL_FICHAS; i++) {
            fichas.add(new FichaPuzzle(i, false));
        }

        fichas.add(new FichaPuzzle(0, true));

        adapter = new PuzzleAdapter(fichas, this::onFichaClick);
        rvTablero.setLayoutManager(new GridLayoutManager(this, COLUMNAS));
        rvTablero.setAdapter(adapter);

        mezclarTablero();
    }


    /**
     * Maneja el evento de clic en una ficha del tablero.
     * Verifica si el movimiento es válido y, de ser así, actualiza el estado del juego.
     *
     * @param posicionClick La posición de la ficha seleccionada en la lista.
     */
    private void onFichaClick(int posicionClick) {
        int posicionVacia = obtenerPosicionVacia();

        if (esMovimientoValido(posicionClick, posicionVacia)) {

            Collections.swap(fichas, posicionClick, posicionVacia);

            adapter.notifyItemChanged(posicionClick);
            adapter.notifyItemChanged(posicionVacia);

            if (verificarVictoria()) {
                Toast.makeText(this, "¡Felicidades! Completaste el puzzle.", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Busca y retorna la posición actual de la ficha vacía en el tablero.
     *
     * @return El índice de la ficha vacía, o -1 si no se encuentra.
     */
    private int obtenerPosicionVacia() {
        for (int i = 0; i < fichas.size(); i++) {
            if (fichas.get(i).isEsVacia()) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Verifica si el movimiento intentado por el usuario es válido.
     * Un movimiento es válido si la ficha seleccionada es adyacente (arriba, abajo, izquierda, derecha)
     * a la ficha vacía.
     *
     * @param posClick La posición de la ficha que se quiere mover.
     * @param posVacia La posición actual de la ficha vacía.
     * @return true si el movimiento es válido, false en caso contrario.
     */
    private boolean esMovimientoValido(int posClick, int posVacia) {
        int filaClick = posClick / COLUMNAS;
        int colClick = posClick % COLUMNAS;
        int filaVacia = posVacia / COLUMNAS;
        int colVacia = posVacia % COLUMNAS;

        // Distancia Manhattan == 1 (Arriba, Abajo, Izq, Der)
        return (Math.abs(filaClick - filaVacia) + Math.abs(colClick - colVacia)) == 1;
    }


    /**
     * Mezcla aleatoriamente las fichas del tablero y actualiza la vista.
     */
    private void mezclarTablero() {
        Collections.shuffle(fichas);
        adapter.actualizarDatos(fichas);
    }


    /**
     * Comprueba si el jugador ha completado el puzzle correctamente.
     * El puzzle se considera resuelto si todas las fichas numéricas están en orden ascendente.
     *
     * @return true si el puzzle está resuelto, false si no.
     */
    private boolean verificarVictoria() {
        for (int i = 0; i < TOTAL_FICHAS - 1; i++) {
            if (fichas.get(i).getNumero() != (i + 1)) {
                return false;
            }
        }
        return true;
    }
}