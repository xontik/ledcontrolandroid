package com.xontik.ledcontroltest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by xontik on 15/03/2016.
 */
public class TetrisActivity extends Activity {

    Button rightButton,leftButton,rotateButton,downButton,cheatButton,pauseButton;
    BluetoothConnection bluetoothConnection;
    AlertDialog.Builder pauseDial,looseDial;
    BluetoothConnection.BluetoothListener btListener;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tetris_layout);
        rightButton = (Button)findViewById(R.id.rightButton);
        leftButton = (Button)findViewById(R.id.leftButton);
        rotateButton = (Button)findViewById(R.id.rotateButton);
        downButton = (Button)findViewById(R.id.downButton);
        cheatButton = (Button)findViewById(R.id.cheatButton);
        pauseButton = (Button)findViewById(R.id.pauseButton);

        bluetoothConnection = BluetoothConnection.getInstance();
        bluetoothConnection.send("tetris:");

        pauseDial = new AlertDialog.Builder(TetrisActivity.this);
        pauseDial.setTitle("Pause");
        pauseDial.setCancelable(true);
        pauseDial.setMessage("Continuer ou quitter ?");
        pauseDial.setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bluetoothConnection.send("go:");
                mediaPlayer.start();
            }
        });
        pauseDial.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TetrisActivity.this.finish();
            }
        });
        pauseDial.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                bluetoothConnection.send("go:");
            }
        });
        looseDial = new AlertDialog.Builder(TetrisActivity.this);
        looseDial.setTitle("Perdu !");
        looseDial.setCancelable(true);
        looseDial.setMessage("Game over !");
        looseDial.setNegativeButton("Quitter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TetrisActivity.this.finish();
            }
        });
        looseDial.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                TetrisActivity.this.finish();
            }
        });
        bluetoothConnection.send("tetris:");
        btListener = new BluetoothConnection.BluetoothListener() {
            @Override
            public void onStateChanged(BluetoothConnection.STATE state) {

                if (state == BluetoothConnection.STATE.DISCONNECTED) {
                    TetrisActivity.this.finish();
                }
            }

            @Override
            public void onCmdIncome(String cmd) {
                if (cmd.contains("loose")) {
                    looseDial.show();
                    mediaPlayer.reset();
                    AssetFileDescriptor afd = TetrisActivity.this.getResources().openRawResourceFd(R.raw.over);
                    try {
                        mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();
                }
                if (cmd.contains("main")) {
                    TetrisActivity.this.finish();
                }
            }
        };
        bluetoothConnection.addBluetoothListener(btListener);

        mediaPlayer = MediaPlayer.create(this, R.raw.tetris);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        leftButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("l:");
            }
        });
        downButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("b:");
            }
        });
        rightButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("r:");
            }
        });
        rotateButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("t:");
            }
        });
        cheatButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TetrisActivity.this, "Cheat pas ! C'est pas bien !", Toast.LENGTH_SHORT).show();
            }
        });
        pauseButton.setOnClickListener(new CompoundButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothConnection.send("pause:");
                mediaPlayer.pause();
                pauseDial.show();
            }
        });

    }
    @Override
    protected void onDestroy(){
        bluetoothConnection.send("end:");
        mediaPlayer.stop();
        super.onDestroy();

    }
    @Override
    protected void onResume(){
        super.onResume();

    }


}
