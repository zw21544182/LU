package com.example.xingwei.lu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.activity.PdfActivity;
import com.example.xingwei.lu.modern.PdfModule;
import com.example.xingwei.lu.util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

/**
 * 创建时间: 2017/11/8
 * 创建人: Administrator
 * 功能描述:
 */

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfHolder> {
    private List<PdfModule> pdfModules;
    private Context context;

    public PdfAdapter(List<PdfModule> pdfModules, Context context) {
        this.pdfModules = pdfModules;
        this.context = context;
    }

    public void setData(List<PdfModule> data) {
        pdfModules.clear();
        pdfModules.addAll(data);
        notifyDataSetChanged();
    }

    public void clearAll() {
        pdfModules.clear();
        notifyDataSetChanged();
    }

    @Override
    public PdfHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_itme, parent, false);
        PdfHolder pdfHolder = new PdfHolder(view);
        return pdfHolder;

    }

    @Override
    public void onBindViewHolder(PdfHolder holder, final int position) {
        holder.tvPdfModifyTime.setText("修改于:" + pdfModules.get(position).getTime());
        holder.tvPdfName.setText(pdfModules.get(position).getName());
        holder.llRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pdfPath = pdfModules.get(position).getPath();
                File file = new File(pdfPath);//新建一个file对象，指定文件路径为 pdfPath
                if (!file.exists()) {//如果文件不存在
                    DataSupport.delete(PdfModule.class, DataSupport.where("path = ?", pdfPath).find(PdfModule.class).get(0).getId());
                    pdfModules.remove(position);
                    notifyDataSetChanged();
                    ToastUtil.getInstance(context).showToast(R.string.no_pdf);
                    return;//返回
                }
                Intent intent = new Intent(context, PdfActivity.class);
                intent.putExtra("path", pdfModules.get(position).getPath());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfModules.size();
    }

    public class PdfHolder extends RecyclerView.ViewHolder {
        TextView tvPdfName;
        TextView tvPdfModifyTime;
        LinearLayout llRootView;

        public PdfHolder(View itemView) {
            super(itemView);
            llRootView = (LinearLayout) itemView.findViewById(R.id.llRootView);
            tvPdfName = (TextView) itemView.findViewById(R.id.tvPdfName);
            tvPdfModifyTime = (TextView) itemView.findViewById(R.id.tvPdfModifyTime);

        }
    }
}
