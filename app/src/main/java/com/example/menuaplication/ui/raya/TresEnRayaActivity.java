package com.example.menuaplication.ui.raya;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.menuaplication.R;

/**
 * Actividad que implementa la lógica y la interfaz del juego Tres en Raya (Tic-Tac-Toe).
 * <p>
 * Gestiona el tablero de juego, los turnos de los jugadores (X y O), la detección
 * de victorias o empates y la actualización visual de la interfaz.
 * </p>
 *
 * @author SRGM
 * @version 1.0
 */
public class TresEnRayaActivity extends AppCompatActivity {

    /**
     * Representación lógica del tablero.
     * 0: Casilla vacía
     * 1: Ficha X
     * 2: Ficha O
     */
    private int[] tablero = {0, 0, 0, 0, 0, 0, 0, 0, 0};

    /** Indica el turno actual: {@code true} para X, {@code false} para O. */
    private boolean turnoX = true;

    /** Bandera para controlar si el juego sigue en curso o ha terminado. */
    private boolean juegoActivo = true;

    /**
     * Matriz de combinaciones ganadoras.
     * Contiene los índices del array {@code tablero} que forman una línea ganadora
     * (horizontales, verticales y diagonales).
     */
    private final int[][] combinacionesGanadoras = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Horizontales
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Verticales
            {0, 4, 8}, {2, 4, 6}             // Diagonales
    };

    private Button[] botones = new Button[9];
    private TextView tvTurno;

    /**
     * Método de creación de la actividad.
     * Inicializa el layout y los componentes visuales.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tres_en_raya);

        inicializarVistas();
    }

    /**
     * Vincula los botones del tablero y los controles de la interfaz con el código.
     * Asigna los listeners para manejar los clics de los jugadores.
     */
    private void inicializarVistas() {
        tvTurno = findViewById(R.id.tvTurno);
        Button btnLimpiar = findViewById(R.id.btnLimpiar);
        Button btnSalir = findViewById(R.id.btnSalir);

        // Mapear botones del 00 al 22 a un array lineal del 0 al 8
        String[] ids = {"btn00", "btn01", "btn02", "btn10", "btn11", "btn12", "btn20", "btn21", "btn22"};

        for (int i = 0; i < botones.length; i++) {
            int resId = getResources().getIdentifier(ids[i], "id", getPackageName());
            botones[i] = findViewById(resId);
            final int posicion = i;
            botones[i].setOnClickListener(v -> realizarJugada(posicion));
        }

        btnLimpiar.setOnClickListener(v -> reiniciarJuego());
        btnSalir.setOnClickListener(v -> finish());
    }

    /**
     * Gestiona la lógica de un movimiento cuando el jugador pulsa una casilla.
     * <p>
     * Verifica si la jugada es válida, actualiza el estado del tablero,
     * comprueba si hay un ganador o empate y cambia el turno.
     * </p>
     *
     * @param posicion Índice (0-8) del botón presionado en el tablero.
     */
    private void realizarJugada(int posicion) {
        // Si la celda no está vacía o el juego terminó, no hacer nada
        if (tablero[posicion] != 0 || !juegoActivo) return;

        // Marcar lógica (1 para X, 2 para O)
        tablero[posicion] = turnoX ? 1 : 2;

        // Actualizar visual
        botones[posicion].setText(turnoX ? "X" : "O");
        botones[posicion].setTextColor(turnoX ? Color.parseColor("#1976D2") : Color.parseColor("#E57373")); // Azul para X, Rojo para O

        // Verificar estado del juego tras el movimiento
        if (verificarGanador()) {
            tvTurno.setText("¡Ganó " + (turnoX ? "X" : "O") + "!");
            tvTurno.setTextColor(Color.parseColor("#388E3C")); // Verde Victoria
            juegoActivo = false;
            Toast.makeText(this, "¡Victoria!", Toast.LENGTH_SHORT).show();
        } else if (verificarEmpate()) {
            tvTurno.setText("¡Empate!");
            juegoActivo = false;
        } else {
            // Cambiar turno si el juego continua
            turnoX = !turnoX;
            tvTurno.setText("Turno: " + (turnoX ? "X" : "O"));
        }
    }

    /**
     * Comprueba si el jugador actual ha completado una línea ganadora.
     *
     * @return {@code true} si se encuentra una combinación ganadora en el tablero,
     * {@code false} en caso contrario.
     */
    private boolean verificarGanador() {
        for (int[] combo : combinacionesGanadoras) {
            // Comprueba si las 3 casillas de la combinación no son 0 y son iguales entre sí
            if (tablero[combo[0]] != 0 &&
                    tablero[combo[0]] == tablero[combo[1]] &&
                    tablero[combo[0]] == tablero[combo[2]]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si el tablero está lleno y no hay ganador (Empate).
     *
     * @return {@code true} si no quedan casillas vacías (0), {@code false} si aún hay movimientos posibles.
     */
    private boolean verificarEmpate() {
        for (int casilla : tablero) {
            if (casilla == 0) return false; // Todavía hay huecos
        }
        return true; // No hay huecos y nadie ganó
    }

    /**
     * Reinicia el juego a su estado inicial.
     * <p>
     * Limpia el array lógico del tablero, restablece los textos de los botones,
     * pone el turno inicial en X y reactiva el juego.
     * </p>
     */
    private void reiniciarJuego() {
        tablero = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        turnoX = true;
        juegoActivo = true;
        tvTurno.setText("Turno: X");
        tvTurno.setTextColor(Color.GRAY);

        for (Button btn : botones) {
            btn.setText("");
        }
    }
}