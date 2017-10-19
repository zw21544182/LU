package com.example.xingwei.lu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.example.xingwei.lu.R;
import com.example.xingwei.lu.base.BaseFragment;

/**
 * 创建时间: 2017/10/18
 * 创建人: Administrator
 * 功能描述:
 */

public class SetFragment extends BaseFragment {
    private View settingView;


    @Override
    public void changState() {

    }

    @Override
    public View initView(LayoutInflater inflater) {
        settingView = inflater.inflate(R.layout.fragment_setting, null);
        initFindViewById(settingView);
        return settingView;
    }

    @Override
    public void initFindViewById(View view) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void click(View view) {

    }
}
