package com.teachingassistant.Activity.Main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.teachingassistant.MyApplication;
import com.teachingassistant.R;
import com.tencent.TIMManager;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        //初始化IMSDK
        TIMManager.getInstance().init(MyApplication.getContext());
        TIMManager.getInstance().disableCrashReport();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadActivity.this,LoginActivity.class);
                LoadActivity.this.startActivity(intent);
                LoadActivity.this.finish();
            }
        },1200);
    }
}
