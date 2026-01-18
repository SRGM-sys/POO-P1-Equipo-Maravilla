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
    private TextView tvCountTransporte, tvCountImpresiones, tvCountEnvases, tvCountReciclaje; // <--- AÑADIDO
    // Estados (Columna "Logro/Estado")
    private TextView tvEstadoTransporte, tvEstadoImpresiones, tvEstadoEnvases, tvEstadoReciclaje; // <--- AÑADIDO
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
        // IDs generales
        tvRangoFechas = findViewById(R.id.tv_rango_fechas);
        tvAnalisisDias = findViewById(R.id.tv_analisis_dias);
        tvAnalisisPerfectos = findViewById(R.id.tv_analisis_perfectos);
        btnRegistrarHoy = findViewById(R.id.btn_registrar_hoy);
        btnVolverMenu = findViewById(R.id.btn_volver_inicio);

        // Tabla: Transporte
        tvCountTransporte = findViewById(R.id.tv_count_transporte);
        tvEstadoTransporte = findViewById(R.id.tv_estado_transporte);

        // Tabla: Impresiones
        tvCountImpresiones = findViewById(R.id.tv_count_impresiones);
        tvEstadoImpresiones = findViewById(R.id.tv_estado_impresiones);

        // Tabla: Envases
        tvCountEnvases = findViewById(R.id.tv_count_envases);
        tvEstadoEnvases = findViewById(R.id.tv_estado_envases);

        // Tabla: Reciclaje (LO QUE FALTABA)
        tvCountReciclaje = findViewById(R.id.tv_count_reciclaje);
        tvEstadoReciclaje = findViewById(R.id.tv_estado_reciclaje);
    }

    private void configurarListeners() {
        btnVolverMenu.setOnClickListener(v -> finish());
        btnRegistrarHoy.setOnClickListener(v -> mostrarDialogoRegistro());
    }

    private void actualizarResumenUI() {
        LocalDate hoy = LocalDate.now();
        LocalDate hace6dias = hoy.minusDays(6);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM");
        tvRangoFechas.setText(String.format("(%s - %s)", hace6dias.format(formato), hoy.format(formato)));

        // Variables para contar
        int cTransporte = 0;
        int cImpresiones = 0;
        int cEnvases = 0;
        int cReciclaje = 0; // <--- AÑADIDO
        int diasConAccion = 0;
        int diasPerfectos = 0;

        // Bucle Mágico: Recorrer los últimos 7 días
        for (int i = 0; i < 7; i++) {
            LocalDate fechaAAnalizar = hace6dias.plusDays(i);
            RegistroSostenibilidad reg = repositorio.obtenerRegistro(fechaAAnalizar);

            if (reg != null) {
                if (reg.isUsoTransporteSostenible()) cTransporte++;
                if (reg.isEvitoImpresiones()) cImpresiones++;
                if (reg.isEvitoEnvasesDescartables()) cEnvases++;
                if (reg.isSeparoResiduos()) cReciclaje++; // <--- AÑADIDO

                int puntos = reg.getPuntosDia();
                if (puntos > 0) diasConAccion++;
                if (puntos == 4) diasPerfectos++;
            }
        }

        // Poner datos en pantalla
        tvCountTransporte.setText(cTransporte + "/7");
        tvCountImpresiones.setText(cImpresiones + "/7");
        tvCountEnvases.setText(cEnvases + "/7");
        tvCountReciclaje.setText(cReciclaje + "/7"); // <--- AÑADIDO

        // Colorear etiquetas
        actualizarColorEstado(tvEstadoTransporte, cTransporte);
        actualizarColorEstado(tvEstadoImpresiones, cImpresiones);
        actualizarColorEstado(tvEstadoEnvases, cEnvases);
        actualizarColorEstado(tvEstadoReciclaje, cReciclaje); // <--- AÑADIDO

        // Textos de Análisis
        tvAnalisisDias.setText("• Días activos: " + diasConAccion + " de 7");
        tvAnalisisPerfectos.setText("• Días perfectos: " + diasPerfectos + " de 7");
    }

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

    private void mostrarDialogoRegistro() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_registro_sostenibilidad);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvFechaDialog = dialog.findViewById(R.id.tv_fecha_dialogo);
        CheckBox cbTrans = dialog.findViewById(R.id.cb_transporte);
        CheckBox cbImp = dialog.findViewById(R.id.cb_impresiones);
        CheckBox cbEnv = dialog.findViewById(R.id.cb_envases);
        CheckBox cbRec = dialog.findViewById(R.id.cb_reciclaje);
        Button btnGuardar = dialog.findViewById(R.id.btn_guardar_dialogo);
        Button btnCancelar = dialog.findViewById(R.id.btn_cancelar_dialogo);

        LocalDate hoy = LocalDate.now();
        tvFechaDialog.setText("Registro para: " + hoy.toString());

        RegistroSostenibilidad actual = repositorio.obtenerRegistro(hoy);
        if (actual != null) {
            cbTrans.setChecked(actual.isUsoTransporteSostenible());
            cbImp.setChecked(actual.isEvitoImpresiones());
            cbEnv.setChecked(actual.isEvitoEnvasesDescartables());
            cbRec.setChecked(actual.isSeparoResiduos());
        }

        btnGuardar.setOnClickListener(v -> {
            RegistroSostenibilidad nuevoRegistro = new RegistroSostenibilidad(hoy);
            nuevoRegistro.setUsoTransporteSostenible(cbTrans.isChecked());
            nuevoRegistro.setEvitoImpresiones(cbImp.isChecked());
            nuevoRegistro.setEvitoEnvasesDescartables(cbEnv.isChecked());
            nuevoRegistro.setSeparoResiduos(cbRec.isChecked());

            repositorio.guardarRegistro(nuevoRegistro);
            actualizarResumenUI();

            Toast.makeText(this, "¡Registro Guardado!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}