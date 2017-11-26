package com.example.xingwei.lu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.xingwei.lu.R;

/**
 * 创建时间: 2017/11/26
 * 创建人: Administrator
 * 功能描述:
 */

public class ImageActivity extends Activity {
    private ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        setContentView(R.layout.activity_image);
        image = (ImageView) findViewById(R.id.image);
        String imagePath = getIntent().getExtras().getString("path");
        Glide.with(this).load(imagePath).into(image);
    }
}
