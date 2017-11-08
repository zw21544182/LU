package com.example.xingwei.lu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.base.BaseFragment;

import java.io.File;

/**
 * 创建时间: 2017/11/8
 * 创建人: Administrator
 * 功能描述:
 */

public class PdfFragment extends BaseFragment {
    private RecyclerView rvPdf;


    @Override
    public void changState() {

    }

    @Override
    public View initView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_pdf, null);
    }

    @Override
    public void initFindViewById(View view) {
        rvPdf = (RecyclerView) view.findViewById(R.id.rvPdf);

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void click(View view) {

    }

    private void getAllFiles(File root) {

        File files[] = root.listFiles();

        if (files != null)
            for (File f : files) {

                if (f.isDirectory()) {
                    getAllFiles(f);
                } else {
                    if (f.getName().contains(".pdf")) ;
                }
            }
    }

    @Override
    protected void updateData(String type) {
        super.updateData(type);
        if (type.equals("pdf")) ;
        {
            Log.d("Zw", "update pdfUI");
            // TODO: 2017/11/8 更新pdfUI操作
        }
    }
}
