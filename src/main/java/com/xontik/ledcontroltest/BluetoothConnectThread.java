package com.xontik.ledcontroltest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


/**
 * Created by xontik on 08/03/2016.
 */
public class BluetoothConnectThread extends Thread {

    private Set<BluetoothDevice> devices;
    private BluetoothDevice arduino;
    public BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket btSocket;
    private InputStream inStream;
    private OutputStream outStream;
    public Handler handler;
    private Message msg;
    public void run() {
        devices = bluetoothAdapter.getBondedDevices();
        msg = handler.obtainMessage(BluetoothConnection.STATECHANGE, BluetoothConnection.STATE.CONNECTING);
        handler.sendMessage(msg);

        for (BluetoothDevice blueDevice : devices) {
            if (blueDevice.getName().contains("xontik")) {
                arduino = blueDevice;
            }
        }
                if (arduino == null) {
                    Log.e("BUG", "Arduino pas appairé sous le nom xontik.");
                } else {

                    try {
                        btSocket = arduino.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    } catch (IOException e) {
                        Log.e("BUG", " socket create failed: " + e.getMessage() + ".");

                    }
                    Log.d("BUG", "...Connecting to Remote...");

                    try {
                        btSocket.connect();
                        Log.d("BUG", "...Connection established and data link opened...");

                    } catch (IOException e) {
                        try {
                            btSocket.close();
                        } catch (IOException e2) {
                            Log.e("BUG", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                        }
                    }


                    // Create a data stream so we can talk to server.
                    Log.d("BUG", "...Creating Socket...");

                    try {
                        outStream = btSocket.getOutputStream();
                        inStream = btSocket.getInputStream();
                    } catch (IOException e) {
                        Log.e("BUG", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
                    }
                    if(btSocket.isConnected()==false){
                        msg = handler.obtainMessage(BluetoothConnection.STATECHANGE, BluetoothConnection.STATE.DISCONNECTED);
                        handler.sendMessage(msg);
                    }else{
                        msg = handler.obtainMessage(BluetoothConnection.OUTPUTSTREAM,outStream);
                        handler.sendMessage(msg);
                        msg = handler.obtainMessage(BluetoothConnection.INPUTSTREAM,inStream);
                        handler.sendMessage(msg);
                        msg = handler.obtainMessage(BluetoothConnection.STATECHANGE, BluetoothConnection.STATE.CONNECTED);
                        handler.sendMessage(msg);
                    }
                }
    }

}
