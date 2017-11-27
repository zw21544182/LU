package com.example.xingwei.lu.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.base.BaseFragment;
import com.example.xingwei.lu.base.MyApp;
import com.example.xingwei.lu.fragment.AudioFragment;
import com.example.xingwei.lu.fragment.PdfFragment;
import com.example.xingwei.lu.fragment.SetFragment;
import com.example.xingwei.lu.util.ToastUtil;

import java.io.File;
import java.util.List;

import li.camera.CameraActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ToastUtil toastUtil;
    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private FragmentTransaction mfragmentTransaction;
    private BaseFragment mCurrentFrgment;
    private PdfFragment pdfFragment;
    private SetFragment setFragment;
    private TextView tvTitle;
    private Button btChose;
    private Handler handler;
    private boolean isRecord = false;
    private static final int DELETESUCESS = 3;
    public static boolean fragmentstate = false;
    private BottomNavigationView navigation;
    private PopupWindow popupWindow;
    private boolean isExit;
    private boolean isPermission;
    private boolean isInitData = false;
    private AudioFragment viedeoFragment, imageFragment, recordFragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            isRecord = false;//指定是不是摄像子界面
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //点击录像时运行的代码
                    btChose.setVisibility(View.VISIBLE);//将选择按钮设为可见
                    btChose.setText("选择");//设置选择按钮上的文字为"选择"
                    tvTitle.setText("录屏");//设置TextView标题为"录屏"
                    if (viedeoFragment == null)//判断viedeoFragment是否为空
                        viedeoFragment = new AudioFragment(AudioFragment.TYPE.VIDEO);//如果为空则创建
                    switchFragment(viedeoFragment);//将viedeoFragment子界面加载到主界面上
                    return true;
                case R.id.navigation_dashboard:
                    //点击截图时运行的代码
                    btChose.setVisibility(View.VISIBLE);//将选择按钮设为可见
                    btChose.setText("选择");//设置选择按钮上的文字为"选择"
                    tvTitle.setText("截图");//设置TextView标题为"截图"
                    if (imageFragment == null)//判断imageFragment是否为空
                        imageFragment = new AudioFragment(AudioFragment.TYPE.PICTURE);//如果为空则创建
                    switchFragment(imageFragment);//将imageFragment子界面加载到主界面上
                    return true;
                case R.id.navigation_record:
                    //点击摄像时会运行的代码
                    isRecord = true;//指定是摄像界面（用来判断按钮btChose的点击事件）
                    btChose.setVisibility(View.VISIBLE);
                    btChose.setText("开始");
                    tvTitle.setText("录像");
                    if (recordFragment == null) {
                        File file = new File(getFilesDir(), "Movie");
                        if (!file.exists())
                            file.mkdir();
                        recordFragment = new AudioFragment(AudioFragment.TYPE.MOVIE);
                    }
                    switchFragment(recordFragment);
                    return true;
                case R.id.navigation_notifications:
                    //点击设置时会运行的代码
                    btChose.setVisibility(View.INVISIBLE);
                    tvTitle.setText("设置");
                    if (setFragment == null)
                        setFragment = new SetFragment();
                    switchFragment(setFragment);
                    return true;
                case R.id.navigation_pdf:
                    //点击课件时会运行的代码
                    tvTitle.setText("课件");
                    if (pdfFragment == null)
                        pdfFragment = new PdfFragment();
                    switchFragment(pdfFragment);
                    return true;


            }
            return false;
        }

    };
    private MyApp myApp;

    /**
     * 界面初始化时调用的方法
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("xwl", "oncreate");
        setContentView(R.layout.activity_main);//加载界面
        initView();//初始化view方法（按ctrl,用鼠标点它可以进入该方法）
        initData();//初始化数据（同上）
        event();//初始化按钮点击事件（就是你点了那个按钮后指定系统要运行什么代码）
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case DELETESUCESS:
                        ToastUtil.getInstance(MainActivity.this).showToast("删除成功");//显示对话框
                        mCurrentFrgment.changState();//子界面更新
                        fragmentstate = false;//更新状态值
                        btChose.setText("选择");//设置选择按钮的文字为（选择）
                        popupWindow.dismiss();//让popupWindow消失
                        Intent intent = new Intent();
                        intent.setAction("com.audioeadd");
                        switch (mCurrentFrgment.getType()) {
                            case VIDEO:
                                intent.putExtra("type", "video");//设置类型为video
                                break;
                            case PICTURE:
                                intent.putExtra("type", "image");//设置类型为image,用于区分子界面更新
                                break;
                        }
                        sendBroadcast(intent);//发送广播通知更新子条目(还有多少条记录)
                        break;
                    case 0:
                        isExit = false;
                        break;
                }
            }
        };
    }

    private void initData() {
        toastUtil = ToastUtil.getInstance(this);//初始化对话框对象
        mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);//获取mMediaProjectionManager，用于录屏截图
        myApp = (MyApp) getApplication();//获取Application(保存全局变量，软件打开后，所有的界面都可以拿到的变量)
    }

    private void event() {
        btChose.setOnClickListener(this);//给选择按钮设置点击事件（ctrl+F输入onClick然后蓝色下键头找到onClick方法 switch  在R.id.btchose下为它要运行的代码）
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);//给屏幕下方的导航栏设置子条目点击事件（ctrl+鼠标左键mOnNavigationItemSelectedListener查看详情）
        navigation.setSelectedItemId(R.id.navigation_home);//设置下方导航栏选中录像

    }

    private void initView() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);//绑定界面中的导航条

        tvTitle = (TextView)
                findViewById(R.id.tvTitle);//绑定界面中的标题View

        btChose = (Button)
                findViewById(R.id.btChose);//绑定界面中的选择按钮
    }


    private void startIntent() {
        Log.d("xwl", "startIntent");
        if (intent != null && result != 0) {
            Log.d("xwl", "ssssssss");
            ((MyApp) getApplication()).setResultCode(result);
            ((MyApp) getApplication()).setResultIntent(intent);
            try {
                myApp.getWindow().createVirtualEnvironment();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("zw", "intent null");
            Log.d("zw", "ssssssss");
            startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            ((MyApp) getApplication()).setMpmngr(mMediaProjectionManager);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            } else if (data != null && resultCode != 0) {
                result = resultCode;
                intent = data;
                startIntent();

            }
        }
    }

    private void switchFragment(Fragment fragment) {
        mfragmentTransaction = getFragmentManager().beginTransaction();
        if (null != mCurrentFrgment) {
            mfragmentTransaction.hide(mCurrentFrgment);
        }
        if (!fragment.isAdded()) {
            mfragmentTransaction.add(R.id.content, fragment, fragment.getClass().getName());
        } else {
            mfragmentTransaction.show(fragment);
        }
        mfragmentTransaction.commit();
        mCurrentFrgment = (BaseFragment) fragment;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //得到权限之后去做的业务
                if (viedeoFragment != null) {
                    viedeoFragment.initData(null);
                }
                startIntent();
            } else {//没有获得到权限
                toastUtil.showToast(getString(R.string.permiss_false));
                finish();
            }
        } else if (requestCode == 2) {

            if (permissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, PhotographActivity.class);
                startActivity(intent);
            } else {//没有获得到权限
                toastUtil.showToast(getString(R.string.permiss_false));
            }

        }

    }

    public void showPopupWindow(View v) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_chose, null);//设置popupWindowd的布局,ctrl+鼠标左键点击popupwindow_chose可以看到它的布局
        RelativeLayout rlNeg;
        RelativeLayout rlPos;
        rlNeg = (RelativeLayout) contentView.findViewById(R.id.rlNeg);//绑定确定布局
        rlPos = (RelativeLayout) contentView.findViewById(R.id.rlPos);//绑定取消布局

        if (popupWindow == null) {//如果popWindow为null则新建对象，初始化一些数据（节省资源）
            popupWindow = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.MATCH_PARENT, dip2px(this, 50), true);//新建popupWindow对象
            popupWindow.setTouchable(true);//设置popupWindow可点击
            popupWindow.setFocusable(false);//设置popupWindow失去焦点
        }
        popupWindow.showAsDropDown(v);//设置popupWindow在v的下方显示
        rlPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//设置点击确定时要运行的代码
                final List<String> deletePaths = mCurrentFrgment.getDeletePaths();
                ToastUtil.getInstance(MainActivity.this).showToast("后台删除中");//显示对话框
                new Thread() {
                    //新建一个线程（代码操作文件时,是耗费时间的操作,如果不在线程中运行的话，会导致卡,相当于分一个人出来做事情）
                    @Override
                    public void run() {
                        super.run();
                        //开始运行时，跑的代码
                        for (String path : deletePaths
                                ) {//遍历要删除的集合 deletePaths
                            Log.d("xwl", "delete   path " + path);//输出打印语句
                            File file = new File(path);//新建一个文件对象
                            file.delete();//通过文件对象删除这个文件
                        }
                        handler.sendEmptyMessage(DELETESUCESS);//for循环完成之后，通知运行完成（ctrl+鼠标左键点handler看完成之后要运行什么代码,其中DELESUCESSP用来区分在哪里发的通知）
                    }
                }.start();//线程开始运行

            }
        });
        rlNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//设置点击取消时要运行的代码
                mCurrentFrgment.changState();//更新子界面的状态
                fragmentstate = false;//设置状态值
                btChose.setText("选择");//设置选择按钮上的文字为"选择"
                popupWindow.dismiss();//让popupWindow的消失
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btChose://点击选择按钮时会运行的代码
                if (!isRecord) {//如果isRecord不为空,确定不是摄像界面
                    if (fragmentstate) {//判断子界面的状态
                        mCurrentFrgment.changState();//改变子界面的状态
                        fragmentstate = false;//设置状态值
                        btChose.setText("选择");//将选择按钮的文字设为“选择”
                        if (popupWindow != null)//如果popupWindow不为空
                            popupWindow.dismiss();//让popupWindow消失
                    } else {
                        mCurrentFrgment.changState();//改变子界面的状态
                        fragmentstate = true;
                        btChose.setText("取消");
                        showPopupWindow(findViewById(R.id.content));//显示popupWindow 其中R.id.content为指定的布局
                    }
                } else {
                    //如果是摄像界面
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ?
                                true : false) {//检查是否有拍照权限
                            CameraActivity.enterCamera(this);//高逼格 哈哈哈
                            return;
                        } else {//如果没有
                            requestPermissions(new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.CAMERA}, 2);//申请拍照权限 回调onRequestPermissionsResult方法
                            return;
                        }

                    }
                    CameraActivity.enterCamera(this);//高逼格 哈哈哈
                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //在界面布局加载完成后要运行的代码（解决大黑屏bug）
        if (Build.VERSION.SDK_INT >= 23) {
            isPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ?
                    true : false;//检查是否有读写权限
            if (isPermission && hasFocus && !isInitData) {
                startIntent();
                isInitData = true;
            }
            if (hasFocus && !isPermission) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);

            }
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (popupWindow != null && popupWindow.isShowing()) {
                mCurrentFrgment.changState();
                fragmentstate = false;
                btChose.setText("选择");
                popupWindow.dismiss();
                return true;
            }
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            toastUtil.showToast("再按一次退出程序");
            // 利用handler延迟发送更改状态信息
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            try {

                myApp.getWindow().stopService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
//            finish();
            System.exit(0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            myApp.getWindow().stopService();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
