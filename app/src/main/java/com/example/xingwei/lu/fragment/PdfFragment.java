package com.example.xingwei.lu.fragment;

import android.app.ProgressDialog;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.base.BaseFragment;
import com.example.xingwei.lu.modern.PdfModule;
import com.example.xingwei.lu.util.FileUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

/**
 * 创建时间: 2017/11/8
 * 创建人: Administrator
 * 功能描述:
 */

public class PdfFragment extends BaseFragment {
    private RecyclerView rvPdf;
    private FileUtil fileUtil;
    private ProgressDialog progressDialog;
    private List<PdfModule> pdfModules;
    private Handler handler;
    private static final int GETPDFSUCESS = 33;
    private PdfDocument pdfDocument;

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
        fileUtil = new FileUtil();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        };
        if (DataSupport.findAll(PdfModule.class).size() == 0) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("pdf文件扫描中");
            }
            new Thread() {
                public void run() {
                    super.run();
                    fileUtil.initAllPdfFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));
                    pdfModules = DataSupport.findAll(PdfModule.class);
                    handler.sendEmptyMessage(GETPDFSUCESS);
                }
            }.start();
        }

    }

    @Override
    public List<String> getDeletePaths() {
        return super.getDeletePaths();
    }

    @Override
    public void click(View view) {

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
