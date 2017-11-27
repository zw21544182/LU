package com.example.xingwei.lu.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.xingwei.lu.R;

import java.io.FileNotFoundException;

/**
 * 创建时间: 2017/11/26
 * 创建人: Administrator
 * 功能描述:
 */

public class ImageActivity extends Activity {
    private ImageView image;
    private ParcelFileDescriptor mInputPFD;

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
        Intent returnIntent = getIntent();
        String action = returnIntent.getAction();
        String type = returnIntent.getType();
        Uri returnUri = null;
        if (action.equals(Intent.ACTION_VIEW) && type.equals("image/*")) {
            returnUri = returnIntent.getData();

        } else if (action.equals(Intent.ACTION_SEND)) {
            returnUri = returnIntent.getParcelableExtra(Intent.EXTRA_STREAM);

        }
        try {
            mInputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(mInputPFD.getFileDescriptor());
        image.setImageBitmap(bitmap);
    }
}
