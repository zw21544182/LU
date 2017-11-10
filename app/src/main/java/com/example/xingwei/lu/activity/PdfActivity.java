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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        initView();
        initData();
    }

    private void initData() {
        pdfPath = getIntent().getExtras().getString("path");
        Log.d("Zw", pdfPath);
        File file = new File(pdfPath);
        if (!file.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        pdfView.fromFile(file).load();
    }

    private void initView() {
        pdfView = (PDFView) findViewById(R.id.pdfView);

    }
}
