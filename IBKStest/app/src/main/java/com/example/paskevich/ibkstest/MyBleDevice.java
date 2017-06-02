package com.example.paskevich.ibkstest;

import android.bluetooth.le.ScanResult;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by paskevich on 04.04.17.
 */

public class MyBleDevice extends MyDevice{
    private String UID;

    public MyBleDevice(ScanResult result, JSONObject advData){
        try{
            this.mCalRssi = advData.getString("AdvTxPower");
            this.UID = advData.getString("Namespace") + advData.getString("Instance");
        }
        catch(JSONException e){
            Log.e("JSONException", "can't read cal. power or UID");
            this.mCalRssi = "-49";
        }

        this.mRssi = new ArrayList<>();

        this.mRssi.add(result.getRssi());

        this.mAddress = result.getDevice().getAddress();

        this.mLastChange = new Date();

        mLastExp = (double)result.getRssi();
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
