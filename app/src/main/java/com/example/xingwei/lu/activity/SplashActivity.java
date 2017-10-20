package com.example.xingwei.lu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.util.SharedPreferencesUtil;

/**
 * 创建时间: 2017/10/20
 * 创建人: Administrator
 * 功能描述:
 */

public class SplashActivity extends Activity {
    private boolean isFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = (boolean) SharedPreferencesUtil.getParam(this, "isFirst", true);
        if (isFirst) {
            setContentView(R.layout.activity_splash);
            SharedPreferencesUtil.setParam(this, "isFirst", false);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(2000);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
