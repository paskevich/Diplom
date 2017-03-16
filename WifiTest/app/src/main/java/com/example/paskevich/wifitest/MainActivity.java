package com.example.paskevich.wifitest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.wifi.*;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    private List<ScanResult> points;

    private WifiManager wifiManager;

    //private Timer autoupdate;

    private WifiScanReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final android.os.Handler handler = new android.os.Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                setPoints();
                showInformation();
            }
        };
        handler.postDelayed(r, 1000);
    }

    public void setPoints(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiScanReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        points = wifiManager.getScanResults();
    }

    public void showInformation(){
        //HashMap<String, Integer> pointsMap = new HashMap<String, Integer>();
        String textString = "";
        for (ScanResult p : points) {
            //if(p.SSID.contains("Eto")){
                //pointsMap.put(p.BSSID, p.level);
                textString += p.SSID + " " + p.BSSID + " " + p.level + '\n';
            //}
        }
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(textString);
    }

    /*@Override
    public void onResume(){
        super.onResume();
        autoupdate = new Timer();
        autoupdate.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateInfo();
                    }
                });
            }
        },1000);
    }

    public void updateInfo(){
        setPoints();
        showInformation();
    }

    @Override
    public void onPause(){
        unregisterReceiver(wifiReceiver);
        autoupdate.cancel();
        super.onPause();
    }*/
}

class WifiScanReceiver extends BroadcastReceiver {
    public void onReceive(Context c, Intent intent) {
    }
}
