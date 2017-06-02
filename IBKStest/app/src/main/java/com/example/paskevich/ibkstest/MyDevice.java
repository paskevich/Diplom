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
    protected Double mLastExp;

    public double getAverage() {
        double average = 0.0;
        for (int i:
             this.mRssi) {
            average += i;
        }
        return average / mRssi.size();
    }

    public double getLinAverage() {
        double average = 0.0;
        for (int i = 0; i < mRssi.size(); i++) {
            average += (i+1) * mRssi.get(i);
        }
        average *= (2.0 / (mRssi.size() * (mRssi.size() + 1)));
        return average;
    }

    public double getExpAverage() {
        double average;
        double alpha = 0.3;
        if(mLastExp == 0.0)
            mLastExp = (double)mRssi.get(mRssi.size()-1);
        average = alpha * mRssi.get(mRssi.size()-1) + (1-alpha) * mLastExp;
        mLastExp = average;
        return average;
    }

    public double getDistance(){
        return Math.pow(10, (Double.parseDouble(this.mCalRssi)-getExpAverage())/25.0);
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
