package com.example.mylibrary;

import android.bluetooth.le.ScanResult;

/**
 * Created by Gabriel on 21/04/2016.
 */
public interface ASScannerCallback {
    void scannedBleDevices(ScanResult result);
}
