package com.example.menuaplication.ui.juego;

import android.os.Bundle;
import android.os.Handler; // Importante para el tiempo de espera al voltear cartas
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.model.juego.TarjetaMemoria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 1. AQUI ARREGLAMOS EL PRIMER ERROR ROJO (Implementar la interfaz)
public class JuegoMemoriaActivity extends AppCompatActivity implements AdaptadorMemoria.OnCartaClickListener {

    // === Variables de la Pantalla (Vistas) ===
    private TextView tvIntentos;
    private TextView tvPares;
    private ImageButton btnVolver;
    private RecyclerView rvTablero;

    // === Variables del Juego ===
    private AdaptadorMemoria adaptador;
    private List<TarjetaMemoria> listaCartas;
    private int contadorIntentos = 0;
    private int contadorPares = 0;
    private final int TOTAL_PARES = 8; // Ajusta esto según cuantas cartas generes

    // Variables para controlar la lógica de pares
    private TarjetaMemoria primeraCarta = null;
    private int posicionPrimeraCarta = -1;
    private boolean turnoBloqueado = false; // Para evitar que toquen mientras se voltean cartas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_memoria); // Asegúrate que este sea el nombre de tu XML

        inicializarVistas();
        configurarTablero();

        // Configurar botón salir
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void inicializarVistas() {
        tvIntentos = findViewById(R.id.tv_intentos);
        tvPares = findViewById(R.id.tv_pares);
        btnVolver = findViewById(R.id.btn_volver_menu);
        rvTablero = findViewById(R.id.rv_tablero_memoria);
    }

    private void configurarTablero() {
        rvTablero.setLayoutManager(new GridLayoutManager(this, 4));

        listaCartas = generarCartas();

        // 2. AQUI ARREGLAMOS EL SEGUNDO ERROR ROJO (El 'this' ahora funciona)
        adaptador = new AdaptadorMemoria(listaCartas, this);
        rvTablero.setAdapter(adaptador);
    }

    // === LÓGICA DE JUEGO ===
    // (Este método se activa AUTOMÁTICAMENTE cuando tocan una carta)
    @Override
    public void onCartaClick(int posicion) {
        // Si el turno está bloqueado (esperando a que se volteen) o la carta ya está lista, ignoramos
        if (turnoBloqueado) return;

        TarjetaMemoria cartaSeleccionada = listaCartas.get(posicion);
        if (cartaSeleccionada.isEstaDescubierta() || cartaSeleccionada.isEstaEmparejada()) {
            return;
        }

        // 1. Voltear la carta seleccionada
        cartaSeleccionada.setEstaDescubierta(true);
        adaptador.notifyItemChanged(posicion);

        // 2. Lógica de comparación
        if (primeraCarta == null) {
            // Es la primera carta que levanta
            primeraCarta = cartaSeleccionada;
            posicionPrimeraCarta = posicion;
        } else {
            // Es la segunda carta, comparamos
            compararCartas(primeraCarta, cartaSeleccionada, posicion);
        }
    }

    private void compararCartas(TarjetaMemoria carta1, TarjetaMemoria carta2, int pos2) {
        turnoBloqueado = true; // Bloqueamos toques extra
        contadorIntentos++;
        tvIntentos.setText("Intentos: " + contadorIntentos);

        if (carta1.getId() == carta2.getId()) {
            // ACIERTO: Son iguales
            carta1.setEstaEmparejada(true);
            carta2.setEstaEmparejada(true);

            contadorPares++;
            tvPares.setText("Pares: " + contadorPares + "/" + TOTAL_PARES);

            primeraCarta = null;
            turnoBloqueado = false; // Desbloqueamos

            if (contadorPares == TOTAL_PARES) {
                Toast.makeText(this, "¡GANASTE! 🎉", Toast.LENGTH_SHORT).show();
            }

        } else {
            // FALLO: Son diferentes
            // Esperamos 1 segundo y las volteamos de nuevo
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carta1.setEstaDescubierta(false);
                    carta2.setEstaDescubierta(false);

                    // Actualizamos solo esas dos cartas
                    adaptador.notifyItemChanged(posicionPrimeraCarta);
                    adaptador.notifyItemChanged(pos2);

                    primeraCarta = null;
                    turnoBloqueado = false; // Desbloqueamos
                }
            }, 1000);
        }
    }

    // === GENERADOR DE CARTAS (Ejemplo Básico) ===
    private List<TarjetaMemoria> generarCartas() {
        List<TarjetaMemoria> cartas = new ArrayList<>();

        // Aquí deberías añadir tus cartas reales. Ejemplo:
        // Añadimos pares (Id 1, Imagen X) dos veces
        cartas.add(new TarjetaMemoria(1, R.drawable.ic_lumen));
        cartas.add(new TarjetaMemoria(1, R.drawable.ic_lumen));
        cartas.add(new TarjetaMemoria(2, R.drawable.ic_water));
        cartas.add(new TarjetaMemoria(2, R.drawable.ic_water));
        cartas.add(new TarjetaMemoria(3, R.drawable.ic_leaf));
        cartas.add(new TarjetaMemoria(3, R.drawable.ic_leaf));
        cartas.add(new TarjetaMemoria(4, R.drawable.ic_task));
        cartas.add(new TarjetaMemoria(4, R.drawable.ic_task));
        // ... añade hasta tener 16 cartas (8 pares)

        Collections.shuffle(cartas); // Barajar
        return cartas;
    }
}