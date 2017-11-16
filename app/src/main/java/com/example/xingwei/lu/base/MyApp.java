package com.example.xingwei.lu.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.xingwei.lu.Window;

import org.litepal.LitePalApplication;

/**
 * 创建时间: 2017/10/17
 * 创建人: Administrator
 * 功能描述:
 */

public class MyApp extends LitePalApplication {
    private String appFilePath;
    private Thread thread;
    private Window window;

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appFilePath = getFilesDir().getAbsolutePath();
        Intent intent = new Intent();
        intent.setAction("com.example.xingwei.lu.MyService");
        intent.setPackage("com.example.xingwei.lu");
        getApplicationContext().bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("zw", "连接成功");
                window = Window.Stub.asInterface(service);
                try {
                    window.initData();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

                Log.d("ZW", "连接失败");
            }
        }, Context.BIND_AUTO_CREATE);

    }

    public String getAppFilePath() {
        return appFilePath;
    }

    public Intent getResultIntent() {
        return mResultIntent;
    }

    public void setResultIntent(Intent mResultIntent) {
        this.mResultIntent = mResultIntent;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public void setResultCode(int mResultCode) {
        this.mResultCode = mResultCode;
    }

    private Intent mResultIntent = null;
    private int mResultCode = 0;

    public MediaProjectionManager getMpmngr() {
        return mMpmngr;
    }

    public void setMpmngr(MediaProjectionManager mMpmngr) {
        this.mMpmngr = mMpmngr;
    }

    private MediaProjectionManager mMpmngr;


}
