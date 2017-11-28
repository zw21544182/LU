package com.example.xingwei.lu.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.base.MyApp;
import com.example.xingwei.lu.modern.PdfPathMoudle;
import com.example.xingwei.lu.util.FileUtil;
import com.example.xingwei.lu.util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 创建时间: 2017/11/10
 * 创建人: Administrator
 * 功能描述:
 */

public class FileServer extends Service {
    private MyApp myApp;
    Thread thread;
    private FileUtil fileUtil;
    private ToastUtil toastUtil;
    private List<PdfPathMoudle> pdfPathMoudles;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileUtil = FileUtil.getInstance(this);
        toastUtil = ToastUtil.getInstance(this);
        myApp = (MyApp) getApplication();
        thread = new Thread() {
            public void run() {
                super.run();
                pdfPathMoudles = DataSupport.findAll(PdfPathMoudle.class);
                if (pdfPathMoudles == null || pdfPathMoudles.size() == 0) {
                    toastUtil.showToast(R.string.addPath);
                    fileUtil.checkPdf(Environment.getExternalStorageDirectory().getAbsolutePath());
                } else {
                    fileUtil.checkPdf(pdfPathMoudles);

                }
                Intent intent = new Intent();
                intent.setAction("com.audioeadd");
                intent.putExtra("type", "pdf");
                sendBroadcast(intent);
                stopSelf();

            }
        };
        thread.start();
//        myApp.setThread(thread);
    }

}
