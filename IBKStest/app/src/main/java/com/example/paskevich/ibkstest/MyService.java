package com.example.paskevich.ibkstest;

import android.app.Service;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.accent_systems.ibks_sdk.scanner.*;

import com.accent_systems.ibks_sdk.scanner.ASBleScanner;
import com.example.mylibrary.*;

import com.example.mylibrary.ASResultParser;
import com.example.mylibrary.ASScannerCallback;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MyService extends Service implements ASScannerCallback {

    List<MyBleDevice> scannedMyBleDevices;

    //List<MyWifiDevice> scannedMyWifiDevices;

    //com.example.mylibrary.ASBleScanner mScanner;

    int err;
    HashMap<String, double[]> structure;
    final double COEFF = 60;        //pixels in meter

    //private WifiManager wifiManager;
    //private WifiScanReceiver wifiReceiver;

    @Override
    public void onCreate(){
        scannedMyBleDevices = new ArrayList<>();

        //scannedMyWifiDevices = new ArrayList<>();

        //TODO: Get this structure initialization away from code
        //
        // vista -> SHA1 -> 75a31fd03212aab1da99 (namespace for all ble-beacons used in single project)
        //
        structure = new HashMap<>();
        structure.put("75a31fd03212aab1da99111111111111", new double[]{2*COEFF,0,0});
        structure.put("75a31fd03212aab1da99222222222222", new double[]{0,2*COEFF,0});
        structure.put("75a31fd03212aab1da99333333333333", new double[]{0,0,COEFF});
        structure.put("75a31fd03212aab1da99444444444444", new double[]{2*COEFF,2*COEFF,COEFF});

        new com.example.mylibrary.ASBleScanner(this, this)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        //
        // useless wifi block
        //
        //wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //wifiReceiver = new WifiScanReceiver();
        //registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        //wifiManager.startScan();
        //

        err = com.example.mylibrary.ASBleScanner.startScan();
    }

    private final IBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return binder;
    }

    public class MyBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }
    }

    //
    // iBKS SDK callback method
    //
    @Override
    public void scannedBleDevices(ScanResult result){

        //Log.d("Callback", "check");

        JSONObject advData = new ASResultParser().getDataFromAdvertising(result);
        String namespace;
        try {
            namespace = advData.getString("Namespace");
        } catch (JSONException e) {
            namespace = null;
        }
        boolean exist = false;
        //boolean readyToShow = false;

        for (MyBleDevice device : scannedMyBleDevices) {
            if(device.getmAddress().equals(result.getDevice().getAddress())) {
                exist = true;
                device.setmLastChange();

                if(device.getmRssi().size() != 3) {
                    device.getmRssi().add(result.getRssi());
                } else {
                    device.getmRssi().add(result.getRssi());
                    device.getmRssi().remove(0);
                }

                if(device.getmRssi().size() == 3){

                    //
                    // no idea...
                    //
                    //setViewVList(device);
                    //runOnUiThread(new Runnable() {
                        //@Override
                        //public void run() {adapter.notifyDataSetChanged();
                        //}
                    //});
                    //Log.d("I just", "updated screen");

                    //
                    //SORT SCANNED DEVICES
                    //
                    /*Collections.sort(scannedMyBleDevices, new Comparator<MyBleDevice>() {
                        @Override
                        public int compare(MyBleDevice o1, MyBleDevice o2){
                            return Double.compare(o2.getAverage(), o1.getAverage());
                        }
                    });*/
                    //
                    //
                    //
                }

                //Log.d("Callback", "added new RSSI measure");

                break;
            }

        }

        if(!exist && namespace!=null && namespace.contains("75a31fd03212aab1da99")){
            MyBleDevice device = new MyBleDevice(result, advData);
            scannedMyBleDevices.add(device);

            //Log.d("I just", "Created new device");
        }
        deleteIfNoFeedback();
    }

    //
    // useless wifi method
    //
    /*private void scannedWifiDevices() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<android.net.wifi.ScanResult> accessPoints = wifiManager.getScanResults();
                for (MyWifiDevice device:
                     scannedMyWifiDevices) {

                }
            }
        });
    }*/

    //
    // this is for progal lib
    //
    /*public Point[] getLocation(){
        Sphere sphere1 = new Sphere(new Point(0,0,0), scannedMyBleDevices.get(0).getDistance());
        Sphere sphere2 = new Sphere(new Point(0,2,0), scannedMyBleDevices.get(1).getDistance());
        Sphere sphere3 = new Sphere(new Point(2,0,0), scannedMyBleDevices.get(2).getDistance());

        return Sphere.getIntersections(sphere1, sphere2, sphere3);
    }*/

    public double[] getLocation(){
        if (scannedMyBleDevices.size()<4)
            return new double[]{0,0,0};

        /*double[][] positions = new double[][]{{0,0,0},{0,2,0},{2,0,0}, {2,2,1}};
        double[] distances = new double[]
                {scannedMyBleDevices.get(0).getDistance(),
                scannedMyBleDevices.get(1).getDistance(),
                scannedMyBleDevices.get(2).getDistance(),
                //scannedMyBleDevices.get(3).getDistance()
                };*/

        ArrayList<double[]> positionsList = new ArrayList<>();
        ArrayList<Double> distancesList = new ArrayList<>();
        for(int i = 0; i < scannedMyBleDevices.size(); i++){
            positionsList.add(i, structure.get(scannedMyBleDevices.get(i).getUID()));
            distancesList.add(i, scannedMyBleDevices.get(i).getDistance());
        }

        Log.d("Positions List", positionsList.toString());
        Log.d("Distances List", distancesList.toString());


        double[][] positions = positionsList.toArray(new double[positionsList.size()][3]);
        //double[][] double_positions = new double[positions.length][positions[0].length];
        double[] distances = new double[distancesList.size()];

        for(int i = 0; i < distances.length; i++){
            distances[i] = distancesList.get(i).doubleValue();
            //for(int j = 0; j < double_positions[0].length; j++){
                //double_positions[i][j] = (double) positions[i][j];
            //}

        }

        double[][] positions2D = new double[positions.length][2];
        to2D(positions, distances, positions2D);

        /*final int BEACONS_NUM = 3;
        double[] distances = new double[BEACONS_NUM];
        double[][] positions = new double[BEACONS_NUM][3];
        for(int i = 0; i < )*/

        //
        //TODO: Fix NPE sometimes
        //
        // used lemmingapex trilateration library
        //
        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver
                (new TrilaterationFunction(positions2D, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();
        double[] result = optimum.getPoint().toArray();
        Log.d("Service", Arrays.toString(result));

        return result;
    }

    public void to2D(double[][] positions, double[] distances, double[][] positions2D) {
        for (int i = 0; i < distances.length; i++) {
            distances[i] = Math.sqrt(Math.pow(distances[i], 2) - Math.pow(positions[i][2] - 1.3 * COEFF, 2));
            for(int j = 0; j < 2; j++) {
                positions2D[i][j] = positions[i][j];
            }
        }
    }

    public String[] getDistances() {
        String[] distances = new String[scannedMyBleDevices.size()];
        for(int i = 0; i < distances.length; i++){
            distances[i] = Double.toString(scannedMyBleDevices.get(i).getDistance())
                    + ' ' + scannedMyBleDevices.get(i).getUID();
        }
        return distances;
    }

    //
    // useless method now
    // could used by activity to check scan was started successfully
    //
    public int getErr(){
        return err;
    }

    public void deleteIfNoFeedback(){
        for(int i = 0; i < scannedMyBleDevices.size(); i++){
            if((new Date().getTime() - scannedMyBleDevices.get(i).getmLastChange().getTime()) > 5000){
                scannedMyBleDevices.remove(i);
            }
        }
    }

    //
    // could used by activity
    //
    HashMap<String, double[]> getStructure(){
        return this.structure;
    }


    public void getLevelToLog() {
        Log.d("LookRSSI", Double.toString(scannedMyBleDevices.get(0).getAverage()));
    }

    //
    //TODO: Use Kalman filter in distance computing
    //
    public double getNextKalman(double x) {
        final double KALMAN_COEFF = 0.3;
        double xx = KALMAN_COEFF * scannedMyBleDevices.get(0).getAverage() + (1-KALMAN_COEFF) * x;
        return xx;
    }
}
//
//useless
//
/*class WifiScanReceiver extends BroadcastReceiver {
    public void onReceive(Context c, Intent intent) {}
}*/
