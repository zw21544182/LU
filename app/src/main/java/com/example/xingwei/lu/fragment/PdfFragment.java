package com.example.xingwei.lu.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.adapter.PdfAdapter;
import com.example.xingwei.lu.base.BaseFragment;
import com.example.xingwei.lu.modern.PdfModule;
import com.example.xingwei.lu.service.FileServer;
import com.example.xingwei.lu.util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * 创建时间: 2017/11/8
 * 创建人: Administrator
 * 功能描述:
 */

public class PdfFragment extends BaseFragment {
    private RecyclerView rvPdf;
    private List<PdfModule> pdfModules;
    private PdfAdapter pdfAdapter;
    private ToastUtil toastUtil;

    @Override
    public AudioFragment.TYPE getType() {
        return null;
    }

    @Override
    public void changState() {

    }

    @Override
    public View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_pdf, null);
        initFindViewById(view);
        return view;
    }

    @Override
    public void initFindViewById(View view) {
        rvPdf = (RecyclerView) view.findViewById(R.id.rvPdf);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Log.d("zw", "pdffragment initdata");
        toastUtil = ToastUtil.getInstance(getActivity());
        restartCheckFile();
        rvPdf.setLayoutManager(new LinearLayoutManager(getActivity()));
        pdfModules = DataSupport.findAll(PdfModule.class);
        pdfAdapter = new PdfAdapter(pdfModules, getActivity());
        rvPdf.setAdapter(pdfAdapter);

    }

    private void restartCheckFile() {
        Intent intent = new Intent(getActivity(), FileServer.class);
        if (!isServiceWork(getActivity(), "com.example.xingwei.lu.service.FileServer")) {
            toastUtil.showToast(R.string.startservice);
            getActivity().startService(intent);
        } else {
            toastUtil.showToast(R.string.stopservice);

            getActivity().stopService(intent);
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
            pdfModules = DataSupport.findAll(PdfModule.class);
            if (pdfModules == null || pdfModules.size() == 0) {
                showToast(R.string.no_pdfs);
                return;
            }
            pdfAdapter.setData(pdfModules);
        }
    }

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    public void checkFile() {
        pdfAdapter.clearAll();
        restartCheckFile();
    }
}
