package com.example.xingwei.lu.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.xingwei.lu.R;
import com.github.barteksc.pdfviewer.PDFView;

/**
 * 创建时间: 2017/11/8
 * 创建人: Administrator
 * 功能描述:
 */

public class PdfActivity extends Activity {
    private PDFView pdfView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        initView();
        initData();
    }

    private void initData() {
        pdfView.fromAsset("test.pdf").load();//打开在assets文件夹里面的资源
    }

    private void initView() {
        pdfView = (PDFView) findViewById(R.id.pdfView);

    }
}
