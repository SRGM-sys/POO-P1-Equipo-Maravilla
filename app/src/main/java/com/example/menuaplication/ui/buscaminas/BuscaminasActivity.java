package com.example.menuaplication.ui.buscaminas;

import android.media.MediaPlayer;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.model.buscaminas.Celda;
import com.example.menuaplication.ui.buscaminas.BuscaminasAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Actividad principal que implementa la lógica del juego Buscaminas.
 * <p>
 * Esta clase gestiona el tablero de juego, la colocación aleatoria de minas,
 * la lógica de descubrimiento de celdas (incluyendo la expansión recursiva de áreas vacías),
 * el control del tiempo y la reproducción de música de fondo durante la partida.
 * </p>
 *
 * @author José Paladines
 * @version 1.0
 */
public class BuscaminasActivity extends AppCompatActivity {

    // Constantes de configuración del tablero
    private final int FILAS = 10;
    private final int COLUMNAS = 8;
    private final int TOTAL_BOMBAS = 12;

    // Elementos de la interfaz de usuario
    private RecyclerView rvTablero;
    private TextView tvMinas, tvEstado;
    private Chronometer cronometro;
    private ImageButton btnVolver, btnReiniciar;

    // Lógica del juego
    private List<Celda> tablero;
    private BuscaminasAdapter adaptador;
    private boolean juegoTerminado = false;
    private boolean esPrimerMovimiento = true;

    // Componentes multimedia
    private MediaPlayer reproductor;

    /**
     * Método de creación de la actividad.
     * Inicializa la interfaz gráfica y configura el estado inicial del juego.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscaminas);

        inicializarVistas();
        iniciarLogicaJuego();
    }

    /**
     * Inicializa y vincula los componentes de la interfaz de usuario (Vistas) con el código.
     * Configura los listeners para los botones de navegación y reinicio.
     */
    private void inicializarVistas() {
        // 1. Vincular vistas con el XML
        rvTablero = findViewById(R.id.rv_tablero_buscaminas);
        tvMinas = findViewById(R.id.tv_minas_restantes);
        tvEstado = findViewById(R.id.tv_estado_juego);
        cronometro = findViewById(R.id.cronometro_juego);
        btnVolver = findViewById(R.id.btn_volver_menu);
        btnReiniciar = findViewById(R.id.btn_reiniciar);

        // 2. Configurar LayoutManager para el RecyclerView (Cuadrícula)
        rvTablero.setLayoutManager(new GridLayoutManager(this, COLUMNAS));

        // 3. Configurar Botones (Listeners)
        btnVolver.setOnClickListener(v -> {
            cronometro.stop();
            finish();
        });

        // Reinicia la lógica sin recrear la actividad para mantener la música
        btnReiniciar.setOnClickListener(v -> {
            iniciarLogicaJuego();
        });
    }

    /**
     * Configura la lógica inicial de una nueva partida.
     * <p>
     * Resetea el tablero, coloca las bombas de manera aleatoria, calcula los números
     * de proximidad y reinicia el cronómetro.
     * </p>
     */
    private void iniciarLogicaJuego() {
        cronometro.stop();

        tablero = new ArrayList<>();
        juegoTerminado = false;
        esPrimerMovimiento = true;

        tvEstado.setText("¡Cuidado con los fantasmas! 👻");
        tvMinas.setText("💣 " + TOTAL_BOMBAS);
        cronometro.setBase(SystemClock.elapsedRealtime());

        // Generar celdas vacías
        for (int i = 0; i < FILAS * COLUMNAS; i++) {
            tablero.add(new Celda());
        }

        // Colocar bombas aleatorias
        int bombasPuestas = 0;
        Random rnd = new Random();
        while (bombasPuestas < TOTAL_BOMBAS) {
            int pos = rnd.nextInt(tablero.size());
            if (!tablero.get(pos).isEsBomba()) {
                tablero.get(pos).setEsBomba(true);
                bombasPuestas++;
            }
        }

        // Calcular números de proximidad
        calcularNumeros();

        // Configurar Adaptador
        adaptador = new BuscaminasAdapter(tablero, this::manejarClick, this::manejarLongClick);
        rvTablero.setAdapter(adaptador);
    }

    /**
     * Calcula cuántas bombas hay alrededor de cada celda que no sea bomba.
     * Actualiza el atributo {@code bombasAlrededor} de cada objeto {@link Celda}.
     */
    private void calcularNumeros() {
        for (int i = 0; i < tablero.size(); i++) {
            if (tablero.get(i).isEsBomba()) continue;
            int count = 0;
            for (int vecino : getVecinos(i)) {
                if (tablero.get(vecino).isEsBomba()) count++;
            }
            tablero.get(i).setBombasAlrededor(count);
        }
    }

