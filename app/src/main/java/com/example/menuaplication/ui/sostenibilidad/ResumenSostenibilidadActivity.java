package com.example.menuaplication.ui.sostenibilidad;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.menuaplication.R;
import com.example.menuaplication.data.RepositorioSostenibilidad;
import com.example.menuaplication.model.sostenibilidad.RegistroSostenibilidad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Actividad principal del módulo de sostenibilidad que visualiza un resumen de los últimos 7 días.
 * Esta clase orquestra la lógica de análisis de datos recuperados del {@link RepositorioSostenibilidad},
 * gestiona la actualización dinámica de la interfaz basada en logros y permite realizar registros
 * rápidos mediante un diálogo emergente.
 *
 * @author erwxn
 * @version 1.1
 */
public class ResumenSostenibilidadActivity extends AppCompatActivity {

    // --- Variables de la Interfaz (Vista) ---
    /** TextView que muestra el periodo de fechas analizado. */
    private TextView tvRangoFechas;

    /** Contadores visuales para cada categoría de acción sostenible. */
    private TextView tvCountTransporte, tvCountImpresiones, tvCountEnvases, tvCountReciclaje;

    /** Etiquetas de estado que cambian de color según el rendimiento (Genial/Bien/Mejorar). */
    private TextView tvEstadoTransporte, tvEstadoImpresiones, tvEstadoEnvases, tvEstadoReciclaje;

    /** Textos informativos sobre días de actividad y días con puntuación perfecta. */
    private TextView tvAnalisisDias, tvAnalisisPerfectos;

    /** Botones de acción para navegación y registro. */
    private Button btnRegistrarHoy, btnVolverMenu;

    // --- Variable de Datos (Modelo) ---
    /** Instancia del repositorio para el acceso a datos persistentes. */
    private RepositorioSostenibilidad repositorio;

    /**
     * Punto de entrada de la actividad. Configura el entorno de la vista y dispara
     * la carga inicial de estadísticas.
     * @param savedInstanceState Estado previo de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_sostenibilidad);

        // 1. Inicializar el "Cerebro" que guarda los datos (Patrón Singleton)
        repositorio = RepositorioSostenibilidad.getInstance(this);

        // 2. Conectar con los elementos visuales del XML
        inicializarVistas();

        // 3. Configurar qué hacen los botones
        configurarListeners();

        // 4. Calcular y mostrar los datos al iniciar
        actualizarResumenUI();
    }

    /**
     * Vincula las variables locales con los componentes definidos en el layout XML.
     */
    private void inicializarVistas() {
        tvRangoFechas = findViewById(R.id.tv_rango_fechas);
        tvAnalisisDias = findViewById(R.id.tv_analisis_dias);
        tvAnalisisPerfectos = findViewById(R.id.tv_analisis_perfectos);
        btnRegistrarHoy = findViewById(R.id.btn_registrar_hoy);
        btnVolverMenu = findViewById(R.id.btn_volver_inicio);

        tvCountTransporte = findViewById(R.id.tv_count_transporte);
        tvEstadoTransporte = findViewById(R.id.tv_estado_transporte);

        tvCountImpresiones = findViewById(R.id.tv_count_impresiones);
        tvEstadoImpresiones = findViewById(R.id.tv_estado_impresiones);

        tvCountEnvases = findViewById(R.id.tv_count_envases);
        tvEstadoEnvases = findViewById(R.id.tv_estado_envases);

        tvCountReciclaje = findViewById(R.id.tv_count_reciclaje);
        tvEstadoReciclaje = findViewById(R.id.tv_estado_reciclaje);
    }

    /**
     * Establece los manejadores de eventos para los botones de la actividad.
     */
    private void configurarListeners() {
        btnVolverMenu.setOnClickListener(v -> finish());
        btnRegistrarHoy.setOnClickListener(v -> mostrarDialogoRegistro());
    }

