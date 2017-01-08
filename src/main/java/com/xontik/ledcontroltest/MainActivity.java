package com.xontik.ledcontroltest;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;


public class MainActivity extends Activity {

    BluetoothAdapter bluetoothAdapter;
    BluetoothConnection bluetoothConnection;
    ToggleButton btToggle;
    Button connectionButton, redButton,greenButton,blueButton,simpleEffectButton,ledButton,pongButton,snakeButton,tetrisButton, drawButton,offButton,sender;
    EditText xNumber, yNumber, cmdEdit;
    BluetoothConnection.BluetoothListener btListener;
    Spinner colorSpinner;
    TextView stateTextView;
    RelativeLayout panel;
    ArrayList<String> colors;
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sender = (Button) findViewById(R.id.sender);
        btToggle = (ToggleButton) findViewById(R.id.btToggle);
        connectionButton = (Button)findViewById(R.id.connectionButton);
        redButton = (Button)findViewById(R.id.redButton);
        blueButton = (Button)findViewById(R.id.blueButton);
        greenButton = (Button)findViewById(R.id.greenButton);
        simpleEffectButton = (Button)findViewById(R.id.simpleEffectButton);
        ledButton = (Button)findViewById(R.id.ledButton);
        pongButton = (Button)findViewById(R.id.pongButton);
        snakeButton = (Button)findViewById(R.id.snakeButton);
        offButton = (Button)findViewById(R.id.offButton);
        tetrisButton = (Button)findViewById(R.id.tetrisButton);
        drawButton = (Button)findViewById(R.id.drawButton);

        colorSpinner = (Spinner)findViewById(R.id.colorSpinner);

        stateTextView = (TextView)findViewById(R.id.stateTextView);
        xNumber = (EditText) findViewById(R.id.xNumber);
        yNumber = (EditText) findViewById(R.id.yNumber);

        panel = (RelativeLayout)findViewById(R.id.panel);
        //----------------------------
        //panel.setVisibility(View.INVISIBLE);
        cmdEdit = (EditText) findViewById(R.id.cmdEdit);


        IntentFilter filter = new IntentFilter(bluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiverChange, filter);
        colors = new ArrayList<String>();
        colors.add("Black");
        colors.add("Cyan");
        colors.add("Purple");
        colors.add("Yellow");
        colors.add("Pink");
        colors.add("Blue");
        colors.add("Green");
        colors.add("Red");
        colors.add("White");



        arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,colors);
        colorSpinner.setAdapter(arrayAdapter);

        btToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                    }
                } else {
                    if (bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.disable();
                    }
                    panel.setVisibility(View.INVISIBLE);
                    connectionButton.setEnabled(false);
                }
            }
        });
        bluetoothConnection = BluetoothConnection.getInstance();
        btListener = new BluetoothConnection.BluetoothListener() {
            @Override
            public void onStateChanged(BluetoothConnection.STATE state) {
                if(state==BluetoothConnection.STATE.CONNECTING){
                    stateTextView.setText("Connecting ...");
                    connectionButton.setEnabled(false);
                    panel.setVisibility(View.INVISIBLE);
                }else if(state==BluetoothConnection.STATE.CONNECTED){
                    connectionButton.setEnabled(false);
                    stateTextView.setText("Connected");
                    panel.setVisibility(View.VISIBLE);
                }else if(state==BluetoothConnection.STATE.DISCONNECTED){
                    connectionButton.setEnabled(true);
                    stateTextView.setText("Disconnected");
                    panel.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this,"Disconnected from the Matrix !",Toast.LENGTH_SHORT);

                }
            }

            @Override
            public void onCmdIncome(String cmd) {

                Toast.makeText(MainActivity.this,cmd,Toast.LENGTH_SHORT).show();
            }
        };
        bluetoothConnection.addBluetoothListener(btListener);
        connectionButton.setOnClickListener(new CompoundButton.OnClickListener() {
            public void onClick(View v) {
                bluetoothConnection.connect();
            }
        });
        redButton.setOnClickListener(new CompoundButton.OnClickListener() {
            public void onClick(View v) {
                bluetoothConnection.send("red:");
            }
        });
        blueButton.setOnClickListener(new CompoundButton.OnClickListener() {
            public void onClick(View v) {
                bluetoothConnection.send("blue:");
            }
        });
         greenButton.setOnClickListener(new CompoundButton.OnClickListener() {
             public void onClick(View v) {
                 bluetoothConnection.send("green:");
             }
         });
         simpleEffectButton.setOnClickListener(new CompoundButton.OnClickListener() {
             public void onClick(View v) {
                 bluetoothConnection.send("simple:");
             }
         });
         ledButton.setOnClickListener(new CompoundButton.OnClickListener() {
             public void onClick(View v) {
                 try {
                     if (Integer.parseInt(xNumber.getText().toString()) < 0 &&
                             Integer.parseInt(xNumber.getText().toString()) > 19 &&
                             Integer.parseInt(xNumber.getText().toString()) < 0 &&
                             Integer.parseInt(xNumber.getText().toString()) > 19) {
                         Toast.makeText(MainActivity.this, "Mauvaises coordonnées ! " + colorSpinner.getSelectedItemId(), Toast.LENGTH_SHORT).show();
                     } else {
                         String x = xNumber.getText().toString();
                         String y = yNumber.getText().toString();
                         if(Integer.parseInt(x)<10){
                             x = "0" + x;
                         }
                         if(Integer.parseInt(y)<10){
                             y = "0" + y;
                         }

                         bluetoothConnection.send("led" + x + y + colorSpinner.getSelectedItemId()+ ":");
                     }
                 }catch(NumberFormatException e){
                     Toast.makeText(MainActivity.this, "Mauvaises coordonnées ! ", Toast.LENGTH_SHORT).show();

                 }
             }
         });
         pongButton.setOnClickListener(new CompoundButton.OnClickListener() {
             public void onClick(View v) {

                 Intent intent = new Intent(MainActivity.this,PongActivity.class);

                 startActivity(intent);


             }
         });
         snakeButton.setOnClickListener(new CompoundButton.OnClickListener(){
             public void onClick(View v){
                 Intent intent = new Intent(MainActivity.this,SnakeActivity.class);

                 startActivity(intent);

             }
         });
        tetrisButton.setOnClickListener(new CompoundButton.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,TetrisActivity.class);

                startActivity(intent);
            }
        });
        drawButton.setOnClickListener(new CompoundButton.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,DrawActivity.class);

                startActivity(intent);
            }
        });
         offButton.setOnClickListener(new CompoundButton.OnClickListener() {
             public void onClick(View v) {
                 bluetoothConnection.send("off:");
             }
         });
        sender.setOnClickListener(new CompoundButton.OnClickListener() {
            public void onClick(View v) {
                bluetoothConnection.send(cmdEdit.getText().toString()+":");
            }
        });


        if (bluetoothAdapter.isEnabled()) {
            btToggle.setChecked(true);
            connectionButton.setEnabled(true);
        }else{
            btToggle.setChecked(false);
            connectionButton.setEnabled(false);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    public void onStop() {
        super.onStop();


    }






    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiverChange);

    }

    private final BroadcastReceiver receiverChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {

                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        btToggle.setChecked(false);
                        connectionButton.setEnabled(false);

                        bluetoothAdapter.cancelDiscovery();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        connectionButton.setEnabled(false);
                        bluetoothAdapter.cancelDiscovery();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        btToggle.setChecked(true);
                        connectionButton.setEnabled(true);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        connectionButton.setEnabled(false);
                        break;
                }
            }
        }
    };
}
