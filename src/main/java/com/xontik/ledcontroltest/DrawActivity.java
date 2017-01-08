package com.xontik.ledcontroltest;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class DrawActivity extends AppCompatActivity {

    ArrayList<String> colors;
    ArrayList<Integer> arraycolors;
    ArrayAdapter<String> arrayAdapter;
    DrawView dview;
    Spinner colorSpinner;
    Button eraseButton,exitButton;
    BluetoothConnection bluetoothConnection;
    ToggleButton linemode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.draw_layout);

        bluetoothConnection = BluetoothConnection.getInstance();

        colorSpinner = (Spinner)findViewById(R.id.colorSpinner);
        dview = (DrawView)findViewById(R.id.view);
        eraseButton = (Button)findViewById(R.id.eraseButton);
        exitButton = (Button)findViewById(R.id.exitButton);
        linemode = (ToggleButton)findViewById(R.id.linemodeToggle);

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
        arraycolors = new ArrayList<Integer>();
        arraycolors.add(Color.BLACK);
        arraycolors.add(Color.CYAN);
        arraycolors.add(Color.rgb(128, 0, 128));
        arraycolors.add(Color.YELLOW);
        arraycolors.add(Color.rgb(255, 192, 203));
        arraycolors.add(Color.BLUE);
        arraycolors.add(Color.GREEN);
        arraycolors.add(Color.RED);
        arraycolors.add(Color.WHITE);
        arrayAdapter = new ArrayAdapter<String>(DrawActivity.this,android.R.layout.simple_list_item_1,colors);
        colorSpinner.setAdapter(arrayAdapter);

        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                dview.setColor(arraycolors.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dview.eraseAll();
                dview.invalidate();
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dview.eraseAll();
                dview.invalidate();
                DrawActivity.this.finish();
            }
        });

        linemode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dview.setLinemode(b);
            }
        });

        dview.setOnSquareChangeListener(new DrawView.OnSquareChangeListener() {
            @Override
            public void onSquareChange(int x, int y, int col) {
                String cmd = "led";
                if(x<10){
                    cmd += "0";
                }
                cmd += Integer.toString(x);
                if(y<10){
                    cmd += "0";
                }
                cmd += Integer.toString(y);

                cmd += "0" + arraycolors.indexOf(col) + ":";
                Log.d("dev", cmd);
                bluetoothConnection.send(cmd);


            }

            @Override
            public void onErase() {
                bluetoothConnection.send("off:");
            }

            @Override
            public void onLineDraw(int x1, int y1, int x2, int y2, int col) {
                String cmd = "line";
                if(x1<10){
                    cmd += "0";
                }
                cmd += Integer.toString(x1);
                if(y1<10){
                    cmd += "0";
                }
                cmd += Integer.toString(y1);
                if(x2<10){
                    cmd += "0";
                }
                cmd += Integer.toString(x2);
                if(y2<10){
                    cmd += "0";
                }
                cmd += Integer.toString(y2);

                cmd += "0" + arraycolors.indexOf(col) + ":";
                Log.d("dev", cmd);
                bluetoothConnection.send(cmd);
            }
        });
    }
}
