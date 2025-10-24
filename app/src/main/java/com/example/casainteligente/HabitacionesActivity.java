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

import com.example.casainteligente.bluetooth.BluetoothHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class HabitacionesActivity extends AppCompatActivity {
    private BluetoothHelper btHelper;

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

        btHelper = BluetoothHelper.getInstance();

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

    // Maneja los eventos de los switchs
    private void manejarSwitch(Switch sw) {
        if (!btHelper.isConnected()) {
            Toast.makeText(this, "Bluetooth no conectado", Toast.LENGTH_SHORT).show();
            return;
        }

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
        btHelper.sendCommand(comandoBT);
    }
}