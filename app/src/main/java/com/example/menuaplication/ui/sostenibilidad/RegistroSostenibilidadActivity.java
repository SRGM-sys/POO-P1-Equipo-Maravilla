package com.example.menuaplication.ui.sostenibilidad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.menuaplication.R;
import com.example.menuaplication.model.sostenibilidad.RegistroSostenibilidad;
import java.time.LocalDate;
import com.example.menuaplication.ui.sostenibilidad.ResumenSostenibilidadActivity;

/**
 * Actividad encargada de gestionar el registro diario de acciones sostenibles del usuario.
 * Permite al usuario interactuar con una serie de opciones (CheckBoxes) que representan
 * hábitos ecológicos. La clase procesa estas entradas, genera un objeto de modelo
 * {@link RegistroSostenibilidad} y calcula la recompensa diaria en Eco-Puntos.
 *
 * @author erwxn
 * @version 1.0
 */
public class RegistroSostenibilidadActivity extends AppCompatActivity {

    /** Componentes de selección para las categorías de sostenibilidad. */
    private CheckBox cbTransporte, cbImpresiones, cbEnvases, cbReciclaje;

    /** Etiqueta informativa para mostrar la fecha actual del sistema. */
    private TextView tvFecha;

    /** Botones de acción para procesar el registro y navegar al resumen. */
    private Button btnGuardar, btnVerResumen;

    /** Botón de navegación tipo imagen para cerrar la actividad actual. */
    private ImageButton btnVolver;

    /**
     * Ciclo de vida: Método de creación de la actividad.
     * Establece el contenido visual, vincula los componentes mediante ID y
     * activa los escuchadores de eventos.
     *
     * @param savedInstanceState Contenedor de datos de estado previo.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_sostenibilidad);

        // Vinculación de componentes visuales con el archivo XML
        inicializarVistas();

        // Establece la fecha automática del sistema en el encabezado de la pantalla
        tvFecha.setText("Fecha: " + LocalDate.now().toString());

        // Configuración de la lógica de respuesta a clics
        configurarListeners();
    }

    /**
     * Centraliza la obtención de referencias de los elementos visuales del Layout.
     * Este método mejora la organización del código al separar el inflado de la lógica.
     */
    private void inicializarVistas() {
        cbTransporte = findViewById(R.id.cb_transporte);
        cbImpresiones = findViewById(R.id.cb_impresiones);
        cbEnvases = findViewById(R.id.cb_envases);
        cbReciclaje = findViewById(R.id.cb_reciclaje);
        tvFecha = findViewById(R.id.tv_fecha_actual);
        btnGuardar = findViewById(R.id.btn_guardar_eco);
        btnVerResumen = findViewById(R.id.btn_ver_resumen);
        btnVolver = findViewById(R.id.btn_volver_sost);
    }

    /**
     * Define el comportamiento interactivo de la interfaz.
     * Incluye la lógica para el cierre de pantalla, el procesamiento de datos
     * y la transición hacia la pantalla de resumen semanal.
     */
    private void configurarListeners() {
        // Finaliza la actividad actual para retornar al nivel anterior en la pila
        btnVolver.setOnClickListener(v -> finish());

        /**
         * Lógica del botón Guardar:
         * 1. Instancia un nuevo RegistroSostenibilidad con la fecha de hoy.
         * 2. Mapea el estado de cada CheckBox (boolean) a los atributos del objeto.
         * 3. Invoca la lógica de negocio del modelo para calcular los puntos.
         * 4. Notifica al usuario el resultado mediante un Toast interactivo.
         */
        btnGuardar.setOnClickListener(v -> {
            RegistroSostenibilidad registro = new RegistroSostenibilidad(LocalDate.now());
            registro.setUsoTransporteSostenible(cbTransporte.isChecked());
            registro.setEvitoImpresiones(cbImpresiones.isChecked());
            registro.setEvitoEnvasesDescartables(cbEnvases.isChecked());
            registro.setSeparoResiduos(cbReciclaje.isChecked());

            // Obtención de puntos calculados dinámicamente según las selecciones del usuario
            int puntos = registro.getPuntosDia();
            Toast.makeText(this, "¡Genial! Has ganado " + puntos + " Eco-Puntos hoy.", Toast.LENGTH_LONG).show();
        });

        /**
         * Lógica del botón Ver Resumen:
         * Inicia una transición explícita hacia {@link ResumenSostenibilidadActivity}
         * para visualizar las estadísticas acumuladas.
         */
        btnVerResumen.setOnClickListener(v -> {
            startActivity(new Intent(this, ResumenSostenibilidadActivity.class));
        });
    }
}