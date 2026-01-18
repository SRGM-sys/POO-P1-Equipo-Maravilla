package com.example.menuaplication.ui.buscaminas; // <--- Verifica tu paquete

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
// Asegúrate de importar tu adaptador correctamente
import com.example.menuaplication.ui.buscaminas.BuscaminasAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BuscaminasActivity extends AppCompatActivity {

    private final int FILAS = 10;
    private final int COLUMNAS = 8;
    private final int TOTAL_BOMBAS = 12;

    private RecyclerView rvTablero;
    private TextView tvMinas, tvEstado;
    private Chronometer cronometro;
    private ImageButton btnVolver, btnReiniciar;

    private List<Celda> tablero;
    private BuscaminasAdapter adaptador;
    private boolean juegoTerminado = false;
    private boolean esPrimerMovimiento = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscaminas); // Asegúrate que el XML se llame así

        inicializarVistas();
        iniciarLogicaJuego();
    }

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
        // Botón Regresar: Cierra la actividad actual
        btnVolver.setOnClickListener(v -> {
            cronometro.stop();
            finish();
        });

        // Botón Reiniciar: Recrea la actividad para empezar de cero limpio
        btnReiniciar.setOnClickListener(v -> {
            recreate();
        });
    }

    private void iniciarLogicaJuego() {
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

    private void manejarClick(int posicion) {
        if (juegoTerminado) return;

        // Iniciar tiempo al primer toque
        if (esPrimerMovimiento) {
            cronometro.setBase(SystemClock.elapsedRealtime());
            cronometro.start();
            esPrimerMovimiento = false;
        }

        Celda celda = tablero.get(posicion);
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

    private void manejarLongClick(int posicion) {
        if (juegoTerminado || tablero.get(posicion).isEstaRevelada()) return;

        Celda celda = tablero.get(posicion);
        celda.setEstaMarcada(!celda.isEstaMarcada());
        adaptador.notifyItemChanged(posicion);

        // Actualizar contador visual
        actualizarContadorMinas();
    }

    private void actualizarContadorMinas() {
        int marcadas = 0;
        for (Celda c : tablero) if (c.isEstaMarcada()) marcadas++;
        tvMinas.setText("💣 " + (TOTAL_BOMBAS - marcadas));
    }

    private void revelarVacias(int pos) {
        for (int vecino : getVecinos(pos)) {
            Celda v = tablero.get(vecino);
            if (!v.isEstaRevelada() && !v.isEsBomba()) {
                v.setEstaRevelada(true);
                if (v.getBombasAlrededor() == 0) revelarVacias(vecino);
            }
        }
    }

    private void perderJuego() {
        juegoTerminado = true;
        cronometro.stop();
        tvEstado.setText("¡BOOM! Te atrapó 💀");

        // Revelar todas las bombas
        for (Celda c : tablero) {
            if (c.isEsBomba()) c.setEstaRevelada(true);
        }
        mostrarDialogoFin(false);
    }

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

    private void mostrarDialogoFin(boolean victoria) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(victoria ? "¡VICTORIA! 🍬" : "¡GAME OVER! 💀");
        builder.setMessage((victoria ? "Has escapado del cementerio." : "Un fantasma te ha llevado.")
                + "\nTiempo: " + cronometro.getText());

        builder.setPositiveButton("Jugar de nuevo", (dialog, which) -> recreate()); // Reinicia fácil
        builder.setNegativeButton("Salir", (dialog, which) -> finish());

        builder.show();
    }
}