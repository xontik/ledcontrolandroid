package com.xontik.ledcontroltest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

/**
 * Created by xontik on 08/03/2016.
 */

public class PongActivity extends Activity {

    Button upButton,downButton,cheatButton,pauseButton;
    BluetoothConnection bluetoothConnection;
    AlertDialog.Builder pauseDial,looseDial;
    BluetoothConnection.BluetoothListener btListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pong_layout);
        upButton = (Button)findViewById(R.id.upButton);
        downButton = (Button)findViewById(R.id.downButton);
        cheatButton = (Button)findViewById(R.id.cheatButton);
        pauseButton = (Button)findViewById(R.id.pauseButton);
        pauseDial = new AlertDialog.Builder(PongActivity.this);
        pauseDial.setTitle("Pause");
        pauseDial.setCancelable(true);
        pauseDial.setMessage("Continuer ou quitter ?");
        pauseDial.setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bluetoothConnection.send("go:");
            }
        });
        pauseDial.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PongActivity.this.finish();
            }
        });
        pauseDial.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                bluetoothConnection.send("go:");
            }
        });
        looseDial = new AlertDialog.Builder(PongActivity.this);
        looseDial.setTitle("Perdu !");
        looseDial.setCancelable(true);
        looseDial.setMessage("Recommencer ou quitter ?");
        looseDial.setPositiveButton("Recommencer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bluetoothConnection.send("go:");
            }
        });
        looseDial.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PongActivity.this.finish();
            }
        });
        looseDial.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                PongActivity.this.finish();
            }
        });
        bluetoothConnection = BluetoothConnection.getInstance();
        bluetoothConnection.send("pong:");
        btListener = new BluetoothConnection.BluetoothListener() {
            @Override
            public void onStateChanged(BluetoothConnection.STATE state) {

                if (state == BluetoothConnection.STATE.DISCONNECTED) {
                    PongActivity.this.finish();
                }
            }

            @Override
            public void onCmdIncome(String cmd) {
                if (cmd.contains("loose")) {
                    looseDial.show();
                }
                if (cmd.contains("main")) {
                    PongActivity.this.finish();
                }
            }
        };
        bluetoothConnection.addBluetoothListener(btListener);
        upButton.setOnClickListener(new CompoundButton.OnClickListener(){
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("up:");
            }
        });
        downButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("down:");
            }
        });
        cheatButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PongActivity.this, "Cheat pas ! C'est pas bien !", Toast.LENGTH_SHORT).show();
            }
        });
        pauseButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("pause:");
                pauseDial.show();
            }
        });

    }
    @Override
    protected void onDestroy(){
        bluetoothConnection.send("end:");
        super.onDestroy();

    }
}
