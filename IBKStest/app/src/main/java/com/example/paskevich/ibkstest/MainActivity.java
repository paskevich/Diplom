package com.example.paskevich.ibkstest;

import java.lang.Math;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.accent_systems.ibks_sdk.scanner.ASBleScanner;
import com.accent_systems.ibks_sdk.scanner.ASResultParser;
import com.accent_systems.ibks_sdk.scanner.ASScannerCallback;
import com.accent_systems.ibks_sdk.utils.ASUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements ASScannerCallback{

    private List<String> scannedDevicesList;
    private ArrayAdapter<String> adapter;

    private ListView devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicesList = (ListView)findViewById(R.id.devicesList);

        scannedDevicesList = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, scannedDevicesList);
        devicesList.setAdapter(adapter);

        new ASBleScanner(this,this).setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

        int err = ASBleScanner.startScan();
        if(err== ASUtils.ERROR_LOCATION_PERMISSION_NOT_GRANTED){
            requestLocationPermissions();
        }
    }

    @TargetApi(23)
    public void requestLocationPermissions(){
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    public void scannedBleDevices(ScanResult result){
        JSONObject advData = new ASResultParser().getDataFromAdvertising(result);
        String calRSSI = "";

        try{
            calRSSI = advData.getString("AdvTxPower");
        }catch (JSONException e){
            Log.e("NEO", "TI OBOSRALSYA");
        }

        String deviceInfo = "RSSI: " + result.getRssi() +
                "  ADDRESS: " + result.getDevice().getAddress() +
                "  cal." + calRSSI;

        boolean exist = false;

        //TODO if device have been lost check
        //possibly impossible

        for(int i=0; i < scannedDevicesList.size(); i++) {
            if (scannedDevicesList.get(i).contains(result.getDevice().getAddress())) {
                exist = true;
                scannedDevicesList.set(i, deviceInfo);
                break;
            }
        }

        if(!exist){
            scannedDevicesList.add(deviceInfo);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public double getDistance(int RSSI, int calRSSI){
        return Math.pow(10,(calRSSI-RSSI)/20);
    }

    //TODO circle drawing

    //TODO getCalRSSI
}