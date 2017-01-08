package com.xontik.ledcontroltest;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xontik on 09/03/2016.
 */
public class BluetoothConnection {
    public static final int INPUTSTREAM = 10;
    public static final int OUTPUTSTREAM = 20;
    public static final int STATECHANGE = 30;
    public static final int CMD = 40;
    public enum STATE{
        DISCONNECTED,CONNECTED,CONNECTING;
    }
   public interface BluetoothListener{
       void onStateChanged(STATE state);
       void onCmdIncome(String cmd);
   }

    private OutputStream _out;
    private InputStream _in;
    private List<BluetoothListener> _listeners;
    private Handler _handler;
    private STATE _state;
    private static BluetoothConnection Instance = new BluetoothConnection();

    public static BluetoothConnection getInstance() {
        return Instance;
    }

    private BluetoothConnection() {
        this._handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==STATECHANGE){
                    changeState((STATE)msg.obj);
                }else if (msg.what==OUTPUTSTREAM){
                    _out = (OutputStream)msg.obj;
                }else if (msg.what==INPUTSTREAM){
                    _in = (InputStream)msg.obj;
                }else if (msg.what==CMD){
                    received((String)msg.obj);
                }

            }
        };
        this._out = null;
        this._in = null;
        this._listeners = new ArrayList<BluetoothListener>();
        this._state = STATE.DISCONNECTED;

    }

    public STATE getState(){
        return this._state;
    }

    public OutputStream getOutputStream() {
        return this._out;
    }
    public InputStream getInputStream() {
        return this._in;
    }
    public void addBluetoothListener(BluetoothListener bl){
        this._listeners.add(bl);
    }
    public void removeBluetoothListener(BluetoothListener bl){
        this._listeners.remove(bl);
    }
    public boolean send(String data){
        if(this._state==STATE.CONNECTED){
            byte[] msgBuffer;
            try {
                msgBuffer = data.getBytes(new String("UTF-8"));
            }catch(IOException e){
                return false;
            }
            Log.d("BUG", "...Sending data: " + Arrays.toString(msgBuffer) + "...");


            try {
                this._out.write(msgBuffer);

            } catch (IOException e) {
                this.changeState(STATE.DISCONNECTED);

                Log.e("BUG", "Fatal Error  "+e.getMessage());
                return false;
            }
        }else{
            this.changeState(STATE.DISCONNECTED);
            return false;
        }
        return true;
    }
    public void connect() {
        BluetoothConnectThread btConnectThread = new BluetoothConnectThread();
        btConnectThread.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btConnectThread.handler = this._handler;
        btConnectThread.start();


    }
    private void receive(){
        BluetoothReceiveThread btReceiveThread = new BluetoothReceiveThread();
        btReceiveThread.inStream = this._in;
        btReceiveThread.handler = this._handler;
        btReceiveThread.start();
        Log.d("BUG","Thread receive created !");
    }
    private void changeState(STATE state){
        this._state = state;
        _listeners.get(_listeners.size()-1).onStateChanged(state);

        if(state == STATE.CONNECTED){
            receive();
        }

    }
    private void received(String msg){
        _listeners.get(_listeners.size()-1).onCmdIncome(msg);

    }

}
