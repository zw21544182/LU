package com.example.xingwei.lu.fragment;

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
        Intent intent = new Intent(getActivity(), FileServer.class);
        getActivity().startService(intent);
        rvPdf.setLayoutManager(new LinearLayoutManager(getActivity()));
        pdfModules = DataSupport.findAll(PdfModule.class);

            pdfAdapter = new PdfAdapter(pdfModules, getActivity());
            rvPdf.setAdapter(pdfAdapter);



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
            pdfModules = DataSupport.findAll(PdfModule.class);
            if (pdfModules.size() == 0) {
                showToast("暂无pdf文件");
                return;
            }
            pdfAdapter.setData(pdfModules);
        }
    }
}
