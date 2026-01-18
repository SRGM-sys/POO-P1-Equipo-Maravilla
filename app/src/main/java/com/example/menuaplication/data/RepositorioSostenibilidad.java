package com.example.menuaplication.data;

import android.content.Context;
import com.example.menuaplication.model.sostenibilidad.RegistroSostenibilidad;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RepositorioSostenibilidad {
    private static final String FILE_NAME = "sostenibilidad_data.ser";
    private static RepositorioSostenibilidad instance;
    private List<RegistroSostenibilidad> registros;
    private Context context;

    // Singleton: Una sola instancia para toda la app
    private RepositorioSostenibilidad(Context context) {
        this.context = context;
        this.registros = new ArrayList<>();
        cargarDatos(); // Cargar datos al iniciar
    }

    public static RepositorioSostenibilidad getInstance(Context context) {
        if (instance == null) {
            instance = new RepositorioSostenibilidad(context);
        }
        return instance;
    }

    // Busca si ya hay un registro hoy
    public RegistroSostenibilidad obtenerRegistro(LocalDate fecha) {
        for (RegistroSostenibilidad r : registros) {
            if (r.getFecha().isEqual(fecha)) return r;
        }
        return null;
    }

    // Guardar o Actualizar
    public void guardarRegistro(RegistroSostenibilidad nuevo) {
        RegistroSostenibilidad existente = obtenerRegistro(nuevo.getFecha());
        if (existente != null) registros.remove(existente);

        registros.add(nuevo);
        guardarEnArchivo();
    }

    // --- MANEJO DE ARCHIVOS ---
    private void guardarEnArchivo() {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(registros);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            registros = (ArrayList<RegistroSostenibilidad>) ois.readObject();
        } catch (Exception e) {
            registros = new ArrayList<>();
        }
    }
}