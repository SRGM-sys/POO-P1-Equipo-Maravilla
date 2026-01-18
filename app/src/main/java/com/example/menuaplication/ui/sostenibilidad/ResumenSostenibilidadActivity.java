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

public class ResumenSostenibilidadActivity extends AppCompatActivity {

    // --- Variables de la Interfaz (Vista) ---
    private TextView tvRangoFechas;
    // Contadores (Columna "Veces")
    private TextView tvCountTransporte, tvCountImpresiones, tvCountEnvases;
    // Estados (Columna "Logro/Estado")
    private TextView tvEstadoTransporte, tvEstadoImpresiones, tvEstadoEnvases;
    // Análisis inferior
    private TextView tvAnalisisDias, tvAnalisisPerfectos;
    // Botones
    private Button btnRegistrarHoy, btnVolverMenu;

    // --- Variable de Datos (Modelo) ---
    private RepositorioSostenibilidad repositorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_sostenibilidad);

        // 1. Inicializar el "Cerebro" que guarda los datos
        repositorio = RepositorioSostenibilidad.getInstance(this);

        // 2. Conectar con los elementos visuales del XML
        inicializarVistas();

        // 3. Configurar qué hacen los botones
        configurarListeners();

        // 4. Calcular y mostrar los datos al iniciar
        actualizarResumenUI();
    }

    private void inicializarVistas() {
        // Asegúrate de que estos IDs existan en tu activity_resumen_sostenibilidad.xml
        tvRangoFechas = findViewById(R.id.tv_rango_fechas);

        // Tabla: Contadores Numéricos
        tvCountTransporte = findViewById(R.id.tv_count_transporte);
        tvCountImpresiones = findViewById(R.id.tv_count_impresiones);
        tvCountEnvases = findViewById(R.id.tv_count_envases);

        // Tabla: Etiquetas de Estado (Bien/Mal)
        tvEstadoTransporte = findViewById(R.id.tv_estado_transporte);
        tvEstadoImpresiones = findViewById(R.id.tv_estado_impresiones);
        tvEstadoEnvases = findViewById(R.id.tv_estado_envases);

        // Tarjeta de Análisis (Abajo)
        tvAnalisisDias = findViewById(R.id.tv_analisis_dias);
        tvAnalisisPerfectos = findViewById(R.id.tv_analisis_perfectos);

        // Botones
        btnRegistrarHoy = findViewById(R.id.btn_registrar_hoy); // Botón principal
        btnVolverMenu = findViewById(R.id.btn_volver_inicio);
    }

    private void configurarListeners() {
        // Botón Volver: Cierra la pantalla y regresa al menú
        btnVolverMenu.setOnClickListener(v -> finish());

        // Botón Registrar: ABRE LA VENTANA EMERGENTE (Requisito 3)
        btnRegistrarHoy.setOnClickListener(v -> mostrarDialogoRegistro());
    }

    /**
     * LÓGICA MAESTRA (Requisito 1):
     * Calcula estadísticas de los últimos 7 días en Tiempo Real
     */
    private void actualizarResumenUI() {
        LocalDate hoy = LocalDate.now();
        LocalDate hace6dias = hoy.minusDays(6); // Rango de 7 días (hoy + 6 atrás)

        // 1. Mostrar Rango de Fechas
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM");
        tvRangoFechas.setText(String.format("(%s - %s)", hace6dias.format(formato), hoy.format(formato)));

        // Variables para contar
        int cTransporte = 0;
        int cImpresiones = 0;
        int cEnvases = 0;
        int diasConAccion = 0;
        int diasPerfectos = 0;

        // 2. Bucle Mágico: Recorrer los últimos 7 días
        for (int i = 0; i < 7; i++) {
            LocalDate fechaAAnalizar = hace6dias.plusDays(i);
            // Pedimos al repositorio el registro de esa fecha
            RegistroSostenibilidad reg = repositorio.obtenerRegistro(fechaAAnalizar);

            if (reg != null) {
                // Si existe registro, sumamos los contadores
                if (reg.isUsoTransporteSostenible()) cTransporte++;
                if (reg.isEvitoImpresiones()) cImpresiones++;
                if (reg.isEvitoEnvasesDescartables()) cEnvases++;

                // Analizar desempeño del día
                int puntos = reg.getPuntosDia();
                if (puntos > 0) diasConAccion++;
                if (puntos == 4) diasPerfectos++; // 4 es el máximo de acciones
            }
        }

        // 3. Poner los datos en la pantalla
        tvCountTransporte.setText(cTransporte + "/7");
        tvCountImpresiones.setText(cImpresiones + "/7");
        tvCountEnvases.setText(cEnvases + "/7");

        // 4. Colorear etiquetas según desempeño
        actualizarColorEstado(tvEstadoTransporte, cTransporte);
        actualizarColorEstado(tvEstadoImpresiones, cImpresiones);
        actualizarColorEstado(tvEstadoEnvases, cEnvases);

        // 5. Textos de Análisis
        tvAnalisisDias.setText("• Días activos: " + diasConAccion + " de 7");
        tvAnalisisPerfectos.setText("• Días perfectos: " + diasPerfectos + " de 7");
    }

    // Helper para cambiar colores (Verde si > 4, Naranja si < 3)
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
     * VENTANA EMERGENTE (Requisito 3):
     * Muestra el Dialog para registrar datos sin salir de la pantalla
     */
    private void mostrarDialogoRegistro() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_registro_sostenibilidad);

        // Fondo transparente para que se vean los bordes redondos del CardView
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // --- Elementos del Dialog ---
        TextView tvFechaDialog = dialog.findViewById(R.id.tv_fecha_dialogo);
        CheckBox cbTrans = dialog.findViewById(R.id.cb_transporte);
        CheckBox cbImp = dialog.findViewById(R.id.cb_impresiones);
        CheckBox cbEnv = dialog.findViewById(R.id.cb_envases);
        CheckBox cbRec = dialog.findViewById(R.id.cb_reciclaje);
        Button btnGuardar = dialog.findViewById(R.id.btn_guardar_dialogo);
        Button btnCancelar = dialog.findViewById(R.id.btn_cancelar_dialogo);

        // Fecha actual
        LocalDate hoy = LocalDate.now();
        tvFechaDialog.setText("Registro para: " + hoy.toString());

        // Cargar datos previos si existen (Para poder editar)
        RegistroSostenibilidad actual = repositorio.obtenerRegistro(hoy);
        if (actual != null) {
            cbTrans.setChecked(actual.isUsoTransporteSostenible());
            cbImp.setChecked(actual.isEvitoImpresiones());
            cbEnv.setChecked(actual.isEvitoEnvasesDescartables());
            cbRec.setChecked(actual.isSeparoResiduos());
        }

        // --- Botón Guardar del Dialog ---
        btnGuardar.setOnClickListener(v -> {
            // Crear o Actualizar Objeto
            RegistroSostenibilidad nuevoRegistro = new RegistroSostenibilidad(hoy);
            nuevoRegistro.setUsoTransporteSostenible(cbTrans.isChecked());
            nuevoRegistro.setEvitoImpresiones(cbImp.isChecked());
            nuevoRegistro.setEvitoEnvasesDescartables(cbEnv.isChecked());
            nuevoRegistro.setSeparoResiduos(cbRec.isChecked());

            // Guardar en Archivo (Requisito 4)
            repositorio.guardarRegistro(nuevoRegistro);

            // Refrescar la pantalla de atrás INMEDIATAMENTE
            actualizarResumenUI();

            Toast.makeText(this, "¡Registro Guardado!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}