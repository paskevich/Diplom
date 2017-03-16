package com.example.paskevich.svg_test;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
//import android.widget.Toast;

public class MainActivity extends Activity{
    public static String REGION_MESSAGE = "regionName";
    private WebView webView;

    // Создадим интерфейс для взаимодействия с JavaScript в SVG
    public class WebAppInterface {
        @JavascriptInterface
        public void regionActivity(String toast) {
            Intent intent = new Intent(MainActivity.this, RegionActivity.class);
            String message = toast;
            intent.putExtra(MainActivity.REGION_MESSAGE, message);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        // Объявим ранее созданный интерфейс
        // Теперь чтобы получить доступ к методам WebAppInterface,
        // достаточно вызвать, например, Android.regionActivity(name)
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        // Ну и непосредственно открываем нашу карту
        webView.loadUrl("file:///android_asset/SuperMap.svg");
    }
}