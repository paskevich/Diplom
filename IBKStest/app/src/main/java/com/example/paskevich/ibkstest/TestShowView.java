package com.example.paskevich.ibkstest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.IBinder;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

/**
 * Created by paskevich on 24.04.17.
 */

//never used anymore

public class TestShowView extends SurfaceView implements SurfaceHolder.Callback {

    private final int PIXELS_IN_METER = 30;

    private DrawThread drawThread;
    private double[] center;



    public TestShowView(Context context, double[] center){
        super(context);
        this.center = center;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int heigth, int width) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {}
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        drawThread = new DrawThread(surfaceHolder);
        drawThread.setRunning(true);
        drawThread.start();
    }

    //SURFACE VIEW DRAWING THREAD CLASS
    class DrawThread extends Thread {

        private boolean running = false;
        private SurfaceHolder surfaceHolder;

        private Paint p;

        double[] center;

        public DrawThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            p = new Paint();
            this.center = center;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (running) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas == null) {
                        continue;
                    }
                    showMeAll(canvas, center);
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        private void showMeAll(Canvas c, double[] center) {
            c.drawARGB(80, 102, 204, 255);
            p.setColor(Color.RED);
            p.setStrokeWidth(2);

            float cx = 200 + (float)center[0];
            float cy = 200 + (float)center[1];

            c.drawCircle(cx, cy, 10, p);

            p.setColor(Color.BLACK);

            c.drawCircle(200, 200, 10, p);
            c.drawCircle(260, 200, 10, p);
            c.drawCircle(200, 260, 10, p);
        }

    }

}
