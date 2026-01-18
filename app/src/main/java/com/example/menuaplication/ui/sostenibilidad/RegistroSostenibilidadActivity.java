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
 * Permite al usuario marcar diversas actividades ecológicas realizadas (transporte,
 * reciclaje, etc.) y calcula la puntuación obtenida en el día. Sirve como punto de
 * entrada para la recolección de datos del módulo de sostenibilidad.
 *
 * @author erwxn
 * @version 1.0
 */
public class RegistroSostenibilidadActivity extends AppCompatActivity {

    /** Checkbox para registrar si se usó transporte sostenible. */
    private CheckBox cbTransporte;
    /** Checkbox para registrar si se evitaron impresiones innecesarias. */
    private CheckBox cbImpresiones;
    /** Checkbox para registrar si se evitó el uso de envases descartables. */
    private CheckBox cbEnvases;
    /** Checkbox para registrar si se realizó separación de residuos. */
    private CheckBox cbReciclaje;
    /** TextView para mostrar la fecha actual del registro. */
    private TextView tvFecha;
    /** Botón para procesar y "guardar" las acciones seleccionadas. */
    private Button btnGuardar;
    /** Botón para navegar hacia la pantalla de resumen. */
    private Button btnVerResumen;
    /** Botón de imagen para regresar a la pantalla anterior. */
    private ImageButton btnVolver;

    /**
     * Inicializa la actividad, infla el diseño XML y prepara los componentes
     * básicos de la interfaz.
     * * @param savedInstanceState Estado previo de la instancia, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_sostenibilidad);

        // Vinculación de componentes visuales
        inicializarVistas();

        // Establece la fecha automática del sistema en el formato estándar
        tvFecha.setText("Fecha: " + LocalDate.now().toString());

        // Configuración de la lógica de interacción
        configurarListeners();
    }

    /**
     * Realiza el enlace (binding) entre las variables de la clase y los componentes
     * definidos en el archivo de diseño XML (layout).
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
     * Configura los escuchadores de eventos (listeners) para todos los elementos
     * interactivos de la pantalla.
     */
    private void configurarListeners() {
        // Cierra la actividad actual para volver atrás
        btnVolver.setOnClickListener(v -> finish());

        /**
         * Lógica del botón Guardar:
         * Crea un objeto de modelo, transfiere el estado de los CheckBoxes y
         * muestra la puntuación calculada mediante un Toast.
         */
        btnGuardar.setOnClickListener(v -> {
            RegistroSostenibilidad registro = new RegistroSostenibilidad(LocalDate.now());
            registro.setUsoTransporteSostenible(cbTransporte.isChecked());
            registro.setEvitoImpresiones(cbImpresiones.isChecked());
            registro.setEvitoEnvasesDescartables(cbEnvases.isChecked());
            registro.setSeparoResiduos(cbReciclaje.isChecked());

            // Obtención de puntos calculados por la lógica de negocio en el modelo
            int puntos = registro.getPuntosDia();
            Toast.makeText(this, "¡Genial! Has ganado " + puntos + " Eco-Puntos hoy.", Toast.LENGTH_LONG).show();


        });

        // Navegación hacia la actividad de Resumen
        btnVerResumen.setOnClickListener(v -> {
            startActivity(new Intent(this, ResumenSostenibilidadActivity.class));
        });
    }
}