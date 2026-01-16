package com.example.menuaplication.ui.juego;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.model.juego.TarjetaMemoria;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JuegoMemoriaActivity extends AppCompatActivity {

    private RecyclerView rvTablero;
    private MaterialButton btnReiniciar;
    private AdaptadorMemoria adaptador;
    private List<TarjetaMemoria> listaTarjetas;

    // Variables de lógica de juego
    private int posicionPrimeraCarta = -1;
    private int posicionSegundaCarta = -1;
    private boolean turnoBloqueado = false; // Evita clics rápidos mientras se valida

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_memoria);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        inicializarVistas();
        iniciarJuego();
    }

    private void inicializarVistas() {
        rvTablero = findViewById(R.id.rv_tablero_juego);
        btnReiniciar = findViewById(R.id.btn_reiniciar_juego);

        // Configurar Grid 4x4
        rvTablero.setLayoutManager(new GridLayoutManager(this, 4));

        btnReiniciar.setOnClickListener(v -> iniciarJuego());
    }

    private void iniciarJuego() {
        listaTarjetas = generarTarjetas();
        Collections.shuffle(listaTarjetas);

        posicionPrimeraCarta = -1;
        posicionSegundaCarta = -1;
        turnoBloqueado = false;

        adaptador = new AdaptadorMemoria(listaTarjetas, this::procesarClicCarta);
        rvTablero.setAdapter(adaptador);
    }

    private List<TarjetaMemoria> generarTarjetas() {
        List<TarjetaMemoria> cartas = new ArrayList<>();
        // Usamos tus iconos actuales para formar los pares
        int[] imagenes = {
                R.drawable.ic_leaf,
                R.drawable.ic_water,
                R.drawable.ic_recycle,
                R.drawable.ic_bus,
                R.drawable.ic_brain,
                R.drawable.ic_task,
                R.drawable.ic_lumen,
                R.drawable.ic_logout // Metáfora: "Salida" de la contaminación
        };

        // Creamos pares (2 de cada imagen)
        int idCount = 0;
        for (int img : imagenes) {
            cartas.add(new TarjetaMemoria(idCount++, img));
            cartas.add(new TarjetaMemoria(idCount++, img));
        }
        return cartas;
    }

    private void procesarClicCarta(int posicion) {
        if (turnoBloqueado) return;

        TarjetaMemoria cartaClickeada = listaTarjetas.get(posicion);

        // Si ya está descubierta o emparejada, ignorar
        if (cartaClickeada.isEstaDescubierta() || cartaClickeada.isEstaEmparejada()) return;

        // Lógica de selección
        if (posicionPrimeraCarta == -1) {
            // Primer clic
            posicionPrimeraCarta = posicion;
            cartaClickeada.setEstaDescubierta(true);
            adaptador.notifyItemChanged(posicion);
        } else {
            // Segundo clic
            if (posicion == posicionPrimeraCarta) return; // Clic en la misma carta

            posicionSegundaCarta = posicion;
            cartaClickeada.setEstaDescubierta(true);
            adaptador.notifyItemChanged(posicion);

            turnoBloqueado = true; // Bloquear inputs
            verificarPareja();
        }
    }

    private void verificarPareja() {
        TarjetaMemoria carta1 = listaTarjetas.get(posicionPrimeraCarta);
        TarjetaMemoria carta2 = listaTarjetas.get(posicionSegundaCarta);

        if (carta1.getImagenRecurso() == carta2.getImagenRecurso()) {
            // ¡Coincidencia!
            carta1.setEstaEmparejada(true);
            carta2.setEstaEmparejada(true);
            resetearSeleccion();
            verificarVictoria();
        } else {
            // No coinciden: Esperar y voltear
            new Handler().postDelayed(() -> {
                carta1.setEstaDescubierta(false);
                carta2.setEstaDescubierta(false);
                adaptador.notifyItemChanged(posicionPrimeraCarta);
                adaptador.notifyItemChanged(posicionSegundaCarta);
                resetearSeleccion();
            }, 1000); // 1 segundo de espera
        }
    }

    private void resetearSeleccion() {
        posicionPrimeraCarta = -1;
        posicionSegundaCarta = -1;
        turnoBloqueado = false;
    }

    private void verificarVictoria() {
        for (TarjetaMemoria t : listaTarjetas) {
            if (!t.isEstaEmparejada()) return;
        }
        Toast.makeText(this, "¡Felicidades! Completaste el Eco Reto", Toast.LENGTH_SHORT).show();
    }
}