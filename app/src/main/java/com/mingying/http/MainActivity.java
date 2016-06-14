package com.mingying.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mingying.http.base.Request;
import com.mingying.http.core.RequestQueue;
import com.mingying.http.core.SimpleHttpAndroid;
import com.mingying.http.requests.StringRequest;

public class MainActivity extends AppCompatActivity {

    private TextView textview;
    private TextView textview2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = (TextView) findViewById(R.id.textview);
        textview2 = (TextView) findViewById(R.id.textview2);


        String url = "http://www.weather.com.cn/adat/sk/101010100.html";
        String url2 = "http://www.weather.com.cn/adat/sk/101020100.html";

        //建议创建全局的一个Queue
        RequestQueue requestQueue = SimpleHttpAndroid.newRequestQueue();
        StringRequest request = new StringRequest(Request.HttpMethod.GET, url, new Request.RequestListener<String>() {
            @Override
            public void onComplete(int code, String response) {
                textview.setText(response);
            }

            @Override
            public void onError(int code, String error) {
                textview.setText(error);
            }
        });

        StringRequest request2 = new StringRequest(Request.HttpMethod.GET, url2, new Request.RequestListener<String>() {
            @Override
            public void onComplete(int code, String response) {
                textview2.setText(response);
            }

            @Override
            public void onError(int code, String error) {
                textview2.setText(error);
            }
        });
        requestQueue.addRequest(request);
        requestQueue.addRequest(request2);
    }
}
