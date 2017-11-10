package com.example.xingwei.lu.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.base.BaseFragment;
import com.example.xingwei.lu.util.SharedPreferencesUtil;
import com.example.xingwei.lu.util.ToastUtil;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:
 */

public class SetFragment extends BaseFragment {
    private static final int SELECT_PHOTO = 88;
    private View settingView;
    private CircleImageView imageView;
    private LinearLayout about;
    private LinearLayout update;
    private LinearLayout size;
    private LinearLayout weixin;
    private ToastUtil toastUtil;
    private String headImagePath;

    @Override
    public void changState() {

    }

    @Override
    public View initView(LayoutInflater inflater) {
        settingView = inflater.inflate(R.layout.fragment_setting, null);
        initFindViewById(settingView);
        return settingView;
    }

    /**
     * 打开相册的方法
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (null != data) {
                    Uri imageUri = data.getData();
                    String imagePath = getImagePathFromUri(getActivity(), imageUri);
                    SharedPreferencesUtil.setParam(getActivity(), "headimage", imagePath);
                    imageView.setImageURI(imageUri);
                } else {
                    showToast("获取头像失败");
                }
                break;
        }
    }

    @Override
    public void initFindViewById(View view) {
        imageView = (CircleImageView) view.findViewById(R.id.imageView);
        about = (LinearLayout) view.findViewById(R.id.about);
        update = (LinearLayout) view.findViewById(R.id.update);
        size = (LinearLayout) view.findViewById(R.id.size);
        weixin = (LinearLayout) view.findViewById(R.id.weixin);

    }

    @Override
    protected void initEvent() {
        super.initEvent();
        imageView.setOnClickListener(this);

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        toastUtil = ToastUtil.getInstance(getActivity());
        headImagePath = (String) SharedPreferencesUtil.getParam(getActivity(), "headimage", "");
        imageView.setImageURI(Uri.parse(headImagePath));
    }


    @Override
    public void click(View view) {
        switch (view.getId()) {
            case R.id.imageView:
                openAlbum();
                break;
        }
    }


    public String getImagePathFromUri(final Context context, Uri picUri) {
        // 选择的图片路径
        String selectPicPath = null;
        Uri selectPicUri = picUri;

        final String scheme = picUri.getScheme();
        if (picUri != null && scheme != null) {
            if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                // content://开头的uri
                Cursor cursor = context.getContentResolver().query(picUri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // 取出文件路径
                    selectPicPath = cursor.getString(columnIndex);

                    // Android 4.1 更改了SD的目录，sdcard映射到/storage/sdcard0
                    if (!selectPicPath.startsWith("/storage") && !selectPicPath.startsWith("/mnt")) {
                        // 检查是否有"/mnt"前缀
                        selectPicPath = "/mnt" + selectPicPath;
                    }
                    //关闭游标
                    cursor.close();
                }
            } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {// file:///开头的uri
                // 替换file://
                selectPicPath = selectPicUri.toString().replace("file://", "");
                int index = selectPicPath.indexOf("/sdcard");
                selectPicPath = index == -1 ? selectPicPath : selectPicPath.substring(index);
                if (!selectPicPath.startsWith("/mnt")) {
                    // 加上"/mnt"头
                    selectPicPath = "/mnt" + selectPicPath;
                }
            }
        }
        return selectPicPath;
    }
}
