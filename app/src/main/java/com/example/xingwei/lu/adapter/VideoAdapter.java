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
import com.example.xingwei.lu.modern.VideoModern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<VideoModern> data;
    private Context context;
    private ViewClick viewClick;
    private boolean isShow;
    private List<String> deletePaths;

    public VideoAdapter(List<VideoModern> data, Context context, ViewClick viewClick) {
        this.data = data;
        this.context = context;
        this.viewClick = viewClick;
        deletePaths = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setData(List<VideoModern> data) {
        this.data = data;
        Collections.reverse(data);
        notifyDataSetChanged();
    }

    public void setShow(boolean show) {
        isShow = show;
        deletePaths.clear();
        notifyDataSetChanged();
    }

    @Override
    public VideoAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_itme, parent, false);
        VideoViewHolder videoViewHolder = new VideoViewHolder(view);
        Log.d("xwl", "onCreateViewHolder ");
        return videoViewHolder;
    }

    public List<String> getDeletePaths() {
        return deletePaths;
    }

    @Override
    public void onBindViewHolder(VideoAdapter.VideoViewHolder holder, final int position) {

        final VideoViewHolder videoViewHolder = holder;
        videoViewHolder.ibRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewClick.rename(data.get(position).getPath());

            }
        });
//        if (data.get(position).getFileName() != null)
//            videoViewHolder.tvFileName.setText(data.get(position).getFileName() + "");
        videoViewHolder.tvTime.setText(data.get(position).getTime() + "      " +
                data.get(position).getDuration());
        Glide.with(context).load(data.get(position).getPath()).into(videoViewHolder.image);
        videoViewHolder.ibRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewClick.rename(data.get(position).getPath());
            }
        });
        if (data.size() - 1 == position) {
            videoViewHolder.last.setVisibility(View.VISIBLE);
        }
        videoViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShow) {
                    viewClick.playVideo(data.get(position).getPath());
                } else {
                    videoViewHolder.chose.setChecked(!videoViewHolder.chose.isChecked());
                }
            }
        });
        videoViewHolder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewClick.share(data.get(position).getPath());
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

    @Override
    public int getItemCount() {

        return data.size();
    }

    public interface ViewClick

    {
        void playVideo(String path);

        void rename(String path);

        void share(String path);
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageView button;
        private TextView tvFileName;
        private ImageButton ibRename;
        private TextView tvTime;
        private ImageButton ibShare;
        private CheckBox chose;
        private View last;


        public VideoViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            button = (ImageView) view.findViewById(R.id.button);
            tvFileName = (TextView) view.findViewById(R.id.tvFileName);
            ibRename = (ImageButton) view.findViewById(R.id.ibRename);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            ibShare = (ImageButton) view.findViewById(R.id.ibShare);
            chose = (CheckBox) view.findViewById(R.id.chose);
            chose.setVisibility(View.INVISIBLE);
            last = view.findViewById(R.id.last);
            last.setVisibility(View.INVISIBLE);
        }


    }
}
