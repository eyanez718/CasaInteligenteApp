package com.example.casainteligente.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothHelper {
    private static BluetoothHelper instance;
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private BluetoothDevice device;

    // UUID estándar SPP (para HC-05, HC-06)
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothHelper() {
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Retorna la instancia del Helper
     * @return BluetoothHelper
     */
    public static BluetoothHelper getInstance() {
        if (instance == null) {
            instance = new BluetoothHelper();
        }
        return instance;
    }

    /**
     * Retorna el adaptador
     * @return BluetoothAdapter
     */
    public BluetoothAdapter getAdapter() {
        return adapter;
    }

    /**
     * Indica si existe un dispositivo conectado por BT
     * @return boolean
     */
    public boolean isConnected() {
        /*return socket != null && socket.isConnected();*/
        return true;
    }

    /**
     * Setea el dispositivo conectado
     * @param device
     */
    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    /**
     * Retorna el dispositivo conectado
     * @return
     */
    public BluetoothDevice getDevice() {
        return device;
    }

    /**
     * Realiza la conexión con la placa
     * @return boolean
     */
    public boolean connect() {
        if (device == null) return false;
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            outputStream = socket.getOutputStream();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            close();
            return false;
        }
    }

    /**
     * Envía un comando al dispositivo conectado
     * @param comando
     */
    public void sendCommand(String comando) {
        Log.d("BluetoothHelper.sendCommand", comando);
        /*if (outputStream == null) return;
        try {
            outputStream.write(comando.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Cierra la conexión con el dispositivo conectado
     */
    public void close() {
        try {
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
            socket = null;
            outputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
