package com.example.study;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.fonts.SystemFonts;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();
    private MainActivity.myBroadCast myBroadCast;
    private Button btn;
    private TextView tv;
    private Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiveBroadCast();
        initView();
        initEvent();
    }

    private void getFontScale() {
        float fontScale = getResources().getConfiguration().fontScale;
        Log.e(TAG, "fontScale" + fontScale);
    }

    private void initEvent() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFontScale();
                //创建一个广播内容，给他发送出去
                Intent intent = new Intent("len.hu");
                intent.putExtra("key", "我是发出去的内容");
                sendBroadcast(intent);
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, serverActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        btn = findViewById(R.id.button);
        tv = findViewById(R.id.textView);
        btn_start = findViewById(R.id.button2);
    }

    private void receiveBroadCast() {
        IntentFilter intentFilter = new IntentFilter("len.hu");//这里的参数意味着我们只能接收带有参数的这个广播，起到限定的作用
        myBroadCast = new myBroadCast();
        registerReceiver(myBroadCast, intentFilter);
    }

    //使用广播我们需要一个广播接收器
    class myBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "我是一个广播接收器");
            String action = intent.getAction();
            Log.e(TAG, action.toString());//len.hu
            String key = intent.getStringExtra("key");
            if (null != key) {
                tv.setText(key);
                tv.setTextSize(50);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadCast);
    }

    //test
}