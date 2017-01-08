package com.xontik.ledcontroltest;

import android.util.Log;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by xontik on 11/05/2016.
 */
public class DrawView extends View {
    public interface OnSquareChangeListener{
        void onSquareChange(int x, int y, int c);
        void onErase();
        void onLineDraw(int x1, int y1, int x2, int y2, int c);
    }
    private int color;
    private int[][] colors = new int[20][20];
    private OnSquareChangeListener _listener;
    public int nbSquare = 20;
    public int squareSpace,squareSide;
    private int lastx,lasty,tx,ty;
    private boolean linemode =  false;
    private boolean down = false;
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        for(int j = 0; j<20;j++) {
            for (int i = 0; i < 20; i++) {
                colors[i][j] = Color.BLACK;
            }
        }
        squareSpace = 2;
        squareSide = -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(squareSide==-1){
            squareSide = (getWidth()-(nbSquare-1)*squareSpace)/20;
            Log.d("calculated",Integer.toString(squareSide));
        }
        /*for(int i = 0; i < arrayPath.size(); i++)
        {
            canvas.drawPath(arrayPath.get(i), arrayPaint.get(i));
        }*/
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);
        p.setStrokeJoin(Paint.Join.ROUND);

        for(int j = 0; j<20;j++) {
            for (int i = 0; i < 20; i++) {
                p.setColor(colors[i][j]);
                canvas.drawRect(2 + i * (47 + 2), 2 + j * (47 + 2), 2 + i * (47 + 2) + 47, 2 + j * (47 + 2) + 47, p);
            }
        }
        if(linemode&&down) {
            p.setColor(color);
            canvas.drawCircle(2 + lastx * (47 + 2) + (47 / 2), 2 + lasty * (47 + 2) + (47 / 2), 10f, p);
            canvas.drawCircle(2 + tx * (47 + 2) + (47 / 2), 2 + ty * (47 + 2) + (47 / 2), 10f, p);

            canvas.drawRect(2 + lastx * (47 + 2) + (47 / 2), 2, 2 + lastx * (47 + 2) + (47 / 2) + 2, 19 * (47 + 2) + 47, p);
            canvas.drawRect(2,2+lasty*(47+2) + (47 / 2),19*(47+2)+47,2+lasty*(47+2) + (47 / 2)+2,p);
            int tmpx,tmpy,vx,vy;
            tmpx  = tx;
            tmpy  = ty;
            if(lastx!=tx&&lasty!=ty) {
                if (Math.abs(lastx - tx) == Math.abs(lasty - ty)) {
                    vx = (lastx - tx) / (Math.abs(lastx - tx));
                    vy = (lasty - ty) / (Math.abs(lasty - ty));
                    while (tmpx != lastx) {
                        canvas.drawCircle(2 + tmpx * (47 + 2) + (47 / 2), 2 + tmpy * (47 + 2) + (47 / 2), 10f, p);
                        tmpx += vx;
                        tmpy += vy;
                    }

                }
            }


        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        int x = (int) (20 * eventX) / (20 * 49);
        int y = (int) (20 * eventY) / (20 * 49);
        if (linemode) {
            y -= 2;
        }
        Log.d("FUCK", "x" + Integer.toString(x) +
                "lastx" + Integer.toString(lastx) +
                "tx" + Integer.toString(tx) +
                "y" + Integer.toString(y) +
                "lasty" + Integer.toString(lasty) +
                "ty" + Integer.toString(ty));
        if (x >= 0 && x < 20 && y >= 0 && y < 20) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(linemode){
                    down = true;
                }else{
                    if (colors[x][y] != color) {
                        colors[x][y] = color;
                        _listener.onSquareChange(x, y, color);
                    }
                }
                lastx = x;
                lasty = y;
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                down = false;
                if(linemode) {
                    if (!(x == lastx && y == lasty)) {
                        drawLine(x,y,lastx,lasty,color);
                    }
                }else{
                    if (!(x == lastx && y == lasty)) {
                        colors[x][y] = color;
                        drawLine(tx,ty,lastx,lasty,color);
                        lastx = x;
                        lasty = y;
                    }
                }
            }else if(event.getAction() == MotionEvent.ACTION_MOVE){
                if(!linemode) {
                    if (!(x == lastx && y == lasty)) {
                        colors[x][y] = color;
                        if(lastx!=x && lasty!=y){
                            drawLine(tx,ty,lastx,lasty,color);
                            _listener.onSquareChange(x,y,color);
                            lastx = x;
                            lasty = y;
                        }
                    }

                }
            }


        }
        else{
            down = false;
        }
        tx = x;
        ty = y;
        invalidate();
        return true;
    }



    public void setColor(int c){
        color = c;
    }
    public void setLinemode(boolean m){
        linemode = m;
    }
    private void drawLine(int x1, int y1, int x2, int y2, int c){
            if(x1 >= 0 && x1<20 && x2 >= 0 && x2<20 && y1 >= 0 && y1<20 && x2 >= 0 && x2<20) {
                int minx, maxx, miny, maxy;
                minx = Math.min(x1, x2);
                maxx = Math.max(x1, x2);
                miny = Math.min(y1, y2);
                maxy = Math.max(y1, y2);
                if (maxx - minx > 1 || maxy - miny > 1) {
                    if (y1 == y2) {
                        for (int i = minx; i <= maxx; i++) {
                            if (colors[i][y1] != color) {
                                colors[i][y1] = color;
                            }
                        }
                        _listener.onLineDraw(x2, y2, x1, y1, color);

                    } else if (x1 == x2) {
                        for (int i = miny; i <= maxy; i++) {
                            if (colors[x1][i] != color) {
                                colors[x1][i] = color;
                            }
                        }
                        _listener.onLineDraw(x2, y2, x1, y1, color);

                    } else if (maxx - minx == maxy - miny) {
                        if ((x1 > x2 && y1 > y2) || (x1 <= x2 && y1 <= y2)) {
                            for (int i = 0; i <= maxx - minx; i++) {
                                if (colors[minx + i][miny + i] != color) {
                                    colors[minx + i][miny + i] = color;
                                }
                            }
                        } else {
                            for (int i = 0; i <= maxx - minx; i++) {
                                if (colors[maxx - i][miny + i] != color) {
                                    colors[maxx - i][miny + i] = color;
                                }
                            }
                        }
                        _listener.onLineDraw(x2, y2, x1, y1, color);
                    }
                }
            }
    }
    public void eraseAll(){
        for(int j = 0; j<20;j++) {
            for (int i = 0; i < 20; i++) {
                if(colors[i][j] != Color.BLACK){
                    colors[i][j] = Color.BLACK;
                }
            }
        }
        _listener.onErase();
        invalidate();
    }
    public void setOnSquareChangeListener(OnSquareChangeListener listener){
        _listener = listener;
    }


}
/*
public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        int x = (int)(20 * eventX) / (20*49);
        int y = (int) (20 * eventY) / (20 * 49);

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if(x>=0 && x<20 && y>=0 && y<20) {
                lastx = x;
                lasty = y;
                if (colors[x][y] != color && !linemode) {
                    colors[x][y] = color;
                    _listener.onSquareChange(x, y, color);
                }
            }
        }
        else if ((event.getAction() == MotionEvent.ACTION_UP && linemode) || (event.getAction() == MotionEvent.ACTION_MOVE && !linemode) ) {
            if(x>=0 && x<20 && y>=0 && y<20 && !(x==lastx && y==lasty)){
                int minx,maxx,miny,maxy;
                minx = Math.min(x, lastx);
                maxx = Math.max(x, lastx);
                miny = Math.min(y, lasty);
                maxy = Math.max(y,lasty);
                if(maxx-minx>1 || maxy-miny>1) {
                    if (y == lasty) {
                        for (int i = minx; i <= maxx; i++) {
                            if (colors[i][y] != color) {
                                colors[i][y] = color;
                            }
                        }
                        _listener.onLineDraw(lastx, lasty, x, y, color);

                    } else if (x == lastx) {
                        for (int i = miny; i <= maxy; i++) {
                            if (colors[x][i] != color) {
                                colors[x][i] = color;
                            }
                        }
                        _listener.onLineDraw(lastx, lasty, x, y, color);

                    } else if (maxx - minx == maxy - miny) {
                        if ((x > lastx && y > lasty) || (x <= lastx && y <= lasty)) {
                            for (int i = 0; i <= maxx - minx; i++) {
                                if (colors[minx + i][miny + i] != color) {
                                    colors[minx + i][miny + i] = color;
                                }
                            }
                        } else {
                            for (int i = 0; i <= maxx - minx; i++) {
                                Log.d("maxx-i", Integer.toString(maxx - i));
                                if (colors[maxx - i][miny + i] != color) {
                                    colors[maxx - i][miny + i] = color;
                                }
                            }
                        }
                        _listener.onLineDraw(lastx, lasty, x, y, color);
                    }
                    if (!linemode) {
                        lastx = x;
                        lasty = y;

                    }
                }else{
                    if (colors[x][y] != color) {
                        colors[x][y] = color;
                        _listener.onSquareChange(x, y, color);
                    }

                }


            }else if(linemode && x>=0 && x<20 && y>=0 && y<20){
                if (colors[x][y] != color) {
                    colors[x][y] = color;
                    _listener.onSquareChange(x, y, color);
                }
            }
        }

        // Schedules a repaint.
        invalidate();
        return true;
    }
 */