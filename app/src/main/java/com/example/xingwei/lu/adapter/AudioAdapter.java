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
import com.example.xingwei.lu.modern.AudioModern;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {
    private List<AudioModern> data;
    private Context context;
    private ViewClick viewClick;
    private boolean isShow;
    private List<String> deletePaths;

    public AudioAdapter(List<AudioModern> data, Context context, ViewClick viewClick) {
        this.data = data;
        this.context = context;
        this.viewClick = viewClick;
        deletePaths = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setData(List<AudioModern> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void setShow(boolean show) {
        isShow = show;
        deletePaths.clear();
        notifyDataSetChanged();
    }

    @Override
    public AudioAdapter.AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_itme, parent, false);
        AudioViewHolder audioViewHolder = new AudioViewHolder(view);
        Log.d("xwl", "onCreateViewHolder ");
        return audioViewHolder;
    }

    public List<String> getDeletePaths() {
        return deletePaths;
    }

    @Override
    public void onBindViewHolder(AudioAdapter.AudioViewHolder holder, final int position) {

        final AudioViewHolder audioViewHolder = holder;
        audioViewHolder.ibRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewClick.rename(data.get(position).getPath());

            }
        });
        audioViewHolder.tvFileName.setText(data.get(position).getFileName() + "");
        audioViewHolder.tvTime.setText(data.get(position).getTime() + "      " +
                data.get(position).getDuration());
        audioViewHolder.button.setVisibility(data.get(position).getFileName().endsWith("mp4") ? View.VISIBLE : View.INVISIBLE);
        Glide.with(context).load(data.get(position).getPath()).into(audioViewHolder.image);
        audioViewHolder.ibRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewClick.rename(data.get(position).getPath());
            }
        });
        if (data.size() - 1 == position) {
            audioViewHolder.last.setVisibility(View.VISIBLE);
        }
        audioViewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isShow) {
                    viewClick.playAudio(data.get(position).getPath());
                } else {
                    audioViewHolder.chose.setChecked(!audioViewHolder.chose.isChecked());
                }
            }
        });
        audioViewHolder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewClick.share(data.get(position).getPath());
            }
        });
        audioViewHolder.chose.setChecked(false);
        if (isShow) {
            audioViewHolder.chose.setVisibility(View.VISIBLE);
            audioViewHolder.chose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
            audioViewHolder.chose.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public interface ViewClick

    {
        void playAudio(String path);

        void rename(String path);

        void share(String path);
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private ImageView button;
        private TextView tvFileName;
        private ImageButton ibRename;
        private TextView tvTime;
        private ImageButton ibShare;
        private CheckBox chose;
        private View last;


        public AudioViewHolder(View view) {
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
