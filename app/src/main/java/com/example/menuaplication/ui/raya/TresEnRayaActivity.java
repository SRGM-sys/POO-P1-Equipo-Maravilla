package com.example.menuaplication.ui.raya;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.menuaplication.R;

public class TresEnRayaActivity extends AppCompatActivity {

    // 0: Vacío, 1: X, 2: O
    private int[] tablero = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private boolean turnoX = true; // true = X, false = O
    private boolean juegoActivo = true;

    // Posiciones ganadoras (índices del array)
    private final int[][] combinacionesGanadoras = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Horizontales
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Verticales
            {0, 4, 8}, {2, 4, 6}             // Diagonales
    };

    private Button[] botones = new Button[9];
    private TextView tvTurno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tres_en_raya);

        inicializarVistas();
    }

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

    private void realizarJugada(int posicion) {
        // Si la celda no está vacía o el juego terminó, no hacer nada
        if (tablero[posicion] != 0 || !juegoActivo) return;

        // Marcar lógica
        tablero[posicion] = turnoX ? 1 : 2;

        // Actualizar visual
        botones[posicion].setText(turnoX ? "X" : "O");
        botones[posicion].setTextColor(turnoX ? Color.parseColor("#1976D2") : Color.parseColor("#E57373")); // Azul para X, Rojo para O

        // Verificar estado
        if (verificarGanador()) {
            tvTurno.setText("¡Ganó " + (turnoX ? "X" : "O") + "!");
            tvTurno.setTextColor(Color.parseColor("#388E3C")); // Verde Victoria
            juegoActivo = false;
            Toast.makeText(this, "¡Victoria!", Toast.LENGTH_SHORT).show();
        } else if (verificarEmpate()) {
            tvTurno.setText("¡Empate!");
            juegoActivo = false;
        } else {
            // Cambiar turno
            turnoX = !turnoX;
            tvTurno.setText("Turno: " + (turnoX ? "X" : "O"));
        }
    }

    private boolean verificarGanador() {
        for (int[] combo : combinacionesGanadoras) {
            if (tablero[combo[0]] != 0 &&
                    tablero[combo[0]] == tablero[combo[1]] &&
                    tablero[combo[0]] == tablero[combo[2]]) {
                return true;
            }
        }
        return false;
    }

    private boolean verificarEmpate() {
        for (int casilla : tablero) {
            if (casilla == 0) return false; // Todavía hay huecos
        }
        return true; // No hay huecos y nadie ganó
    }

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