package com.example.xingwei.lu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xingwei.lu.R;
import com.example.xingwei.lu.modern.ImageModern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<ImageModern> data;
    private Context context;
    private ImageViewClick imageViewClick;
    private boolean isShow;
    private List<String> deletePaths;

    public ImageAdapter(List<ImageModern> data, Context context, ImageViewClick imageViewClick) {
        this.data = data;
        Collections.reverse(data);
        this.context = context;
        this.imageViewClick = imageViewClick;
        deletePaths = new ArrayList<>();
    }

    public void setShow(boolean show) {
        isShow = show;
        deletePaths.clear();
        notifyDataSetChanged();
    }

    public void setData(List<ImageModern> data) {
        this.data = data;
        Collections.reverse(data);
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_itme, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);
        Log.d("xwl", "onCreateViewHolder ");
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        Log.d("xwl", "image onBindViewHolder");
        final ImageViewHolder videoViewHolder = holder;
        videoViewHolder.tvFileName.setText(data.get(position).getFileName());
        videoViewHolder.tvTime.setText(data.get(position).getTime());
        Glide.with(context).load(data.get(position).getPath()).into(videoViewHolder.image);
        videoViewHolder.button.setVisibility(View.INVISIBLE);
        videoViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShow) {
                    imageViewClick.showImage(data.get(position).getPath());
                } else {
                    videoViewHolder.chose.setChecked(!videoViewHolder.chose.isChecked());
                }
            }
        });
        videoViewHolder.ibRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewClick.rename(data.get(position).getPath());

            }
        });
        if (data.size() - 1 == position) {
            videoViewHolder.last.setVisibility(View.VISIBLE);
        }
        videoViewHolder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewClick.share(data.get(position).getPath());
            }
        });
        videoViewHolder.chose.setChecked(false);
        if (isShow) {
            videoViewHolder.chose.setVisibility(View.VISIBLE);
            videoViewHolder.chose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        deletePaths.add(data.get(position).getPath());
                    } else {
                        deletePaths.remove(data.get(position).getPath());
                    }
                    Log.d("xwl", "delete date size " + deletePaths.size());
                }
            });
        } else {
            videoViewHolder.chose.setVisibility(View.INVISIBLE);

        }
    }

    public List<String> getDeletePaths() {
        return deletePaths;
    }

    public interface ImageViewClick {
        /**
         * 显示图片时要执行的代码
         * @param path 文件路径
         */
        void showImage(String path);

        /**
         * 重命名时要执行的代码
         * @param path 文件路径
         */
        void rename(String path);

        /**
         * 分享时要执行的代码
         * @param path  文件路径
         */
        void share(String path);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageView button;
        private TextView tvFileName;
        private ImageButton ibRename;
        private TextView tvTime;
        private CheckBox chose;
        private View last;
        private ImageButton ibShare;


        public ImageViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            button = (ImageView) view.findViewById(R.id.button);
            tvFileName = (TextView) view.findViewById(R.id.tvFileName);
            ibRename = (ImageButton) view.findViewById(R.id.ibRename);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            chose = (CheckBox) view.findViewById(R.id.chose);
            chose.setVisibility(View.INVISIBLE);
            last = (View) view.findViewById(R.id.last);
            ibShare = (ImageButton) view.findViewById(R.id.ibShare);

        }


    }
}
