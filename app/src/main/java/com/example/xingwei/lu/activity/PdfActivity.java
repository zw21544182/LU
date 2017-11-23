package com.example.xingwei.lu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.xingwei.lu.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

/**
 * 创建时间: 2017/11/8
 * 创建人: Administrator
 * 功能描述:
 */

public class PdfActivity extends Activity {
    private PDFView pdfView;
    private String pdfPath;

    /**
     * 界面初始化时调用的方法
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);//加载布局
        initView();//绑定布局中的view
        initData();//初始化数据
    }

    /**
     * 初始化数据
     */
    private void initData() {
        pdfPath = getIntent().getExtras().getString("path");//获取上一个界面中传来的名字为path的数据（）
        Log.d("Zw", pdfPath);//控制台打印
        File file = new File(pdfPath);//新建一个file对象，指定文件路径为 pdfPath
        if (!file.exists()) {//如果文件不存在
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();//输出对话框
            finish();//结束activity
            return;//返回
        }
        pdfView.fromFile(file).load();//pdfView加载pdf文件
    }

    /**
     * 绑定View
     */
    private void initView() {
        pdfView = (PDFView) findViewById(R.id.pdfView);//绑定pdfView（其中PdfView为github开源自定义控件,用于展示pdf文件支持缩放，放大等多指操作）
    }
}
