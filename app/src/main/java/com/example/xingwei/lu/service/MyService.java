package com.example.xingwei.lu.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.Window;
import com.example.xingwei.lu.activity.MainActivity;
import com.example.xingwei.lu.base.MyApp;
import com.example.xingwei.lu.util.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * 创建时间: 2017/11/15
 * 创建人: Administrator
 * 功能描述:
 */

public class MyService extends Service {
    public final static String ACTION_BUTTON = "com.notification.intent.action.ButtonClick";
    public final static String INTENT_BUTTONID_TAG = "ButtonId";
    public final static int BUTTON_START_ID = 1;
    public final static int BUTTON_MAIN_ID = 2;
    public final static int BUTTON_STOP_ID = 3;
    public final static int BUTTON_IMAGE_ID = 4;
    private MediaProjectionManager mediaProjectionManager;
    private RemoteViews remoteView;
    private final int NOTIFICATION_ID = 0xa01;
    private ButtonReceiver receiver;
    private NotificationManager notificationManager;
    private Notification notification;
    private boolean isStart = false;
    private boolean isStop = true;
    private MyApp myApp;
    private String path;
    private ToastUtil toastUtil;
    private ImageReader mImageReader;
    private int windowHeight;
    private int windowWidth;
    private SimpleDateFormat dateFormat;
    private String strDate;
    private String pathImage;
    private String nameImage;
    private WindowManager mWindowManager1;
    private DisplayMetrics metrics;
    private int mScreenDensity;
    private MediaProjection mMediaProjection;
    private Handler handler;
    private VirtualDisplay mVirtualDisplay;
    private File videoFile;
    private MediaRecorder mMediaRecorder;
    private final Window.Stub stub = new Window.Stub() {
        @Override
        public void initData() throws RemoteException {
            Log.d("xwls", "initData");
            myApp = (MyApp) getApplication();
            handler = new Handler();
            toastUtil = ToastUtil.getInstance(MyService.this);
            path = getFilesDir().getAbsolutePath();
            mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            sendNotify();
        }

        @Override
        public void createVirtualEnvironment() throws RemoteException {
            File file = new File(getFilesDir(), "Video");
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(path, "Pictures");
            if (!file.exists()) {
                file.mkdir();
            }
            if (mMediaProjection == null)
                mMediaProjection = mediaProjectionManager.getMediaProjection(myApp.getResultCode(), myApp.getResultIntent());
            mWindowManager1 = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
            windowWidth = mWindowManager1.getDefaultDisplay().getWidth();
            windowHeight = mWindowManager1.getDefaultDisplay().getHeight();
            metrics = new DisplayMetrics();
            mWindowManager1.getDefaultDisplay().getMetrics(metrics);
            mScreenDensity = metrics.densityDpi;
            mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565
            virtualDisplay();
        }

        @Override
        public void stopService() throws RemoteException {
            if (receiver != null)
                unregisterReceiver(receiver);
            receiver = null;
            notificationManager.cancel(NOTIFICATION_ID);
            MyService.this.stopSelf();
        }
    };

    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    public class ButtonReceiver extends BroadcastReceiver {
        private MediaRecorder mMediaRecorder;
        private Bitmap mBitmap;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_BUTTON)) {
                //通过传递过来的ID判断按钮点击属性或者通过getResultCode()获得相应点击事件
                Log.d("xwl", "code " + getResultCode());
                collapseStatusBar(context);
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case BUTTON_START_ID:
                        Log.d("xwl", "点击录制/暂停按钮");
                        // TODO: 2017/10/17 录制视频/暂停录制
                        event_startorpause();
                        break;
                    case BUTTON_MAIN_ID:
                        Log.d("xwl", "进入主界面");
                        // TODO: 2017/10/17  进入主界面（判断是否被系统杀死）
                        event_enterMain(context);
                        break;
                    case BUTTON_STOP_ID:
                        Log.d("xwl", "点击停止按钮");
                        // TODO: 2017/10/17 停止录制，通知更新UI
                        evnnt_stop();
                        break;
                    case BUTTON_IMAGE_ID:
                        Log.d("xwl", "点击了截屏按钮");
                        // TODO: 2017/10/17 截屏操作，通知UI更新
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startCapture();
                            }
                        }, 500);
                        break;
                    default:
                        break;
                }
            }
        }

        private void startCapture() {

            strDate = dateFormat.format(new java.util.Date());
            pathImage = path + "/Pictures/";
            nameImage = pathImage + "pic" + strDate + ".png";
            if (mImageReader == null) {
                Log.d("ZWW", "mImageReader NULL");
            }
            Image image = mImageReader.acquireLatestImage();
            if (image == null) {
                Log.e(TAG, "image is null.");
                return;
            }
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            mBitmap.copyPixelsFromBuffer(buffer);
            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height);
            image.close();

            if (mBitmap != null) {
                // 保存或者显示...
                saveBitmap(mBitmap);
            }

        }

        private void saveBitmap(Bitmap mBitmap) {

            try {
                Log.d("xwl", "src " + nameImage);
                File fileImage = new File(nameImage);
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    Log.d("xwl", "sucess");
                    toastUtil.showToast("图片保存成功");
                    sendInfoToActivity(R.id.ivImage);

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();

                toastUtil.showToast("权限问题");
            } catch (IOException e) {
                e.printStackTrace();

                toastUtil.showToast("读写异常");
            }
        }

        public void collapseStatusBar(Context context) {
            try {
                Object statusBarManager = context.getSystemService("statusbar");
                Method collapse;
                if (Build.VERSION.SDK_INT <= 16) {
                    collapse = statusBarManager.getClass().getMethod("collapse");
                } else {
                    collapse = statusBarManager.getClass().getMethod("collapsePanels");
                }
                collapse.invoke(statusBarManager);
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
    }

    private void evnnt_stop() {
//            Thread thread = myApp.getThread();
//            if (thread != null)
//                try {
//                    thread.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
        if (mMediaRecorder != null) {
            isStop = true;
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            toastUtil.showToast("录屏成功");
            remoteView.setImageViewResource(R.id.ivStart, R.drawable.start);
            isStart = false;
            notification.contentView = remoteView;
            notificationManager.notify(NOTIFICATION_ID, notification);
            sendInfoToActivity(R.id.ivStop);
        } else {
            toastUtil.showToast(getString(R.string.no_start));
        }
//            if (thread != null)
//                thread.notify();
    }

    private void sendInfoToActivity(int viewId) {
        Intent intent = new Intent();
        intent.setAction("com.audioeadd");
        if (viewId == R.id.ivImage) {
            intent.putExtra("type", "image");
        } else {
            intent.putExtra("type", "video");

        }
        sendBroadcast(intent);

    }

    private void event_startorpause() {

        if (isStart) {
            mMediaRecorder.pause();
            remoteView.setImageViewResource(R.id.ivStart, R.drawable.start);
            isStart = false;
        } else {

            if (isStop) {
                //停止状态
                toastUtil.showToast("开始录屏");

                videoFile = new File(path + "/Video", dateFormat.format(new Date()) + ".mp4");
                Log.d("xwl", "path " + videoFile.getAbsolutePath());
                if (!videoFile.exists()) {
                    try {
                        videoFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
            mMediaRecorder = createMediaRecorder(videoFile);
            if (isStop) {
                mVirtualDisplay = createVirtualDisplay();
                mMediaRecorder.start();
            } // 必须在mediaRecorder.prepare() 之后调用，否则报错"fail to get surface"
            else {
                mMediaRecorder.resume();
            }
            remoteView.setImageViewResource(R.id.ivStart, R.drawable.pause);
            isStart = true;
        }
        notification.contentView = remoteView;
        notificationManager.notify(NOTIFICATION_ID, notification);
        isStop = false;

    }

    private MediaRecorder createMediaRecorder(File file) {
        MediaRecorder mediaRecorder = null;
        if (mMediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            if (Build.VERSION.SDK_INT >= 23) {
                mediaRecorder.setVideoSize(windowWidth, windowHeight);  //after setVideoSource(), setOutFormat()
                mediaRecorder.setVideoEncodingBitRate(5 * windowWidth * windowHeight);
            }
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  //after setOutputFormat()
            mediaRecorder.setVideoFrameRate(60); //after setVideoSource(), setOutFormat()

            try {
                mediaRecorder.prepare();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d("xwl", "IllegalStateException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("xwl", "IOException");

            }
        } else {
            mediaRecorder = mMediaRecorder;
        }

        return mediaRecorder;
    }

    private VirtualDisplay createVirtualDisplay() {
        Log.i(TAG, "Create VirtualDisplay");
        return mMediaProjection.createVirtualDisplay(TAG, windowWidth, windowHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
    }

    private void event_enterMain(Context context) {
        Intent in = new Intent(context, MainActivity.class);
        in.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);
    }

    private void sendNotify() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        // 此处设置的图标仅用于显示新提醒时候出现在设备的通知栏
        mBuilder.setSmallIcon(R.drawable.logo);
        notification = mBuilder.build();
        receiver = new ButtonReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        registerReceiver(receiver, intentFilter);
        remoteView = new RemoteViews(getPackageName(), R.layout.layout_notify);
        remoteView.setOnClickPendingIntent(R.id.ivStart, bteEvent(R.id.ivStart));
        remoteView.setOnClickPendingIntent(R.id.iventerMain, bteEvent(R.id.iventerMain));
        remoteView.setOnClickPendingIntent(R.id.ivImage, bteEvent(R.id.ivImage));
        remoteView.setOnClickPendingIntent(R.id.ivStop, bteEvent(R.id.ivStop));
        notification.contentView = remoteView;
        notification.flags = Notification.FLAG_NO_CLEAR;
        Log.d("xwls", "notify");
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private PendingIntent bteEvent(int ViewId) {
        int EVENT_ID = 0;
        switch (ViewId) {
            case R.id.ivStart:
                EVENT_ID = BUTTON_START_ID;
                break;
            case R.id.iventerMain:
                EVENT_ID = BUTTON_MAIN_ID;
                break;
            case R.id.ivStop:
                EVENT_ID = BUTTON_STOP_ID;
                break;
            case R.id.ivImage:
                EVENT_ID = BUTTON_IMAGE_ID;
                break;
        }
        //设置点击的事件
        Intent buttonIntent = new Intent(ACTION_BUTTON);
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, EVENT_ID);
        Log.d("xwl", "EVENT_ID " + EVENT_ID);
        PendingIntent intent_paly = PendingIntent.getBroadcast(this, EVENT_ID, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return intent_paly;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("xwl", "service destory");
        unregisterReceiver(receiver);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }
}
