package com.example.menuaplication.ui.sostenibilidad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.menuaplication.R;
import com.example.menuaplication.ui.menu.MainActivity;

/**
 * Actividad que presenta el resumen de sostenibilidad ("Resumen Eco") al usuario.
 * Esta clase gestiona la interfaz visual final del módulo de sostenibilidad y proporciona
 * la navegación para regresar al menú principal de la aplicación. Se encarga de limpiar
 * la pila de actividades para evitar navegaciones circulares no deseadas.
 *
 * @author erwxn
 * @version 1.0
 */
public class ResumenSostenibilidadActivity extends AppCompatActivity {

    /**
     * Método de retrollamada (callback) que se invoca cuando el sistema crea la actividad.
     * Aquí se inicializa la interfaz de usuario, se configura la barra de acción (ActionBar)
     * y se establecen los escuchadores de eventos para los botones de navegación.
     *
     * @param savedInstanceState Si la actividad se reinicia, este Bundle contiene los datos
     * que suministró más recientemente en onSaveInstanceState.
     * De lo contrario, es nulo.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_sostenibilidad);

        // Mentor Tip: Habilitar el botón "Atrás" en la barra superior si existe
        // Configuración de la ActionBar para mostrar el título y la flecha de retorno.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Resumen Eco");
        }

        // Encontrar el botón (usando el ID nuevo y estandarizado)
        // Se obtiene la referencia al componente visual del botón de retorno.
        Button btnVolverInicio = findViewById(R.id.btn_volver_inicio);

        // Configurar el click
        // Define la lógica de navegación al presionar el botón "Volver al Inicio".
        btnVolverInicio.setOnClickListener(v -> {
            // Intent explícito para volver al menú principal (MainActivity).
            Intent intent = new Intent(ResumenSostenibilidadActivity.this, MainActivity.class);

            // Flags para limpiar la pila de actividades (para que no puedas volver atrás al resumen).
            // FLAG_ACTIVITY_CLEAR_TOP: Si la actividad ya está en la pila, elimina las que están encima.
            // FLAG_ACTIVITY_NEW_TASK: Inicia la actividad en una nueva tarea si es necesario.
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
            finish(); // Cierra esta actividad para liberar recursos y sacarla de la pila.
        });
    }

    /**
     * Este método se llama cuando el usuario presiona el botón de "Atrás" (la flecha)
     * en la barra de acción superior.
     * Delega la acción al comportamiento predeterminado del botón físico de atrás (onBackPressed),
     * permitiendo una experiencia de usuario consistente.
     *
     * @return true para indicar que el evento de navegación hacia arriba ha sido consumido.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}