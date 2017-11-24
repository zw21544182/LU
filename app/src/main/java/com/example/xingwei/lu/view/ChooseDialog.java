package com.example.xingwei.lu.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.util.SharedPreferencesUtil;
import com.example.xingwei.lu.util.ToastUtil;


/**
 * 创建时间: 2017/7/12
 * 创建人: zhongwang
 * 功能描述: 显示DetectionDetail nodeInfo对话框
 */

public class ChooseDialog extends Dialog implements View.OnClickListener {
    private Context context;      // 上下文
    private int layoutResID;      // 布局文件id
    private RelativeLayout rlPos;
    private RelativeLayout rlNeg;
    private RadioGroup rgSize;


    private SharedPreferencesUtil sharedPreferencesUtil;


    public ChooseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public ChooseDialog(Context context) {
        super(context, R.style.dialog_node); //dialog的样式
        this.context = context;
        this.layoutResID = R.layout.dialog_choose;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);
        initView();
        initEvent();
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
        window.setWindowAnimations(R.style.bottom_menu_animation); // 添加动画效果
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth() * 7 / 10;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);// 点击Dialog外部消失

    }

    private void initEvent() {
        rlPos.setOnClickListener(this);
        rlNeg.setOnClickListener(this);
    }

    private void initView() {
        rgSize = (RadioGroup) findViewById(R.id.rgSize);
        rlPos = (RelativeLayout) findViewById(R.id.rlPos);
        rlNeg = (RelativeLayout) findViewById(R.id.rlNeg);

    }


    @Override
    public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.rlPos:
                SharedPreferencesUtil.setParam(context, "size", rgSize.getCheckedRadioButtonId());
                ToastUtil.getInstance(context).showToast(context.getString(R.string.savesucess));
                break;

        }
    }
}
