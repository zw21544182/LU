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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.example.xingwei.lu.fragment.ImageFragment;
import com.example.xingwei.lu.fragment.RecordFragment;
import com.example.xingwei.lu.fragment.SetFragment;
import com.example.xingwei.lu.fragment.ViedeoFragment;
import com.example.xingwei.lu.service.MainService;
import com.example.xingwei.lu.util.ToastUtil;

import java.io.File;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ToastUtil toastUtil;
    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private FragmentTransaction mfragmentTransaction;
    private BaseFragment mCurrentFrgment;
    private ViedeoFragment viedeoFragment;
    private ImageFragment imageFragment;
    private RecordFragment recordFragment;
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
    private Intent serviceIntent;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            isRecord = false;
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    btChose.setVisibility(View.VISIBLE);
                    btChose.setText("选择");

                    tvTitle.setText("录屏");
                    if (viedeoFragment == null)
                        viedeoFragment = new ViedeoFragment();
                    switchFragment(viedeoFragment);
                    return true;
                case R.id.navigation_dashboard:
                    btChose.setVisibility(View.VISIBLE);
                    btChose.setText("选择");

                    tvTitle.setText("截图");
                    if (imageFragment == null)
                        imageFragment = new ImageFragment();
                    switchFragment(imageFragment);
                    return true;
                case R.id.navigation_record:
                    isRecord = true;
                    btChose.setVisibility(View.VISIBLE);
                    btChose.setText("开始");
                    tvTitle.setText("录像");
                    if (recordFragment == null)
                        recordFragment = new RecordFragment(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LU/Movie");
                    switchFragment(recordFragment);
                    return true;
                case R.id.navigation_notifications:
                    btChose.setVisibility(View.INVISIBLE);
                    tvTitle.setText("设置");
                    if (setFragment == null)
                        setFragment = new SetFragment();
                    switchFragment(setFragment);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("xwl", "oncreate");
        setContentView(R.layout.activity_main);
        initView();
        event();
        onCallPermission();
        toastUtil = ToastUtil.getInstance(this);
        if (savedInstanceState == null) {
            mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case DELETESUCESS:
                        ToastUtil.getInstance(MainActivity.this).showToast("删除成功");
                        mCurrentFrgment.changState();
                        fragmentstate = false;
                        btChose.setText("选择");
                        popupWindow.dismiss();
                        Intent intent = new Intent();
                        intent.setAction("com.audioeadd");
                        sendBroadcast(intent);
                        break;
                    case 0:
                        isExit = false;
                        break;
                }
            }
        };
    }

    private void event() {
        btChose.setOnClickListener(this);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

    }

    private void initView() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        tvTitle = (TextView)
                findViewById(R.id.tvTitle);

        btChose = (Button)
                findViewById(R.id.btChose);
    }


    public void onCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//判断当前系统的SDK版本是否大于23
            //如果当前申请的权限没有授权
            if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                //第一次请求权限的时候返回false,第二次shouldShowRequestPermissionRationale返回true
                //如果用户选择了“不再提醒”永远返回false。
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                }
                //请求权限
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {//已经授权了就走这条分支
                createRootPath();
                startIntent();
            }
        }
    }

    private void createRootPath() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "LU");
        if (!file.exists()) {
            file.mkdir();
        }


    }

    private void startIntent() {
        Log.d("xwl", "startIntent");
        if (intent != null && result != 0) {
            ((MyApp) getApplication()).setResultCode(result);
            ((MyApp) getApplication()).setResultIntent(intent);
            serviceIntent = new Intent(getApplicationContext(), MainService.class);
            startService(serviceIntent);
        } else {
            if (mMediaProjectionManager != null) {
                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
                ((MyApp) getApplication()).setMpmngr(mMediaProjectionManager);
            }
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
                createRootPath();
                startIntent();
            } else {//没有获得到权限
                toastUtil.showToast(getString(R.string.permiss_false));
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btChose:
                if (!isRecord) {
                    if (fragmentstate) {
                        mCurrentFrgment.changState();
                        fragmentstate = false;
                        btChose.setText("选择");
                        if (popupWindow != null)
                            popupWindow.dismiss();
                    } else {
                        mCurrentFrgment.changState();
                        fragmentstate = true;
                        btChose.setText("取消");
                        showPopupWindow(findViewById(R.id.content));
                    }
                } else {
                    Intent intent = new Intent(this, PhotographActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    public void showPopupWindow(View v) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_chose, null);
        RelativeLayout rlNeg;
        RelativeLayout rlPos;
        rlNeg = (RelativeLayout) contentView.findViewById(R.id.rlNeg);
        rlPos = (RelativeLayout) contentView.findViewById(R.id.rlPos);

        if (popupWindow == null) {
            popupWindow = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.MATCH_PARENT, dip2px(this, 50), true);
            popupWindow.setTouchable(true);
            popupWindow.setFocusable(false);
        }
        popupWindow.showAsDropDown(v);
        rlPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<String> deletePaths = mCurrentFrgment.getDeletePaths();
                ToastUtil.getInstance(MainActivity.this).showToast("后台删除中");
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        for (String path : deletePaths
                                ) {
                            Log.d("xwl", "delete   path " + path);
                            File file = new File(path);
                            file.delete();
                        }
                        handler.sendEmptyMessage(DELETESUCESS);
                    }
                }.start();

            }
        });
        rlNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentFrgment.changState();
                fragmentstate = false;
                btChose.setText("选择");
                popupWindow.dismiss();
            }
        });
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
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("xwl", "activity destroy");
        stopService(serviceIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);    //将这一行注释掉，阻止activity保存fragment的状态,解决Fragment穿透重叠现象
    }
}
