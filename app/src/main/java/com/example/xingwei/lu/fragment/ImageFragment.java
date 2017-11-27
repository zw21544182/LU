package com.example.xingwei.lu.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.activity.ImageActivity;
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
        //子界面初始化方法
        imageView = inflater.inflate(R.layout.fragment_image, null);//初始化界面
        initFindViewById(imageView);//绑定View
        return imageView;
    }

    @Override
    public void initFindViewById(View view) {
        tvImagePath = (TextView) view.findViewById(R.id.tvImagePath);//绑定tvImagePath
        rvImage = (RecyclerView) view.findViewById(R.id.rvImage);//绑定rvImage
        imagePath = "LU" + "/Pictures";
        Log.d("xwl", "initData " + imagePath);
        tvImagePath.setText(imagePath);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        tvImagePath.setOnClickListener(this);//设置点击事件（在onclick中查看）
    }

    @Override
    public void changState() {
        boolean isChose = !MainActivity.fragmentstate;//从MainActivity中获取fragmentState的值
        imageAdapter.setShow(isChose);//改变rvImage中展示的内容
    }

    /**
     * 返回选中的条目文件路径
     *
     * @return 为List集合
     */
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

    /**
     * 进入图片文件夹
     */
    private void enterImagePath() {
        File file = new File(getActivity().getFilesDir().getAbsolutePath() + "/LU", "Pictures");
        if (!file.exists()) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //设置intent的data和Type属性。android 7.0以上crash,改用provider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri fileUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);//android 7.0以上
            intent.setDataAndType(fileUri, "*/*");
            grantUriPermission(getActivity(), fileUri, intent);
        } else {
            intent.setDataAndType(/*uri*/Uri.fromFile(file), "*/*");
        }
        //跳转
        startActivity(intent);


    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        rvImage.setLayoutManager(new LinearLayoutManager(getActivity()));//设置rvImage显示时的布局参数
        List<ImageModern> imageModerns = new FileUtil().getImageInfoByPath(getActivity().getFilesDir().getAbsolutePath() + "/Pictures");//从文件工具类中获取集合
        //新建一个适配器
        imageAdapter = new ImageAdapter(imageModerns, getActivity(), new ImageAdapter.ImageViewClick() {

            @Override
            public void showImage(String path) {

                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);//打开新的activity查看图片（系统自带activty）
            }

            @Override
            public void rename(final String path) {
                Log.d("XWL", "FILE PATH" + path);
                final File file = new File(path);//新建file对象
                String oldName = file.getName().substring(0, file.getName().length() - 4);//去掉后缀名
                renameDialog = new RenameDialog(getActivity(), R.layout.dialog_rename, new int[]{R.id.llnegative, R.id.llpostive});
                renameDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.llpostive:
                                String newName = renameDialog.getRenameString();
                                if (new FileUtil().isRename(newName, FileUtil.IMAGE)) {
                                    //判断是否重名
                                    showToast("已存在相同文件名");//显示对话框
                                } else {
                                    Log.d("xwl", "old name " + file.getAbsolutePath());
                                    file.renameTo(new File(file.getParent() + "/" + newName + ".png"));//通过file对象重命名为用户输入的文本
                                    showToast("更新");//显示对话框
                                    List<ImageModern> imageModerns1 = new FileUtil().getImageInfoByPath(
                                            getActivity().getFilesDir().getAbsolutePath() + "/Pictures"
                                    );//获取新的文件集合
                                    imageAdapter.setData(imageModerns1);//将集合设置进适配器
                                    renameDialog.dismiss();//让重命名对话框消失

                                }

                                break;
                            case R.id.llnegative:
                                renameDialog.dismiss();//让重命名对话框消失
                                break;

                        }
                    }
                });
                renameDialog.show();//展示重命名对话框
                renameDialog.setRenameString(oldName);//将文件名加载至对话框
            }

            @Override
            public void share(String path) {

                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    File f = new File(path);
                    if (!f.exists()) {
                        return;
                    }
                    Uri u = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", f);
                    intent.putExtra(Intent.EXTRA_STREAM, u);
                }
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra("sms_body", "感谢使用");            //邮件内容
                intent.setType("image/*");                    //设置类型
                getActivity().startActivity(intent);
            }
        });
        rvImage.setAdapter(imageAdapter);//将适配器与rvImage绑定

    }


    @Override
    protected void updateData(String type) {
        super.updateData(type);
        //绑定数据
        if (type.equals("image")) {
            List<ImageModern> videoModerns = new FileUtil().getImageInfoByPath(getActivity().getFilesDir().getAbsolutePath() + "/Pictures");
            Log.d("xwl", "image data size " + videoModerns.size());
            imageAdapter.setData(videoModerns);
        }
    }


}
