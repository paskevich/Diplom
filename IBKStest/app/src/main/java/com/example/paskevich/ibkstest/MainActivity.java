package com.example.paskevich.ibkstest;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private MyService service;
    private boolean bound = false;

    private double x = 0;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MyService.MyBinder myBinder = (MyService.MyBinder) binder;
            service = myBinder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TestShowView(this));
        //setContentView(R.layout.activity_main);
        //takeTextPos();
        //takeTextLog();
    }

    @Override
    protected void onStart(){
        super.onStart();
        requestLocationPermissions();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        //takeTextLog();
        //takeKalmanLog();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(bound){
            unbindService(connection);
            bound = false;
        }
    }

    @TargetApi(23)
    public void requestLocationPermissions(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void takeTextPos() {
        final TextView posView = (TextView) findViewById(R.id.posView);
        final android.os.Handler handler = new android.os.Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Point[] pos = null;
                String[] pos = {};
                String textPos = "";
                if (service != null) {
                    pos = service.getDistances();
                    Log.d("i tried", "get location");
                }
                if (pos.length != 0) {
                    for (int i = 0; i < pos.length; i++) {
                        textPos += pos[i] + '\n';
                    }
                } else {
                    textPos = "CRAP!";
                }
                Log.d("Position: ", textPos);
                posView.setText(textPos);
                handler.postDelayed(this, 2000);
            }
        });

    }

    public void takeTextLog() {
        final TextView posView = (TextView)findViewById(R.id.posView);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(service!=null && service.scannedMyBleDevices.size()!=0) {
                    posView.setText("OK");
                    service.getLevelToLog();
                }
                handler.postDelayed(this, 450);
            }
        });
    }

    public void takeKalmanLog() {
        Log.d("Kalman", "is ready");
        if(service!=null && service.scannedMyBleDevices.size()!=0) {
            setX(service.scannedMyBleDevices.get(0).getAverage());
            Log.d("check", "ok");
        }

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("KALMAN", "started");
                if(service!=null && service.scannedMyBleDevices.size()!=0) {
                    setX(service.getNextKalman(getX()));
                }
                Log.d("KALMAN", Double.toString(getX()));
                handler.postDelayed(this, 1000);
            }
        });
    }

    public synchronized double getX() {
        return x;
    }

    public synchronized void setX(double x) {
        this.x = x;
    }

    //
    //I'm sorry for this :c
    //

    class TestShowView extends SurfaceView implements SurfaceHolder.Callback {

        private final int PIXELS_IN_METER = 60;

        private DrawThread drawThread;


        public TestShowView(Context context) {
            super(context);
            getHolder().addCallback(this);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int height, int width) {}

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

            public DrawThread(SurfaceHolder surfaceHolder) {
                this.surfaceHolder = surfaceHolder;
                p = new Paint();
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
                        showMeAll(canvas);
                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }

            /*@Override
            public void run() {
                Canvas canvas;
                canvas = surfaceHolder.lockCanvas(null);
                canvas.drawARGB(80, 102, 204, 255);
                while(running){
                    showMeAll(canvas);
                }
            }*/

            private void showMeAll(Canvas c) {
                c.drawARGB(80, 102, 204, 255);
                p.setColor(Color.RED);
                p.setStrokeWidth(2);

                double[] center = service.getLocation();

                float cx = (float)center[0];
                float cy = (float)center[1];

                c.drawCircle(cx+200, cy+200, 10, p);

                p.setColor(Color.BLACK);

                /*c.drawCircle((float)service.getStructure().get("11111111111111111111111111111111")[0],
                        (float)service.getStructure().get("11111111111111111111111111111111")[1],
                        10, p);
                c.drawCircle((float)service.getStructure().get("22222222222222222222222222222222")[0],
                        (float)service.getStructure().get("22222222222222222222222222222222")[1],
                        10, p);
                c.drawCircle((float)service.getStructure().get("33333333333333333333333333333333")[0],
                        (float)service.getStructure().get("33333333333333333333333333333333")[1], 10, p);*/

                c.drawCircle(0+200,0+200,10, p);
                c.drawCircle(120+200,0+200, 10, p);
                c.drawCircle(0+200, 120+200, 10, p);
                c.drawCircle(120+200, 120+200, 10, p);

                //c.drawText(service.getDistances().toString(), 100, c.getHeight()-100, p);
                //c.drawText(center.toString(), 100, c.getHeight()-50, p);
            }
        }

    }
}