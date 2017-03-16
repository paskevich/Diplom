package com.example.paskevich.svg_test;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RegionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);
        String string = getIntent().getStringExtra(MainActivity.REGION_MESSAGE);
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(string);
    }
}
