package com.example.menuaplication.ui.juego;

import android.os.Bundle;
import android.os.Handler;
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

public class JuegoMemoriaActivity extends AppCompatActivity {

    // Vistas del Layout
    private RecyclerView rvTablero;
    private TextView tvPares;    // Referencia a tu tv_pares
    private TextView tvIntentos; // Referencia a tu tv_intentos
    private ImageButton btnVolver; // Referencia a tu btn_volver_menu

    // Variables lógicas
    private AdaptadorMemoria adaptador;
    private List<TarjetaMemoria> listaTarjetas;
    private int paresEncontrados = 0;
    private int intentos = 0; // Contador de intentos

    private TarjetaMemoria primeraTarjetaSeleccionada = null;
    private boolean turnoBloqueado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_memoria); // Usa TU layout

        inicializarVistas();
        configurarTablero();
        iniciarJuego();
    }

    private void inicializarVistas() {
        // Vinculamos con TUS IDs del XML
        rvTablero = findViewById(R.id.rv_tablero_memoria);
        tvPares = findViewById(R.id.tv_pares);
        tvIntentos = findViewById(R.id.tv_intentos);
        btnVolver = findViewById(R.id.btn_volver_menu);

        // Configurar botón volver
        btnVolver.setOnClickListener(v -> finish()); // Cierra la activity y vuelve al anterior
    }

    private void configurarTablero() {
        // Tu tablero 4x4
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        rvTablero.setLayoutManager(layoutManager);
    }

    private void iniciarJuego() {
        listaTarjetas = new ArrayList<>();

        // Iconos
        int[] imagenes = {
                R.drawable.ic_arbolito,     // Imagen 1
                R.drawable.ic_molino,      // Imagen 2
                R.drawable.ic_reciclaje,     // Imagen 3
                R.drawable.ic_odish,      // Imagen 4
                R.drawable.ic_bicicleta,     // Imagen 5
                R.drawable.ic_aguita,  // Imagen 6
                R.drawable.ic_proyecto,  // Imagen 7
                R.drawable.ic_tarea      // Imagen 8
        };

        for (int img : imagenes) {
            listaTarjetas.add(new TarjetaMemoria(img));
            listaTarjetas.add(new TarjetaMemoria(img));
        }

        Collections.shuffle(listaTarjetas);
        adaptador = new AdaptadorMemoria(listaTarjetas, this::manejarClickTarjeta);
        rvTablero.setAdapter(adaptador);

        // Resetear textos
        actualizarMarcadores();
    }

    private void manejarClickTarjeta(TarjetaMemoria tarjeta, int posicion) {
        if (turnoBloqueado || tarjeta.isEncontrada() || tarjeta.isVolteada()) return;

        tarjeta.setVolteada(true);
        adaptador.notifyItemChanged(posicion);

        if (primeraTarjetaSeleccionada == null) {
            primeraTarjetaSeleccionada = tarjeta;
        } else {
            turnoBloqueado = true;
            intentos++; // Aumentamos intentos al seleccionar la segunda carta
            actualizarMarcadores();
            verificarCoincidencia(primeraTarjetaSeleccionada, tarjeta);
            primeraTarjetaSeleccionada = null;
        }
    }

    private void verificarCoincidencia(TarjetaMemoria t1, TarjetaMemoria t2) {
        if (t1.getImagenResId() == t2.getImagenResId()) {
            t1.setEncontrada(true);
            t2.setEncontrada(true);
            paresEncontrados++;
            actualizarMarcadores();
            turnoBloqueado = false;

            if (paresEncontrados == 8) {
                Toast.makeText(this, "¡Juego Terminado! Intentos: " + intentos, Toast.LENGTH_LONG).show();
            }
        } else {
            new Handler().postDelayed(() -> {
                t1.setVolteada(false);
                t2.setVolteada(false);
                adaptador.notifyDataSetChanged();
                turnoBloqueado = false;
            }, 1000);
        }
    }

    private void actualizarMarcadores() {
        // Actualizamos los textos de tu layout
        tvPares.setText("Pares: " + paresEncontrados + "/8");
        tvIntentos.setText("Intentos: " + intentos);
    }
}