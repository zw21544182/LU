package com.example.xingwei.lu.base;

import android.content.Intent;
import android.media.projection.MediaProjectionManager;

import org.litepal.LitePalApplication;

/**
 * 创建时间: 2017/10/17
 * 创建人: Administrator
 * 功能描述:
 */

public class MyApp extends LitePalApplication {
    private String appFilePath;



    @Override
    public void onCreate() {
        super.onCreate();
        appFilePath = getFilesDir().getAbsolutePath();


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
