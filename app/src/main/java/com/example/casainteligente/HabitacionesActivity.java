package com.example.casainteligente;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.casainteligente.bluetooth.BluetoothHelper;
import com.example.casainteligente.controladores.ControladorHabitaciones;
import com.example.casainteligente.modelos.Habitacion;

import java.util.ArrayList;

public class HabitacionesActivity extends AppCompatActivity {
    private BluetoothHelper btHelper;
    private ControladorHabitaciones controladorHabitaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_habitaciones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Manejo la barra de acciones
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        // Referencio al controlador de habitaciones
        controladorHabitaciones = ControladorHabitaciones.getInstance(this);
        // Referencio a la instancia del bluetooth
        btHelper = BluetoothHelper.getInstance(this);

        LinearLayout layoutHabitaciones = findViewById(R.id.layoutHabitaciones);
        Button btnAgregar = findViewById(R.id.btnAgregarHabitacion);
        Button btnEditar = findViewById(R.id.btnEditarHabitacion);
        Button btnEliminar = findViewById(R.id.btnEliminarHabitacion);

        cargarHabitaciones(layoutHabitaciones);

        btnAgregar.setOnClickListener(v -> mostrarDialogoAgregarHabitacion(layoutHabitaciones));
        btnEditar.setOnClickListener(v -> editarHabitacion(layoutHabitaciones));
        btnEliminar.setOnClickListener(v -> eliminarHabitacion(layoutHabitaciones));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cargarHabitaciones(LinearLayout layoutHabitaciones) {
        layoutHabitaciones.removeAllViews();
        ArrayList<Habitacion> lista = controladorHabitaciones.getListaHabitaciones();

        for (Habitacion h : lista) {
            Switch sw = new Switch(this);
            String txtHabitacion = "(" + h.getId() + ") " + h.getNombre();
            sw.setText(txtHabitacion);
            sw.setChecked(h.getEstado());
            // Aplico margenes a los switchs
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
            // Convierto los 20dp en pixeles
            float densidad = getResources().getDisplayMetrics().density;
            int margenLateral = Math.round(20 * densidad);
            int margenVertical = Math.round(10 * densidad);
            // Aplicar los márgenes izquierdo y derecho
            params.setMargins(margenLateral, margenVertical, margenLateral, margenVertical);
            sw.setLayoutParams(params);
            // Fin de aplicaión de margenes
            sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
                h.setEstado(isChecked);
                controladorHabitaciones.actualizarEstado(h, isChecked);
                btHelper.sendCommand(h.getId() + (isChecked ? "1" : "0"));
                Toast.makeText(this,
                        h.getNombre() + (isChecked ? " encendida" : " apagada"),
                        Toast.LENGTH_SHORT).show();
            });
            layoutHabitaciones.addView(sw);
        }
    }

    private void mostrarDialogoAgregarHabitacion(LinearLayout layoutHabitaciones) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar nueva habitación");

        // Contenedor vertical para los dos EditText
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputId = new EditText(this);
        inputId.setHint("ID numérico (único)");
        inputId.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputId);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre de la habitación");
        inputNombre.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(inputNombre);

        builder.setView(layout);

        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String idText = inputId.getText().toString().trim();
            String nombre = inputNombre.getText().toString().trim();

            if (idText.isEmpty() || nombre.isEmpty()) {
                Toast.makeText(this, "Debes ingresar un ID y un nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = Integer.parseInt(idText);

            if (id < 1 || id > 6) {
                Toast.makeText(this, "El ID debe ser un número entre 1 y 6", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar duplicados
            for (Habitacion h : controladorHabitaciones.getListaHabitaciones()) {
                if (h.getId() == id) {
                    Toast.makeText(this, "Ya existe una habitación con ese ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (h.getNombre().equalsIgnoreCase(nombre)) {
                    Toast.makeText(this, "Ya existe una habitación con ese nombre", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Habitacion nueva = new Habitacion(id, nombre, false);
            controladorHabitaciones.agregarHabitacion(nueva);
            cargarHabitaciones(layoutHabitaciones);
            Toast.makeText(this, "Habitación " + nombre + " agregada", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void editarHabitacion(LinearLayout layoutHabitaciones) {
        ArrayList<Habitacion> lista = controladorHabitaciones.getListaHabitaciones();
        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay habitaciones para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar lista de habitaciones para elegir cuál editar
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            Habitacion h = lista.get(i);
            nombres[i] = h.getId() + " - " + h.getNombre();
        }

        new AlertDialog.Builder(this)
                .setTitle("Selecciona una habitación para editar")
                .setItems(nombres, (dialog, which) -> mostrarDialogoEditarHabitacion(lista.get(which), layoutHabitaciones))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarHabitacion(Habitacion habitacion, LinearLayout layoutHabitaciones) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar habitación");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputId = new EditText(this);
        inputId.setHint("ID numérico (único)");
        inputId.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputId.setText(String.valueOf(habitacion.getId()));
        layout.addView(inputId);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre de la habitación");
        inputNombre.setInputType(InputType.TYPE_CLASS_TEXT);
        inputNombre.setText(habitacion.getNombre());
        layout.addView(inputNombre);

        builder.setView(layout);

        builder.setPositiveButton("Guardar cambios", (dialog, which) -> {
            String idText = inputId.getText().toString().trim();
            String nombre = inputNombre.getText().toString().trim();

            if (idText.isEmpty() || nombre.isEmpty()) {
                Toast.makeText(this, "Debes ingresar un ID y un nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            int nuevoId = Integer.parseInt(idText);

            // Valido que el id sea un número entre 1 y 6
            if (nuevoId < 1 || nuevoId > 6) {
                Toast.makeText(this, "El ID debe ser un número entre 1 y 6", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar duplicados (exceptuando la habitación actual)
            for (Habitacion h : controladorHabitaciones.getListaHabitaciones()) {
                if (h != habitacion) {
                    if (h.getId() == nuevoId) {
                        Toast.makeText(this, "Ya existe una habitación con ese ID", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (h.getNombre().equalsIgnoreCase(nombre)) {
                        Toast.makeText(this, "Ya existe una habitación con ese nombre", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            // Actualizar datos
            habitacion.setId(nuevoId);
            habitacion.setNombre(nombre);
            controladorHabitaciones.guardarCambios();
            cargarHabitaciones(layoutHabitaciones);
            Toast.makeText(this, "Habitación actualizada", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void eliminarHabitacion(LinearLayout layoutHabitaciones) {
        ArrayList<Habitacion> lista = controladorHabitaciones.getListaHabitaciones();
        if (lista.isEmpty()) {
            Toast.makeText(this, "No hay habitaciones para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear lista de nombres para el diálogo
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            nombres[i] = lista.get(i).getNombre();
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar habitación")
                .setItems(nombres, (dialog, which) -> {
                    Habitacion seleccionada = lista.get(which);
                    controladorHabitaciones.eliminarHabitacion(seleccionada);
                    cargarHabitaciones(layoutHabitaciones);
                    Toast.makeText(this, seleccionada.getNombre() + " eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}