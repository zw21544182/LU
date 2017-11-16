package com.example.xingwei.lu.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.adapter.VideoAdapter;
import com.example.xingwei.lu.base.BaseFragment;
import com.example.xingwei.lu.dialog.RenameDialog;
import com.example.xingwei.lu.modern.VideoModern;
import com.example.xingwei.lu.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:
 */

public class RecordFragment extends BaseFragment {
    private View videoView;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LU/Movie";
    private RecyclerView rvVideo;
    private RenameDialog renameDialog;
    private VideoAdapter videoAdapter;
    private FileUtil fileUtil;
    private List<VideoModern> videomoderns;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != 0) {
                VideoModern videoModern = (VideoModern) msg.obj;
                if (videomoderns != null) {
                    videomoderns.add(videoModern);
                    videoAdapter.setData(videomoderns);
                }
            }
        }
    };

    @Override
    public void changState() {

    }

    public RecordFragment(String path) {
        this.path = path;
    }

    @Override
    public void onResume() {
        super.onResume();
        videomoderns.clear();
        fileUtil.getVideoInfoByPath(path, handler);
        videoAdapter.setData(videomoderns);
    }

    @Override
    public View initView(LayoutInflater inflater) {
        videoView = inflater.inflate(R.layout.fragment_record, null);
        initFindViewById(videoView);
        return videoView;
    }

    @Override
    public void initFindViewById(View view) {
        rvVideo = (RecyclerView) view.findViewById(R.id.rvVideo);

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        fileUtil = new FileUtil();
        videomoderns = new ArrayList<>();
        rvVideo.setLayoutManager(new LinearLayoutManager(getContext()));
        videoAdapter = new VideoAdapter(videomoderns, getActivity(), new VideoAdapter.ViewClick() {
            @Override
            public void playVideo(String path) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String type = "*/*";
                Uri uri = Uri.parse(path);
                intent.setDataAndType(uri, type);
                startActivity(intent);
            }

            @Override
            public void rename(final String path) {
                Log.d("XWL", "FILE PATH" + path);
                final File file = new File(path);
                String oldName = file.getName().substring(0, file.getName().length() - 4);
                renameDialog = new RenameDialog(getActivity(), R.layout.dialog_rename, new int[]{R.id.llnegative, R.id.llpostive});
                renameDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.llpostive:
                                String newName = renameDialog.getRenameString();
                                if (new FileUtil().isRename(newName, FileUtil.MOVIE)) {
                                    showToast("已存在相同文件名");
                                } else {
                                    Log.d("xwl", "old name " + file.getAbsolutePath());
                                    file.renameTo(new File(file.getParent() + "/" + newName + ".mp4"));
                                    showToast("更新");
                                    videomoderns.clear();
                                    new FileUtil().getVideoInfoByPath(path, handler
                                    );
                                    videoAdapter.setData(videomoderns);
                                    renameDialog.dismiss();

                                }

                                break;
                            case R.id.llnegative:
                                renameDialog.dismiss();
                                break;

                        }
                    }
                });
                renameDialog.show();
                renameDialog.setRenameString(oldName);
            }

            @Override
            public void share(String path) {

                Uri uri = Uri.parse(path);   //图片路径
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra("sms_body", "感谢使用");            //邮件内容
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/png");                    //设置类型
                getActivity().startActivity(intent);
            }
        });
        rvVideo.setAdapter(videoAdapter);
    }

    @Override
    public void click(View view) {

    }
}
