package com.example.menuaplication.ui.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menuaplication.R;
import com.example.menuaplication.data.RepositorioActividades;
import com.example.menuaplication.model.actividades.Actividad;
import com.example.menuaplication.model.actividades.ActividadAcademica;
import com.example.menuaplication.model.actividades.ActividadPersonal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Actividad principal del módulo que muestra la lista de actividades registradas.
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 * <li>Mostrar el RecyclerView con el {@link ActividadAdapter}.</li>
 * <li>Filtrar actividades por tipo (Todas, Académicas, Personales).</li>
 * <li>Ordenar actividades (Nombre, Fecha, Avance).</li>
 * <li>Filtrar automáticamente las actividades vencidas para que no aparezcan.</li>
 * </ul>
 *
 * @author José Paladines
 * @version 1.0
 */
public class ListaActividadesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ActividadAdapter adapter;
    private Spinner spinnerFiltro, spinnerOrden;
    private Button btnCrear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_actividades);

        // Bindings
        recyclerView = findViewById(R.id.recyclerActividades);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        spinnerOrden = findViewById(R.id.spinnerOrden);
        btnCrear = findViewById(R.id.btnCrear);

        // Botón Back (si lo agregaste al XML)
        ImageButton btnBack = findViewById(R.id.btnBackLista);
        if(btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // --- CONFIGURACIÓN DE SPINNERS VISUALMENTE MEJORADOS ---
        ArrayAdapter<String> filtroAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_custom, new String[]{"Todos", "Académica", "Personal"});
        filtroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(filtroAdapter);

        ArrayAdapter<String> ordenAdapter = new ArrayAdapter<>(this, R.layout.item_spinner_custom, new String[]{"Nombre (A-Z)", "Vencimiento", "Avance"});
        ordenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrden.setAdapter(ordenAdapter);
        // ----------------------------------------------------------

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActividadAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Listeners para los filtros
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { refrescarLista(); }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerOrden.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { refrescarLista(); }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Botón para crear
        btnCrear.setOnClickListener(v -> startActivity(new Intent(this, CrearActividadActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // IMPORTANTE: Refrescar la lista cada vez que volvemos a esta pantalla
        refrescarLista();
    }

    /**
     * Obtiene la lista completa de actividades, aplica las reglas de negocio
     * (eliminar vencidas, filtrar por tipo) y ordena los resultados.
     */
    private void refrescarLista() {
        List<Actividad> todas = RepositorioActividades.getInstance().getListaActividades();
        List<Actividad> filtradas = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        // 1. Filtro
        String filtro = spinnerFiltro.getSelectedItem().toString();

        for (Actividad a : todas) {
            // --- FILTRO DE VENCIMIENTO ---
            // Si la fecha ya pasó y NO está al 100%, la ignoramos (no se agrega a la lista)
            boolean estaVencida = a.getFechaVencimiento().isBefore(ahora) && a.getPorcentajeAvance() < 100;
            if (estaVencida) {
                continue; // Salta a la siguiente iteración del bucle
            }
            // -----------------------------

            if (filtro.equals("Todos")) {
                filtradas.add(a);
            } else if (filtro.equals("Académica")) {
                if (a instanceof ActividadAcademica) filtradas.add(a);
            } else {
                if (a instanceof ActividadPersonal) filtradas.add(a);
            }
        }

        // 2. Ordenamiento
        String orden = spinnerOrden.getSelectedItem().toString();
        if (orden.contains("Nombre")) {
            Collections.sort(filtradas, Comparator.comparing(Actividad::getNombre));
        } else if (orden.contains("Vencimiento")) {
            Collections.sort(filtradas, (a, b) -> a.getFechaVencimiento().compareTo(b.getFechaVencimiento()));
        } else if (orden.contains("Avance")) {
            // De mayor a menor avance
            Collections.sort(filtradas, (a, b) -> Double.compare(b.getPorcentajeAvance(), a.getPorcentajeAvance()));
        }

        // Actualizar el adaptador
        adapter.setActividades(filtradas);
    }
}