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
 * Created by xontik on 15/03/2016.
 */
public class SnakeActivity extends Activity {
    Button rightButton,leftButton,upButton,downButton,cheatButton,pauseButton;
    BluetoothConnection bluetoothConnection;
    AlertDialog.Builder pauseDial,looseDial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snake_layout);
        rightButton = (Button)findViewById(R.id.rightButton);
        leftButton = (Button)findViewById(R.id.leftButton);
        upButton = (Button)findViewById(R.id.upButton);
        downButton = (Button)findViewById(R.id.downButton);
        cheatButton = (Button)findViewById(R.id.cheatButton);
        pauseButton = (Button)findViewById(R.id.pauseButton);

        bluetoothConnection = BluetoothConnection.getInstance();

        pauseDial = new AlertDialog.Builder(SnakeActivity.this);
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
                SnakeActivity.this.finish();
            }
        });
        pauseDial.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                bluetoothConnection.send("go:");
            }
        });
        looseDial = new AlertDialog.Builder(SnakeActivity.this);
        looseDial.setTitle("Perdu !");
        looseDial.setCancelable(true);
        looseDial.setMessage("Recommencer ou quitter ?");
        /*looseDial.setPositiveButton("Recommencer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bluetoothConnection.send("go:");
            }
        });*/
        looseDial.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SnakeActivity.this.finish();
            }
        });

        looseDial.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                SnakeActivity.this.finish();
            }
        });
        bluetoothConnection.send("snake:");
        bluetoothConnection.addBluetoothListener(new BluetoothConnection.BluetoothListener() {
            @Override
            public void onStateChanged(BluetoothConnection.STATE state) {

                if (state == BluetoothConnection.STATE.DISCONNECTED) {
                    SnakeActivity.this.finish();
                }
            }

            @Override
            public void onCmdIncome(String cmd) {
                if (cmd.contains("loose")) {
                    //looseDial.show();
                    SnakeActivity.this.finish();
                }
                if (cmd.contains("main")) {
                    SnakeActivity.this.finish();
                }
            }
        });
        leftButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("left:");
            }
        });
        downButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("down:");
            }
        });
        rightButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("right:");
            }
        });
        upButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("up:");
            }
        });
        cheatButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SnakeActivity.this, "Cheat pas ! C'est pas bien !", Toast.LENGTH_SHORT).show();
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
