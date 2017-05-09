package com.example.paskevich.ibkstest;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by paskevich on 02.05.17.
 */

public class MyWifiDevice extends MyDevice {
    public MyWifiDevice(ScanResult result){
        this.mRssi = new ArrayList<>();
        this.mRssi.add(result.level);
        this.mAddress = result.BSSID;
        this.mLastChange = new Date();
    }
}