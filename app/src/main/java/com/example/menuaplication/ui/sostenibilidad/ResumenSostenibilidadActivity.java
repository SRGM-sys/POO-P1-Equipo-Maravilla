package com.example.menuaplication.ui.sostenibilidad;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.menuaplication.R;
import com.example.menuaplication.data.RepositorioSostenibilidad;
import com.example.menuaplication.model.sostenibilidad.RegistroSostenibilidad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Actividad encargada de visualizar el resumen semanal de las actividades de sostenibilidad.
 * <p>
 * Esta clase gestiona la lógica de presentación de estadísticas, permitiendo navegar entre semanas
 * personalizadas (comenzando el año 2026), visualizar el progreso diario y semanal, y registrar
 * nuevas acciones para días específicos o el actual.
 * </p>
 * <p>
 * El sistema de semanas tiene una lógica personalizada donde la Semana 1 abarca del 1 al 4 de Enero,
 * y las semanas subsiguientes son bloques estándar de 7 días.
 * </p>
 *
 * @author erwxn
 * @version 1.0
 */
public class ResumenSostenibilidadActivity extends AppCompatActivity {

    /** TextViews para mostrar el rango de fechas y el número de la semana actual. */
    private TextView tvRangoFechas, tvNumeroSemana;

    /** TextViews contadores para cada categoría de acción sostenible (Transporte, Impresiones, Envases, Reciclaje). */
    private TextView tvCountTransporte, tvCountImpresiones, tvCountEnvases, tvCountReciclaje;

    /** TextViews que muestran el estado cualitativo (Genial, Bien, Mejorar) de cada categoría. */
    private TextView tvEstadoTransporte, tvEstadoImpresiones, tvEstadoEnvases, tvEstadoReciclaje;

    /** TextViews para el análisis general de días activos y días perfectos con porcentajes. */
    private TextView tvAnalisisDias, tvAnalisisPerfectos;

    /** Botones para registrar un día específico y volver al menú principal. */
    private Button btnRegistrarDia, btnVolverMenu;

    /** Botones de navegación para cambiar entre la semana anterior y la siguiente. */
    private ImageButton btnSemanaAnt, btnSemanaSig;

    /** Instancia del repositorio para acceder a los datos persistentes de sostenibilidad. */
    private RepositorioSostenibilidad repositorio;

    // --- Control de Semanas ---
    /** Índice de la semana que se está visualizando actualmente (1-52). */
    private int semanaActualIndex = 1;

    /** Fecha base para el inicio del cálculo de semanas (1 de Enero de 2026). */
    private final LocalDate FECHA_INICIO_AÑO = LocalDate.of(2026, 1, 1);

