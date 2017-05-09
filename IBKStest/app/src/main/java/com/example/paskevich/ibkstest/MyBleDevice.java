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

public class MyDevice {
    private List<Integer> RSSI;
    private String calRSSI;
    private String address;
    private Date lastChange;
    private String UID;
    private final double COEFF = 60.0;

    public MyDevice(ScanResult result, JSONObject advData){
        try{
            this.calRSSI = advData.getString("AdvTxPower");
            this.UID = advData.getString("Namespace") + advData.getString("Instance");
        }
        catch(JSONException e){
            Log.e("JSONException", "can't read cal. power or UID");
            this.calRSSI = "-51";
        }

        this.RSSI = new ArrayList<>();

        this.RSSI.add(result.getRssi());

        this.address = result.getDevice().getAddress();

        this.lastChange = new Date();
    }

    @Override
    public String toString(){
        return address + '\n' + getAverage() + '\n' + getDistance();
    }

    public String getAddress(){
        return this.address;
    }

    public List<Integer> getRSSI(){
        return this.RSSI;
    }

    public double getAverage(){
        double average = 0.0;
        for (int i : this.RSSI) {
            average += i;
        }
        return average / 3.0;
    }

    public double getDistance(){
        //return Math.pow(10, (Double.parseDouble(this.calRSSI)-getAverage())/20.0);
        //return 0.5 * Math.pow(10, (Double.parseDouble(this.calRSSI)-getAverage())/20.0);
        return Math.pow(10, (-23.0 - 10 - getAverage() -10.0 * 2.7 * Math.log10(2.4) + 30.0 * 2.7 - 32.44)/10.0*2.7);
    }

    public Date getLastChange(){
        return this.lastChange;
    }

    public void setLastChange(){
        this.lastChange = new Date();
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
