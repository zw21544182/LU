package com.example.xingwei.lu.activity;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import com.example.xingwei.lu.R;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Integer.parseInt;

/**
 * 创建时间: 2017/10/19
 * 创建人: Administrator
 * 功能描述:
 */

public class PhotographActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {
    private SurfaceView surfaceView;
    private Camera camera;
    private Camera.Parameters parameters;
    private SurfaceHolder surfaceHolder;
    private ImageButton ibRecord;
    private MediaRecorder mMediaRecorder;
    SimpleDateFormat dateFormat;
    private String path = Environment.getExternalStorageDirectory().getPath() + "/LU/Movie";
    private boolean isStart = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    private void initView() {
        ibRecord = (ImageButton) findViewById(R.id.ibRecord);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setFocusable(true);
        surfaceView.setOnClickListener(this);
        surfaceView.setBackgroundColor(TRIM_MEMORY_BACKGROUND);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.setFixedSize(400, 300);
        surfaceHolder.addCallback(this);
        dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        ibRecord.setOnClickListener(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 实现自动对焦
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦
                    doAutoFocus();
                }
            }
        });
    }

    // 相机参数的初始化设置
    private void initCamera() {
        if (null == camera) {
            camera = Camera.open();
        }
        parameters = camera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        if (!Build.MODEL.equals("KORIDY H30")) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
        } else {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        camera.setParameters(parameters);
        setDispaly(camera);
        camera.startPreview();
        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
    }

    // 控制图像的正确显示方向
    private void setDispaly(Camera camera) {
        if (parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    // 实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        } catch (Exception e) {
            Log.e("Came_e", "图像出错");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.surfaceView:
                doAutoFocus();
                break;
            case R.id.ibRecord:
                if (!isStart) {
                    initRecord();
                    ibRecord.setBackgroundResource(R.drawable.stop);
                } else {
                    stopRecord();
                    ibRecord.setBackgroundResource(R.drawable.record);

                }
                isStart = !isStart;
                break;
            default:
                break;
        }
    }

    // handle button auto focus
    private void doAutoFocus() {
        parameters = camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(parameters);
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦。
                    if (!Build.MODEL.equals("KORIDY H30")) {
                        parameters = camera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
                        camera.setParameters(parameters);
                    } else {
                        parameters = camera.getParameters();
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        camera.setParameters(parameters);
                    }
                }
            }
        });
    }

    private void initRecord() {
        mMediaRecorder = new MediaRecorder();
        try {
            mMediaRecorder.reset();
            if (camera != null) {
                camera.unlock();
                mMediaRecorder.setCamera(camera);
            }
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);// 音频源率，然后就清晰了
            mMediaRecorder.setOrientationHint(90);// 输出旋转90度，保持竖屏录制
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
            mMediaRecorder.setVideoSize(640, 480);
            mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 音频格式
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);// 视频录制格式
            // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
            File file = new File(path);
            if (!file.exists()) file.mkdir();
            Log.d("xwl", "file path" + file.getAbsolutePath());
            File newFile = new File(file, dateFormat.format(new Date()) + ".mp4");
            // mediaRecorder.setMaxDuration(Constant.MAXVEDIOTIME * 1000);
            mMediaRecorder.setOutputFile(newFile.getAbsolutePath());
            mMediaRecorder.setVideoFrameRate(30);

            mMediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();

        }
        try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            Log.d("xwl", "IllegalStateException");
            e.printStackTrace();
        } catch (RuntimeException e) {
            Log.d("xwl", "RuntimeException");

            e.printStackTrace();
        } catch (Exception e) {
            Log.d("xwl", "Exception");

            e.printStackTrace();
        }
    }

    private void pauseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.pause();
        }
    }

    private void stopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
        }
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

}
