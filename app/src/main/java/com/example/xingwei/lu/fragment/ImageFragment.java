package com.example.xingwei.lu.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.activity.MainActivity;
import com.example.xingwei.lu.adapter.ImageAdapter;
import com.example.xingwei.lu.base.BaseFragment;
import com.example.xingwei.lu.dialog.RenameDialog;
import com.example.xingwei.lu.modern.ImageModern;
import com.example.xingwei.lu.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:
 */

public class ImageFragment extends BaseFragment {
    private View imageView;
    private TextView tvImagePath;
    private String imagePath;
    private RecyclerView rvImage;
    private ImageAdapter imageAdapter;
    private AudioBroadcastReceiver receiver;
    private RenameDialog renameDialog;

    @Override
    public View initView(LayoutInflater inflater) {
        imageView = inflater.inflate(R.layout.fragment_image, null);
        initFindViewById(imageView);
        return imageView;
    }

    @Override
    public void initFindViewById(View view) {
        tvImagePath = (TextView) view.findViewById(R.id.tvImagePath);
        rvImage = (RecyclerView) view.findViewById(R.id.rvImage);
        imagePath = "LU" + "/Pictures";
        Log.d("xwl", "initData " + imagePath);
        tvImagePath.setText(imagePath);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        tvImagePath.setOnClickListener(this);
    }

    @Override
    public void changState() {
        boolean isChose = !MainActivity.fragmentstate;
        imageAdapter.setShow(isChose);
    }

    @Override
    public List<String> getDeletePaths() {
        return imageAdapter.getDeletePaths();
    }

    @Override
    public void click(View view) {
        if (view == tvImagePath) {
            showToast("进入图片文件夹");
            // TODO: 2017/10/18 进入图片文件夹
            enterImagePath();
        }
    }

    private void enterImagePath() {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        rvImage.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<ImageModern> imageModerns = new FileUtil().getImageInfoByPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LU/Pictures");
        imageAdapter = new ImageAdapter(imageModerns, getActivity(), new ImageAdapter.ImageViewClick() {
            @Override
            public void showImage(String path) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String type = "image/png";
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
                                if (new FileUtil().isRename(newName, FileUtil.IMAGE)) {
                                    showToast("已存在相同文件名");
                                } else {
                                    Log.d("xwl", "old name " + file.getAbsolutePath());
                                    file.renameTo(new File(file.getParent() + "/" + newName + ".png"));

                                    showToast("更新");
                                    List<ImageModern> imageModerns1 = new FileUtil().getImageInfoByPath(
                                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/LU/Pictures"
                                    );
                                    imageAdapter.setData(imageModerns1);
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
        rvImage.setAdapter(imageAdapter);

    }


    @Override
    protected void updateData(String type) {
        super.updateData(type);
        if (type.equals("image")) {
            List<ImageModern> videoModerns = new FileUtil().getImageInfoByPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LU/Pictures");
            Log.d("xwl", "image data size " + videoModerns.size());
            imageAdapter.setData(videoModerns);
        }
    }


}
