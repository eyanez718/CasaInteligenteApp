package com.example.casainteligente;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class HabitacionesActivity extends AppCompatActivity {
    // Variables para conexión bluetooth
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    // UUID estándar del perfil SPP (para módulos HC-05 / HC-06)
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Dirección MAC del módulo Bluetooth (REEMPLAZAR POR LA REAL)
    private static final String MAC_ADDRESS = "00:11:22:33:44:55";

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

        // Inicializar Bluetooth
        inicializarBluetooth();

        Switch swHabitacion1 = findViewById(R.id.swHabitacion1);
        Switch swHabitacion2 = findViewById(R.id.swHabitacion2);
        Switch swHabitacion3 = findViewById(R.id.swHabitacion3);
        Switch swHabitacion4 = findViewById(R.id.swHabitacion4);
        Switch swHabitacion5 = findViewById(R.id.swHabitacion5);
        Switch swHabitacion6 = findViewById(R.id.swHabitacion6);

        View.OnClickListener switchListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manejarSwitch((Switch) v);
            }
        };

        swHabitacion1.setOnClickListener(switchListener);
        swHabitacion2.setOnClickListener(switchListener);
        swHabitacion3.setOnClickListener(switchListener);
        swHabitacion4.setOnClickListener(switchListener);
        swHabitacion5.setOnClickListener(switchListener);
        swHabitacion6.setOnClickListener(switchListener);
    }

    // Inicializa conexión Bluetooth en un hilo separado
    private void inicializarBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_LONG).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth apagado. Actívalo.", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            try {
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
                bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();

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
                runOnUiThread(() ->
                        Toast.makeText(this, "Conectado a " + device.getName(), Toast.LENGTH_SHORT).show()
                );
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al conectar Bluetooth", Toast.LENGTH_LONG).show()
                );
                e.printStackTrace();
            }
        }).start();
    }

    // Maneja los eventos de los switchs
    private void manejarSwitch(Switch sw) {
        int id = sw.getId();
        String mensaje;
        String comandoBT = "";

        if (id == R.id.swHabitacion1) {
            if (sw.isChecked()) {
                mensaje = "¡Habitación 1 encendida!";
                comandoBT = "11";
            } else {
                mensaje = "¡Habitación 1 apagada!";
                comandoBT = "10";
            }
        } else if (id == R.id.swHabitacion2) {
            if (sw.isChecked()) {
                mensaje = "¡Habitación 2 encendida!";
                comandoBT = "21";
            } else {
                mensaje = "¡Habitación 2 apagada!";
                comandoBT = "20";
            }
        } else if (id == R.id.swHabitacion3) {
            if (sw.isChecked()) {
                mensaje = "¡Habitación 3 encendida!";
                comandoBT = "31";
            } else {
                mensaje = "¡Habitación 3 apagada!";
                comandoBT = "30";
            }
        } else if (id == R.id.swHabitacion4) {
            if (sw.isChecked()) {
                mensaje = "¡Habitación 4 encendida!";
                comandoBT = "41";
            } else {
                mensaje = "¡Habitación 4 apagada!";
                comandoBT = "40";
            }
        } else if (id == R.id.swHabitacion5) {
            if (sw.isChecked()) {
                mensaje = "¡Habitación 5 encendida!";
                comandoBT = "51";
            } else {
                mensaje = "¡Habitación 5 apagada!";
                comandoBT = "50";
            }
        } else if (id == R.id.swHabitacion6) {
            if (sw.isChecked()) {
                mensaje = "¡Habitación 6 encendida!";
                comandoBT = "61";
            } else {
                mensaje = "¡Habitación 6 apagada!";
                comandoBT = "60";
            }
        } else {
            mensaje = "Acción desconocida";
        }

        // Mostrar el mensaje avisando de la acción
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();

        // Enviar comando por Bluetooth
        enviarComandoBT(comandoBT);
    }

    // Envia un comando por Bluetooth
    private void enviarComandoBT(String comando) {
        if (outputStream == null) {
            Toast.makeText(this, "Bluetooth no conectado", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                outputStream.write(comando.getBytes());
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error al enviar comando", Toast.LENGTH_SHORT).show()
                );
                e.printStackTrace();
            }
        }).start();
    }

    // Cierra conexión al salir
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (outputStream != null) outputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}