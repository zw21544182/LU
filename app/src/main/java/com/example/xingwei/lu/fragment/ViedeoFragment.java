package com.example.xingwei.lu.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.activity.MainActivity;
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

public class ViedeoFragment extends BaseFragment {
    private View videoView;
    private RecyclerView rvVideo;
    private VideoAdapter videoAdapter;
    private FileUtil fileUtil;
    private RenameDialog renameDialog;
    private List<VideoModern> videomoderns;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    videoAdapter.setData(videomoderns);
                    break;
                case 1:
                    videomoderns = (List<VideoModern>) msg.obj;
                    if (videomoderns != null) {
                        videoAdapter.setData(videomoderns);
                    }
                    break;
            }


        }
    };

    @Override
    public void changState() {
        boolean isChose = !MainActivity.fragmentstate;
        videoAdapter.setShow(isChose);
    }

    @Override
    public View initView(LayoutInflater inflater) {
        videoView = inflater.inflate(R.layout.fragment_video, null);
        Log.d("xwl", "video initview");
        initFindViewById(videoView);
        return videoView;
    }

    @Override
    public void initFindViewById(View view) {
        rvVideo = (RecyclerView) view.findViewById(R.id.rvVideo);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Log.d("xwl", "VideoFragment initData");
        fileUtil = new FileUtil();
        videomoderns = new ArrayList<>();
        rvVideo.setLayoutManager(new LinearLayoutManager(getContext()));
        videoAdapter = new VideoAdapter(videomoderns, getActivity(), new VideoAdapter.ViewClick() {
            @Override
            public void playVideo(String path) {
                File file = new File(getActivity().getFilesDir().getAbsolutePath() + "/Video", path);
                if (!file.exists()) {
                    showToast(getString(R.string.no_video));
                    return;
                }
                openFile(file);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                File file = new File(getActivity().getFilesDir() + "/Video", path);
//                if (!file.exists()) {
//                    showToast(getString(R.string.no_video));
//                    return;
//                }
//                Uri contentUri = Uri.parse(file.getAbsolutePath());
//                Log.d("xwls", "playVideo");
//                if (Build.VERSION.SDK_INT >= 24) {
//                    Log.d("xwls", "api 24");
//
//                    File imagePath = new File(getActivity().getFilesDir(), "Video");
//                    File newFile = new File(imagePath, path);
//                    contentUri = FileProvider.getUriForFile(getContext(), "com.example.xingwei.lu.provider", newFile);
//                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
//                }
//                intent.setDataAndType(contentUri, "video/*");
//                startActivity(intent);
            }

            private void openFile(File f) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                String type = "video/*";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri u = FileProvider.getUriForFile(getActivity(), "com.example.xingwei.lu.provider", f);
                    intent.setDataAndType(u, type);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    intent.setDataAndType(Uri.fromFile(f), type);
                }
                startActivity(intent);
            }

            @Override
            public void rename(String path) {
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
                                if (new FileUtil().isRename(newName, FileUtil.VIDEO)) {
                                    showToast("已存在相同文件名");
                                } else {
                                    Log.d("xwl", "old name " + file.getAbsolutePath());
                                    file.renameTo(new File(file.getParent() + "/" + newName + ".mp4"));

                                    showToast("更新");
                                    List<VideoModern> imageModerns1 = new FileUtil().getVideoInfoByPath(getActivity().getFilesDir().getAbsolutePath() + "/Video"
                                    );
                                    videoAdapter.setData(imageModerns1);
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

                Uri uri = Uri.parse("content://" + getActivity().getPackageName() + "/Video/" + path);   //图片路径
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra("sms_body", "感谢使用");            //邮件内容
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType("image/png");                    //设置类型
                getActivity().startActivity(intent);
            }
        });
        rvVideo.setAdapter(videoAdapter);
        fileUtil.getVideoInfoByPath(getActivity().getFilesDir().getAbsolutePath() + "/Video", handler);

    }

    @Override
    public void click(View view) {

    }

    @Override
    public List<String> getDeletePaths() {
        return videoAdapter.getDeletePaths();
    }

    @Override
    protected void updateData(String type) {
        super.updateData(type);
        if (type.equals("video")) {
            videomoderns.clear();
            fileUtil.getVideoInfoByPath(getActivity().getFilesDir().getAbsolutePath() + "/Video", handler);
        }
    }


}
