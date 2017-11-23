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
 * 功能描述:欢迎界面
 */

public class SplashActivity extends Activity {
    private boolean isFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //界面初始化时会调用的方法
        isFirst = (boolean) SharedPreferencesUtil.getParam(this, "isFirst", true);//从缓存中取名字为isFirst的值，如果没有值将true赋值
        if (isFirst) {//如果isFirst为true
            setContentView(R.layout.activity_splash);//加载布局
            SharedPreferencesUtil.setParam(this, "isFirst", false);//在缓存中加入名字为isFirst值为true的变量
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }.start();
        } else {//如果isFirst的值为false了 确定不是第一次进入,直接进入主界面
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();//将自身界面销毁
        }

    }
}