    /**
     * Realiza el cálculo estadístico de los últimos 7 días y actualiza los componentes visuales.
     * Itera sobre el historial para contar frecuencias de hábitos sostenibles y determina
     * el nivel de logro alcanzado.
     */
    private void actualizarResumenUI() {
        LocalDate hoy = LocalDate.now();
        LocalDate hace6dias = hoy.minusDays(6);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM");
        tvRangoFechas.setText(String.format("(%s - %s)", hace6dias.format(formato), hoy.format(formato)));

        int cTransporte = 0, cImpresiones = 0, cEnvases = 0, cReciclaje = 0;
        int diasConAccion = 0, diasPerfectos = 0;

        // Bucle Mágico: Análisis de la ventana de tiempo de 7 días
        for (int i = 0; i < 7; i++) {
            LocalDate fechaAAnalizar = hace6dias.plusDays(i);
            RegistroSostenibilidad reg = repositorio.obtenerRegistro(fechaAAnalizar);

            if (reg != null) {
                if (reg.isUsoTransporteSostenible()) cTransporte++;
                if (reg.isEvitoImpresiones()) cImpresiones++;
                if (reg.isEvitoEnvasesDescartables()) cEnvases++;
                if (reg.isSeparoResiduos()) cReciclaje++;

                int puntos = reg.getPuntosDia();
                if (puntos > 0) diasConAccion++;
                if (puntos == 4) diasPerfectos++;
            }
        }

        // Reflejar resultados en los TextViews de conteo
        tvCountTransporte.setText(cTransporte + "/7");
        tvCountImpresiones.setText(cImpresiones + "/7");
        tvCountEnvases.setText(cEnvases + "/7");
        tvCountReciclaje.setText(cReciclaje + "/7");

        // Actualización visual de etiquetas mediante lógica de colores
        actualizarColorEstado(tvEstadoTransporte, cTransporte);
        actualizarColorEstado(tvEstadoImpresiones, cImpresiones);
        actualizarColorEstado(tvEstadoEnvases, cEnvases);
        actualizarColorEstado(tvEstadoReciclaje, cReciclaje);

        tvAnalisisDias.setText("• Días activos: " + diasConAccion + " de 7");
        tvAnalisisPerfectos.setText("• Días perfectos: " + diasPerfectos + " de 7");
    }

    /**
     * Cambia el texto y el color de fondo de un TextView basado en la frecuencia de una acción.
     * @param tv El TextView a modificar.
     * @param cantidad Número de veces que se realizó la acción en la semana.
     */
    private void actualizarColorEstado(TextView tv, int cantidad) {
        if (cantidad >= 5) {
            tv.setText("¡Genial!");
            tv.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_green));
        } else if (cantidad >= 3) {
            tv.setText("Bien");
            tv.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_blue));
        } else {
            tv.setText("Mejorar");
            tv.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark));
        }
    }

    /**
     * Despliega un Diálogo (Pop-up) personalizado para registrar las acciones del día actual.
     * Si ya existe un registro hoy, precarga los valores para su edición.
     */
    private void mostrarDialogoRegistro() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_registro_sostenibilidad);

        // Hace que el fondo del diálogo sea transparente para respetar las esquinas redondeadas del XML
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Inicialización de componentes dentro del Diálogo
        TextView tvFechaDialog = dialog.findViewById(R.id.tv_fecha_dialogo);
        CheckBox cbTrans = dialog.findViewById(R.id.cb_transporte);
        CheckBox cbImp = dialog.findViewById(R.id.cb_impresiones);
        CheckBox cbEnv = dialog.findViewById(R.id.cb_envases);
        CheckBox cbRec = dialog.findViewById(R.id.cb_reciclaje);
        Button btnGuardar = dialog.findViewById(R.id.btn_guardar_dialogo);
        Button btnCancelar = dialog.findViewById(R.id.btn_cancelar_dialogo);

        LocalDate hoy = LocalDate.now();
        tvFechaDialog.setText("Registro para: " + hoy.toString());

        // Precarga de datos existentes
        RegistroSostenibilidad actual = repositorio.obtenerRegistro(hoy);
        if (actual != null) {
            cbTrans.setChecked(actual.isUsoTransporteSostenible());
            cbImp.setChecked(actual.isEvitoImpresiones());
            cbEnv.setChecked(actual.isEvitoEnvasesDescartables());
            cbRec.setChecked(actual.isSeparoResiduos());
        }

        // Lógica para persistir la información y refrescar la UI principal
        btnGuardar.setOnClickListener(v -> {
            RegistroSostenibilidad nuevoRegistro = new RegistroSostenibilidad(hoy);
            nuevoRegistro.setUsoTransporteSostenible(cbTrans.isChecked());
            nuevoRegistro.setEvitoImpresiones(cbImp.isChecked());
            nuevoRegistro.setEvitoEnvasesDescartables(cbEnv.isChecked());
            nuevoRegistro.setSeparoResiduos(cbRec.isChecked());

            repositorio.guardarRegistro(nuevoRegistro);
            actualizarResumenUI(); // Refresco inmediato de la tabla al cerrar

            Toast.makeText(this, "¡Registro Guardado!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}