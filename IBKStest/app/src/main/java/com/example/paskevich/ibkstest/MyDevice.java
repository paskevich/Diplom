package com.example.paskevich.ibkstest;

import java.util.Date;
import java.util.List;

/**
 * Created by paskevich on 02.05.17.
 */

public abstract class MyDevice {
    protected String mCalRssi;
    protected List<Integer> mRssi;
    protected String mAddress;
    protected Date mLastChange;

    public double getAverage() {
        double average = 0.0;
        for (int i:
             this.mRssi) {
            average += i;
        }
        return average / mRssi.size();
    }

    public double getDistance(){
        return Math.pow(10, (Double.parseDouble(this.mCalRssi)-getAverage())/25.0);
    }

    public Date getmLastChange() {
        return this.mLastChange;
    }

    public void setmLastChange() {
        this.mLastChange = new Date();
    }

    public String getmAddress() {
        return this.mAddress;
    }

    public List<Integer> getmRssi() {
        return this.mRssi;
    }

    @Override
    public String toString(){
        return mAddress + '\n' + getAverage() + '\n' + getDistance();
    }
}
