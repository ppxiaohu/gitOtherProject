package com.example.study;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class serverActivity extends AppCompatActivity {

    private final String downLoadUrl = "https://dldir1.qq.com/qqfile/qq/PCQQ9.6.7/QQ9.6.7.28815.exe";
    private Button btn_start;
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        btn_start = findViewById(R.id.button3);
        tv = findViewById(R.id.textView2);
        initEvent();

    }

    private void initEvent() {
        Intent lenhu = new Intent(this,DownLoadService.class);
        lenhu.setAction("len.hu");
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lenhu.putExtra("key",downLoadUrl);
                startService(lenhu);
                tv.setVisibility(View.VISIBLE);
            }
        });





    }
}