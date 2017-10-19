package com.example.xingwei.lu.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.xingwei.lu.R;

import java.io.IOException;

/**
 * 创建时间: 2017/10/19
 * 创建人: Administrator
 * 功能描述:
 */

public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceView cameraView;
    private SurfaceHolder holder;
    private EditText et;
    private Button bt;
    private Camera.AutoFocusCallback mAutoFocusCallback;
    private Camera.Parameters parameters;
    private WindowManager windowManager;
    private int width;
    private int height;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();
    }

    private void initView() {
        windowManager = getWindowManager();
        width = windowManager.getDefaultDisplay().getWidth();
        height = windowManager.getDefaultDisplay().getHeight();
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        et = (EditText) findViewById(R.id.et);
        bt = (Button) findViewById(R.id.bt);
        mAutoFocusCallback = new Camera.AutoFocusCallback() {

            public void onAutoFocus(boolean success, Camera camera) {
                // TODO Auto-generated method stub
                if (success) {

                }
            }
        };
        holder = cameraView.getHolder();
        holder.addCallback(this);
        camera = Camera.open(0);
        camera.autoFocus(mAutoFocusCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            camera.setDisplayOrientation(90);
            parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        camera.setDisplayOrientation(90);


    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.release();
        camera = null;
    }
}
