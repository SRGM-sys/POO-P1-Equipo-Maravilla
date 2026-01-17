package com.example.menuaplication.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.menuaplication.ui.menu.MainActivity;
import com.example.menuaplication.R;
import com.example.menuaplication.data.RepositorioActividades;

/**
 * Actividad de bienvenida o pantalla de carga (Splash Screen).
 *
 * Esta es la primera pantalla que visualiza el usuario al abrir la aplicación.
 * Sus responsabilidades técnicas y visuales son:
 * - Mostrar la identidad visual de la aplicación (Logo) mientras se carga el sistema.
 * - Forzar la configuración de tema (modo claro) para asegurar consistencia visual.
 * - Inicializar la capa de datos: Crea la instancia del {@link RepositorioActividades}
 * para cargar los datos desde el almacenamiento local antes de que el usuario interactúe con la app.
 * - Gestionar la transición automática hacia el menú principal ({@link MainActivity}) tras un breve retraso.
 *
 * @author SRGM
 * @version 1.0
 */
public class SplashActivity extends AppCompatActivity {

    /** Duración de la pantalla de carga en milisegundos (3 segundos). */
    private static final int SPLASH_DELAY_MS = 3000;

    /**
     * Método de creación de la actividad.
     * Se encarga de:
     * 1. Configurar la UI (layout, ocultar ActionBar, desactivar modo noche).
     * 2. Inicializar el Singleton de {@link RepositorioActividades} pasando el contexto de la aplicación.
     * 3. Programar un {@link Handler} para ejecutar la navegación a la siguiente pantalla después de 3 segundos.
     *
     * @param savedInstanceState Estado guardado de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Forzar modo claro para evitar conflictos de colores en el Splash
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash);

        // Ocultar la barra de acción para que el splash sea "Full Screen" visualmente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // -----------------------------------------------------------
        // INICIALIZACIÓN DE DATOS CRÍTICOS
        // Inicializamos el repositorio aquí para que lea el archivo de datos
        // mientras el usuario ve el logo. Así, al llegar al menú, los datos ya existen.
        // -----------------------------------------------------------
        RepositorioActividades.getInstance(this);

        // Programar la transición a MainActivity después del delay definido
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                // Finalizar SplashActivity para que el usuario no pueda volver a ella con el botón "Atrás"
                finish();
            }
        }, 3000);
    }
}