    /**
     * Obtiene los índices de las celdas adyacentes a una posición dada.
     *
     * @param pos Índice de la celda central.
     * @return Lista de índices de las celdas vecinas válidas (dentro de los límites del tablero).
     */
    private List<Integer> getVecinos(int pos) {
        List<Integer> vecinos = new ArrayList<>();
        int r = pos / COLUMNAS;
        int c = pos % COLUMNAS;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nr = r + i;
                int nc = c + j;
                if (nr >= 0 && nr < FILAS && nc >= 0 && nc < COLUMNAS && !(i == 0 && j == 0)) {
                    vecinos.add(nr * COLUMNAS + nc);
                }
            }
        }
        return vecinos;
    }

    // --- Lógica de Interacción ---

    /**
     * Maneja el evento de clic corto en una celda (Intentar descubrir).
     *
     * @param posicion Índice de la celda pulsada.
     */
    private void manejarClick(int posicion) {
        if (juegoTerminado) return;

        // Iniciar tiempo al primer toque
        if (esPrimerMovimiento) {
            cronometro.setBase(SystemClock.elapsedRealtime());
            cronometro.start();
            esPrimerMovimiento = false;
        }

        Celda celda = tablero.get(posicion);
        // No hacer nada si está marcada (protegida) o ya revelada
        if (celda.isEstaMarcada() || celda.isEstaRevelada()) return;

        celda.setEstaRevelada(true);

        if (celda.isEsBomba()) {
            perderJuego();
        } else if (celda.getBombasAlrededor() == 0) {
            revelarVacias(posicion);
        }

        adaptador.notifyDataSetChanged();
        if (!juegoTerminado) verificarVictoria();
    }

    /**
     * Maneja el evento de clic largo en una celda (Marcar/Desmarcar bandera).
     *
     * @param posicion Índice de la celda pulsada.
     */
    private void manejarLongClick(int posicion) {
        if (juegoTerminado || tablero.get(posicion).isEstaRevelada()) return;

        Celda celda = tablero.get(posicion);
        celda.setEstaMarcada(!celda.isEstaMarcada());
        adaptador.notifyItemChanged(posicion);

        // Actualizar contador visual de minas restantes
        actualizarContadorMinas();
    }

    /**
     * Actualiza el texto de la interfaz que muestra cuántas minas quedan por encontrar,
     * basado en el número de banderas colocadas por el usuario.
     */
    private void actualizarContadorMinas() {
        int marcadas = 0;
        for (Celda c : tablero) if (c.isEstaMarcada()) marcadas++;
        tvMinas.setText("💣 " + (TOTAL_BOMBAS - marcadas));
    }

    /**
     * Algoritmo recursivo (Flood Fill) para revelar automáticamente las celdas vacías adyacentes.
     * Se detiene al encontrar celdas que tienen bombas cerca (números).
     *
     * @param pos Posición inicial desde donde expandir.
     */
    private void revelarVacias(int pos) {
        for (int vecino : getVecinos(pos)) {
            Celda v = tablero.get(vecino);
            if (!v.isEstaRevelada() && !v.isEsBomba()) {
                v.setEstaRevelada(true);
                if (v.getBombasAlrededor() == 0) revelarVacias(vecino);
            }
        }
    }

    /**
     * Gestiona el final del juego cuando el usuario detona una bomba.
     * Detiene el cronómetro, revela todas las bombas y muestra el diálogo de derrota.
     */
    private void perderJuego() {
        juegoTerminado = true;
        cronometro.stop();
        tvEstado.setText("¡BOOM! Te atrapó 💀");

        // Revelar todas las bombas para que el usuario vea dónde estaban
        for (Celda c : tablero) {
            if (c.isEsBomba()) c.setEstaRevelada(true);
        }
        mostrarDialogoFin(false);
    }

    /**
     * Verifica si el usuario ha ganado la partida.
     * La victoria ocurre cuando todas las celdas que NO son bombas han sido reveladas.
     */
    private void verificarVictoria() {
        int reveladas = 0;
        for (Celda c : tablero) {
            if (c.isEstaRevelada() && !c.isEsBomba()) reveladas++;
        }

        if (reveladas == (FILAS * COLUMNAS) - TOTAL_BOMBAS) {
            juegoTerminado = true;
            cronometro.stop();
            tvEstado.setText("¡Sobreviviste! 🍬");
            mostrarDialogoFin(true);
        }
    }

    /**
     * Muestra un diálogo emergente informando el resultado de la partida.
     *
     * @param victoria {@code true} si el usuario ganó, {@code false} si perdió.
     */
    private void mostrarDialogoFin(boolean victoria) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(victoria ? "¡VICTORIA! 🍬" : "¡GAME OVER! 💀");
        builder.setMessage((victoria ? "Has escapado del cementerio." : "Un fantasma te ha llevado.")
                + "\nTiempo: " + cronometro.getText());

        builder.setPositiveButton("Jugar de nuevo", (dialog, which) -> {
            iniciarLogicaJuego(); // ¡Reinicia sin cortar la música!
        });
        builder.setNegativeButton("Salir", (dialog, which) -> finish());

        builder.show();
    }

    // --------------------------------------------------------------------------------
    // Métodos del Ciclo de Vida para Control de Música
    // --------------------------------------------------------------------------------

    /**
     * Se llama cuando la actividad pasa a primer plano.
     * Inicializa o reanuda la música de fondo.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (reproductor == null) {
            // Inicializa con el archivo mp3 (ost1)
            reproductor = MediaPlayer.create(this, R.raw.ost1);
            reproductor.setLooping(true); // Para que se repita infinitamente
            reproductor.start();
        } else if (!reproductor.isPlaying()) {
            reproductor.start();
        }
    }

    /**
     * Se llama cuando la actividad pierde el foco (pausa, segundo plano).
     * Pausa la música para no molestar al usuario fuera de la app.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (reproductor != null && reproductor.isPlaying()) {
            reproductor.pause();
        }
    }

    /**
     * Se llama cuando la actividad es destruida.
     * Libera los recursos del reproductor de música.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (reproductor != null) {
            reproductor.stop();
            reproductor.release();
            reproductor = null;
        }
    }
}