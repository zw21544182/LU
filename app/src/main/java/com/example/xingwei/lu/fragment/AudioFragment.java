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
import android.widget.TextView;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.activity.MainActivity;
import com.example.xingwei.lu.adapter.AudioAdapter;
import com.example.xingwei.lu.base.BaseFragment;
import com.example.xingwei.lu.dialog.RenameDialog;
import com.example.xingwei.lu.modern.AudioModern;
import com.example.xingwei.lu.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:
 */

public class AudioFragment extends BaseFragment {
    private View audioView;
    private RecyclerView rvAudio;
    private TextView tvFileDir;
    private AudioAdapter audioAdapter;
    private MainActivity mainActivity;
    private FileUtil fileUtil;
    private RenameDialog renameDialog;
    private List<AudioModern> audiomoderns;
    private TYPE type;
    private String audioPath = "";

    private AudioFragment() {
    }

    public AudioFragment(TYPE type) {
        this.type = type;
    }

    public enum TYPE {
        PICTURE, VIDEO, MOVIE
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    audioAdapter.clear();
                    break;
                case 1:
                    audiomoderns = (List<AudioModern>) msg.obj;
                    if (audiomoderns != null) {
                        Log.d("xwls", "audiomoderns size" + audiomoderns.size());

                        audioAdapter.setData(audiomoderns);
                    } else {
                        audioAdapter.clear();
                    }
                    break;
            }


        }
    };

    @Override
    public void changState() {
        boolean isChose = !MainActivity.fragmentstate;
        audioAdapter.setShow(isChose);
    }

    @Override
    public View initView(LayoutInflater inflater) {
        audioView = inflater.inflate(R.layout.fragment_video, null);
        initFindViewById(audioView);
        return audioView;
    }

    @Override
    public void initFindViewById(View view) {
        rvAudio = (RecyclerView) view.findViewById(R.id.rvVideo);
        tvFileDir = (TextView) view.findViewById(R.id.tvFileDir);
    }

    @Override
    public TYPE getType() {
        return type;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        switch (type) {
            case PICTURE:
                audioPath = mainActivity.getFilesDir().getAbsolutePath() + "/Pictures";
                break;
            case MOVIE:
                audioPath = mainActivity.getFilesDir().getAbsolutePath() + "/Movie";
                break;
            case VIDEO:
                audioPath = mainActivity.getFilesDir().getAbsolutePath() + "/Video";
                break;
            default:
                audioPath = mainActivity.getFilesDir().getAbsolutePath() + "/Pictures";
                break;

        }
        fileUtil = FileUtil.getInstance(mainActivity);
        if (audiomoderns == null)
            audiomoderns = new ArrayList<>();
        audiomoderns.clear();
        rvAudio.setLayoutManager(new LinearLayoutManager(getContext()));
        audioAdapter = new AudioAdapter(audiomoderns, getActivity(), new AudioAdapter.ViewClick() {
            @Override
            public void playAudio(String path) {
                File file = new File(path);
                if (!file.exists()) {
                    showToast(getString(R.string.no_audio));
                    return;
                }
                openFile(file);
            }

            private void openFile(File f) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String type = getMiType(f);
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
                final File file = new File(path);
                String oldName = file.getName().substring(0, file.getName().length() - 4);
                renameDialog = new RenameDialog(getActivity(), R.layout.dialog_rename, new int[]{R.id.llnegative, R.id.llpostive});
                renameDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.llpostive:
                                String newName = renameDialog.getRenameString();
                                if (fileUtil.isRename(newName, type)) {
                                    showToast("已存在相同文件名");
                                } else {
                                    Log.d("xwl", "old name " + file.getAbsolutePath());
                                    file.renameTo(new File(file.getParent() + "/" + newName + ".mp4"));

                                    showToast("更新");
                                    fileUtil.getAudioInfoByPath(getActivity().getFilesDir().getAbsolutePath() + "/Audio"
                                            , handler);
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
                Intent intent = new Intent();
                File f = new File(path);
                if (!f.exists()) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    Uri u = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", f);
                    intent.putExtra(Intent.EXTRA_STREAM, u);
                }
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra("sms_body", "感谢使用");            //邮件内容
                intent.setType(getMiType(f));                    //设置类型
                getActivity().startActivity(intent);
            }
        });
        rvAudio.setAdapter(audioAdapter);
        fileUtil.getAudioInfoByPath(audioPath, handler);

    }

    private String getMiType(File f) {
        String res = "";
        if (f.getName().endsWith("mp4")) {
            res = "video/*";
        } else if (f.getName().endsWith("png")) {
            res = "image/*";
        }
        return res;
    }

    @Override
    public void click(View view) {

    }

    @Override
    public List<String> getDeletePaths() {
        return audioAdapter.getDeletePaths();
    }

    @Override
    protected void updateData(String type) {
        super.updateData(type);
        switch (AudioFragment.this.type) {
            case VIDEO:
                if (type.equals("video")) {
                    audiomoderns.clear();
                    fileUtil.getAudioInfoByPath(audioPath, handler);
                }
                break;
            case PICTURE:
                if (type.equals("image")) {
                    audiomoderns.clear();
                    fileUtil.getAudioInfoByPath(audioPath, handler);
                }
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (type) {
            case MOVIE:
                fileUtil.getAudioInfoByPath(audioPath, handler);
                break;
        }
    }
}
