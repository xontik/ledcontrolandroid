package com.xontik.ledcontroltest;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by xontik on 09/03/2016.
 */
public class BluetoothReceiveThread extends Thread {public static final int STATE = 30;

    public InputStream inStream;
    public Handler handler;
    private Message msg;
    public boolean running = true;
    public void run() {

        int i ;
        char c;
        String cmd ="";
        while(running) {
            try {
                Log.d("BUG", "Tentative lecture sur stream : "+inStream.toString());
                i = inStream.read();
                if (i == -1) {

                } else {
                    c = (char) i;
                    if (c == ':') {
                        msg = handler.obtainMessage(BluetoothConnection.CMD, cmd);
                        handler.sendMessage(msg);
                        cmd ="";
                    } else {
                        cmd += c;
                    }
                }
            } catch (IOException e) {
                Log.d("BUG", "Erreur lecture " + e.getMessage());
                running = false;
                msg = handler.obtainMessage(BluetoothConnection.STATECHANGE, BluetoothConnection.STATE.DISCONNECTED);
                handler.sendMessage(msg);
            }
        }

    }

}
