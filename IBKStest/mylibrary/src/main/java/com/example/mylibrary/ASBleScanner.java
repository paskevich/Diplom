package com.example.mylibrary;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContextWrapper;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.accent_systems.ibks_sdk.utils.ASUtils;

/**
 * Created by Gabriel on 20/04/2016.
 */
public class ASBleScanner {

    private static String TAG = "ASBleScanner";
    static ContextWrapper _cw;
    static ASScannerCallback _cb;

    private static BluetoothLeScanner scanner;
    private static BluetoothAdapter mBluetoothAdapter;
    private static ScanSettings scanSettings;

    public ASBleScanner(ContextWrapper activity, ASScannerCallback scannerCallback) {
        this._cw = activity;
        this._cb = scannerCallback;
    }

    //START SCAN
    public static int startScan(){
        int err;
        if((err = checkBlePermissions())!=ASUtils.TASK_OK){
            return err;
        }

        if((err = inicializeBluetooth())!=ASUtils.TASK_OK){
            return err;
        }

        if((err = initScanSettings())!=ASUtils.TASK_OK){
            return err;
        }

        Log.i(TAG,"Start Scan");
        scanner.startScan(null, scanSettings, mScanCallback);
        return ASUtils.TASK_OK;
    }

    //STOP SCAN
    public static void stopScan(){
        scanner.stopScan(mScanCallback);
    }

    //SET SCAN MODE
    public static int setScanMode(int ScanMode){
        int err = 0;
        if((err = inicializeBluetooth())!=0){
            return err;
        }

        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder.setScanMode(ScanMode);
        scanSettings = scanSettingsBuilder.build();
        if(mBluetoothAdapter == null){
            return ASUtils.ERROR_BLUETOOTH_ADAPTER_NULL;
        }
        scanner = mBluetoothAdapter.getBluetoothLeScanner();
        return ASUtils.TASK_OK;
    }

    public static BluetoothAdapter getmBluetoothAdapter()
    {
        if( (inicializeBluetooth()) != ASUtils.TASK_OK ){
            return null;
        }
        return mBluetoothAdapter;
    }

    //CHECK LOCATION PERMISSION FOR ANDROID M OR HIGHER
    @TargetApi(23)
    public static int checkBlePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Log.i(TAG, "CHECK BT - VERSION IS M");

            if (_cw.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return ASUtils.ERROR_LOCATION_PERMISSION_NOT_GRANTED;
            }
        }
        return ASUtils.TASK_OK;
    }

    //INICIALIZE SCAN SETTINGS
    private static int initScanSettings(){
        if(scanner == null || scanSettings == null){
            ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
            scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
            scanSettings = scanSettingsBuilder.build();
            if(mBluetoothAdapter == null){
                return ASUtils.ERROR_BLUETOOTH_ADAPTER_NULL;
            }
            scanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
        return ASUtils.TASK_OK;
    }

    //INITIALIZE BLUETOOTH
    public static int inicializeBluetooth(){
        if (!_cw.getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return ASUtils.ERROR_BLE_NOT_SUPPORTED;
        }

        if (!_cw.getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            return  ASUtils.ERROR_BLUETOOTH_NOT_SUPPORTED;
        } else {
            if(mBluetoothAdapter == null){
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            if (mBluetoothAdapter == null) {
                return ASUtils.ERROR_BLUETOOTH_ADAPTER_NULL;
            }else{
                if (!mBluetoothAdapter.isEnabled()) {
                    return ASUtils.ERROR_BLUETOOTH_NOT_ENABLED;
                }
            }
        }

        return ASUtils.TASK_OK;
    }

    private static ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(_cb != null){
                _cb.scannedBleDevices(result);
            }else{
                Log.i(TAG,"Scanner Callback is NULL!");
            }
        }
    };



}

