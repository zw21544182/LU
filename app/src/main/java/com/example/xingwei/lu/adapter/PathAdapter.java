package com.example.xingwei.lu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.modern.PdfPathMoudle;

import java.util.List;

/**
 * 创建时间: 2017/11/28
 * 创建人: Administrator
 * 功能描述:
 */

public class PathAdapter extends RecyclerView.Adapter<PathAdapter.ViewHolder> {
    private List<PdfPathMoudle> data;
    private Context context;

    public PathAdapter(List<PdfPathMoudle> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setData(List<PdfPathMoudle> data) {
        this.data.clear();
        this.data.addAll(data);
        Log.d("ZWS", "data size " + data.size());
        notifyDataSetChanged();
    }

    public void addData(PdfPathMoudle content) {
        this.data.add(content);
        notifyDataSetChanged();
    }

    public PdfPathMoudle getDataByPostion(int postion) {
        if (data.size() <= postion)
            return null;
        return data.get(postion);
    }

    public List<PdfPathMoudle> getAllData() {
        return data;
    }

    public void clearAllData() {
        this.data.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_itmepath, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String s = data.get(position).getPath();
        holder.tvPath.setText(s.substring(s.lastIndexOf("/")));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPath;


        public ViewHolder(View view) {
            super(view);
            tvPath = (TextView) view.findViewById(R.id.tvPath);

        }
    }
}
