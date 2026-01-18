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

/**
 * Actividad principal que controla la lógica del juego de memoria.
 * Gestiona el tablero, el emparejamiento de cartas, el conteo de intentos y el estado del juego.
 *
 * @author TheMatthias
 */
public class JuegoMemoriaActivity extends AppCompatActivity {

    private RecyclerView rvTablero;
    private TextView tvPares;
    private TextView tvIntentos;
    private ImageButton btnVolver;

    private AdaptadorMemoria adaptador;
    private List<TarjetaMemoria> listaTarjetas;
    private int paresEncontrados = 0;
    private int intentos = 0;

    private TarjetaMemoria primeraTarjetaSeleccionada = null;
    private boolean turnoBloqueado = false;


    /**
     * Método llamado al crear la actividad.
     * Configura la interfaz, inicializa las vistas y arranca el juego.
     *
     * @param savedInstanceState Estado guardado de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_memoria);

        inicializarVistas();
        configurarTablero();
        iniciarJuego();
    }


    /**
     * Inicializa los componentes de la interfaz de usuario vinculándolos con sus IDs en el layout XML.
     * También configura el listener para el botón de retroceso.
     */
    private void inicializarVistas() {
        rvTablero = findViewById(R.id.rv_tablero_memoria);
        tvPares = findViewById(R.id.tv_pares);
        tvIntentos = findViewById(R.id.tv_intentos);
        btnVolver = findViewById(R.id.btn_volver_menu);

        btnVolver.setOnClickListener(v -> finish());
    }


    /**
     * Configura el RecyclerView con un GridLayoutManager para mostrar las cartas en una cuadrícula de 4 columnas.
     */
    private void configurarTablero() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        rvTablero.setLayoutManager(layoutManager);
    }


    /**
     * Inicia una nueva partida.
     * Crea la lista de cartas duplicando las imágenes, las baraja aleatoriamente,
     * configura el adaptador y reinicia los marcadores.
     */
    private void iniciarJuego() {
        listaTarjetas = new ArrayList<>();

        int[] imagenes = {
                R.drawable.ic_arbolito,
                R.drawable.ic_molino,
                R.drawable.ic_reciclaje,
                R.drawable.ic_odish,
                R.drawable.ic_bicicleta,
                R.drawable.ic_aguita,
                R.drawable.ic_frutita,
                R.drawable.ic_sakura
        };

        for (int img : imagenes) {
            listaTarjetas.add(new TarjetaMemoria(img));
            listaTarjetas.add(new TarjetaMemoria(img));
        }

        Collections.shuffle(listaTarjetas);
        adaptador = new AdaptadorMemoria(listaTarjetas, this::manejarClickTarjeta);
        rvTablero.setAdapter(adaptador);

        actualizarMarcadores();
    }


    /**
     * Maneja el evento de clic en una carta.
     * Controla la lógica de voltear cartas, bloquear el turno temporalmente y verificar coincidencias.
     *
     * @param tarjeta  La carta seleccionada.
     * @param posicion La posición de la carta en el adaptador.
     */
    private void manejarClickTarjeta(TarjetaMemoria tarjeta, int posicion) {
        if (turnoBloqueado || tarjeta.isEncontrada() || tarjeta.isVolteada()) return;

        tarjeta.setVolteada(true);
        adaptador.notifyItemChanged(posicion);

        if (primeraTarjetaSeleccionada == null) {
            primeraTarjetaSeleccionada = tarjeta;
        } else {
            turnoBloqueado = true;
            intentos++;
            actualizarMarcadores();
            verificarCoincidencia(primeraTarjetaSeleccionada, tarjeta);
            primeraTarjetaSeleccionada = null;
        }
    }


    /**
     * Verifica si dos cartas seleccionadas son idénticas.
     * Si coinciden, las marca como encontradas. Si no, las voltea nuevamente después de un retraso.
     *
     * @param t1 La primera carta seleccionada.
     * @param t2 La segunda carta seleccionada.
     */
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


    /**
     * Actualiza los TextViews de la interfaz con el número actual de pares encontrados e intentos realizados.
     */
    private void actualizarMarcadores() {
        // Actualizamos los textos de tu layout
        tvPares.setText("Pares: " + paresEncontrados + "/8");
        tvIntentos.setText("Intentos: " + intentos);
    }
}