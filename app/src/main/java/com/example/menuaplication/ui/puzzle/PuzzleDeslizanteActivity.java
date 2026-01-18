package com.example.menuaplication.ui.puzzle;

import android.media.MediaPlayer;
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
 * <p>
 * Esta clase gestiona la lógica central del juego, incluyendo la inicialización del tablero,
 * el control de movimientos, la verificación de victoria y la gestión de la música de fondo
 * respetando el ciclo de vida de la actividad.
 * </p>
 *
 * @author TheMatthias
 * @version 1.0
 */
public class PuzzleDeslizanteActivity extends AppCompatActivity {

    private RecyclerView rvTablero;
    private Button btnReiniciar;
    private ImageButton btnVolver;
    private PuzzleAdapter adapter;
    private List<FichaPuzzle> fichas;
    private MediaPlayer mediaPlayer;

    private static final int COLUMNAS = 5;
    private static final int FILAS = 7;
    private static final int TOTAL_FICHAS = COLUMNAS * FILAS;


    /**
     * Método de creación de la actividad.
     * Inicializa la interfaz gráfica y configura el estado inicial del juego llamando a
     * los métodos de configuración de vistas y lógica.
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
     * Se llama cuando la actividad va a comenzar a interactuar con el usuario.
     * <p>
     * En este método se gestiona el inicio o la reanudación de la música de fondo.
     * Si el reproductor no existe, se crea e inicia. Si ya existe pero estaba pausado,
     * se reanuda la reproducción.
     * </p>
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.ost2);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } else if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    /**
     * Se llama cuando el sistema está a punto de poner la actividad en segundo plano.
     * <p>
     * Pausa la música de fondo para conservar recursos y no molestar al usuario
     * mientras está fuera de la aplicación.
     * </p>
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**
     * Se llama antes de que la actividad sea destruida.
     * <p>
     * Libera los recursos asociados al {@link MediaPlayer} para evitar fugas de memoria.
     * Es fundamental para asegurar que la música no siga sonando o ocupando memoria
     * una vez cerrada la actividad.
     * </p>
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    /**
     * Inicializa los componentes de la interfaz de usuario y asigna los escuchadores de eventos (listeners).
     * Vincula los botones y el tablero con sus respectivas vistas en el XML.
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
     * <p>
     * Crea la lista de fichas numéricas y la ficha vacía, configura el {@link RecyclerView}
     * con un {@link GridLayoutManager} para la cuadrícula y mezcla el tablero para comenzar la partida.
     * </p>
     */
    private void configurarJuego() {
        fichas = new ArrayList<>();

        for (int i = 1; i < TOTAL_FICHAS; i++) {
            fichas.add(new FichaPuzzle(i, false));
        }

        // Agregamos la ficha vacía al final (posición 0 visualmente o lógica según implementación)
        fichas.add(new FichaPuzzle(0, true));

        adapter = new PuzzleAdapter(fichas, this::onFichaClick);
        rvTablero.setLayoutManager(new GridLayoutManager(this, COLUMNAS));
        rvTablero.setAdapter(adapter);

        mezclarTablero();
    }


    /**
     * Maneja el evento de clic en una ficha del tablero.
     * <p>
     * Verifica si el movimiento hacia la casilla vacía es válido. Si lo es, realiza el intercambio
     * en la lista de datos, notifica al adaptador para actualizar la vista y verifica si el
     * jugador ha ganado la partida.
     * </p>
     *
     * @param posicionClick La posición de la ficha seleccionada en la lista del adaptador.
     */
    private void onFichaClick(int posicionClick) {
        int posicionVacia = obtenerPosicionVacia();

        if (esMovimientoValido(posicionClick, posicionVacia)) {

            Collections.swap(fichas, posicionClick, posicionVacia);

            // Notificamos cambios solo en las posiciones afectadas para mejor rendimiento
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
     * @return El índice de la ficha vacía en la lista, o -1 si no se encuentra (caso de error).
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
     * <p>
     * Un movimiento se considera válido únicamente si la ficha seleccionada es adyacente
     * ortogonalmente (arriba, abajo, izquierda o derecha) a la ficha vacía.
     * Se utiliza la distancia Manhattan para simplificar este cálculo.
     * </p>
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

        // Distancia Manhattan == 1 implica adyacencia directa (sin diagonales)
        return (Math.abs(filaClick - filaVacia) + Math.abs(colClick - colVacia)) == 1;
    }


    /**
     * Mezcla aleatoriamente las fichas del tablero y actualiza la vista.
     * Utiliza {@link Collections#shuffle(List)} para aleatorizar el orden.
     */
    private void mezclarTablero() {
        Collections.shuffle(fichas);
        adapter.actualizarDatos(fichas);
    }


    /**
     * Comprueba si el jugador ha completado el puzzle correctamente.
     * <p>
     * El puzzle se considera resuelto si todas las fichas numéricas están en orden
     * estrictamente ascendente (1, 2, 3...).
     * </p>
     *
     * @return true si el puzzle está resuelto, false si aún hay fichas desordenadas.
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