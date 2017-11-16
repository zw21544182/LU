package com.example.xingwei.lu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.xingwei.lu.Window;

/**
 * 创建时间: 2017/11/15
 * 创建人: Administrator
 * 功能描述:
 */

public class MyService extends Service {
    private final Window.Stub stub = new Window.Stub() {
        @Override
        public void initData() throws RemoteException {
            Log.d("zw", "initData");
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }
}
