package com.example.xingwei.lu.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.adapter.PathAdapter;
import com.example.xingwei.lu.modern.PdfPathMoudle;

import org.litepal.crud.DataSupport;

import java.util.List;

import li.filedirchoose.ChooseFileActivity;

/**
 * 创建时间: 2017/11/28
 * 创建人: Administrator
 * 功能描述:
 */

public class PdfFilePathActivty extends Activity implements View.OnClickListener {
    private List<PdfPathMoudle> pdfPathMoudles;
    private PathAdapter pathAdapter;
    private LinearLayout container;
    private TextView tvTitle;
    private Button btChose;
    private RecyclerView rvFilePath;
    private static final int REQUESCODE = 34;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filepath);
        initView();
        initData();
    }

    private void initData() {
        pdfPathMoudles = DataSupport.findAll(PdfPathMoudle.class);
        pathAdapter = new PathAdapter(pdfPathMoudles, this);
        rvFilePath.setAdapter(pathAdapter);
    }

    private void initView() {
        container = (LinearLayout) findViewById(R.id.container);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        btChose = (Button) findViewById(R.id.btChose);
        btChose.setOnClickListener(this);
        rvFilePath = (RecyclerView) findViewById(R.id.rvFilePath);
        rvFilePath.setLayoutManager(new LinearLayoutManager(this));
        tvTitle.setText(getString(R.string.pdfpath));
    }

    @Override
    public void onClick(View view) {
        ChooseFileActivity.enterActivityForResult(this, REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESCODE && resultCode == ChooseFileActivity.RESULTCODE) {
            List<String> strings = data.getStringArrayListExtra(ChooseFileActivity.SELECTPATH);
            Log.d("ZWW", "DATA SIZE " + strings.size());
            if (null != strings) {
                PdfPathMoudle pdfPathMoudle = new PdfPathMoudle();
                for (String s : strings) {
                    pdfPathMoudle.setPath(s);
                    pdfPathMoudle.saveOrUpdate("path = ?", pdfPathMoudle.getPath());
                    pdfPathMoudle.clearSavedState();
                }
                pdfPathMoudles = DataSupport.findAll(PdfPathMoudle.class);
                Log.d("ZWW", "LITEPAL SIZE " + pdfPathMoudles.size());

                pathAdapter.setData(pdfPathMoudles);
            }
        }
    }
}