    /**
     * Método de creación de la actividad.
     * Inicializa el repositorio, las vistas, los listeners y calcula la semana inicial a mostrar
     * basada en la fecha actual del dispositivo.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_sostenibilidad);

        repositorio = RepositorioSostenibilidad.getInstance(this);
        inicializarVistas();
        configurarListeners();

        // Calcular la semana actual basada en la fecha de hoy
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(FECHA_INICIO_AÑO)) {
            semanaActualIndex = 1;
        } else {
            semanaActualIndex = calcularSemanaDesdeFecha(hoy);
        }

        actualizarResumenSemanal();
    }

    /**
     * Vincula los componentes de la interfaz de usuario definidos en el XML con las variables de la clase.
     */
    private void inicializarVistas() {
        tvRangoFechas = findViewById(R.id.tv_rango_fechas);
        tvNumeroSemana = findViewById(R.id.tv_numero_semana);
        tvAnalisisDias = findViewById(R.id.tv_analisis_dias);
        tvAnalisisPerfectos = findViewById(R.id.tv_analisis_perfectos);

        btnRegistrarDia = findViewById(R.id.btn_registrar_dia);
        btnVolverMenu = findViewById(R.id.btn_volver_inicio);

        btnSemanaAnt = findViewById(R.id.btn_semana_ant);
        btnSemanaSig = findViewById(R.id.btn_semana_sig);

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
     * Configura los manejadores de eventos (Listeners) para los botones de la interfaz.
     * Define la lógica de navegación entre semanas y la apertura del selector de fechas.
     */
    private void configurarListeners() {
        btnVolverMenu.setOnClickListener(v -> finish());

        btnRegistrarDia.setOnClickListener(v -> mostrarDatePicker());

        btnSemanaAnt.setOnClickListener(v -> {
            if (semanaActualIndex > 1) {
                semanaActualIndex--;
                actualizarResumenSemanal();
            }
        });

        btnSemanaSig.setOnClickListener(v -> {
            if (semanaActualIndex < 52) {
                semanaActualIndex++;
                actualizarResumenSemanal();
            }
        });
    }

    /**
     * Muestra un diálogo de selección de fecha (DatePicker).
     * Restringe la fecha mínima al 1 de Enero de 2026. Al seleccionar una fecha,
     * abre el diálogo de registro de actividades para ese día específico.
     */
    private void mostrarDatePicker() {
        LocalDate hoy = LocalDate.now();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate fechaSeleccionada = LocalDate.of(year, month + 1, dayOfMonth);
                    mostrarDialogoRegistro(fechaSeleccionada);
                },
                hoy.getYear(), hoy.getMonthValue() - 1, hoy.getDayOfMonth()
        );
        datePickerDialog.getDatePicker().setMinDate(java.sql.Date.valueOf("2026-01-01").getTime());
        datePickerDialog.show();
    }

    /**
     * Calcula la fecha de inicio de una semana dada según la lógica personalizada del proyecto.
     * <p>
     * Lógica:
     * - Semana 1: Comienza el 1 de Enero de 2026.
     * - Semana N (>1): Comienza el 5 de Enero más (N-2) semanas completas.
     * </p>
     *
     * @param numeroSemana El número de la semana (1-52).
     * @return La fecha de inicio de la semana.
     */
    private LocalDate obtenerInicioSemana(int numeroSemana) {
        if (numeroSemana == 1) return FECHA_INICIO_AÑO;
        return FECHA_INICIO_AÑO.plusDays(4).plusWeeks(numeroSemana - 2);
    }

    /**
     * Calcula la fecha de fin de una semana dada.
     * <p>
     * - Semana 1: Termina el 4 de Enero (duración de 4 días).
     * - Semana N (>1): Termina 6 días después de su fecha de inicio (duración de 7 días).
     * </p>
     *
     * @param numeroSemana El número de la semana (1-52).
     * @return La fecha de fin de la semana.
     */
    private LocalDate obtenerFinSemana(int numeroSemana) {
        if (numeroSemana == 1) return FECHA_INICIO_AÑO.plusDays(3);
        return obtenerInicioSemana(numeroSemana).plusDays(6);
    }

    /**
     * Determina a qué número de semana pertenece una fecha específica.
     *
     * @param fecha La fecha a evaluar.
     * @return El índice de la semana correspondiente (1-52).
     */
    private int calcularSemanaDesdeFecha(LocalDate fecha) {
        if (fecha.isBefore(FECHA_INICIO_AÑO.plusDays(4))) return 1;
        long diasDesde5Enero = ChronoUnit.DAYS.between(FECHA_INICIO_AÑO.plusDays(4), fecha);
        return (int) (diasDesde5Enero / 7) + 2;
    }

    /**
     * Actualiza la interfaz de usuario con el resumen estadístico de la semana seleccionada.
     * <p>
     * Realiza las siguientes operaciones:
     * 1. Determina el rango de fechas de la semana actual.
     * 2. Recupera los registros del repositorio para ese rango.
     * 3. Calcula los totales por categoría (Transporte, Reciclaje, etc.).
     * 4. Calcula los días con actividad y los días perfectos (4 acciones completadas).
     * 5. Actualiza los contadores, estados de color y porcentajes en pantalla.
     * </p>
     */
    private void actualizarResumenSemanal() {
        LocalDate inicioSemana = obtenerInicioSemana(semanaActualIndex);
        LocalDate finSemana = obtenerFinSemana(semanaActualIndex);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd MMM");
        tvNumeroSemana.setText("Semana " + semanaActualIndex);
        tvRangoFechas.setText(String.format("%s - %s (2026)", inicioSemana.format(formato), finSemana.format(formato)));

        List<RegistroSostenibilidad> registrosSemana = repositorio.obtenerRegistrosEnRango(inicioSemana, finSemana);

        int cTransporte = 0, cImpresiones = 0, cEnvases = 0, cReciclaje = 0;
        int diasConAccion = 0, diasPerfectos = 0;
        long diasTotalesSemana = ChronoUnit.DAYS.between(inicioSemana, finSemana) + 1;

        for (RegistroSostenibilidad reg : registrosSemana) {
            if (reg.isUsoTransporteSostenible()) cTransporte++;
            if (reg.isEvitoImpresiones()) cImpresiones++;
            if (reg.isEvitoEnvasesDescartables()) cEnvases++;
            if (reg.isSeparoResiduos()) cReciclaje++;

            int puntos = reg.getPuntosDia();
            if (puntos > 0) diasConAccion++;
            if (puntos == 4) diasPerfectos++;
        }

        String max = "/" + diasTotalesSemana;
        tvCountTransporte.setText(cTransporte + max);
        tvCountImpresiones.setText(cImpresiones + max);
        tvCountEnvases.setText(cEnvases + max);
        tvCountReciclaje.setText(cReciclaje + max);

        actualizarColorEstado(tvEstadoTransporte, cTransporte, (int)diasTotalesSemana);
        actualizarColorEstado(tvEstadoImpresiones, cImpresiones, (int)diasTotalesSemana);
        actualizarColorEstado(tvEstadoEnvases, cEnvases, (int)diasTotalesSemana);
        actualizarColorEstado(tvEstadoReciclaje, cReciclaje, (int)diasTotalesSemana);

        // --- CÁLCULO DEL PORCENTAJE ---
        int porcentajeActivos = 0;
        int porcentajePerfectos = 0;

        if (diasTotalesSemana > 0) {
            porcentajeActivos = (int) ((diasConAccion / (double) diasTotalesSemana) * 100);
            porcentajePerfectos = (int) ((diasPerfectos / (double) diasTotalesSemana) * 100);
        }

        tvAnalisisDias.setText(String.format("• Días activos: %d de %d (%d%%)", diasConAccion, diasTotalesSemana, porcentajeActivos));
        tvAnalisisPerfectos.setText(String.format("• Días perfectos: %d de %d (%d%%)", diasPerfectos, diasTotalesSemana, porcentajePerfectos));
    }

    /**
     * Actualiza el texto y el color de fondo de un indicador de estado basado en el desempeño.
     *
     * @param tv TextView a actualizar.
     * @param cantidad Cantidad de veces que se realizó la acción.
     * @param maximo Cantidad máxima de días en la semana actual (4 o 7).
     */
    private void actualizarColorEstado(TextView tv, int cantidad, int maximo) {
        float porcentaje = (float) cantidad / maximo;

        if (porcentaje >= 0.7) {
            tv.setText("Genial");
            tv.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_green));
        } else if (porcentaje >= 0.4) {
            tv.setText("Bien");
            tv.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_blue));
        } else {
            tv.setText("Mejorar");
            tv.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_orange_dark));
        }
    }

    /**
     * Muestra un diálogo modal para registrar o editar las acciones sostenibles de una fecha específica.
     * Si ya existen datos para esa fecha, el diálogo se precarga con la información existente.
     * Al guardar, se actualiza el repositorio y se refresca la vista del resumen semanal.
     *
     * @param fecha La fecha para la cual se realiza el registro.
     */
    private void mostrarDialogoRegistro(LocalDate fecha) {
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

        tvFechaDialog.setText("Registro para: " + fecha.toString());

        RegistroSostenibilidad actual = repositorio.obtenerRegistro(fecha);
        if (actual != null) {
            cbTrans.setChecked(actual.isUsoTransporteSostenible());
            cbImp.setChecked(actual.isEvitoImpresiones());
            cbEnv.setChecked(actual.isEvitoEnvasesDescartables());
            cbRec.setChecked(actual.isSeparoResiduos());
        }

        btnGuardar.setOnClickListener(v -> {
            RegistroSostenibilidad nuevoRegistro = new RegistroSostenibilidad(fecha);
            nuevoRegistro.setUsoTransporteSostenible(cbTrans.isChecked());
            nuevoRegistro.setEvitoImpresiones(cbImp.isChecked());
            nuevoRegistro.setEvitoEnvasesDescartables(cbEnv.isChecked());
            nuevoRegistro.setSeparoResiduos(cbRec.isChecked());

            repositorio.guardarRegistro(nuevoRegistro);

            LocalDate inicioSemana = obtenerInicioSemana(semanaActualIndex);
            LocalDate finSemana = obtenerFinSemana(semanaActualIndex);

            // Verificar si la fecha editada está dentro de la semana visible para refrescar o cambiar de semana
            if (!fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana)) {
                actualizarResumenSemanal();
            } else {
                semanaActualIndex = calcularSemanaDesdeFecha(fecha);
                actualizarResumenSemanal();
            }

            Toast.makeText(this, "¡Registro Guardado!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}