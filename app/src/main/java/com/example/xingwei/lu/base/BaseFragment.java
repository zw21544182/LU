package com.example.xingwei.lu.base;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xingwei.lu.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    public View view;
    private ToastUtil toastUtil;
    private AudioBroadcastReceiver receiver;

    public void showToast(String content) {
        toastUtil.showToast(content);
    }

    @Override
    public void onClick(View view) {
        click(view);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = initView(inflater);
        return view;
    }


    public View getRootView() {
        return view;
    }

    //子类复写此方法初始化事件
    protected void initEvent() {
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toastUtil = ToastUtil.getInstance(getActivity());
        initData(savedInstanceState);
        initBroadcast();
        initEvent();

    }

    public abstract void changState();

    private void initBroadcast() {
        if (receiver == null) {
            receiver = new AudioBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.audioeadd");
            getActivity().registerReceiver(receiver, filter);
        }
    }

    /*
        子类实现此方法返回View展示
         */
    public abstract View initView(LayoutInflater inflater);

    //初始化控件
    public abstract void initFindViewById(View view);

    //子类在此方法中实现数据的初始化
    public abstract void initData(@Nullable Bundle savedInstanceState);

    public abstract void click(View view);

    public List<String> getDeletePaths() {
        return new ArrayList<>();
    }

    public class AudioBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData(intent.getExtras().getString("type"));
        }
    }

    protected void updateData(String type) {
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(receiver);
    }
}

