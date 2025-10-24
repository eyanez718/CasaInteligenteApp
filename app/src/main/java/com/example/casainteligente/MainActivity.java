package com.example.casainteligente;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.casainteligente.bluetooth.BluetoothHelper;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothHelper btHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listaDispositivos = findViewById(R.id.lstDispositivos);
        Button btnConectar = findViewById(R.id.btnConectar);
        Button btnHabitaciones = findViewById(R.id.btnHabitaciones);

        btHelper = BluetoothHelper.getInstance();

        mostrarDispositivosEmparejados(listaDispositivos);

        btnConectar.setOnClickListener(v -> conectarDispositivoSeleccionado());

        btnHabitaciones.setOnClickListener(v -> {
            if (btHelper.isConnected()) {
                startActivity(new Intent(MainActivity.this, HabitacionesActivity.class));
            } else {
                Toast.makeText(this, "Primero conecta un dispositivo Bluetooth", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDispositivosEmparejados(ListView listaDispositivos) {
        var adapter = btHelper.getAdapter();
        if (adapter == null) {
            Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_LONG).show();
            return;
        }

        if (!adapter.isEnabled()) {
            Toast.makeText(this, "Activa el Bluetooth primero", Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> dispositivos = adapter.getBondedDevices();
        ArrayList<String> listaNombres = new ArrayList<>();
        ArrayList<BluetoothDevice> listaObjetos = new ArrayList<>();

        for (BluetoothDevice device : dispositivos) {
            listaNombres.add(device.getName() + "\n" + device.getAddress());
            listaObjetos.add(device);
        }

        ArrayAdapter<String> adaptador =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, listaNombres);
        listaDispositivos.setAdapter(adaptador);
        listaDispositivos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listaDispositivos.setOnItemClickListener((parent, view, position, id) -> {
            btHelper.setDevice(listaObjetos.get(position));
            Toast.makeText(this, "Seleccionado: " + listaObjetos.get(position).getName(), Toast.LENGTH_SHORT).show();
        });
    }

    private void conectarDispositivoSeleccionado() {
        if (btHelper.getDevice() == null) {
            Toast.makeText(this, "Selecciona un dispositivo primero", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                runOnUiThread(() -> Toast.makeText(this, "Permiso BLUETOOTH_CONNECT requerido", Toast.LENGTH_SHORT).show());
                return;
            }

            boolean exito = btHelper.connect();
            runOnUiThread(() -> {
                if (exito)
                    Toast.makeText(this, "Conectado a " + btHelper.getDevice().getName(), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Error al conectar", Toast.LENGTH_LONG).show();
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btHelper.close();
    }
}